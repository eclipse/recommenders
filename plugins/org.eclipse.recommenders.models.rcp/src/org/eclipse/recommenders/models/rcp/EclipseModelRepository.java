package org.eclipse.recommenders.models.rcp;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import org.eclipse.recommenders.internal.rcp.wiring.RecommendersModule.LocalModelRepositoryLocation;
import org.eclipse.recommenders.internal.rcp.wiring.RecommendersModule.RemoteModelRepositoryLocation;
import org.eclipse.recommenders.models.AetherModelRepository;
import org.eclipse.recommenders.models.IModelRepository;
import org.eclipse.recommenders.models.ModelArchiveCoordinate;
import org.eclipse.recommenders.models.ProjectCoordinate;

import com.google.common.base.Optional;

public class EclipseModelRepository implements IModelRepository {

    AetherModelRepository delegate;

    @Inject
    public EclipseModelRepository(@LocalModelRepositoryLocation File repodir,
            @RemoteModelRepositoryLocation String remote) throws Exception {
        delegate = new AetherModelRepository(repodir.getParentFile(), remote);
        delegate.open();
    }

    @Override
    public void resolve(ModelArchiveCoordinate model) throws Exception {
        delegate.resolve(model);
    }

    @Override
    public void delete(ModelArchiveCoordinate model) throws IOException {
        delegate.delete(model);
    }

    @Override
    public Optional<File> getLocation(ModelArchiveCoordinate coordinate) {
        return delegate.getLocation(coordinate);
    }

    @Override
    public ModelArchiveCoordinate[] findModelArchives(ProjectCoordinate coordinate, String modelType) {
        return delegate.findModelArchives(coordinate, modelType);
    }

    @Override
    public Optional<ModelArchiveCoordinate> findBestModelArchive(ProjectCoordinate coordinate, String modelType) {
        return delegate.findBestModelArchive(coordinate, modelType);
    }

}
