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
package org.eclipse.recommenders.models.archives;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.recommenders.models.ProjectCoordinate;

import com.google.common.base.Optional;

/**
 * A {@link ModelArchiveCache} is responsible for downloading and caching (file-based) model artifacts from a remote
 * maven repository. It has a local working directory where it stores model artifacts and some meta-data, and is
 * configured with a remote (maven) repository URL from which it fetches model artifacts on demand.
 */
public abstract class ModelArchiveCache {

    /**
     * The coordinate under which the model search index of the remote model repository is addressable.
     */
    public static ModelArchiveCoordinate INDEX = new ModelArchiveCoordinate("org.eclipse.recommenders", "index",
            "index", "zip", "0.0.0-SNAPSHOT");

    /**
     * Utility method that checks whether the given coordinate is the index coordinate.
     */
    public static boolean isModelIndex(ModelArchiveCoordinate coord) {
        return INDEX.equals(coord);
    }

    /**
     * Changes the remote repository used to resolve and download artifacts. This change takes effect immediately.
     */
    public abstract void setRemote(String url);

    /**
     * Resolves the given model coordinate to a local file and downloads the corresponding file from the remote
     * repository if not locally available.
     * 
     * @return the local copy of the model artifact
     * @throws IOException
     *             if no model could be downloaded due to, e.g., the coordinate does not exist on the remote repository
     *             or a network/io error occurred.
     */
    public abstract void resolve(ModelArchiveCoordinate model, IProgressMonitor monitor) throws IOException;

    /**
     * Deletes the artifact represented by the given coordinate from the local file system.
     */
    public abstract void delete(ModelArchiveCoordinate model, IProgressMonitor monitor) throws IOException;

    /**
     * Checks if the file for the given coordinate exists in the local file system.
     */
    public abstract boolean isCached(ModelArchiveCoordinate coord);

    /**
     * Returns the file for the given coordinate - if it exists. Note that this call does <b>not</b> download any
     * resources from the remote repository. It only touches the local file system.
     */
    public abstract Optional<File> getLocation(ModelArchiveCoordinate coord);

    /**
     * Searches the model index for all model archives matching the given {@link ProjectCoordinate} and model-type.
     */
    public abstract ModelArchiveCoordinate[] findModelArchives(ProjectCoordinate projectCoord, String modelType);

    /**
     * Searches the model index for the best model archive matching the given {@link ProjectCoordinate} and model-type.
     */
    public abstract Optional<ModelArchiveCoordinate> findBestModelArchive(ProjectCoordinate projectCoord,
            String modelType);

}