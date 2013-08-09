/**
 * Copyright (c) 2010, 2012 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 *    Patrick Gottschaemmer, Olav Lenz - Introduced ProxySelector
 *    Olav Lenz - externalize Strings.
 */
package org.eclipse.recommenders.models;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Optional.*;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.eclipse.recommenders.models.AetherModelRepository.DownloadCallback.NULL;
import static org.eclipse.recommenders.utils.Throws.throwUnsupportedOperation;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import org.apache.maven.repository.internal.DefaultServiceLocator;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.apache.maven.wagon.Wagon;
import org.apache.maven.wagon.providers.file.FileWagon;
import org.eclipse.recommenders.utils.Executors;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.DependencyCollectionContext;
import org.sonatype.aether.collection.DependencySelector;
import org.sonatype.aether.connector.wagon.WagonProvider;
import org.sonatype.aether.connector.wagon.WagonRepositoryConnectorFactory;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.Authentication;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.Proxy;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.resolution.DependencyResult;
import org.sonatype.aether.spi.connector.RepositoryConnectorFactory;
import org.sonatype.aether.transfer.TransferCancelledException;
import org.sonatype.aether.transfer.TransferEvent;
import org.sonatype.aether.transfer.TransferListener;
import org.sonatype.aether.util.DefaultRepositorySystemSession;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.maven.wagon.AhcWagon;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

public class AetherModelRepository implements IModelRepository {

    private ListeningExecutorService executor = MoreExecutors.listeningDecorator(Executors.coreThreadsTimoutExecutor(1,
            Thread.MIN_PRIORITY, "model-downloader"));

    private RemoteRepository remote;
    private File basedir;

    private RepositorySystem system;

    public AetherModelRepository(File basedir, String remote) throws Exception {
        this.basedir = basedir;
        system = createRepositorySystem();
        this.remote = new RemoteRepository("remote-models", "default", remote);
    }

    public ListenableFuture<File> schedule(final ModelArchiveCoordinate model) {
        return schedule(model, NULL);
    }

    public ListenableFuture<File> schedule(final ModelArchiveCoordinate model, DownloadCallback callback) {
        final DownloadCallback cb = firstNonNull(callback, DownloadCallback.NULL);
        final Artifact coord = new DefaultArtifact(model.getGroupId(), model.getArtifactId(), model.getClassifier(),
                model.getExtension(), model.getVersion());
        return executor.submit(new DownloadArtifactTask(coord, new TransferListener() {

            @Override
            public void transferSucceeded(TransferEvent e) {
                cb.downloadSucceeded();
            }

            @Override
            public void transferStarted(TransferEvent e) throws TransferCancelledException {
                cb.downloadStarted();
            }

            @Override
            public void transferProgressed(TransferEvent e) throws TransferCancelledException {
                cb.downloadProgressed();
            }

            @Override
            public void transferInitiated(TransferEvent e) throws TransferCancelledException {
                cb.downloadInitiated();
            }

            @Override
            public void transferFailed(TransferEvent e) {
                cb.downloadFailed();
            }

            @Override
            public void transferCorrupted(TransferEvent e) throws TransferCancelledException {
                cb.downloadCorrupted();
            }
        }));
    }

    @Override
    public void resolve(ModelArchiveCoordinate model) throws Exception {
        schedule(model, null).get();
    }

    public void delete(ModelArchiveCoordinate model) throws IOException {
        throwUnsupportedOperation();
    }

    @Override
    public Optional<File> getLocation(ModelArchiveCoordinate coord) {
        File result = new File(basedir, computePath(coord));
        if (!result.exists()) {
            // TODO no logic implemented that prevents multiple resolutions of
            // the same type
            schedule(coord, new DownloadCallback());
            return absent();
        }
        return of(result);
    }

    private String computePath(ModelArchiveCoordinate artifact) {
        String groupId = artifact.getGroupId().replace('.', '/');
        String artifactId = artifact.getArtifactId();
        String version = artifact.getVersion();
        String classifier = artifact.getClassifier();
        String extension = artifact.getExtension();

        StringBuilder sb = new StringBuilder();
        sb.append(groupId).append('/').append(artifactId).append('/').append(version).append('/').append(artifactId)
                .append('-').append(version);
        if (!isEmpty(classifier)) {
            sb.append('-').append(classifier);
        }
        sb.append('.').append(extension);
        return sb.toString();
    }

