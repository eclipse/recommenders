/**
 * Copyright (c) 2010, 2012 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Lerch - initial API and implementation.
 *    Marcel Bruch - generalized API.
 */
package org.eclipse.recommenders.models.archives.impl;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;
import static org.eclipse.recommenders.utils.Checks.ensureIsNotNull;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.pool.KeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.eclipse.recommenders.models.archives.IModelArchive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

/**
 * An implementation of an {@link IModelArchive} that uses a model-pool to improve performance. It guarantees that every
 * caller gets a fresh instance of the model.
 * <p>
 * To facility reuse of the pooling logic, the model creation is delegated to an {@link IModelFactory} that contains the
 * specific bits who to actually load the model.
 * 
 * @see IModelFactory
 * @see IModelArchiveFactory
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PoolingModelArchive<K, M> implements IModelArchive<K, M> {
    private Logger log = LoggerFactory.getLogger(getClass());
    private GenericKeyedObjectPool pool;
    private IModelFactory<K, M> factory;
    private Map<M, K> objects = Maps.newHashMap();

    public PoolingModelArchive(final IModelFactory<K, M> factory) {
        this.factory = ensureIsNotNull(factory);
        pool = createPool();
    }

    private GenericKeyedObjectPool createPool() {
        GenericKeyedObjectPool pool = new GenericKeyedObjectPool(new PooledObjectsFactory());
        pool.setMaxTotal(10);
        pool.setMaxIdle(3);
        pool.setWhenExhaustedAction(GenericKeyedObjectPool.WHEN_EXHAUSTED_FAIL);
        // run clean up every 5 minutes:
        pool.setTimeBetweenEvictionRunsMillis(5 * 60 * 1000);
        // models are evictable after 5 minutes
        pool.setMinEvictableIdleTimeMillis(5 * 60 * 1000);
        return pool;
    }

    @Override
    public void open() {
        factory.open();
    }

    @Override
    public void close() throws IOException {
        factory.close();
        try {
            pool.close();
        } catch (Exception e) {
            // wrap pool's Exception into IOException
            throw new IOException(e);
        }
    }

    @Override
    public boolean hasModel(K key) {
        return factory.hasModel(key);
    }

    @Override
    public Optional<M> acquireModel(final K key) {
        try {
            M model = (M) pool.borrowObject(key);
            if (model != null) objects.put(model, key);
            return fromNullable(model);
        } catch (final Exception e) {
            log.error("Exception while loading model for key '" + key + "'" + e.getMessage(), e);
            return absent();
        }
    }

    @Override
    public void releaseModel(final M model) {
        try {
            if (objects.containsKey(model)) {
                K key = objects.get(model);
                pool.returnObject(key, model);
            }
        } catch (final Exception e) {
            log.error("Exception while releasing model'" + model + "'", e);
        }
    }

    /**
     * internal interface used to hide the implementation detail that Apache Commons Pooling is used from the
     * implementors.
     */
    private class PooledObjectsFactory implements KeyedPoolableObjectFactory {
        @Override
        public boolean validateObject(Object key, Object obj) {
            return factory.validateModel((K) key, (M) obj);
        }

        @Override
        public void passivateObject(Object key, Object obj) throws Exception {
            factory.passivateModel((K) key, (M) obj);
        }

        @Override
        public Object makeObject(final Object key) throws Exception {
            return factory.createModel((K) key);
        }

        @Override
        public void destroyObject(Object key, Object obj) throws Exception {
            factory.destroyModel((K) key, (M) obj);
        }

        @Override
        public void activateObject(Object key, Object obj) throws Exception {
            factory.activateModel((K) key, (M) obj);
        }
    }
}
