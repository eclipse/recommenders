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
package org.eclipse.recommenders.models.mapping.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.models.mapping.DependencyType;
import org.eclipse.recommenders.models.mapping.IDependencyInfo;
import org.eclipse.recommenders.models.mapping.IMappingStrategy;
import org.eclipse.recommenders.utils.annotations.Testing;
import org.eclipse.recommenders.utils.archive.MavenPomJarIdExtractor;

import com.google.common.base.Optional;

/**
 * Implementation based on {@link MavenPomJarIdExtractor}.
 */
public class MavenPomPropertiesStrategy implements IMappingStrategy {

    private static final String POM_FILE_ENDING_REGEX = ".*/maven/.*/.*/pom.properties";
    public static final String PROPERTY_KEY_VERSION = "version";
    public static final String PROPERTY_KEY_ARTIFACT_ID = "artifactId";
    public static final String PROPERTY_KEY_GROUP_ID = "groupId";

    private final IFileToJarFileConverter jarFileConverter;

    public MavenPomPropertiesStrategy() {
        this.jarFileConverter = new DefaultJarFileConverter();
    }

    @Testing
    public MavenPomPropertiesStrategy(IFileToJarFileConverter fileToJarFileConverter) {
        this.jarFileConverter = fileToJarFileConverter;
    }

    @Override
    public Optional<ProjectCoordinate> extractProjectCoordinate(IDependencyInfo dependencyInfo) {
        if (!isApplicable(dependencyInfo.getType())) {
            return Optional.absent();
        }
        Optional<JarFile> optionalJarFile = readJarFileIn(dependencyInfo.getFile());
        if (!optionalJarFile.isPresent()) {
            return Optional.absent();
        }
        JarFile jarFile = optionalJarFile.get();
        Optional<InputStream> optionalFileName = findPomFile(jarFile);
        if (!optionalFileName.isPresent()) {
            return Optional.absent();
        }
        InputStream pomInputStream = optionalFileName.get();
        Optional<ProjectCoordinate> optionalProjectCoordinate = parseProjectCoordinate(pomInputStream, dependencyInfo);
        return optionalProjectCoordinate;
    }

    private Optional<ProjectCoordinate> parseProjectCoordinate(InputStream inputStream, IDependencyInfo dependencyInfo) {
        final Properties properties = new Properties();
        try {
            properties.load(inputStream);
            String groupID = parseGroupID(properties);
            String artifactID = parseArtifactID(properties);
            if (!groupID.equals(extractGroupID(dependencyInfo.getFile().getName()))) {
                return Optional.absent();
            }
            if (!artifactID.equals(extractArtifactID(dependencyInfo.getFile().getName()))) {
                return Optional.absent();
            }
            ProjectCoordinate pc = new ProjectCoordinate(groupID, artifactID, parseVersion(properties));
            return Optional.fromNullable(pc);
        } catch (IOException e) {
            return Optional.absent();
        }
    }

    private Optional<InputStream> findPomFile(JarFile jarFile) {
        for (Enumeration<JarEntry> elements = jarFile.entries(); elements.hasMoreElements();) {
            ZipEntry entry = elements.nextElement();
            if (isPomFile(entry.getName())) {
                try {
                    return Optional.fromNullable(jarFile.getInputStream(entry));
                } catch (IOException e) {
                    return Optional.absent();
                }
            }
        }
        return Optional.absent();
    }

    private boolean isPomFile(String fileName) {
        return fileName.matches(POM_FILE_ENDING_REGEX);
    }

    private Optional<JarFile> readJarFileIn(File file) {
        return jarFileConverter.createJarFile(file);
    }

    private String parseAttribute(final Properties properties, String attributeName) {
        String value = properties.getProperty(attributeName);
        if (value == null) {
            value = "";
        }
        return value;
    }

    private String parseGroupID(final Properties properties) {
        return parseAttribute(properties, PROPERTY_KEY_GROUP_ID);
    }

    private String parseArtifactID(final Properties properties) {
        return parseAttribute(properties, PROPERTY_KEY_ARTIFACT_ID);
    }

    private String parseVersion(final Properties properties) {
        return parseAttribute(properties, PROPERTY_KEY_VERSION);
    }

    @Override
    public boolean isApplicable(DependencyType dependencyTyp) {
        return dependencyTyp == DependencyType.JAR;
    }

    public static String extractGroupID(String fileName) {
        return extract(fileName, 3);
    }

    public static String extractArtifactID(String fileName) {
        return extract(fileName, 2);
    }

    public static String extract(String fileName, int index) {
        String[] split = fileName.split("/");
        if (split.length >= 5) {
            return split[split.length - index];
        }
        return "";
    }

    public interface IFileToJarFileConverter {
        public Optional<JarFile> createJarFile(File file);
    }

    private class DefaultJarFileConverter implements IFileToJarFileConverter {
        @Override
        public Optional<JarFile> createJarFile(File file) {
            try {
                JarFile jarFile = new JarFile(file.getAbsolutePath());
                return Optional.fromNullable(jarFile);
            } catch (IOException e) {
                return Optional.absent();
            }
        }
    }

}
