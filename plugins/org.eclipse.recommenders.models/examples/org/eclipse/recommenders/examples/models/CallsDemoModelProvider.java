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
package org.eclipse.recommenders.examples.models;

import java.io.InputStream;
import java.util.Map;

import org.eclipse.recommenders.models.IInputStreamTransformer;
import org.eclipse.recommenders.models.IModelArchiveCoordinateAdvisor;
import org.eclipse.recommenders.models.IModelRepository;
import org.eclipse.recommenders.models.IUniqueName;
import org.eclipse.recommenders.models.PoolingModelProvider;
import org.eclipse.recommenders.utils.Zips;
import org.eclipse.recommenders.utils.names.ITypeName;

import com.google.common.base.Optional;

public class CallsDemoModelProvider extends PoolingModelProvider<IUniqueName<ITypeName>, Object> {

    public CallsDemoModelProvider(IModelRepository repo, IModelArchiveCoordinateAdvisor index,
            Map<String, IInputStreamTransformer> transformers) {
        super(repo, index, "call", transformers);
    }

    @Override
    protected Optional<Object> loadModel(InputStream stream, IUniqueName<ITypeName> key) throws Exception {
        Object model = null; // ... do things with s to create a model
        return Optional.of(model);
    }

    @Override
    protected String getPath(IUniqueName<ITypeName> key) {
        return Zips.path(key.getName(), ".net");
    }
}
