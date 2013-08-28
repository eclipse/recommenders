/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.models;

import java.io.File;

import com.google.common.base.Optional;

public interface IModelRepository {

    /**
     * Returns the file for the given model coordinate if it exists locally.
     * 
     * Depending on the implementation, calling this method <b>may</b> also trigger a background download of the
     * requested file.
     * 
     * This method can be assumed to return quickly.
     */
    Optional<File> getLocation(ModelCoordinate mc);

    /**
     * Resolves the given model coordinate to a local file. If the model does not yet exist locally this method attempts
     * to download the model from the remote repository.
     * 
     * This method blocks the caller until the download (if necessary) is finished; callers must not asusme that this
     * method returns quickly.
     * 
     * @return the path to the locally cached model archive.
     * 
     * @throws Exception
     *             if no file could be downloaded, e.g., because the model coordinate does not exist in the remote
     *             repository or an I/O error has occurred.
     */
    Optional<File> resolve(ModelCoordinate mc) throws Exception;

    Optional<File> resolve(ModelCoordinate mc, DownloadCallback callback) throws Exception;
}
