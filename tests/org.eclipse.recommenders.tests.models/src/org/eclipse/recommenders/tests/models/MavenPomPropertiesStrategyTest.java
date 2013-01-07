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
import java.util.Properties;

import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.models.mapping.DependencyType;
import org.eclipse.recommenders.models.mapping.IDependencyInfo;
import org.eclipse.recommenders.models.mapping.IMappingStrategy;
import org.eclipse.recommenders.models.mapping.impl.DependencyInfo;
import org.eclipse.recommenders.models.mapping.impl.MavenPomPropertiesStrategy;
import org.eclipse.recommenders.models.mapping.impl.MavenPomPropertiesStrategy.IFileToJarFileConverter;
import org.junit.Test;

import com.google.common.base.Optional;

public class MavenPomPropertiesStrategyTest {

    private static final ProjectCoordinate expectedProjectCoordinate = new ProjectCoordinate("org.eclipse.group",
            "org.eclipse.artifact", "1.2.3");

    private static Properties createProperties(final String groupId, final String artifactId, final String version) {
        Properties properties = new Properties();
        properties.put(MavenPomPropertiesStrategy.PROPERTY_KEY_GROUP_ID, groupId);
        properties.put(MavenPomPropertiesStrategy.PROPERTY_KEY_ARTIFACT_ID, artifactId);
        properties.put(MavenPomPropertiesStrategy.PROPERTY_KEY_VERSION, version);
        return properties;
    }

    @Test
    public void testValidPomProperties() {
        final String propertiesFileName = "test.jar/META-INF/maven/org.eclipse.group/org.eclipse.artifact/pom.properties";

        IFileToJarFileConverter fileToJarFileConverter = (new IFileToJarFileConverterMockBuilder()).put(
                propertiesFileName, createProperties("org.eclipse.group", "org.eclipse.artifact", "1.2.3")).build();

        IDependencyInfo info = new DependencyInfo(new File("test.jar"), DependencyType.JAR);

        IMappingStrategy sut = new MavenPomPropertiesStrategy(fileToJarFileConverter);
        Optional<ProjectCoordinate> optionalProjectCoordinate = sut.extractProjectCoordinate(info);

        assertEquals(expectedProjectCoordinate, optionalProjectCoordinate.get());
    }

    @Test
    public void testValidPomPropertiesInWrongDirectoryStructure() {
        final String propertiesFileName = "test.jar/pom.properties";

        IFileToJarFileConverter fileToJarFileConverter = (new IFileToJarFileConverterMockBuilder()).put(
                propertiesFileName, createProperties("org.eclipse.group", "org.eclipse.artifact", "1.2.3")).build();

        IDependencyInfo info = new DependencyInfo(new File("test.jar"), DependencyType.JAR);

        IMappingStrategy sut = new MavenPomPropertiesStrategy(fileToJarFileConverter);
        Optional<ProjectCoordinate> optionalProjectCoordinate = sut.extractProjectCoordinate(info);

        assertFalse(optionalProjectCoordinate.isPresent());
    }

    @Test
    public void testValidPomPropertiesNotLocatedInMetaInfDirectory() {
        final String propertiesFileName = "test.jar/maven/org.eclipse.group/org.eclipse.artifact/pom.properties";

        IFileToJarFileConverter fileToJarFileConverter = (new IFileToJarFileConverterMockBuilder()).put(
                propertiesFileName, createProperties("org.eclipse.group", "org.eclipse.artifact", "1.2.3")).build();

        IDependencyInfo info = new DependencyInfo(new File("test.jar"), DependencyType.JAR);

        IMappingStrategy sut = new MavenPomPropertiesStrategy(fileToJarFileConverter);
        Optional<ProjectCoordinate> optionalProjectCoordinate = sut.extractProjectCoordinate(info);

        assertFalse(optionalProjectCoordinate.isPresent());
    }

    @Test
    public void testValidPomPropertiesInWrongDirectory() {
        final String propertiesFileName = "test.jar/invalid/META-INF/maven/org.eclipse.group/org.eclipse.artifact/pom.properties";

        IFileToJarFileConverter fileToJarFileConverter = (new IFileToJarFileConverterMockBuilder()).put(
                propertiesFileName, createProperties("org.eclipse.group", "org.eclipse.artifact", "1.2.3")).build();

        IDependencyInfo info = new DependencyInfo(new File("test.jar"), DependencyType.JAR);

        IMappingStrategy sut = new MavenPomPropertiesStrategy(fileToJarFileConverter);
        Optional<ProjectCoordinate> optionalProjectCoordinate = sut.extractProjectCoordinate(info);

        assertFalse(optionalProjectCoordinate.isPresent());
    }

    @Test
    public void testValidPomPropertiesMavenDirectoryMissing() {
        final String propertiesFileName = "test.jar/invalid/META-INF/org.eclipse.group/org.eclipse.artifact/pom.properties";

        IFileToJarFileConverter fileToJarFileConverter = (new IFileToJarFileConverterMockBuilder()).put(
                propertiesFileName, createProperties("org.eclipse.group", "org.eclipse.artifact", "1.2.3")).build();

        IDependencyInfo info = new DependencyInfo(new File("test.jar"), DependencyType.JAR);

        IMappingStrategy sut = new MavenPomPropertiesStrategy(fileToJarFileConverter);
        Optional<ProjectCoordinate> optionalProjectCoordinate = sut.extractProjectCoordinate(info);

        assertFalse(optionalProjectCoordinate.isPresent());
    }

