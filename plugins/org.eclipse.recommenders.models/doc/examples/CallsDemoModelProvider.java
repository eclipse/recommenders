package examples;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.recommenders.models.IBasedName;
import org.eclipse.recommenders.models.archives.AbstractModelProvider;
import org.eclipse.recommenders.models.archives.IModelArchiveCoordinateProvider;
import org.eclipse.recommenders.models.archives.ModelArchiveCoordinate;
import org.eclipse.recommenders.utils.Zips;
import org.eclipse.recommenders.utils.names.ITypeName;

public class CallsDemoModelProvider extends AbstractModelProvider<IBasedName<ITypeName>, Object> {

    public CallsDemoModelProvider(IModelArchiveCoordinateProvider modelIdProvider) {
        super(modelIdProvider);
    }

    @Override
    protected Object createModel(IBasedName<ITypeName> key, ZipFile modelArchive, ModelArchiveCoordinate modelId) throws IOException {
        String path = Zips.path(key.getName(), ".net");
        ZipEntry entry = new ZipEntry(path);
        InputStream s = modelArchive.getInputStream(entry);
        Object model = null; // ... do things with s to create a model
        s.close();
        return model;
    }
}
