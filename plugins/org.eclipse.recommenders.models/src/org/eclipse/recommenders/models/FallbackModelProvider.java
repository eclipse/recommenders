/*******************************************************************************
 * Copyright (c) 2013 Michael Kutschke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Michael Kutschke - initial API and implementation
 ******************************************************************************/
package org.eclipse.recommenders.models;

import static com.google.common.base.Optional.absent;

import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * A non-thread-safe implementation of {@link IModelProvider} that loads models from model zip files using a
 * {@link ModelRepository}. Note that {@link #acquireModel(IUniqueName)} attempts to download matching model archives
 * immediately and thus blocks until the download is completed.
 */
public abstract class FallbackModelProvider<K extends IUniqueName<?>, M> extends SimpleModelProvider<K, M> {

    private Logger log = LoggerFactory.getLogger(getClass());

    protected List<String> modelTypes;
    private IModelArchiveCoordinateAdvisor index;

    public FallbackModelProvider(IModelRepository cache, IModelArchiveCoordinateAdvisor index, List<String> modelTypes) {
        super(cache, index, null);
        this.index = index;
        this.modelTypes = modelTypes;
    }

    @Override
    public Optional<M> acquireModel(K key) {
        try {
            ModelCoordinate mc = null;
            for (Iterator<String> it = modelTypes.iterator(); it.hasNext();) {
                mc = index.suggest(key.getProjectCoordinate(), it.next()).orNull();
                if (mc != null) {
                    break;
                }
            }
            // unknown model? return immediately
            if (mc == null) {
                return absent();
            }
            final ZipFile zip;
            try {
                zip = openZips.get(mc);
            } catch (UncheckedExecutionException e) {
                if (IllegalStateException.class.equals(e.getCause().getClass())) {
                    // repository.getLocation(..) returned absent. Try to load ZIP file again next time.
                    return absent();
                } else {
                    throw e;
                }
            }
            return loadModel(zip, key, mc.getClassifier());
        } catch (Exception e) {
            log.error("Exception while loading model " + key, e);
            return absent();
        }
    }

    protected abstract Optional<M> loadModel(ZipFile zip, K key, String classifier) throws Exception;

}
