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

/**
 * Used by {@link PoolingModelArchive} and {@link CachingModelArchive}. Implementors rather extend
 * {@link ModelFactoryAdapter} then implement this interface from scratch.
 */
public interface IModelFactory<K, M> extends Closeable {

    public void open();

    public boolean validateModel(K key, M model);

    public void passivateModel(K key, M model) throws Exception;

    public boolean hasModel(K key);

    public M createModel(K key) throws Exception;

    public void destroyModel(K key, M obj) throws Exception;

    public void activateModel(K key, M model) throws Exception;

    @Override
    public void close() throws IOException;

}