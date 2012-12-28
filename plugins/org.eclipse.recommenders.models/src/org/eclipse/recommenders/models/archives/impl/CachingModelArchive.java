/**
 * Copyright (c) 2010, 2012 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.models.archives.impl;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.eclipse.recommenders.models.archives.IModelArchive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;

/**
 * A caching implementation of {@link IModelArchive}. Models returned by this archive are shared between all clients,
 * i.e., repeated calls to {@link #acquireModel(Object)} may (potentially will) return the same instance. As a
 * consequence models returned by this implementation should be state-less.
 * <p>
 * This implementation disposes models on demand, i.e., after the model hasn't been accessed for a while or when a
 * certain amount of models is already loaded.
 */
public class CachingModelArchive<K, M> implements IModelArchive<K, M> {
    private Logger log = LoggerFactory.getLogger(getClass());

    private Cache<K, M> cache;

    private final IModelFactory<K, M> loader;

    public CachingModelArchive(IModelFactory<K, M> loader) {
        this(loader, 5, 100);
    }

    public CachingModelArchive(IModelFactory<K, M> loader, int expireAfterAccessTimeoutInMinutes, int maxCacheSize) {
        this.loader = loader;
        CacheBuilder.newBuilder().expireAfterAccess(expireAfterAccessTimeoutInMinutes, TimeUnit.MINUTES)
                .maximumSize(maxCacheSize).build(new CacheLoaderWrapper());
    }

    @Override
    public boolean hasModel(K key) {
        try {
            return loader.hasModel(key);
        } catch (Exception e) {
            log.debug("Exception occurred while checking model existence for key " + key, e);
            return false;
        }
    }

    @Override
    public Optional<M> acquireModel(K key) {
        try {
            return fromNullable(cache.get(key));
        } catch (Exception e) {
            log.debug("Exception occurred while fetching model for key " + key, e);
            return absent();
        }
    }

    @Override
    public void releaseModel(M value) {
        // ignore that event.
    }

    @Override
    public void open() {
        loader.open();
    }

    @Override
    public void close() throws IOException {
        loader.close();
        cache.cleanUp();
    }

    private final class CacheLoaderWrapper extends CacheLoader<K, M> {
        @Override
        public M load(K key) throws Exception {
            M model = loader.createModel(key);
            loader.activateModel(key, model);
            return model;
        }
    }
}
