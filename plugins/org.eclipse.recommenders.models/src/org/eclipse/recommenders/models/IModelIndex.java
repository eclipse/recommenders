package org.eclipse.recommenders.models;

import java.io.IOException;
import java.util.Collection;

import com.google.common.base.Optional;

public interface IModelIndex extends IModelArchiveCoordinateResolver {

    public static final ModelArchiveCoordinate INDEX = new ModelArchiveCoordinate("org.eclipse.recommenders", "index",
            null, "zip", "0.0.0");

    @Override
    void open() throws IOException;

    @Override
    void close() throws IOException;

    Collection<ModelArchiveCoordinate> getKnownModels(String modelType);

    Optional<ProjectCoordinate> suggestProjectCoordinateByArtifactId(String artifactId);

    Optional<ProjectCoordinate> suggestProjectCoordinateByFingerprint(String fingerprint);

}
