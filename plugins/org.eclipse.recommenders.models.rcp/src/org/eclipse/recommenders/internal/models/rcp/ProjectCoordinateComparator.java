/*******************************************************************************
 * Copyright (c) 2014 Varun Gupta.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Varun Gupta - initial API and implementation
 ******************************************************************************/
package org.eclipse.recommenders.internal.models.rcp;

import java.util.Comparator;

import org.eclipse.recommenders.models.ProjectCoordinate;

public class ProjectCoordinateComparator implements Comparator<ProjectCoordinate> {
    @Override
    public int compare(ProjectCoordinate pc1, ProjectCoordinate pc2) {
        int cmpGroupId = pc1.getGroupId().compareToIgnoreCase(pc2.getGroupId());
        int cmpArtId = pc1.getArtifactId().compareToIgnoreCase(pc2.getArtifactId());
        int cmpVersion = pc1.getVersion().compareToIgnoreCase(pc2.getVersion());
        int cmpFinal = cmpGroupId != 0 ? cmpGroupId : cmpArtId != 0 ? cmpArtId : cmpVersion;
        return cmpFinal;
    }
}