    @Test
    public void testInvalidFileNameEnding() {
        final String propertiesFileName = "test.jar/META-INF/maven/org.eclipse.group/org.eclipse.artifact/pom.invalid";

        IFileToJarFileConverter fileToJarFileConverter = (new IFileToJarFileConverterMockBuilder()).put(
                propertiesFileName, createProperties("org.eclipse.group", "org.eclipse.artifact", "1.2.3")).build();

        IDependencyInfo info = new DependencyInfo(new File("test.jar"), DependencyType.JAR);

        IMappingStrategy sut = new MavenPomPropertiesStrategy(fileToJarFileConverter);
        Optional<ProjectCoordinate> optionalProjectCoordinate = sut.extractProjectCoordinate(info);

        assertFalse(optionalProjectCoordinate.isPresent());
    }

    @Test
    public void testInvalidArtifactIdInPomPropertiesFileName() {

        final String propertiesFileName = "test.jar/META-INF/maven/org.eclipse.group/invalid/pom.properties";

        IFileToJarFileConverter fileToJarFileConverter = (new IFileToJarFileConverterMockBuilder()).put(
                propertiesFileName, createProperties("org.eclipse.group", "org.eclipse.artifact", "1.2.3")).build();

        IDependencyInfo info = new DependencyInfo(new File("test.jar"), DependencyType.JAR);

        IMappingStrategy sut = new MavenPomPropertiesStrategy(fileToJarFileConverter);
        Optional<ProjectCoordinate> optionalProjectCoordinate = sut.extractProjectCoordinate(info);

        assertFalse(optionalProjectCoordinate.isPresent());
    }

    @Test
    public void testInvalidArtifactIdAndGroudIdInFileName() {
        final String propertiesFileName = "test.jar/META-INF/maven/invalid/org.eclipse.artifact/pom.properties";

        IFileToJarFileConverter fileToJarFileConverter = (new IFileToJarFileConverterMockBuilder()).put(
                propertiesFileName, createProperties("org.eclipse.group", "org.eclipse.artifact", "1.2.3")).build();

        IDependencyInfo info = new DependencyInfo(new File("test.jar"), DependencyType.JAR);

        IMappingStrategy sut = new MavenPomPropertiesStrategy(fileToJarFileConverter);
        Optional<ProjectCoordinate> optionalProjectCoordinate = sut.extractProjectCoordinate(info);

        assertFalse(optionalProjectCoordinate.isPresent());
    }

    @Test
    public void testNotApplicableTyp() {
        final String propertiesFileName = "test.jar/META-INF/maven/org.eclipse.group/org.eclipse.artifact/pom.properties";

        IFileToJarFileConverter fileToJarFileConverter = (new IFileToJarFileConverterMockBuilder()).put(
                propertiesFileName, createProperties("org.eclipse.group", "org.eclipse.artifact", "1.2.3")).build();

        IDependencyInfo info = new DependencyInfo(new File("test.jar"), DependencyType.PROJECT);

        IMappingStrategy sut = new MavenPomPropertiesStrategy(fileToJarFileConverter);
        Optional<ProjectCoordinate> optionalProjectCoordinate = sut.extractProjectCoordinate(info);

        assertFalse(optionalProjectCoordinate.isPresent());
    }

    @Test
    public void testApplicabaleTypButNoFile() {
        IFileToJarFileConverter fileToJarFileConverter = (new IFileToJarFileConverterMockBuilder()).build();

        IDependencyInfo info = new DependencyInfo(new File(""), DependencyType.JAR);

        IMappingStrategy sut = new MavenPomPropertiesStrategy(fileToJarFileConverter);
        Optional<ProjectCoordinate> optionalProjectCoordinate = sut.extractProjectCoordinate(info);

        assertFalse(optionalProjectCoordinate.isPresent());
    }

    @Test
    public void testJarContainMoreOneCorrectAndOneWrongPomProperties() {
        final String propertiesFileName1 = "test.jar/META-INF/maven/org.eclipse.group/org.eclipse.artifact/pom.properties";
        final String propertiesFileName2 = "test.jar/META-INF/maven/org.eclipse.group/org.eclipse.artifact/invalid/pom.properties";

        IFileToJarFileConverter fileToJarFileConverter = (new IFileToJarFileConverterMockBuilder())
                .put(propertiesFileName1, createProperties("org.eclipse.group", "org.eclipse.artifact", "1.2.3"))
                .put(propertiesFileName2, createProperties("org.eclipse.group", "org.eclipse.artifact", "1.0.0"))
                .build();

        IDependencyInfo info = new DependencyInfo(new File("test.jar"), DependencyType.JAR);

        IMappingStrategy sut = new MavenPomPropertiesStrategy(fileToJarFileConverter);
        Optional<ProjectCoordinate> optionalProjectCoordinate = sut.extractProjectCoordinate(info);

        assertEquals(expectedProjectCoordinate, optionalProjectCoordinate.get());
    }

    @Test
    public void testJarContainMoreOneCorrectAndOneWrongPomProperties2() {
        final String propertiesFileName1 = "test.jar/META-INF/maven/org.eclipse.group/org.eclipse.artifact/pom.properties";
        final String propertiesFileName2 = "test.jar/META-INF/maven/org.eclipse.group/invalid/pom.properties";

        IFileToJarFileConverter fileToJarFileConverter = (new IFileToJarFileConverterMockBuilder())
                .put(propertiesFileName2, createProperties("org.eclipse.group", "org.eclipse.artifact", "1.0.0"))
                .put(propertiesFileName1, createProperties("org.eclipse.group", "org.eclipse.artifact", "1.2.3"))
                .build();

        IDependencyInfo info = new DependencyInfo(new File("test.jar"), DependencyType.JAR);

        IMappingStrategy sut = new MavenPomPropertiesStrategy(fileToJarFileConverter);
        Optional<ProjectCoordinate> optionalProjectCoordinate = sut.extractProjectCoordinate(info);

        assertEquals(expectedProjectCoordinate, optionalProjectCoordinate.get());
    }

}
