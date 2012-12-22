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
package org.eclipse.recommenders.models;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;

public interface IModelRepository {

    /**
     * The coordinate under which the model search index of the remote model repository is addressable.
     */
    Coordinate INDEX = new Coordinate("org.eclipse.recommenders", "index", "index", "zip", "0.0.0");

    /**
     * Changes the remote repository used to resolve and download artifacts. This change takes effect immediately.
     * 
     */
    void setRemote(String url);

    /**
     * Resolves the given model coordinate to a local file and downloads the corresponding file from a remote repository
     * if not locally available.
     * 
     * @return the local copy of the model artifact
     */
    File resolve(Coordinate model, IProgressMonitor monitor);

    /**
     * Checks whether the given model coordinate already exists in the local working directory. It does not download any
     * resources from the remote repository. It only checks the local file system
     */
    boolean isCached(Coordinate model);

    /**
     * Deletes the artifact represented by the given coordinate from the local file system.
     */
    void delete(Coordinate model, IProgressMonitor monitor);

}