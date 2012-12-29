/**
 * Copyright (c) 2010, 2012 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Lerch - initial API and implementation.
 */
package org.eclipse.recommenders.models.archives.impl;

import static com.google.common.base.Optional.absent;

import java.io.IOException;

import org.eclipse.recommenders.models.archives.IModelArchive;

import com.google.common.base.Optional;

/**
 * Primitive implementation of an {@link IModelArchive} that always returns <code>false</code> or
 * {@link Optional#absent()} when asked for a model.
 */
public class NullModelArchive<K, M> implements IModelArchive<K, M> {
    @SuppressWarnings("rawtypes")
    private static final IModelArchive NULL = new NullModelArchive();

    @SuppressWarnings("unchecked")
    public static <K, M> IModelArchive<K, M> empty() {
        return NULL;
    }

    @Override
    public void open() {
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public boolean hasModel(K key) {
        return false;
    }

    @Override
    public Optional<M> acquireModel(K key) {
        return absent();
    }

    @Override
    public void releaseModel(M value) {
    }

}
