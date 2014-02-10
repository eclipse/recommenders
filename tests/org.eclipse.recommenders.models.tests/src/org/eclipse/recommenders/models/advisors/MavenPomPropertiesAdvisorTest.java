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
package org.eclipse.recommenders.models.advisors;

import static com.google.common.collect.ImmutableMap.of;
import static org.eclipse.recommenders.models.DependencyType.JAR;
import static org.eclipse.recommenders.models.DependencyType.PROJECT;
import static org.eclipse.recommenders.models.advisors.MavenPomPropertiesAdvisor.ARTIFACT_ID;
import static org.eclipse.recommenders.models.advisors.MavenPomPropertiesAdvisor.GROUP_ID;
import static org.eclipse.recommenders.models.advisors.MavenPomPropertiesAdvisor.VERSION;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.eclipse.recommenders.models.DependencyInfo;
import org.eclipse.recommenders.models.IProjectCoordinateAdvisor;
import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.tests.models.utils.IFileToJarFileConverterMockBuilder;
import org.eclipse.recommenders.utils.Zips.IFileToJarFileConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
public class MavenPomPropertiesAdvisorTest {

    private static final Properties ORG_EXAMPLE_PROPS = createProperties("org.example", "example", "1.0.0");
    private static final Properties COM_EXAMPLE_PROPS = createProperties("com.example", "example", "1.0.0");
    private static final ProjectCoordinate COORDINATE = new ProjectCoordinate("org.example", "example", "1.0.0");

    private final DependencyInfo dependency;
    private final IFileToJarFileConverter fileToJarFileConverter;
    private final Optional<ProjectCoordinate> expectedCoordinate;

    public MavenPomPropertiesAdvisorTest(DependencyInfo dependency, IFileToJarFileConverter fileToJarFileConverter,
            Optional<ProjectCoordinate> expectedCoordinate) {
        this.dependency = dependency;
        this.fileToJarFileConverter = fileToJarFileConverter;
        this.expectedCoordinate = expectedCoordinate;
    }

    @Parameters
    public static Collection<Object[]> scenarios() {
        LinkedList<Object[]> scenarios = Lists.newLinkedList();

        // Valid pom.properties, correct directory structure
        scenarios.add(jarDependency(of("META-INF/maven/org.example/example/pom.properties", ORG_EXAMPLE_PROPS),
                COORDINATE));

        // Wrong directory structure: pom.properties in root directory
        scenarios.add(jarDependency(of("pom.properties", ORG_EXAMPLE_PROPS), null));

        // Wrong directory structure: META-INF directory missing
        scenarios.add(jarDependency(of("maven/org.example/example/pom.properties", ORG_EXAMPLE_PROPS), null));

        // Wrong directory structure: maven directory missing
        scenarios.add(jarDependency(of("META-INF/org.example/example/pom.properties", ORG_EXAMPLE_PROPS), null));

        // Wrong directory structure: invalid file extension
        scenarios.add(jarDependency(of("META-INF/maven/org.example/example/pom.invalid", ORG_EXAMPLE_PROPS), null));

        // Invalid pom.properties: groupId mismatch
        scenarios.add(jarDependency(of("META-INF/maven/invalid/example/pom.properties", ORG_EXAMPLE_PROPS), null));

        // Invalid pom.properties: artifactId mismatch
        scenarios.add(jarDependency(of("META-INF/maven/org.example/invalid/pom.properties", ORG_EXAMPLE_PROPS), null));

        // Wrong dependency type
        scenarios.add(projectDependency(of("META-INF/maven/org.example/example/pom.properties", ORG_EXAMPLE_PROPS),
                null));

        // Multiple pom.properties, only one valid
        scenarios.add(jarDependency(
                of("META-INF/maven/invalid/example/pom.properties", ORG_EXAMPLE_PROPS,
                        "META-INF/maven/org.example/example/pom.properties", ORG_EXAMPLE_PROPS), COORDINATE));
        scenarios.add(jarDependency(
                of("META-INF/maven/org.example/example/pom.properties", ORG_EXAMPLE_PROPS,
                        "META-INF/maven/invalid/example/pom.properties", ORG_EXAMPLE_PROPS), COORDINATE));

        // Multiple pom.properties, all valid
        scenarios.add(jarDependency(
                of("META-INF/maven/org.example/example/pom.properties", ORG_EXAMPLE_PROPS,
                        "META-INF/maven/com.example/example/pom.properties", COM_EXAMPLE_PROPS), null));
        scenarios.add(jarDependency(
                of("META-INF/maven/com.example/example/pom.properties", COM_EXAMPLE_PROPS,
                        "META-INF/maven/org.example/example/pom.properties", ORG_EXAMPLE_PROPS), null));

        return scenarios;
    }

    @Test
    public void testScenario() {
        IProjectCoordinateAdvisor sut = new MavenPomPropertiesAdvisor(fileToJarFileConverter);

        Optional<ProjectCoordinate> result = sut.suggest(dependency);

        assertThat(result, is(expectedCoordinate));
    }

    private static Object[] jarDependency(Map<String, Properties> propertyFiles,
            ProjectCoordinate expectedProjectCoordinate) {
        File jarFile = new File("example.jar").getAbsoluteFile();
        return new Object[] { new DependencyInfo(jarFile, JAR), createIFileToJarFileConverter(propertyFiles),
                Optional.fromNullable(expectedProjectCoordinate) };
    }

    private static Object[] projectDependency(Map<String, Properties> propertyFiles,
            ProjectCoordinate expectedProjectCoordinate) {
        File projectDirectory = new File("example").getAbsoluteFile();
        return new Object[] { new DependencyInfo(projectDirectory, PROJECT),
                createIFileToJarFileConverter(propertyFiles), Optional.fromNullable(expectedProjectCoordinate) };
    }

    private static Properties createProperties(String groupId, String artifactId, String version) {
        Properties properties = new Properties();
        properties.put(GROUP_ID, groupId);
        properties.put(ARTIFACT_ID, artifactId);
        properties.put(VERSION, version);
        return properties;
    }

    private static IFileToJarFileConverter createIFileToJarFileConverter(Map<String, Properties> propertyFiles) {
        IFileToJarFileConverterMockBuilder builder = new IFileToJarFileConverterMockBuilder();
        for (Entry<String, Properties> propertyFile : propertyFiles.entrySet()) {
            builder.put(propertyFile.getKey(), propertyFile.getValue());
        }
        return builder.build();
    }
}
