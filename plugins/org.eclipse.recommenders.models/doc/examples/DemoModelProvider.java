package examples;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.pool.BaseKeyedPoolableObjectFactory;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.eclipse.recommenders.models.IBasedName;
import org.eclipse.recommenders.models.IModelProvider;
import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.models.archives.IModelArchiveCoordinateProvider;
import org.eclipse.recommenders.models.archives.ModelArchiveCacheEvents.ModelArchiveInstalledEvent;
import org.eclipse.recommenders.models.archives.ModelArchiveCoordinate;
import org.eclipse.recommenders.utils.IOUtils;
import org.eclipse.recommenders.utils.Zips;
import org.eclipse.recommenders.utils.annotations.Nullable;
import org.eclipse.recommenders.utils.names.ITypeName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.Subscribe;

public class DemoModelProvider implements IModelProvider<IBasedName<ITypeName>, String> {

    Logger log = LoggerFactory.getLogger(getClass());

    // which zip files are currently open?
    Map<ModelArchiveCoordinate, ZipFile> openZips;

    // which models are currently pooled?
    Multimap<ModelArchiveCoordinate, IBasedName<ITypeName>> pooledModels;

    // which models are currently borrowed to someone?
    IdentityHashMap<String, IBasedName<ITypeName>> borrowedModels = Maps.newIdentityHashMap();

    // will be a specific one for call models for example:
    IModelArchiveCoordinateProvider modelIdProvider;
    GenericKeyedObjectPool<IBasedName<ITypeName>, String> pool = createPool();

    public DemoModelProvider(IModelArchiveCoordinateProvider modelIdProvider) {
        this.modelIdProvider = modelIdProvider;
    }

    @Subscribe
    public void onEvent(ModelArchiveInstalledEvent e) {
        ModelArchiveCoordinate modelId = e.coordinate;
        closeZipFile(modelId);
        clearPooledModels(modelId);
    }

    private void clearPooledModels(ModelArchiveCoordinate modelId) {
        for (IBasedName<ITypeName> key : pooledModels.get(modelId)) {
            pool.clear(key);
        }
    }

    private void closeZipFile(@Nullable ModelArchiveCoordinate modelId) {
        ZipFile zip = openZips.remove(modelId);
        if (zip == null) return;
        IOUtils.closeQuietly(zip);
    }

    @Override
    public Optional<String> acquireModel(IBasedName<ITypeName> key) {
        Optional<ModelArchiveCoordinate> opt = modelIdProvider.get(key.getBase());
        if (!opt.isPresent()) return Optional.<String> absent();
        try {
            String model = pool.borrowObject(key);
            return of(model);
        } catch (Exception e) {
            log.error("Couldn't obtain model for " + key, e);
            return absent();
        }
    }

    @Override
    public void releaseModel(String value) {
        try {
            IBasedName<ITypeName> key = borrowedModels.remove(value);
            pool.returnObject(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        closePool();
        closeZipFiles();
    }

    private void closePool() {
        try {
            pool.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void closeZipFiles() {
        for (ZipFile zip : openZips.values()) {
            IOUtils.closeQuietly(zip);
        }
    }

    private GenericKeyedObjectPool createPool() {
        GenericKeyedObjectPool pool = new GenericKeyedObjectPool(new PoolFactory());
        pool.setMaxTotal(10);
        pool.setMaxIdle(3);
        pool.setWhenExhaustedAction(GenericKeyedObjectPool.WHEN_EXHAUSTED_FAIL);
        // run clean up every 5 minutes:
        pool.setTimeBetweenEvictionRunsMillis(5 * 60 * 1000);
        // models are evictable after 5 minutes
        pool.setMinEvictableIdleTimeMillis(5 * 60 * 1000);
        return pool;
    }

    private String createModel(IBasedName<ITypeName> key) throws IOException {
        ModelArchiveCoordinate modelId = modelIdProvider.get(key.getBase()).orNull();
        ZipFile zipFile = openZips.get(modelId);
        if (zipFile == null) {
            return "";
        } else {
            String path = Zips.path(key.getName(), ".net");
            ZipEntry entry = new ZipEntry(path);
            zipFile.getInputStream(entry);
            // ... do things
            pooledModels.put(modelId, key);
            return "model";
        }
    }

    private void destroyModel(IBasedName<ITypeName> key, String obj) {
        pooledModels.remove(key, obj);
        // if there are no more models loaded
        ProjectCoordinate projectid = key.getBase();
        ModelArchiveCoordinate modelId = modelIdProvider.get(projectid).orNull();
        if (!pooledModels.containsKey(modelId)) {
            closeZipFile(modelId);
        }
    }

    private void passivateModel(IBasedName<ITypeName> key, String obj) {
        // TODO clear model state to free memory
    }

    private final class PoolFactory extends BaseKeyedPoolableObjectFactory<IBasedName<ITypeName>, String> {
        @Override
        public String makeObject(IBasedName<ITypeName> key) throws Exception {
            return createModel(key);
        }

        @Override
        public void destroyObject(IBasedName<ITypeName> key, String obj) throws Exception {
            destroyModel(key, obj);
        }

        @Override
        public void passivateObject(IBasedName<ITypeName> key, String obj) throws Exception {
            passivateModel(key, obj);
        }
    }
}
