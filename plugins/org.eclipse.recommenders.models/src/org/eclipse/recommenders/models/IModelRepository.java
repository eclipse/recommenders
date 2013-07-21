package org.eclipse.recommenders.models;

import java.io.File;
import java.io.IOException;

import com.google.common.base.Optional;

public interface IModelRepository {

    /**
     * The coordinate under which the model search index of the remote model repository is addressable.
     */
    public static ModelArchiveCoordinate INDEX = new ModelArchiveCoordinate("org.eclipse.recommenders", "index",
            "index", "zip", "0.0.0-SNAPSHOT");

    /**
     * Resolves the given model coordinate to a local file and downloads the corresponding file from the remote
     * repository if not locally available.
     * 
     * @throws Exception
     *             if no model could be downloaded due to, e.g., the coordinate does not exist on the remote repository
     *             or a network/io error occurred.
     */
    public abstract void resolve(ModelArchiveCoordinate model) throws Exception;

    /**
     * Deletes the artifact represented by the given coordinate from the local file system.
     */
    public abstract void delete(ModelArchiveCoordinate model) throws IOException;

    /**
     * Returns the file for the given coordinate - if it exists. Note that this call does <b>not</b> download any
     * resources from the remote repository. It only touches the local file system.
     */
    public abstract Optional<File> getLocation(ModelArchiveCoordinate coordinate);

    /**
     * Searches the model index for all model archives matching the given {@link ProjectCoordinate} and model-type.
     */
    public abstract ModelArchiveCoordinate[] findModelArchives(ProjectCoordinate coordinate, String modelType);

    /**
     * Searches the model index for the best model archive matching the given {@link ProjectCoordinate} and model-type.
     */
    public abstract Optional<ModelArchiveCoordinate> findBestModelArchive(ProjectCoordinate coordinate, String modelType);

}