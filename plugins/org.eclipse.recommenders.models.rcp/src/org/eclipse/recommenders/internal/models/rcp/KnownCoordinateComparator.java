/**
 * Copyright (c) 2010, 2013,2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *         Varun Gupta : comparator for sorting the KnownCoordinates
 */
package org.eclipse.recommenders.internal.models.rcp;

import java.util.Comparator;

import org.eclipse.recommenders.internal.models.rcp.ModelRepositoriesView.KnownCoordinate;

public class KnownCoordinateComparator implements Comparator<KnownCoordinate> {
    @Override
    public int compare(KnownCoordinate pc1, KnownCoordinate pc2) {
        int cmpGroupId = pc1.pc.getGroupId().compareToIgnoreCase(pc2.pc.getGroupId());
        int cmpArtId = pc1.pc.getArtifactId().compareToIgnoreCase(pc2.pc.getArtifactId());
        int cmpVersion = pc1.pc.getVersion().compareToIgnoreCase(pc2.pc.getVersion());
        int cmpFinal = cmpGroupId != 0 ? cmpGroupId : cmpArtId != 0 ? cmpArtId : cmpVersion;
        return cmpFinal;
    }
}
