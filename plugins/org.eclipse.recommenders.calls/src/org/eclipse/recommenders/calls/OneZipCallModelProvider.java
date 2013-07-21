package org.eclipse.recommenders.calls;

import static com.google.common.base.Optional.of;
import static org.eclipse.recommenders.utils.IOUtils.closeQuietly;
import static org.eclipse.recommenders.utils.Zips.closeQuietly;
import static org.eclipse.recommenders.utils.Zips.readFully;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.recommenders.commons.bayesnet.BayesianNetwork;
import org.eclipse.recommenders.models.BasedTypeName;
import org.eclipse.recommenders.utils.Openable;
import org.eclipse.recommenders.utils.Zips;
import org.eclipse.recommenders.utils.names.ITypeName;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * A model provider that uses a single zip file to resolve and load call models from.
 * <p>
 * Note that this provider does not implement any pooling behavior, i.e., calls to {@link #acquireModel(BasedTypeName)}
 * may return the <b>same</b> {@link ICallModel} independent of whether {@link #releaseModel(ICallModel)} was called or
 * not. Thus, these <b>models should not be shared between and used by several recommenders at the same time</b>.
 */
@Beta
public class OneZipCallModelProvider implements ICallModelProvider, Openable {

    private final File models;
    private ZipFile zip;
    private final LoadingCache<ITypeName, ICallModel> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(3, TimeUnit.MINUTES)
            .maximumSize(30)
            .build(new CallNetCacheLoader());

    public OneZipCallModelProvider(File models) {
        this.models = models;
    }

    @Override
    public void open() throws IOException {
        readFully(models);
        zip = new ZipFile(models);
    }

    @Override
    public void close() throws IOException {
        closeQuietly(zip);
    }

    @Override
    public Optional<ICallModel> acquireModel(BasedTypeName key) {
        try {
            ICallModel net = cache.get(key.getName());
            net.reset();
            return of(net);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return Optional.absent();
        }
    }

    @Override
    public void releaseModel(ICallModel value) {

    }

    private final class CallNetCacheLoader extends CacheLoader<ITypeName, ICallModel> {
        @Override
        public ICallModel load(ITypeName type) throws Exception {
            String path = Zips.path(type, ".data");
            ZipEntry entry = zip.getEntry(path);
            if (entry == null) {
                return NullCallModel.NULL_MODEL;
            }
            InputStream is = null;
            try {
                is = zip.getInputStream(entry);
                BayesianNetwork bayesNet = BayesianNetwork.read(is);
                return new JayesCallModel(type, bayesNet);
            } catch (Exception e) {
                e.printStackTrace();
                return NullCallModel.NULL_MODEL;
            } finally {
                closeQuietly(is);
            }
        }
    }
}
