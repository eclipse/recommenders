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

import java.io.IOException;

public abstract class ModelFactoryAdapter<K, M> implements IModelFactory<K, M> {

    @Override
    public void open() {
    }

    @Override
    public boolean validateModel(K key, M model) {
        return true;
    }

    @Override
    public void passivateModel(K key, M model) {
    }

    @Override
    public void destroyModel(K key, M obj) {
    }

    @Override
    public void activateModel(K key, M model) {
    }

    @Override
    public void close() throws IOException {
    }
}
