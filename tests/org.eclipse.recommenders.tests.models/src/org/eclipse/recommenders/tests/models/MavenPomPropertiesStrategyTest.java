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
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.jar.JarFile;

import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.models.mapping.DependencyType;
import org.eclipse.recommenders.models.mapping.IDependencyInfo;
import org.eclipse.recommenders.models.mapping.IMappingStrategy;
import org.eclipse.recommenders.models.mapping.impl.DependencyInfo;
import org.eclipse.recommenders.models.mapping.impl.MavenPomPropertiesStrategy;
import org.eclipse.recommenders.models.mapping.impl.MavenPomPropertiesStrategy.IFileToJarFileConverter;
import org.eclipse.recommenders.tests.JarFileMockBuilder;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Optional;

public class MavenPomPropertiesStrategyTest {

    private static final String VALID_FILENAME_ENDING = "pom.properties";
    private static final String INVALID_FILENAME_ENDING = "test.txt";
    private static final String VALID_FILENANE_BEGINNING = "maven";
    private static final String INVALID_FILENANE_BEGINNING = "invalid";

    private static final String GROUP_ID = "groupID";
    private static final String ARTIFACT_ID = "artifact";
    private static final String VERSION = "1.2.3";

    private static File createMockedFile(String filename) {
        File file = Mockito.mock(File.class);
        Mockito.when(file.getName()).thenReturn(filename);
        return file;
    }

    private static IFileToJarFileConverter createFileToJarFileConverter(final String fileName, final String version,
            final String groupId, final String artifactId) {

        return new IFileToJarFileConverter() {

            @Override
            public Optional<JarFile> createJarFile(File file) {
                if (file.getName().equals(fileName)) {
                    final JarFileMockBuilder builder = new JarFileMockBuilder();
                    builder.addEntry(
                            fileName,
                            new ByteArrayInputStream((MavenPomPropertiesStrategy.PROPERTY_KEY_VERSION + "=" + version
                                    + "\n" + MavenPomPropertiesStrategy.PROPERTY_KEY_GROUP_ID + "=" + groupId + "\n"
                                    + MavenPomPropertiesStrategy.PROPERTY_KEY_ARTIFACT_ID + "=" + artifactId + "")
                                    .getBytes()));
                    return Optional.fromNullable(builder.build());
                }
                return Optional.absent();
            }
        };
    }

    private static String createFileName(String begining, String groupId, String artifactId, String version,
            String fileEnding) {
        return "/" + begining + "/" + groupId + "/" + artifactId + "/" + fileEnding;
    }

    @Test
    public void testValidPomProperties() {
        final String fileName = createFileName(VALID_FILENANE_BEGINNING, GROUP_ID, ARTIFACT_ID, VERSION,
                VALID_FILENAME_ENDING);

        File file = createMockedFile(fileName);
        IFileToJarFileConverter fileToJarFileConverter = createFileToJarFileConverter(fileName, VERSION, GROUP_ID,
                ARTIFACT_ID);

        IDependencyInfo info = new DependencyInfo(file, DependencyType.JAR, null);

        IMappingStrategy mavenPomStrategy = new MavenPomPropertiesStrategy(fileToJarFileConverter);
        Optional<ProjectCoordinate> optionalProjectCoordinate = mavenPomStrategy.extractProjectCoordinate(info);
        if (!optionalProjectCoordinate.isPresent()) {
            fail();
        }
        ProjectCoordinate actual = optionalProjectCoordinate.get();

        ProjectCoordinate expected = new ProjectCoordinate(GROUP_ID, ARTIFACT_ID, VERSION);

        assertEquals(expected, actual);
    }

    @Test
    public void testInvalidFileNameEnding() {
        final String fileName = createFileName(VALID_FILENANE_BEGINNING, GROUP_ID, ARTIFACT_ID, VERSION,
                INVALID_FILENAME_ENDING);

        File file = createMockedFile(fileName);
        IFileToJarFileConverter fileToJarFileConverter = createFileToJarFileConverter(fileName, VERSION, GROUP_ID,
                ARTIFACT_ID);

        IDependencyInfo info = new DependencyInfo(file, DependencyType.JAR, null);

        IMappingStrategy mavenPomStrategy = new MavenPomPropertiesStrategy(fileToJarFileConverter);
        Optional<ProjectCoordinate> optionalProjectCoordinate = mavenPomStrategy.extractProjectCoordinate(info);

        assertFalse(optionalProjectCoordinate.isPresent());
    }