    private RepositorySystem createRepositorySystem() throws Exception {
        @SuppressWarnings("deprecation")
        DefaultServiceLocator locator = new DefaultServiceLocator();
        locator.setServices(WagonProvider.class, new ManualWagonProvider());
        locator.addService(RepositoryConnectorFactory.class, WagonRepositoryConnectorFactory.class);
        return locator.getService(RepositorySystem.class);
    }

    @Override
    public String toString() {
        return basedir.getAbsolutePath();
    }

    public String getRemoteUrl() {
        return remote.getUrl();
    }

    public void setProxy(String type, String host, int port, String user, String pass) {
        Authentication auth = user == null ? null : new Authentication(user, pass);
        Proxy proxy = type == null ? null : new Proxy(type, host, port, auth);
        remote.setProxy(proxy);
    }

    // TODO: need an API to reset proxy settings.
    public void unsetProxy() {
        remote.setProxy(null);
    }

    public void setAuthentication(String user, String pass) {
        remote.setAuthentication(new Authentication(user, pass));
    }

    private final class DownloadArtifactTask implements Callable<File> {
        private final Artifact coord;
        private final TransferListener callback;

        private DownloadArtifactTask(Artifact coord, TransferListener callback) {
            this.coord = coord;
            this.callback = callback;
        }

        @Override
        public File call() throws Exception {
            DefaultRepositorySystemSession session = newSession();
            session.setTransferListener(callback);
            session.setDependencySelector(new TheArtifactOnlyDependencySelector());
            Dependency dependency = new Dependency(coord, "model");

            CollectRequest collectRequest = new CollectRequest();
            collectRequest.setRoot(dependency);
            collectRequest.addRepository(remote);

            DependencyRequest dependencyRequest = new DependencyRequest();
            dependencyRequest.setCollectRequest(collectRequest);
            DependencyResult dependencies = system.resolveDependencies(session, dependencyRequest);
            DependencyNode root = dependencies.getRoot();
            File file = root.getDependency().getArtifact().getFile();
            return file;
        }

        private synchronized DefaultRepositorySystemSession newSession() {
            MavenRepositorySystemSession session = new MavenRepositorySystemSession();
            LocalRepository localRepo = new LocalRepository(basedir);
            session.setLocalRepositoryManager(system.newLocalRepositoryManager(localRepo));
            return session;
        }

    }

    private static class TheArtifactOnlyDependencySelector implements DependencySelector {

        @Override
        public boolean selectDependency(Dependency d) {
            // we don't want any dependencies to be returned. Just the artifact
            // itself.
            return false;
        }

        @Override
        public DependencySelector deriveChildSelector(DependencyCollectionContext c) {
            return this;
        }
    }

    public static class DownloadCallback {
        public static final DownloadCallback NULL = new DownloadCallback();

        void downloadSucceeded() {
        }

        void downloadCorrupted() {
        }

        void downloadFailed() {
        }

        void downloadInitiated() {
        }

        void downloadProgressed() {
        }

        void downloadStarted() {
        }
    }

    /**
     * A simplistic provider for wagon instances when no Plexus-compatible IoC container is used.
     */
    private class ManualWagonProvider implements org.sonatype.aether.connector.wagon.WagonProvider {

        @Override
        public Wagon lookup(String roleHint) throws Exception {
            if ("http".equals(roleHint) || "https".equals(roleHint)) { //$NON-NLS-1$ //$NON-NLS-2$
                AhcWagon ahcWagon = new AhcWagon();
                // TODO set timeout to 300s instead of 60s to solve timeouts.
                // experimental.
                ahcWagon.setTimeout(300 * 1000);
                return ahcWagon;
                // return new WebDavWagon();
            } else if ("file".equals(roleHint)) {
                return new FileWagon();
            } else {
                return null;
            }
        }

        @Override
        public void release(Wagon wagon) {
        }
    }
}
