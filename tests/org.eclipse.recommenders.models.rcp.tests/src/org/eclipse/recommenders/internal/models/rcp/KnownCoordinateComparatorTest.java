/**
 * Copyright (c) Varun Gupta.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Varun Gupta - initial API and implementation
 */
package org.eclipse.recommenders.internal.models.rcp;

import static org.eclipse.recommenders.utils.Constants.EXT_ZIP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.recommenders.coordinates.ProjectCoordinate;
import org.eclipse.recommenders.models.ModelCoordinate;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class KnownCoordinateComparatorTest {

    public static final ProjectCoordinate ORG_EXAM_ONE_1_0_0 = new ProjectCoordinate("org.exam", "one", "1.0.0");
    public static final ProjectCoordinate ORG_EXAMPLE_TWO_2_0_0 = new ProjectCoordinate("org.example", "two", "2.0.0");
    public static final ProjectCoordinate ORG_EXAMPLE_THREE_3_0_0 = new ProjectCoordinate("org.example", "three",
            "3.0.0");
    public static final ProjectCoordinate ORG_EXAMPLE_THREE_4_0_0 = new ProjectCoordinate("org.example", "three",
            "4.0.0");
    public static final ModelCoordinate MC1 = new ModelCoordinate("org.example", "one", "call", EXT_ZIP, "1.0.0");
    public static final ModelCoordinate MC2 = new ModelCoordinate("org.example", "two", "call", EXT_ZIP, "2.0.0");
    public static final ModelCoordinate MC3 = new ModelCoordinate("org.example", "three", "call", EXT_ZIP, "3.0.0");
    public static final ModelCoordinate MC4 = new ModelCoordinate("org.example", "four", "call", EXT_ZIP, "4.0.0");
    public static final ImmutableSet<String> MODEL_CLASSIFIERS = ImmutableSet.of("org.example");
    public static ModelRepositoriesView MRC = new ModelRepositoriesView(null, null, null, null, MODEL_CLASSIFIERS,
            null);

    @Test
    public void testForGroupId() throws Exception {
        ModelRepositoriesView.KnownCoordinate kc1 = MRC.new KnownCoordinate("", ORG_EXAM_ONE_1_0_0,
                ImmutableList.of(MC1, MC2));
        ModelRepositoriesView.KnownCoordinate kc2 = MRC.new KnownCoordinate("", ORG_EXAMPLE_TWO_2_0_0,
                ImmutableList.of(MC1, MC2));
        ModelRepositoriesView.KnownCoordinate coordinates[] = { kc2, kc1 };
        List<ModelRepositoriesView.KnownCoordinate> coordinate = Arrays.asList(coordinates);

        Collections.sort(coordinate, new KnownCoordinateComparator());
        assertThat(coordinate, contains(kc1, kc2));
    }

    @Test
    public void testForArtifactId() throws Exception {
        ModelRepositoriesView.KnownCoordinate kc1 = MRC.new KnownCoordinate("", ORG_EXAMPLE_TWO_2_0_0,
                ImmutableList.of(MC1, MC2));
        ModelRepositoriesView.KnownCoordinate kc2 = MRC.new KnownCoordinate("", ORG_EXAMPLE_THREE_3_0_0,
                ImmutableList.of(MC1, MC2));
        ModelRepositoriesView.KnownCoordinate coordinates[] = { kc1, kc2 };
        List<ModelRepositoriesView.KnownCoordinate> coordinate = Arrays.asList(coordinates);

        Collections.sort(coordinate, new KnownCoordinateComparator());

        assertThat(coordinate, contains(kc1, kc2));
    }

    @Test
    public void testForVersion() throws Exception {
        ModelRepositoriesView.KnownCoordinate kc1 = MRC.new KnownCoordinate("", ORG_EXAMPLE_THREE_3_0_0,
                ImmutableList.of(MC1, MC2));
        ModelRepositoriesView.KnownCoordinate kc2 = MRC.new KnownCoordinate("", ORG_EXAMPLE_THREE_4_0_0,
                ImmutableList.of(MC1, MC2));
        ModelRepositoriesView.KnownCoordinate coordinates[] = { kc1, kc2 };
        List<ModelRepositoriesView.KnownCoordinate> coordinate = Arrays.asList(coordinates);

        Collections.sort(coordinate, new KnownCoordinateComparator());

        assertThat(coordinate, contains(kc1, kc2));
    }

}
