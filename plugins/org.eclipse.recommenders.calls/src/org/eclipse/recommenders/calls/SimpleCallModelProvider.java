package org.eclipse.recommenders.calls;

import static com.google.common.base.Optional.absent;
import static org.eclipse.recommenders.utils.Constants.CLASS_CALL_MODELS;

import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.recommenders.commons.bayesnet.BayesianNetwork;
import org.eclipse.recommenders.models.BasedTypeName;
import org.eclipse.recommenders.models.IModelProvider;
import org.eclipse.recommenders.models.IModelRepository;
import org.eclipse.recommenders.models.SimpleModelProvider;
import org.eclipse.recommenders.utils.IOUtils;
import org.eclipse.recommenders.utils.Zips;
import org.eclipse.recommenders.utils.names.ITypeName;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;

/**
 * A non-thread-safe implementation of {@link IModelProvider} for call models that keeps references on the model
 * archives.
 * <p>
 * Note that models should not be shared between several recommenders.
 */
@Beta
public class SimpleCallModelProvider extends SimpleModelProvider<BasedTypeName, ICallModel> implements
        ICallModelProvider {

    public SimpleCallModelProvider(IModelRepository repo) {
        super(repo, CLASS_CALL_MODELS);
    }

    @Override
    protected Optional<ICallModel> loadModel(ZipFile zip, BasedTypeName key) throws Exception {
        return load(zip, key);
    }

    public static Optional<ICallModel> load(ZipFile zip, BasedTypeName key) throws Exception {
        ITypeName type = key.getName();
        String path = Zips.path(type, ".data");
        ZipEntry entry = zip.getEntry(path);
        if (entry == null) {
            return absent();
        }
        InputStream s = zip.getInputStream(entry);
        BayesianNetwork net = BayesianNetwork.read(s);
        IOUtils.closeQuietly(s);
        ICallModel m = new JayesCallModel(type, net);
        return Optional.fromNullable(m);

    }
}
