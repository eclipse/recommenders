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
package org.eclipse.recommenders.models.repo;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.recommenders.models.Gav;

import com.google.common.base.Optional;

/**
 * A {@link LocalModelRepository} is responsible for downloading and caching (file-based) model artifacts from a remote
 * maven repository. It has a local working directory where it stores model artifacts and some meta-data, and is
 * configured with a remote (maven) repository URL from which it fetches model artifacts on demand.
 */
public abstract class LocalModelRepository {

    /**
     * The coordinate under which the model search index of the remote model repository is addressable.
     */
    public static Coordinate INDEX = new Coordinate("org.eclipse.recommenders", "index", "index", "zip", "0.0.0");

    /**
     * Changes the remote repository used to resolve and download artifacts. This change takes effect immediately.
     */
    public abstract void setRemote(String url);

    /**
     * Resolves the given model coordinate to a local file and downloads the corresponding file from a remote repository
     * if not locally available.
     * 
     * @return the local copy of the model artifact
     */
    public abstract File resolve(Coordinate model, IProgressMonitor monitor);

    /**
     * Deletes the artifact represented by the given coordinate from the local file system.
     */
    public abstract void delete(Coordinate model, IProgressMonitor monitor);

    /**
     * Returns the file for the given coordinate. This operation returns a handle only - this file may or may not exist.
     * Note that this call does <b>not</b> trigger any resolution steps.
     */
    public abstract File get(Coordinate coord);

    /**
     * Checks whether the given model coordinate already exists in the local working directory. It does not download any
     * resources from the remote repository. It only checks the local file system
     */
    public abstract boolean isCached(Coordinate model);

    /**
     * Searches for the best matching model in the model repository.
     */
    public abstract Optional<Coordinate> searchModel(Gav gav, String modeltype);

}