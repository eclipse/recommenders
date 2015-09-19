package org.eclipse.recommenders.internal.models.rcp;

import static org.eclipse.recommenders.utils.Constants.EXT_ZIP;
import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.recommenders.coordinates.ProjectCoordinate;
import org.eclipse.recommenders.models.ModelCoordinate;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class KnownCoordinateComparatorTest {

    public static final ProjectCoordinate PC1 = new ProjectCoordinate("org.exam", "one", "1.0.0");
    public static final ProjectCoordinate PC2 = new ProjectCoordinate("org.example", "two", "2.0.0");
    public static final ProjectCoordinate PC3 = new ProjectCoordinate("org.example", "three", "3.0.0");
    public static final ProjectCoordinate PC4 = new ProjectCoordinate("org.example", "three", "4.0.0");

    public static final ModelCoordinate MC1 = new ModelCoordinate("org.example", "one", "call", EXT_ZIP, "1.0.0");
    public static final ModelCoordinate MC2 = new ModelCoordinate("org.example", "two", "call", EXT_ZIP, "2.0.0");
    public static final ModelCoordinate MC3 = new ModelCoordinate("org.example", "three", "call", EXT_ZIP, "3.0.0");
    public static final ModelCoordinate MC4 = new ModelCoordinate("org.example", "four", "call", EXT_ZIP, "4.0.0");
    public static final ImmutableSet<String> modelClassiffiers = ImmutableSet.of("org.example");
    public static ModelRepositoriesView MRC = new ModelRepositoriesView(null, null, null, null, modelClassiffiers,
            null);

    @Test

    public void testForGroupId() {

        ModelRepositoriesView.KnownCoordinate kc1 = MRC.new KnownCoordinate("", PC1, ImmutableList.of(MC1, MC2));
        ModelRepositoriesView.KnownCoordinate kc2 = MRC.new KnownCoordinate("", PC2, ImmutableList.of(MC1, MC2));
        ModelRepositoriesView.KnownCoordinate coordinates[] = { kc2, kc1 };
        ModelRepositoriesView.KnownCoordinate sortedCoordinates[] = { kc1, kc2 };
        List<ModelRepositoriesView.KnownCoordinate> coordinate = Arrays.asList(coordinates);
        Collections.sort(coordinate, new KnownCoordinateComparator());
        assertArrayEquals(sortedCoordinates, coordinate.toArray());
    }

    public void testForArtifactId() {
        ModelRepositoriesView.KnownCoordinate kc1 = MRC.new KnownCoordinate("", PC2, ImmutableList.of(MC1, MC2));
        ModelRepositoriesView.KnownCoordinate kc2 = MRC.new KnownCoordinate("", PC3, ImmutableList.of(MC1, MC2));
        ModelRepositoriesView.KnownCoordinate coordinates[] = { kc1, kc2 };
        ModelRepositoriesView.KnownCoordinate sortedCoordinates[] = { kc2, kc1 };
        List<ModelRepositoriesView.KnownCoordinate> coordinate = Arrays.asList(coordinates);
        Collections.sort(coordinate, new KnownCoordinateComparator());
        assertArrayEquals(sortedCoordinates, coordinate.toArray());
    }

    public void testForVersion() {
        ModelRepositoriesView.KnownCoordinate kc1 = MRC.new KnownCoordinate("", PC3, ImmutableList.of(MC1, MC2));
        ModelRepositoriesView.KnownCoordinate kc2 = MRC.new KnownCoordinate("", PC4, ImmutableList.of(MC1, MC2));
        ModelRepositoriesView.KnownCoordinate coordinates[] = { kc1, kc2 };
        ModelRepositoriesView.KnownCoordinate sortedCoordinates[] = { kc2, kc1 };
        List<ModelRepositoriesView.KnownCoordinate> coordinate = Arrays.asList(coordinates);
        Collections.sort(coordinate, new KnownCoordinateComparator());
        assertArrayEquals(sortedCoordinates, coordinate.toArray());
    }

}
