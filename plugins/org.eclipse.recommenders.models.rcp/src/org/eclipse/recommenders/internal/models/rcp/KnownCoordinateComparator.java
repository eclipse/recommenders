package org.eclipse.recommenders.internal.models.rcp;

import java.util.Comparator;

import org.eclipse.recommenders.internal.models.rcp.ModelRepositoriesView.KnownCoordinate;

public class KnownCoordinateComparator implements Comparator<KnownCoordinate> {
    @Override
    public int compare(KnownCoordinate pc1, KnownCoordinate pc2) {
        int cmpVal = pc1.pc.getGroupId().compareToIgnoreCase(pc2.pc.getGroupId());
        if (cmpVal != 0) {
            return cmpVal;
        } else {
            cmpVal = pc1.pc.getArtifactId().compareToIgnoreCase(pc2.pc.getArtifactId());
            if (cmpVal != 0) {
                return cmpVal;
            } else {
                return pc1.pc.getVersion().compareToIgnoreCase(pc2.pc.getVersion());

            }

        }
    }
}