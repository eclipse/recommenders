/**
 * Copyright (c) 2011 Stefan Henss.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Henss - initial API and implementation.
 */
package org.eclipse.recommenders.apidocs;

import static com.google.common.base.Optional.of;
import static org.eclipse.recommenders.utils.Constants.*;

import java.io.InputStream;
import java.util.Map;

import org.eclipse.recommenders.models.IInputStreamTransformer;
import org.eclipse.recommenders.models.IModelIndex;
import org.eclipse.recommenders.models.IModelRepository;
import org.eclipse.recommenders.models.PoolingModelProvider;
import org.eclipse.recommenders.models.UniqueMethodName;
import org.eclipse.recommenders.utils.IOUtils;
import org.eclipse.recommenders.utils.Zips;
import org.eclipse.recommenders.utils.gson.GsonUtil;

import com.google.common.base.Optional;

public class MethodSelfCallsDirectivesModelProvider extends
        PoolingModelProvider<UniqueMethodName, MethodSelfcallDirectives> {

    public MethodSelfCallsDirectivesModelProvider(IModelRepository repository, IModelIndex index,
            Map<String, IInputStreamTransformer> transformers) {
        super(repository, index, CLASS_SELFM_MODEL, transformers);
    }

    @Override
    protected Optional<MethodSelfcallDirectives> loadModel(InputStream is, UniqueMethodName key) throws Exception {
        MethodSelfcallDirectives res = GsonUtil.deserialize(is, MethodSelfcallDirectives.class);
        IOUtils.closeQuietly(is);
        return of(res);
    }

    @Override
    protected String getPath(UniqueMethodName key) {
        return Zips.path(key.getName(), DOT_JSON);
    }
}
