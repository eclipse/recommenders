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

import java.io.Closeable;
import java.io.IOException;

import org.eclipse.recommenders.models.IModelProvider;
import org.eclipse.recommenders.models.ProjectCoordinate;

import com.google.common.base.Optional;

/**
 * A {@link IModelArchive} is a file containing recommendation models for a given {@link ProjectCoordinate}, typically
 * downloaded by a {@link ModelArchiveCache}. These files are typically in ZIP format but don't have to.
 * <p>
 * This interface assumes that models can be <em>acquired</em> from a {@link IModelArchive} and should be
 * <em>released</em> after they've been used by the client. This allows implementations to leverage pooling or caching
 * strategies for models to improve performance.
 * <p>
 * Note that typically only one {@link IModelArchive} exists per coordinate but several instances of the same model may
 * exist (e.g., when using object pools to improve performance).
 */
public interface IModelArchive<K, M> extends Closeable {

    /**
     * Informs the caller whether this archive contains a model for the given key. This method does not load the model.
     */
    boolean hasModel(final K key);

    /**
     * Returns a model for the given key. The model may be a cached instance. If no model was loaded yet - or no free
     * instance is available in the pool - the archive may create a fresh instance.
     * <p>
     * Note that an implementation may decide to only keep a limited number of models per key in memory and may return
     * {@link Optional#absent()} if the pool is exhausted.
     * 
     * @see #releaseModel(Object)
     */
    Optional<M> acquireModel(final K key);

    /**
     * Puts a model back to the archive. The model is not allowed to be used by the caller anymore after returning it,
     * i.e., if a caller needs the model again later it needs to be acquired again.
     * 
     * @see #acquireModel(Object)
     */
    void releaseModel(final M value);

    /**
     * Callback that allows the model archive implementation to open resources/stream, build internal indexes and the
     * like. These resources should be release when {@link #close()} is called.
     * 
     * @see #close()
     */
    void open();

    /**
     * Closes the this archive. This method is expected to be called by {@link IModelProvider} when they are closed.
     */
    @Override
    public void close() throws IOException;
}
