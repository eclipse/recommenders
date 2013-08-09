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

import static com.google.common.base.Optional.absent;
import static org.eclipse.recommenders.internal.models.rcp.ModelsRcpModule.INDEX_BASEDIR;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;
import org.eclipse.recommenders.models.AetherModelRepository.DownloadCallback;
import org.eclipse.recommenders.models.IModelIndex;
import org.eclipse.recommenders.models.IModelRepository;
import org.eclipse.recommenders.models.ModelArchiveCoordinate;
import org.eclipse.recommenders.models.ModelIndex;
import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.models.rcp.ModelEvents.ModelRepositoryUrlChangedEvent;
import org.eclipse.recommenders.rcp.IRcpService;
import org.eclipse.recommenders.utils.Pair;
import org.eclipse.recommenders.utils.Urls;
import org.eclipse.recommenders.utils.Zips;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.Subscribe;

/**
 * The Eclipse RCP wrapper around an IModelIndex that responds to (@link ModelRepositoryChangedEvent)s by closing the
 * underlying, downloading the new index if required and reopening the index.
 */
public class EclipseModelIndex implements IModelIndex, IRcpService {

    @Inject
    @Named(INDEX_BASEDIR)
    File basedir;

    @Inject
    ModelsRcpPreferences prefs;

    @Inject
    IModelRepository repository;

    private ModelIndex delegate;

    Cache<Pair<ProjectCoordinate, String>, Optional<ModelArchiveCoordinate>> cache = CacheBuilder.newBuilder()
            .maximumSize(10).concurrencyLevel(1).build();

    @Subscribe
    public void onModelRepositoryChanged(ModelRepositoryUrlChangedEvent e) throws IOException {
        close();
        open();
    }

    @PostConstruct
    @Override
    public void open() throws IOException {
        final File indexdir = new File(basedir, Urls.mangle(prefs.remote));
        delegate = new ModelIndex(indexdir);
        repository.resolve(INDEX, new DownloadCallback() {
            @Override
            public void downloadInitiated() {
                super.downloadInitiated();
            }

            @Override
            public void downloadSucceeded() {
                super.downloadSucceeded();
            }
        });

        if (indexAlreadyDownloaded(indexdir)) {
            delegate.open();
        } else {
            // schedule a job to download the index and open it after the index is there.
            new Job("Initializing Recommenders model search index...") {

                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    monitor.beginTask("Downloading model index.", IProgressMonitor.UNKNOWN);
                    try {
                        repository.resolve(INDEX);
                        File location = repository.getLocation(INDEX).orNull();
                        indexdir.mkdirs();
                        Zips.unzip(location, indexdir);
                        delegate.open();
                    } catch (Exception e) {
                        return new Status(
                                IStatus.ERROR,
                                Constants.BUNDLE_ID,
                                NLS.bind(
                                        "Initializing model index failed. Please manually clear folder {} and retrigger model index download.",
                                        indexdir), e);
                    } finally {
                        monitor.done();
                    }
                    return Status.OK_STATUS;
                }

            }
            // we should give the EclipseModelRepository some time to close the old repository and create the new one.
            .schedule(1000);
        }
    }

    private boolean indexAlreadyDownloaded(File location) {
        return location.exists() && location.listFiles().length > 1;
        // 2 = if this folder contains an index, there must be more than one file...
        // On mac, we often have hidden files in the folder. This is just simple heuristic.
    }

    @PreDestroy
    @Override
    public void close() throws IOException {
        delegate.close();
    }

    @Override
    public Optional<ModelArchiveCoordinate> suggest(final ProjectCoordinate coord, final String modelType) {
        Pair<ProjectCoordinate, String> key = Pair.newPair(coord, modelType);
        try {
            return cache.get(key, new Callable<Optional<ModelArchiveCoordinate>>() {

                @Override
                public Optional<ModelArchiveCoordinate> call() {
                    return delegate.suggest(coord, modelType);
                }
            });
        } catch (ExecutionException e) {
            // TODO log this exception
            return absent();
        }

    }

    @Override
    public ImmutableSet<ModelArchiveCoordinate> suggestCandidates(ProjectCoordinate coord, String modelType) {
        return delegate.suggestCandidates(coord, modelType);
    }

    @Override
    public ImmutableSet<ModelArchiveCoordinate> getKnownModels(String modelType) {
        return delegate.getKnownModels(modelType);
    }

    @Override
    public Optional<ProjectCoordinate> suggestProjectCoordinateByArtifactId(String artifactId) {
        return delegate.suggestProjectCoordinateByArtifactId(artifactId);
    }

    @Override
    public Optional<ProjectCoordinate> suggestProjectCoordinateByFingerprint(String fingerprint) {
        return delegate.suggestProjectCoordinateByFingerprint(fingerprint);
    }
}