    @Test
    public void testInvalidFileNameBeginning() {
        final String fileName = createFileName(INVALID_FILENANE_BEGINNING, GROUP_ID, ARTIFACT_ID, VERSION,
                VALID_FILENAME_ENDING);

        File file = createMockedFile(fileName);
        IFileToJarFileConverter fileToJarFileConverter = createFileToJarFileConverter(fileName, VERSION, GROUP_ID,
                ARTIFACT_ID);

        IDependencyInfo info = new DependencyInfo(file, DependencyType.JAR, null);

        IMappingStrategy mavenPomStrategy = new MavenPomPropertiesStrategy(fileToJarFileConverter);
        Optional<ProjectCoordinate> optionalProjectCoordinate = mavenPomStrategy.extractProjectCoordinate(info);

        assertFalse(optionalProjectCoordinate.isPresent());
    }

    @Test
    public void testInvalidArtifactIdInFileName() {
        final String fileName = createFileName(VALID_FILENANE_BEGINNING, GROUP_ID, ARTIFACT_ID, VERSION,
                VALID_FILENAME_ENDING);

        File file = createMockedFile(fileName);
        IFileToJarFileConverter fileToJarFileConverter = createFileToJarFileConverter(fileName, VERSION, GROUP_ID,
                "other");

        IDependencyInfo info = new DependencyInfo(file, DependencyType.JAR, null);

        IMappingStrategy mavenPomStrategy = new MavenPomPropertiesStrategy(fileToJarFileConverter);
        Optional<ProjectCoordinate> optionalProjectCoordinate = mavenPomStrategy.extractProjectCoordinate(info);

        assertFalse(optionalProjectCoordinate.isPresent());
    }

    @Test
    public void testInvalidArtifactIdAndGroudIdInFileName() {
        final String fileName = createFileName(VALID_FILENANE_BEGINNING, GROUP_ID, ARTIFACT_ID, VERSION,
                VALID_FILENAME_ENDING);

        File file = createMockedFile(fileName);
        IFileToJarFileConverter fileToJarFileConverter = createFileToJarFileConverter(fileName, VERSION, "other",
                "other");

        IDependencyInfo info = new DependencyInfo(file, DependencyType.JAR, null);

        IMappingStrategy mavenPomStrategy = new MavenPomPropertiesStrategy(fileToJarFileConverter);
        Optional<ProjectCoordinate> optionalProjectCoordinate = mavenPomStrategy.extractProjectCoordinate(info);

        assertFalse(optionalProjectCoordinate.isPresent());
    }

    @Test
    public void testInvalidFileEndingAndArtifactIdInFileName() {
        final String fileName = createFileName(VALID_FILENANE_BEGINNING, GROUP_ID, ARTIFACT_ID, VERSION,
                INVALID_FILENAME_ENDING);

        File file = createMockedFile(fileName);
        IFileToJarFileConverter fileToJarFileConverter = createFileToJarFileConverter(fileName, VERSION, GROUP_ID,
                "other");

        IDependencyInfo info = new DependencyInfo(file, DependencyType.JAR, null);

        IMappingStrategy mavenPomStrategy = new MavenPomPropertiesStrategy(fileToJarFileConverter);
        Optional<ProjectCoordinate> optionalProjectCoordinate = mavenPomStrategy.extractProjectCoordinate(info);

        assertFalse(optionalProjectCoordinate.isPresent());
    }

