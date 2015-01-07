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
package org.eclipse.recommenders.overrides;

import static org.eclipse.recommenders.utils.Constants.DOT_JSON;

import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;

import org.eclipse.recommenders.models.IInputStreamTransformer;
import org.eclipse.recommenders.models.IModelArchiveCoordinateAdvisor;
import org.eclipse.recommenders.models.IModelRepository;
import org.eclipse.recommenders.models.IUniqueName;
import org.eclipse.recommenders.models.PoolingModelProvider;
import org.eclipse.recommenders.utils.Constants;
import org.eclipse.recommenders.utils.Zips;
import org.eclipse.recommenders.utils.names.ITypeName;

import com.google.common.base.Optional;

public class PoolingOverrideModelProvider extends PoolingModelProvider<IUniqueName<ITypeName>, IOverrideModel>
        implements IOverrideModelProvider {

    public PoolingOverrideModelProvider(IModelRepository repository, IModelArchiveCoordinateAdvisor index,
            List<IInputStreamTransformer> transformers) {
        super(repository, index, Constants.CLASS_OVRM_MODEL, transformers);
    }

    @Override
    protected Optional<IOverrideModel> loadModel(InputStream stream, IUniqueName<ITypeName> key) throws Exception {
        return JayesOverrideModel.load(stream, key.getName());
    }

    @Override
    protected void passivateModel(IOverrideModel model) {
        model.reset();
    }

    @Override
    protected ZipEntry getEntry(IUniqueName<ITypeName> key) {
        return new ZipEntry(Zips.path(key.getName(), DOT_JSON));
    }
}
