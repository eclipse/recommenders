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

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.fromNullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;
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
import com.google.common.collect.Sets;

/**
 * Implementation based on {@link MavenPomJarIdExtractor}.
 */
public class MavenPomPropertiesStrategy implements IMappingStrategy {

    private static final String POM_FILE_ENDING_REGEX = "/META-INF/maven/.*/.*/pom.properties";
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
            return absent();
        }
        Optional<JarFile> optionalJarFile = readJarFileIn(dependencyInfo.getFile().getName());
        if (!optionalJarFile.isPresent()) {
            return absent();
        }
        JarFile jarFile = optionalJarFile.get();
        Set<ZipEntry> pomZipEntries = findPomPropertiesEntries(dependencyInfo.getFile().getName(), jarFile);

        for (ZipEntry zipEntry : pomZipEntries) {
            InputStream pomPropertiesInputStream;
            try {
                pomPropertiesInputStream = jarFile.getInputStream(zipEntry);
            } catch (IOException e) {
                return absent();
            }
            Optional<ProjectCoordinate> optionalProjectCoordinate = parseProjectCoordinate(pomPropertiesInputStream,
                    zipEntry.getName());
            try {
                pomPropertiesInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (optionalProjectCoordinate.isPresent()) {
                return optionalProjectCoordinate;
            }
        }
        return absent();
    }

    private Optional<ProjectCoordinate> parseProjectCoordinate(InputStream inputStream, String propertiesFileName) {
        final Properties properties = new Properties();
        try {
            properties.load(inputStream);
            String groupID = parseGroupID(properties);
            String artifactID = parseArtifactID(properties);
            if (!groupID.equals(extractGroupID(propertiesFileName))) {
                return absent();
            }
            if (!artifactID.equals(extractArtifactID(propertiesFileName))) {
                return absent();
            }
            ProjectCoordinate pc = new ProjectCoordinate(groupID, artifactID, parseVersion(properties));
            return fromNullable(pc);
        } catch (IOException e) {
            return absent();
        }
    }

    private Set<ZipEntry> findPomPropertiesEntries(String jarFileName, JarFile jarFile) {
        Set<ZipEntry> pomEntries = Sets.newHashSet();
        for (Enumeration<JarEntry> elements = jarFile.entries(); elements.hasMoreElements();) {
            ZipEntry entry = elements.nextElement();
            if (isPomFile(jarFileName, entry.getName())) {
                pomEntries.add(entry);
            }
        }
        return pomEntries;
    }

    private boolean isPomFile(String jarFileName, String fileName) {
        return fileName.matches(jarFileName + POM_FILE_ENDING_REGEX);
    }

    private Optional<JarFile> readJarFileIn(String fileName) {
        return jarFileConverter.createJarFile(fileName);
    }

    private String parseAttribute(final Properties properties, String attributeName) {
        String value = properties.getProperty(attributeName);
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
        if (split.length >= 4) {
            return split[split.length - index];
        }
        return "";
    }

    public interface IFileToJarFileConverter {
        public Optional<JarFile> createJarFile(String fileName);
    }

    private class DefaultJarFileConverter implements IFileToJarFileConverter {
        @Override
        public Optional<JarFile> createJarFile(String fileName) {
            try {
                JarFile jarFile = new JarFile(fileName);
                return fromNullable(jarFile);
            } catch (IOException e) {
                return absent();
            }
        }
    }

}
