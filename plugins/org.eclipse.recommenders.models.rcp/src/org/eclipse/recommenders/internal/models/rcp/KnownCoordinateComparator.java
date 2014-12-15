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
