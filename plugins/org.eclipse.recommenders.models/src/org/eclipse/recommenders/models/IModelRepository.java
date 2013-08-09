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
     * Returns the file for the given coordinate - if it exists. Note that if the file does not exist, this call
     * immediately returns but triggers a background process that attempts to download the model archive from the remote
     * repository.
     */
    Optional<File> getLocation(ModelArchiveCoordinate coordinate);

    /**
     * Resolves the given model coordinate to a local file and downloads the corresponding file from the remote
     * repository if not locally available. This call blocks the caller until the download finished.
     * 
     * @throws Exception
     *             if no model could be downloaded due to, e.g., the coordinate does not exist on the remote repository
     *             or a network/io error occurred.
     */
    void resolve(ModelArchiveCoordinate model) throws Exception;

}
