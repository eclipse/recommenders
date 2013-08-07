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
package org.eclipse.recommenders.models;

import static org.eclipse.recommenders.models.DependencyType.PROJECT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.eclipse.recommenders.models.advisors.MavenPomXmlAdvisor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Optional;

public class MavenPomXmlStrategyTest {

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private DependencyInfo info;
    private File projectFolder;

    @Before
    public void init() throws IOException {
        projectFolder = folder.newFolder("ProjectFolder");
        info = new DependencyInfo(projectFolder, PROJECT);
    }

    @Test
    public void testProjectCoordinateIsExtractedCorrectFromSimplePom() throws IOException {
        Model model = new Model();
        model.setGroupId("org.example");
        model.setArtifactId("org.example.sample");
        model.setVersion("1.0.0-SNAPSHOT");
        writeModelToFile(new File(projectFolder, "pom.xml"), model);

        IProjectCoordinateAdvisor sut = new MavenPomXmlAdvisor();
        Optional<ProjectCoordinate> optionalProjectCoordinate = sut.suggest(info);

        ProjectCoordinate expected = new ProjectCoordinate("org.example", "org.example.sample", "1.0.0");
        assertEquals(expected, optionalProjectCoordinate.get());
    }

    @Test
    public void testVariablesInPomResultInAbsent() throws IOException {
        Model model = new Model();
        model.setGroupId("${groupId}");
        model.setArtifactId("${artifactId}");
        model.setVersion("${version}");
        writeModelToFile(new File(projectFolder, "pom.xml"), model);

        IProjectCoordinateAdvisor sut = new MavenPomXmlAdvisor();
        Optional<ProjectCoordinate> optionalProjectCoordinate = sut.suggest(info);

        assertFalse(optionalProjectCoordinate.isPresent());
    }

    @Test
    public void testMissingValuesInPomWithoutParentResultInAbsent() throws IOException {
        writeModelToFile(new File(projectFolder, "pom.xml"), new Model());

        IProjectCoordinateAdvisor sut = new MavenPomXmlAdvisor();
        Optional<ProjectCoordinate> optionalProjectCoordinate = sut.suggest(info);

        assertFalse(optionalProjectCoordinate.isPresent());
    }

    @Test
    public void testMissingValuesAreTakenFromParent() throws IOException {
        Parent parent = new Parent();
        parent.setArtifactId("plugins");
        parent.setGroupId("org.example.sample");
        parent.setVersion("1.0.0-SNAPSHOT");

        Model model = new Model();
        model.setArtifactId("org.example.sample.feature");
        model.setParent(parent);

        writeModelToFile(new File(projectFolder, "pom.xml"), model);

        IProjectCoordinateAdvisor sut = new MavenPomXmlAdvisor();
        Optional<ProjectCoordinate> optionalProjectCoordinate = sut.suggest(info);

        ProjectCoordinate expected = new ProjectCoordinate("org.example.sample", "org.example.sample.feature", "1.0.0");
        assertEquals(expected, optionalProjectCoordinate.get());
    }

    private static void writeModelToFile(File file, Model model) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        new MavenXpp3Writer().write(fileOutputStream, model);
        fileOutputStream.close();
    }

}
