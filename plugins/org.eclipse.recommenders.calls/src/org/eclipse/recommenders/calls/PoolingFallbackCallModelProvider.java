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
package org.eclipse.recommenders.calls;

import static org.eclipse.recommenders.utils.Constants.CLASS_CALL2_MODELS;
import static org.eclipse.recommenders.utils.Constants.CLASS_CALL_MODELS;

import java.util.Arrays;
import java.util.zip.ZipFile;

import org.eclipse.recommenders.models.IModelArchiveCoordinateAdvisor;
import org.eclipse.recommenders.models.IModelRepository;
import org.eclipse.recommenders.models.PoolingFallbackModelProvider;
import org.eclipse.recommenders.models.UniqueTypeName;

import com.google.common.base.Optional;

public class PoolingFallbackCallModelProvider extends PoolingFallbackModelProvider<UniqueTypeName, ICallModel>
        implements
        ICallModelProvider {

    public PoolingFallbackCallModelProvider(IModelRepository repo, IModelArchiveCoordinateAdvisor index) {
        super(repo, index, Arrays.asList(CLASS_CALL2_MODELS, CLASS_CALL_MODELS));
    }

    @Override
    protected Optional<ICallModel> loadModel(ZipFile zip, UniqueTypeName key) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void passivateModel(ICallModel model) {
        model.reset();
    }

    @Override
    protected Optional<ICallModel> loadModel(ZipFile zip, UniqueTypeName key, String classifier) throws Exception {
        if (classifier.equals(CLASS_CALL2_MODELS)) {
            return JayesCallModel.loadFromJBif(zip, key.getName());
        } else if (classifier.equals(CLASS_CALL_MODELS)) {
            return JayesCallModel.load(zip, key.getName());
        }
        return Optional.absent();
    }
}
