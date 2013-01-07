/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Olav Lenz - initial API and implementation
 */
package org.eclipse.recommenders.tests.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.util.Map;

import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.models.mapping.DependencyType;
import org.eclipse.recommenders.models.mapping.IDependencyInfo;
import org.eclipse.recommenders.models.mapping.IMappingStrategy;
import org.eclipse.recommenders.models.mapping.impl.DependencyInfo;
import org.eclipse.recommenders.models.mapping.impl.JREIDEVersionStrategy;
import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

public class JREIDEVersionStrategyTest {

    private static final File JAVA_HOME_FOLDER = new File("JAVA_HOME/");
    private static final ProjectCoordinate EXPECTED_PROJECT_COORDINATE = new ProjectCoordinate("jre", "jre", "1.0.0");

    private static Map<String, String> createAttributesMapForVersion(String version) {
        Map<String, String> attributes = Maps.newHashMap();
        attributes.put(DependencyInfo.JRE_VERSION_IDE, version);
        return attributes;
    }

    @Test
    public void testNotSupportedType() {
        IDependencyInfo info = new DependencyInfo(JAVA_HOME_FOLDER, DependencyType.JAR);
        IMappingStrategy sut = new JREIDEVersionStrategy();

        sut.searchForProjectCoordinate(info);
    }

    @Test
    public void testMissingInformation() {
        IDependencyInfo info = new DependencyInfo(JAVA_HOME_FOLDER, DependencyType.JRE);
        IMappingStrategy sut = new JREIDEVersionStrategy();

        Optional<ProjectCoordinate> extractProjectCoordinate = sut.searchForProjectCoordinate(info);

        assertFalse(extractProjectCoordinate.isPresent());
    }

    @Test
    public void testValidJRE() {
        IDependencyInfo info = new DependencyInfo(JAVA_HOME_FOLDER, DependencyType.JRE,
                createAttributesMapForVersion("1.0.0"));
        IMappingStrategy sut = new JREIDEVersionStrategy();

        Optional<ProjectCoordinate> projectCoordinate = sut.searchForProjectCoordinate(info);

        assertEquals(EXPECTED_PROJECT_COORDINATE, projectCoordinate.get());
    }

}
