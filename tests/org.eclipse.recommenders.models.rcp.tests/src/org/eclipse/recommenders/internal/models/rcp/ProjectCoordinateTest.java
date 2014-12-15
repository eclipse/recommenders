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

import java.util.Collections;
import java.util.List;

import org.eclipse.recommenders.models.ProjectCoordinate;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

public class ProjectCoordinateTest {

    @Test
    public void test() {
        ProjectCoordinate pc1 = new ProjectCoordinate("org.eclipse.zest", "org.eclipse.zest.core", "1.0.0");
        ProjectCoordinate pc2 = new ProjectCoordinate("org.eclipse", "osgi", "4.0.0");
        ProjectCoordinate pc3 = new ProjectCoordinate("org.ECLIPSE", "OSGI", "7.0.0");
        List<ProjectCoordinate> proCoordinates = Lists.newArrayList();
        List<ProjectCoordinate> proCoordinatesSorted = Lists.newArrayList();
        ProjectCoordinateComparator pcc = new ProjectCoordinateComparator();
        proCoordinates.add(pc1);
        proCoordinates.add(pc2);
        proCoordinates.add(pc3);
        Collections.sort(proCoordinates, pcc);
        proCoordinatesSorted.add(pc2);
        proCoordinatesSorted.add(pc3);
        proCoordinatesSorted.add(pc1);
        Assert.assertThat(proCoordinatesSorted, IsIterableContainingInOrder.contains(proCoordinates.toArray()));

    }
}
