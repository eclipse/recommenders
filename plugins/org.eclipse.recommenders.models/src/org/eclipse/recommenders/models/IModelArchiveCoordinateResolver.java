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

import java.io.Closeable;
import java.io.IOException;

import org.eclipse.recommenders.utils.Openable;

import com.google.common.base.Optional;

/**
 * A model index provides a view on the content of a model repository. It provides utility functions to search for
 * available models, the best matching model for a given project coordinate etc.
 */
public interface IModelArchiveCoordinateResolver extends Openable, Closeable {

    /**
     * Returns a list of all model coordinate that match the given group-id and artifact-id.
     */
    ModelArchiveCoordinate[] suggestCandidates(ProjectCoordinate coord, String modelType);

    /**
     * Returns the best matching model the index could find for the given project coordinate.
     */
    Optional<ModelArchiveCoordinate> suggest(ProjectCoordinate coord, String modelType);

    /**
     * Although part of the API, this method is not expected to be called by normal clients but form the instance that
     * created and manages this index (usually the DI framework)
     */
    @Override
    void open() throws IOException;

    /**
     * Although part of the API, this method is not expected to be called by normal clients but form the instance that
     * created and manages this index (usually the DI framework)
     */
    @Override
    void close() throws IOException;

}
