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

import java.io.Closeable;

import org.eclipse.recommenders.models.Gav;

import com.google.common.base.Optional;

/**
 * A {@link IModelArchive} is a zip-file containing recommendation models for a given {@link Gav}, typically downloaded
 * by a {@link ModelCache}.
 * <p>
 * The interface assumes that models can be <em>acquired</em> from a model archive and should be <em>released</em> after
 * they've been used by the client. This allows implementations to leverage pooling or caching strategies for models to
 * improve performance.
 * 
 * <p>
 * Note that typically only one {@link IModelArchive} exists per coordinate but several instances of the same model may
 * exist (e.g., when using object pools to improve performance).
 */
public interface IModelArchive<K, M> extends Closeable {

    boolean hasModel(final K key);

    Optional<M> acquireModel(final K key);

    void releaseModel(final M value);

    void open();
}