package org.eclipse.recommenders.internal.models.rcp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.collection.IsIterableContainingInOrder;

import org.eclipse.recommenders.models.ProjectCoordinate;
import org.junit.Assert;
import org.junit.Test;

public class ProjectCoordinateComparatorTest {
    // for groupid test
    ProjectCoordinate pc1 = new ProjectCoordinate("org.eclipse.zest", "org.eclipse.zest.core", "1.0.90");
    ProjectCoordinate pc2 = new ProjectCoordinate("org.eclipse", "osgi", "4.90.0");
    ProjectCoordinate pc3 = new ProjectCoordinate("org.ECLIPS", "OSGI", "7.0.0");

    // for artifactid test
    ProjectCoordinate pc4 = new ProjectCoordinate("org.eclipse", "org.eclipse.zest.core", "4.90.0");
    ProjectCoordinate pc5 = new ProjectCoordinate("org.eclipse", "osgi", "4.90.0");
    ProjectCoordinate pc6 = new ProjectCoordinate("org.eclipse", "osg", "4.90.0");

    // for version test
    ProjectCoordinate pc7 = new ProjectCoordinate("org.eclipse", "osgi", "4.10.9");
    ProjectCoordinate pc8 = new ProjectCoordinate("org.eclipse", "osgi", "4.1.9");
    ProjectCoordinate pc9 = new ProjectCoordinate("org.eclipse", "osgi", "2.1.8");

    List<ProjectCoordinate> Groupid = new ArrayList<ProjectCoordinate>(Arrays.asList(pc1, pc2, pc3));
    List<ProjectCoordinate> GroupidSorted = new ArrayList<ProjectCoordinate>(Arrays.asList(pc3, pc2, pc1));

    List<ProjectCoordinate> artifactId = new ArrayList<ProjectCoordinate>(Arrays.asList(pc4, pc5, pc6));
    List<ProjectCoordinate> artifactIdSorted = new ArrayList<ProjectCoordinate>(Arrays.asList(pc4, pc6, pc5));

    List<ProjectCoordinate> version = new ArrayList<ProjectCoordinate>(Arrays.asList(pc7, pc8, pc9));
    List<ProjectCoordinate> versionSorted = new ArrayList<ProjectCoordinate>(Arrays.asList(pc9, pc8, pc7));

    @Test
    public void groupidTest() {

        Collections.sort(Groupid, new ProjectCoordinateComparator());
        Assert.assertThat(GroupidSorted, IsIterableContainingInOrder.contains(Groupid.toArray()));
    }

    @Test
    public void artifactidTest() {

        Collections.sort(artifactId, new ProjectCoordinateComparator());
        Assert.assertThat(artifactIdSorted, IsIterableContainingInOrder.contains(artifactId.toArray()));
    }

    @Test
    public void versionTest() {

        Collections.sort(version, new ProjectCoordinateComparator());
        Assert.assertThat(versionSorted, IsIterableContainingInOrder.contains(version.toArray()));
    }
}