package org.eclipse.recommenders.internal.models.rcp;

import java.util.Comparator;
import java.util.StringTokenizer;

import org.eclipse.recommenders.coordinates.ProjectCoordinate;
import org.eclipse.recommenders.internal.models.rcp.ModelRepositoriesView.KnownCoordinate;

public class KnownCoordinateComparator implements Comparator<KnownCoordinate> {

    @Override
    public int compare(KnownCoordinate kc1, KnownCoordinate kc2) {
        ProjectCoordinate pc1 = kc1.pc, pc2 = kc2.pc;

        int value = comparison(pc1.getGroupId(), pc2.getGroupId());
        if (value == 0) {
            value = comparison(pc1.getArtifactId(), pc2.getArtifactId());
            if (value == 0) {
                return checkForVersions(pc1.getVersion(), pc2.getVersion());
            }
        }
        return value;
    }

    public int comparison(String str1, String str2) {
        return str1.compareToIgnoreCase(str2);
    }

    public int checkForVersions(String ver1, String ver2) {
        StringTokenizer st1, st2;
        st1 = new StringTokenizer(ver1, ".");
        st2 = new StringTokenizer(ver2, ".");
        int x, y;
        while (st1.hasMoreTokens()) {
            x = Integer.parseInt(st1.nextToken());
            y = Integer.parseInt(st2.nextToken());
            if (x != y) {
                return x - y;
            }
        }
        return 0;
    }
}