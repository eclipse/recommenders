/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.models.rcp;

import static org.eclipse.recommenders.internal.models.rcp.ModelsRcpModule.REPOSITORY_BASEDIR;
import static org.eclipse.recommenders.utils.Urls.mangle;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.net.proxy.IProxyData;
import org.eclipse.core.net.proxy.IProxyService;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.recommenders.models.AetherModelRepository;
import org.eclipse.recommenders.models.AetherModelRepository.DownloadCallback;
import org.eclipse.recommenders.models.IModelRepository;
import org.eclipse.recommenders.models.ModelArchiveCoordinate;
import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.models.rcp.ModelEvents.ModelRepositoryUrlChangedEvent;
import org.eclipse.recommenders.rcp.IRcpService;
import org.eclipse.recommenders.utils.Pair;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * The Eclipse RCP wrapper around an {@link IModelRepository} that responds to (@link ModelRepositoryChangedEvent)s by
 * reconfiguring the underlying repository. It also manages proxy settings and handling of auto download properties.
 */
public class EclipseModelRepository implements IModelRepository, IRcpService {

    @Inject
    @Named(REPOSITORY_BASEDIR)
    File basedir;

    @Inject
    IProxyService proxy;

    @Inject
    ModelsRcpPreferences prefs;

    AetherModelRepository delegate;
    Cache<Pair<ProjectCoordinate, String>, Optional<ModelArchiveCoordinate>> cache = CacheBuilder.newBuilder()
            .maximumSize(10).concurrencyLevel(1).build();

    @PostConstruct
    void open() throws Exception {
        File cache = new File(basedir, mangle(prefs.remote));
        cache.mkdirs();
        delegate = new AetherModelRepository(cache, prefs.remote);
    }

    @PreDestroy
    void close() throws Exception {
    }

    @Subscribe
    public void onModelRepositoryChanged(ModelRepositoryUrlChangedEvent e) throws Exception {
        close();
        open();
    }

    @Override
    public Optional<File> resolve(ModelArchiveCoordinate model) throws Exception {
        updateProxySettings();
        return delegate.resolve(model);
    }

    @Override
    public ListenableFuture<File> resolve(ModelArchiveCoordinate model, DownloadCallback callback) {
        return delegate.resolve(model, callback);
    }

    private void updateProxySettings() {
        if (!proxy.isProxiesEnabled()) {
            delegate.unsetProxy();
            return;
        }
        try {
            URI uri = new URI(prefs.remote);
            IProxyData[] entries = proxy.select(uri);
            if (entries.length == 0) {
                delegate.unsetProxy();
                return;
            }

            IProxyData proxyData = entries[0];
            String type = proxyData.getType().toLowerCase();
            String host = proxyData.getHost();
            int port = proxyData.getPort();
            String userId = proxyData.getUserId();
            String password = proxyData.getPassword();
            delegate.setProxy(type, host, port, userId, password);
        } catch (URISyntaxException e) {
            delegate.unsetProxy();
        }
    }

    @Override
    public Optional<File> getLocation(final ModelArchiveCoordinate coordinate) {
        Optional<File> location = delegate.getLocation(coordinate);
        if (!location.isPresent() && prefs.autoDownloadEnabled) {
            scheduleDownload(coordinate);
        }
        return location;
    }

    private void scheduleDownload(final ModelArchiveCoordinate coordinate) {
        new Job(" Downloading " + coordinate) {
            {
                schedule();
            }

            @Override
            protected IStatus run(final IProgressMonitor monitor) {
                updateProxySettings();
                try {
                    File file = resolve(coordinate, new DownloadCallback() {
                        @Override
                        public void downloadStarted() {
                            monitor.beginTask("Downloading", IProgressMonitor.UNKNOWN);
                        }

                        @Override
                        public void downloadProgressed(long transferredBytes, long totalBytes) {
                            String string = humanReadableByteCount(transferredBytes) + "/"
                                    + humanReadableByteCount(totalBytes);
                            monitor.subTask(string);
                            monitor.worked(1);
                        }

                        String humanReadableByteCount(long bytes) {
                            int unit = 1024;
                            if (bytes < unit) {
                                return bytes + " B";
                            }
                            int exp = (int) (Math.log(bytes) / Math.log(unit));
                            String pre = "KMGTPE".charAt(exp - 1) + "i";
                            return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
                        }

                        @Override
                        public void downloadSucceeded() {
                            monitor.done();
                        }
                    }).get();
                } catch (Exception e) {
                    return new Status(IStatus.ERROR, Constants.BUNDLE_ID, "failed to download " + coordinate, e);
                }
                return Status.OK_STATUS;
            }
        };
    }
}
