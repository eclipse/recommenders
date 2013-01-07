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

import java.io.File;

import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.models.mapping.IElementInfo;
import org.eclipse.recommenders.models.mapping.IMappingStrategy;
import org.eclipse.recommenders.models.mapping.ElementType;
import org.eclipse.recommenders.models.mapping.impl.ElementInfo;
import org.eclipse.recommenders.models.mapping.impl.MavenPomStrategy;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Optional;

/**
 * Test scenarios based on {@link PomExtractionTest}
 */
public class MavenPomStrategyTest {

    @Test
    public void notSupportedTyp() {
        IElementInfo info = new ElementInfo(new File(""), ElementType.PROJECT);

        IMappingStrategy mavenPomStrategy = new MavenPomStrategy();
        Optional<ProjectCoordinate> extractProjectCoordinate = mavenPomStrategy.extractProjectCoordinate(info);

        Assert.assertTrue(!extractProjectCoordinate.isPresent());
    }

    @Test
    public void supportedTypButNoFile() {
        IElementInfo info = new ElementInfo(new File(""), ElementType.JAR);

        IMappingStrategy mavenPomStrategy = new MavenPomStrategy();
        Optional<ProjectCoordinate> extractProjectCoordinate = mavenPomStrategy.extractProjectCoordinate(info);

        Assert.assertTrue(!extractProjectCoordinate.isPresent());
    }

    @Test
    public void supportedTypButWrongFile() {
        IElementInfo info = new ElementInfo(new File("pom.test"), ElementType.JAR);

        IMappingStrategy mavenPomStrategy = new MavenPomStrategy();
        Optional<ProjectCoordinate> extractProjectCoordinate = mavenPomStrategy.extractProjectCoordinate(info);

        Assert.assertTrue(!extractProjectCoordinate.isPresent());
    }

    @Test
    public void testPom() {

    }

}
