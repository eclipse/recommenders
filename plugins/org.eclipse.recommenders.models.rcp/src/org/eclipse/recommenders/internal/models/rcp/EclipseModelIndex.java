/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 *    Olav Lenz - multi index support.
 */
package org.eclipse.recommenders.internal.models.rcp;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static org.eclipse.recommenders.internal.models.rcp.ModelsRcpModule.INDEX_BASEDIR;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.io.FileUtils;
import org.eclipse.recommenders.models.IModelIndex;
import org.eclipse.recommenders.models.IModelRepository;
import org.eclipse.recommenders.models.ModelCoordinate;
import org.eclipse.recommenders.models.ModelIndex;
import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.models.rcp.ModelEvents.ModelArchiveDownloadedEvent;
import org.eclipse.recommenders.models.rcp.ModelEvents.ModelIndexOpenedEvent;
import org.eclipse.recommenders.models.rcp.ModelEvents.ModelRepositoryClosedEvent;
import org.eclipse.recommenders.models.rcp.ModelEvents.ModelRepositoryOpenedEvent;
import org.eclipse.recommenders.rcp.IRcpService;
import org.eclipse.recommenders.utils.Pair;
import org.eclipse.recommenders.utils.Urls;
import org.eclipse.recommenders.utils.Zips;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * The Eclipse RCP wrapper around an IModelIndex that responds to (@link ModelRepositoryChangedEvent)s by closing the
 * underlying, downloading the new index if required and reopening the index.
 */
public class EclipseModelIndex implements IModelIndex, IRcpService {

    Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    @Named(INDEX_BASEDIR)
    File basedir;

    @Inject
    ModelsRcpPreferences prefs;

    @Inject
    IModelRepository repository;

    @Inject
    EventBus bus;

    private Map<String, ModelIndex> delegates = Maps.newHashMap();

    private Map<String, File> activedirs = Maps.newHashMap();

    private Cache<Pair<ProjectCoordinate, String>, Optional<ModelCoordinate>> cache = CacheBuilder.newBuilder()
            .maximumSize(10).concurrencyLevel(1).build();

    @PostConstruct
    @Override
    public void open() throws IOException {
        doOpen(true);
    }

    private void doOpen(boolean scheduleIndexUpdate) throws IOException {
        String[] remoteUrls = prefs.remotes;
        delegates.clear();
        activedirs.clear();
        basedir.mkdir();

        for (String url : remoteUrls) {
            doOpen(url, scheduleIndexUpdate);
        }

    }

    private void doOpen(String remoteUrl, boolean scheduleIndexUpdate) throws IOException {
        File indexLocation = new File(basedir, Urls.mangle(remoteUrl));
        ModelIndex modelIndex = new ModelIndex(indexLocation);
        activedirs.put(remoteUrl, indexLocation);
        delegates.put(remoteUrl, modelIndex);
        if (!indexAlreadyDownloaded(indexLocation) || scheduleIndexUpdate) {
            triggerIndexDownload(remoteUrl);
            return;
        }
        modelIndex.open();
        bus.post(new ModelIndexOpenedEvent());
    }

    private void triggerIndexDownload(String remoteUrl) {
        ModelCoordinate mc = createCopyWithRemoteUrlHint(remoteUrl);
        new DownloadModelArchiveJob(repository, mc, true, bus).schedule(300);
    }

    private ModelCoordinate createCopyWithRemoteUrlHint(String remoteUrl) {
        ModelCoordinate mc = new ModelCoordinate(INDEX.getGroupId(), INDEX.getArtifactId(), INDEX.getClassifier(),
                INDEX.getExtension(), INDEX.getVersion());
        return addRepositoryUrlHint(mc, remoteUrl);
    }

    private boolean indexAlreadyDownloaded(File location) {
        return location.exists() && location.listFiles().length > 1;
        // 2 = if this folder contains an index, there must be more than one file...
        // On mac, we often have hidden files in the folder. This is just simple heuristic.
    }

    @PreDestroy
    @Override
    public void close() throws IOException {
        cache.invalidateAll();
        for (String remoteUrl : delegates.keySet()) {
            close(remoteUrl);
        }
    }
    
