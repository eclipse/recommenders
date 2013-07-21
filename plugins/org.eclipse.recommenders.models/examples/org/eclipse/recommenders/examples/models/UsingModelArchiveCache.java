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

import java.io.IOException;

import org.eclipse.recommenders.models.IModelRepository;
import org.eclipse.recommenders.models.ModelArchiveCoordinate;
import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.utils.Pair;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

@SuppressWarnings("unused")
public class UsingModelArchiveCache {

    void downloadModelArchive(final ModelArchiveCoordinate model, final IModelRepository repository) throws Exception {
        repository.resolve(model);
    }

    void findLocalModelArchive(final ModelArchiveCoordinate model, final IModelRepository repository) throws Exception {
        if (!repository.getLocation(model).isPresent()) {
            repository.resolve(model);
        }
    }

    void deleteCachedModelArchive(final ModelArchiveCoordinate model, final IModelRepository repository)
            throws IOException {
        repository.delete(model);
    }

    void deleteIndex(final IModelRepository repository) throws IOException {
        repository.delete(IModelRepository.INDEX);
    }

    void findAllModelArtifacts(final ProjectCoordinate[] gavs, final IModelRepository cache,
            final IModelArchiveCoordinateProvider[] modelProviders) {

        Table<ProjectCoordinate, String, Pair<ModelArchiveCoordinate, Boolean>> mappings = HashBasedTable.create();
        for (ProjectCoordinate projectCoord : gavs) {
            for (IModelArchiveCoordinateProvider modelProvider : modelProviders) {
                ModelArchiveCoordinate modelCoord = modelProvider.find(projectCoord).orNull();
                if (modelCoord != null) {
                    boolean cached = cache.getLocation(modelCoord).isPresent();
                    mappings.put(projectCoord, modelProvider.getType(), Pair.newPair(modelCoord, cached));
                }
            }
        }
        // update ui...
    }

}
