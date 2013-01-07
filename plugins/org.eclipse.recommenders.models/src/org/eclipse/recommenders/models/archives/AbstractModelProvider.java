package org.eclipse.recommenders.models.archives;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static org.eclipse.recommenders.utils.Checks.ensureIsNotNull;

import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.zip.ZipFile;

import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.eclipse.recommenders.models.IBasedName;
import org.eclipse.recommenders.models.IModelProvider;
import org.eclipse.recommenders.models.archives.ModelArchiveCacheEvents.ModelArchiveInstalledEvent;
import org.eclipse.recommenders.utils.IOUtils;
import org.eclipse.recommenders.utils.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;

public abstract class AbstractModelProvider<K extends IBasedName<?>, M> implements IModelProvider<K, M> {

    private Logger log = LoggerFactory.getLogger(getClass());

    // which zip files are currently open?
    private Map<ModelArchiveCoordinate, ZipFile> openZips = Maps.newHashMap();

    // which models are currently pooled?
    private ListMultimap<ModelArchiveCoordinate, K> pooledModels = LinkedListMultimap.create();

    // which models are currently borrowed to someone?
    private IdentityHashMap<M, K> borrowedModels = Maps.newIdentityHashMap();

    // will be a specific one for call models for example:
    // REVIEW: do we need this?
    private IModelArchiveCoordinateProvider modelIdProvider;

    // model pool
    // REVIEW: we may want to make pool creation configurable later?
    private GenericKeyedObjectPool<K, M> modelPool = createModelPool();

    public AbstractModelProvider(IModelArchiveCoordinateProvider modelIdProvider) {
        this.modelIdProvider = modelIdProvider;
    }

    private GenericKeyedObjectPool<K, M> createModelPool() {
        GenericKeyedObjectPool<K, M> pool = new GenericKeyedObjectPool<K, M>(new ModelPoolFactoryMediator());
        pool.setMaxTotal(30);
        pool.setMaxIdle(5);
        pool.setWhenExhaustedAction(GenericKeyedObjectPool.WHEN_EXHAUSTED_FAIL);
        // run clean up every 5 minutes:
        pool.setTimeBetweenEvictionRunsMillis(5 * 60 * 1000);
        // models are evictable after 5 minutes
        pool.setMinEvictableIdleTimeMillis(5 * 60 * 1000);
        return pool;
    }

    @Override
    public Optional<M> acquireModel(K key) {
        Optional<ModelArchiveCoordinate> opt = modelIdProvider.get(key.getBase());
        if (!opt.isPresent()) return Optional.absent();
        try {
            M model = modelPool.borrowObject(key);
            return of(model);
        } catch (Exception e) {
            log.error("Couldn't obtain model for " + key, e);
            return absent();
        }
    }

    protected abstract M createModel(K key, ZipFile modelArchive, ModelArchiveCoordinate modelId) throws IOException;

    @Override
    public void releaseModel(M model) {
        try {
            K key = borrowedModels.remove(model);
            modelPool.returnObject(key, model);
        } catch (Exception e) {
            log.error("Exception while releasing Couldn't release model " + model, e);
        }
    }

    protected void passivateModel(K key, M model, ModelArchiveCoordinate modelId) {
    };

    protected void destroyModel(K key, M model, ModelArchiveCoordinate modelId) {
    };

    @Subscribe
    public void onEvent(ModelArchiveInstalledEvent e) {
        ModelArchiveCoordinate modelId = e.coordinate;
        closeZipFile(modelId);
        clearPooledModels(modelId);
    }

    private void closeZipFile(@Nullable ModelArchiveCoordinate modelId) {
        ZipFile zip = openZips.remove(modelId);
        if (zip == null) return;
        IOUtils.closeQuietly(zip);
    }

    private void clearPooledModels(ModelArchiveCoordinate modelId) {
        for (K key : pooledModels.get(modelId)) {
            modelPool.clear(key);
        }
    }

    @Override
    public void close() throws IOException {
        closePool();
        closeZipFiles();
    }

    private void closePool() {
        try {
            modelPool.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeZipFiles() {
        for (ZipFile zip : openZips.values()) {
            IOUtils.closeQuietly(zip);
        }
    }

    /**
     * Mediates calls from Apache Commons Pool implementation to our {create,destroy,passivate}Model() methods above.
     */
    private final class ModelPoolFactoryMediator extends BaseKeyedPoolableObjectFactory<K, M> {
        @Override
        public M makeObject(K key) throws Exception {
            ModelArchiveCoordinate modelId = ensureIsNotNull(modelIdProvider.get(key.getBase()).orNull());
            ZipFile zipFile = openZips.get(modelId);
            if (zipFile == null) return null;
            M model = createModel(key, zipFile, modelId);
            pooledModels.put(modelId, key);
            return model;
        }

        /**
         * Removes the given model from the list of tracked pooled models and closes the zip-file this model originates
         * from if no other model is loaded from this zip-file.
         * 
         * @see AbstractModelProvider#destroyModel(IBasedName, Object, ModelArchiveCoordinate)
         */
        @Override
        public void destroyObject(K key, M model) throws Exception {
            ModelArchiveCoordinate modelId = ensureIsNotNull(modelIdProvider.get(key.getBase()).orNull());
            pooledModels.remove(key, model);
            // if there are no more models loaded
            if (!pooledModels.containsKey(modelId)) {
                closeZipFile(modelId);
            }
            destroyModel(key, model, modelId);
        }

        @Override
        public void passivateObject(K key, M model) throws Exception {
            ModelArchiveCoordinate modelId = ensureIsNotNull(modelIdProvider.get(key.getBase()).orNull());
            passivateModel(key, model, modelId);
        }
    }
}