    private void close(String remoteUrl) throws IOException{
        ModelIndex index = delegates.get(remoteUrl);
        if (index != null){
            index.close();
        }
    }

    /**
     * This implementation caches the previous results
     */
    @Override
    public Optional<ModelCoordinate> suggest(final ProjectCoordinate pc, final String modelType) {
        Pair<ProjectCoordinate, String> key = Pair.newPair(pc, modelType);
        try {
            return cache.get(key, new Callable<Optional<ModelCoordinate>>() {

                @Override
                public Optional<ModelCoordinate> call() {
                    for (Entry<String, ModelIndex> entry : delegates.entrySet()) {
                        Optional<ModelCoordinate> suggest = entry.getValue().suggest(pc, modelType);
                        if (suggest.isPresent()) {
                            ModelCoordinate mc = suggest.get();
                            mc.addHint(ModelCoordinate.REPOSITORY_URL, entry.getKey());
                            return of(mc);
                        }
                    }

                    return absent();
                }
            });
        } catch (ExecutionException e) {
            log.error("Exception occured while accessing model coordinates cache", e);
            return absent();
        }
    }

    @Override
    public ImmutableSet<ModelCoordinate> suggestCandidates(ProjectCoordinate pc, String modelType) {
        Set<ModelCoordinate> candidates = Sets.newHashSet();
        for (Entry<String, ModelIndex> entry : delegates.entrySet()) {
            candidates.addAll(addRepositoryUrlHint(entry.getValue().suggestCandidates(pc, modelType), entry.getKey()));
        }

        return ImmutableSet.copyOf(candidates);
    }

    public Set<ModelCoordinate> addRepositoryUrlHint(Set<ModelCoordinate> modelCoordinates, String url) {
        for (ModelCoordinate modelCoordinate : modelCoordinates) {
            addRepositoryUrlHint(modelCoordinate, url);
        }
        return modelCoordinates;
    }

    private ModelCoordinate addRepositoryUrlHint(ModelCoordinate modelCoordinate, String url) {
        modelCoordinate.addHint(ModelCoordinate.REPOSITORY_URL, url);
        return modelCoordinate;
    }

    @Override
    public ImmutableSet<ModelCoordinate> getKnownModels(String modelType) {
        Set<ModelCoordinate> models = Sets.newHashSet();
        for (Entry<String, ModelIndex> entry : delegates.entrySet()) {
            models.addAll(addRepositoryUrlHint(entry.getValue().getKnownModels(modelType), entry.getKey()));
        }

        return ImmutableSet.copyOf(models);
    }

    @Override
    public Optional<ProjectCoordinate> suggestProjectCoordinateByArtifactId(String artifactId) {
        for (ModelIndex index : delegates.values()) {
            Optional<ProjectCoordinate> suggest = index.suggestProjectCoordinateByArtifactId(artifactId);
            if (suggest.isPresent()) {
                return suggest;
            }
        }

        return absent();
    }

    @Override
    public Optional<ProjectCoordinate> suggestProjectCoordinateByFingerprint(String fingerprint) {
        for (ModelIndex index : delegates.values()) {
            Optional<ProjectCoordinate> suggest = index.suggestProjectCoordinateByFingerprint(fingerprint);
            if (suggest.isPresent()) {
                return suggest;
            }
        }

        return absent();
    }

    @Subscribe
    public void onEvent(ModelRepositoryOpenedEvent e) throws IOException {
        doOpen(true);
    }

    @Subscribe
    public void onEvent(ModelRepositoryClosedEvent e) throws IOException {
        close();
    }

    @Subscribe
    public void onEvent(ModelArchiveDownloadedEvent e) throws IOException {
        if (INDEX.equals(e.model)) {
            File location = repository.getLocation(e.model, false).orNull();
            Optional<String> hint = e.model.getHint(ModelCoordinate.REPOSITORY_URL);
            if (hint.isPresent()) {
                String remoteUrl = hint.get();
                close(remoteUrl);
                File file = activedirs.get(remoteUrl);
                file.mkdir();
                FileUtils.cleanDirectory(file);
                Zips.unzip(location, file);
                doOpen(remoteUrl, false);
            }
        }
    }
}
