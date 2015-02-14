package org.eclipse.recommenders.models;

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
        // max and min considered if there is any one of them missing <major version>.<minor version>.<incremental
        // version>

        int length = Math.min(version1.length, version2.length);
        int maximum = Math.max(version1.length, version2.length);

        for (int i = 0; i < length; i++) {
            if (version1[i].compareTo(version2[i]) < 0) {
                val = -1;
                break;
            } else if (version1[i].compareTo(version2[i]) > 0) {
                val = 1;
                break;
            }
        }
        if (val == 0) {
            if (maximum == version1.length) {
                val = 1;
            } else {
                val = -1;
            }
        }
        return val;
    }
}