    @Test
    public void testInvalidFileEndingAndGroupIdInFileName() {
        final String fileName = createFileName(VALID_FILENANE_BEGINNING, GROUP_ID, ARTIFACT_ID, VERSION,
                INVALID_FILENAME_ENDING);

        File file = createMockedFile(fileName);
        IFileToJarFileConverter fileToJarFileConverter = createFileToJarFileConverter(fileName, VERSION, "other",
                ARTIFACT_ID);

        IDependencyInfo info = new DependencyInfo(file, DependencyType.JAR, null);

        IMappingStrategy mavenPomStrategy = new MavenPomPropertiesStrategy(fileToJarFileConverter);
        Optional<ProjectCoordinate> optionalProjectCoordinate = mavenPomStrategy.extractProjectCoordinate(info);

        assertFalse(optionalProjectCoordinate.isPresent());
    }

    @Test
    public void testInvalidFileEndingAndArtifactIdAndGroudIdInFileName() {
        final String fileName = createFileName(VALID_FILENANE_BEGINNING, GROUP_ID, ARTIFACT_ID, VERSION,
                INVALID_FILENAME_ENDING);

        File file = createMockedFile(fileName);
        IFileToJarFileConverter fileToJarFileConverter = createFileToJarFileConverter(fileName, VERSION, "other",
                "other");

        IDependencyInfo info = new DependencyInfo(file, DependencyType.JAR, null);

        IMappingStrategy mavenPomStrategy = new MavenPomPropertiesStrategy(fileToJarFileConverter);
        Optional<ProjectCoordinate> optionalProjectCoordinate = mavenPomStrategy.extractProjectCoordinate(info);

        assertFalse(optionalProjectCoordinate.isPresent());
    }

    @Test
    public void testNotApplicableTyp() {
        final String fileName = createFileName(VALID_FILENANE_BEGINNING, GROUP_ID, ARTIFACT_ID, VERSION,
                VALID_FILENAME_ENDING);

        File file = createMockedFile(fileName);
        IFileToJarFileConverter fileToJarFileConverter = createFileToJarFileConverter(fileName, VERSION, "other",
                "other");

        IDependencyInfo info = new DependencyInfo(file, DependencyType.PROJECT, null);

        IMappingStrategy mavenPomStrategy = new MavenPomPropertiesStrategy(fileToJarFileConverter);
        Optional<ProjectCoordinate> optionalProjectCoordinate = mavenPomStrategy.extractProjectCoordinate(info);

        assertFalse(optionalProjectCoordinate.isPresent());
    }

    @Test
    public void testApplicabaleTypButNoFile() {
        final String fileName = "";

        File file = createMockedFile(fileName);
        IFileToJarFileConverter fileToJarFileConverter = new IFileToJarFileConverter() {

            @Override
            public Optional<JarFile> createJarFile(File file) {
                return Optional.absent();
            }
        };

        IDependencyInfo info = new DependencyInfo(file, DependencyType.JAR, null);

        IMappingStrategy mavenPomStrategy = new MavenPomPropertiesStrategy(fileToJarFileConverter);
        Optional<ProjectCoordinate> optionalProjectCoordinate = mavenPomStrategy.extractProjectCoordinate(info);

        assertFalse(optionalProjectCoordinate.isPresent());
    }

    @Test
    public void testMissingValuesInPomProperties() {
        final String fileName = createFileName(VALID_FILENANE_BEGINNING, GROUP_ID, ARTIFACT_ID, VERSION,
                INVALID_FILENAME_ENDING);

        IDependencyInfo info = new DependencyInfo(createMockedFile(fileName), DependencyType.JAR, null);

        MavenPomPropertiesStrategy mavenPomStrategy = new MavenPomPropertiesStrategy(new IFileToJarFileConverter() {

            @Override
            public Optional<JarFile> createJarFile(File file) {
                if (file.getName().equals(fileName)) {
                    final JarFileMockBuilder builder = new JarFileMockBuilder();
                    builder.addEntry(fileName, new ByteArrayInputStream(new byte[0]));
                    return Optional.fromNullable(builder.build());
                }
                return Optional.absent();
            }
        });
        Optional<ProjectCoordinate> optionalProjectCoordinate = mavenPomStrategy.extractProjectCoordinate(info);

        assertFalse(optionalProjectCoordinate.isPresent());
    }

}
