/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 *    Olav Lenz - Move to new file.
 */
package org.eclipse.recommenders.internal.models.rcp;

import java.util.Set;

import org.eclipse.recommenders.models.IModelIndex;
import org.eclipse.recommenders.models.ModelCoordinate;
import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.utils.Constants;

import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;

class TriggerModelDownloadActionForProjectCoordinates extends TriggerModelDownloadActionForModelCoordinates {

    private final String[] modelTypes = { Constants.CLASS_CALL_MODELS, Constants.CLASS_OVRM_MODEL,
            Constants.CLASS_OVRP_MODEL, Constants.CLASS_OVRD_MODEL, Constants.CLASS_SELFC_MODEL,
            Constants.CLASS_SELFM_MODEL };

    private IModelIndex modelIndex;

    private Set<ProjectCoordinate> pcs = Sets.newHashSet();

    TriggerModelDownloadActionForProjectCoordinates(String text, IModelIndex modelIndex, EclipseModelRepository repo,
            EventBus bus) {
        super(text, repo, bus);
        this.modelIndex = modelIndex;
    }

    TriggerModelDownloadActionForProjectCoordinates(String text, Set<ProjectCoordinate> pcs, IModelIndex modelIndex,
            EclipseModelRepository repo, EventBus bus) {
        this(text, modelIndex, repo, bus);
        this.pcs = pcs;
    }

    @Override
    public void run() {
        triggerDownloadForProjectCoordinates(pcs);
    }

    public void triggerDownloadForProjectCoordinates(Set<ProjectCoordinate> pcs) {
        Set<ModelCoordinate> mcs = Sets.newHashSet();
        for (ProjectCoordinate pc : pcs) {
            for (String modelType : modelTypes) {
                ModelCoordinate mc = modelIndex.suggest(pc, modelType).orNull();
                if (mc != null) {
                    mcs.add(mc);
                }
            }
        }
        triggerDownloadForModelCoordinates(mcs);
    }
}
