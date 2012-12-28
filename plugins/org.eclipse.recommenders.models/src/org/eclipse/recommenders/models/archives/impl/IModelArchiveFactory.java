package org.eclipse.recommenders.models.archives.impl;

import java.io.IOException;

import org.eclipse.recommenders.models.archives.IModelArchive;
import org.eclipse.recommenders.models.archives.ModelArchiveCache;
import org.eclipse.recommenders.models.archives.ModelArchiveCoordinate;

/**
 * Responsible for creating a {@link IModelArchive} from a given {@link ModelArchiveCoordinate} and
 * {@link ModelArchiveCache}.
 */
public interface IModelArchiveFactory<K, M> {

    /**
     * Creates and opens a {@link IModelArchive} from the given coordinate and cache - if it exists. The returned model
     * archive is ready-to-use by the caller.
     * <p>
     * Note that this method is not expected to trigger a model archive download from the remote repository.
     * 
     * @throws IOException
     *             if the given coordinate could not be found in the {@link ModelArchiveCache} or an exception occurred
     *             while opening the archive.
     * 
     * @see IModelArchive#open()
     */
    IModelArchive<K, M> create(ModelArchiveCoordinate coord, ModelArchiveCache repository) throws IOException;
}
