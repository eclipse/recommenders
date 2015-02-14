package org.eclipse.recommenders.internal.models.rcp;

import java.util.Comparator;

import org.eclipse.recommenders.models.ProjectCoordinate;

public class ProjectCoordinateComparator implements Comparator<ProjectCoordinate> {
    @Override
    public int compare(ProjectCoordinate pc1, ProjectCoordinate pc2) {
        int cmpGroupId = pc1.getGroupId().compareToIgnoreCase(pc2.getGroupId());
        int cmpArtId = pc1.getArtifactId().compareToIgnoreCase(pc2.getArtifactId());
        int cmpVersion = versionComparison(pc1.getVersion(), pc2.getVersion());
        int cmpFinal = cmpGroupId != 0 ? cmpGroupId : cmpArtId != 0 ? cmpArtId : cmpVersion;
        return cmpFinal;
    }

    public static int versionComparison(String str1, String str2) {
        int val = 0;
        String[] version1 = str1.split("\\.");
        String[] version2 = str2.split("\\.");

        for (int i = 0; i < version1.length; i++) {
            if (version1[i].compareTo(version2[i]) < 0) {
                val = -1;
                return val;
            } else if (version1[i].compareTo(version2[i]) > 0) {
                val = 1;
                return val;
            }
        }
        return val;
    }
}
