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

import static com.google.common.base.Optional.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.eclipse.recommenders.utils.IOUtils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.Sets;

/**
 * Implementation based on {@link MavenPomJarIdExtractor}.
 */
public class MavenPomPropertiesStrategy extends AbstractStrategy {

    private static final String POM_PROPERTIES_FILE_REGEX = "META-INF/maven/.*/.*/pom.properties";
    public static final String PROPERTY_KEY_VERSION = "version";
    public static final String PROPERTY_KEY_ARTIFACT_ID = "artifactId";
    public static final String PROPERTY_KEY_GROUP_ID = "groupId";

    private final IFileToJarFileConverter jarFileConverter;

    public MavenPomPropertiesStrategy() {
        jarFileConverter = new DefaultJarFileConverter();
    }

    @VisibleForTesting
    public MavenPomPropertiesStrategy(IFileToJarFileConverter fileToJarFileConverter) {
        jarFileConverter = fileToJarFileConverter;
    }

    @Override
    protected Optional<ProjectCoordinate> extractProjectCoordinateInternal(DependencyInfo dependencyInfo) {
        Optional<JarFile> optionalJarFile = readJarFileIn(dependencyInfo.getFile());
        if (!optionalJarFile.isPresent()) {
            return absent();
        }
        JarFile jarFile = optionalJarFile.get();
        try {
            return extractProjectCoordinateOfJarFile(jarFile);
        } catch (IOException e) {
            return absent();
        } finally {
            IOUtils.close(jarFile);
        }
    }

    private Optional<ProjectCoordinate> extractProjectCoordinateOfJarFile(JarFile jarFile) throws IOException {
        Set<ZipEntry> pomZipEntries = findPomPropertiesEntries(jarFile);
        for (ZipEntry zipEntry : pomZipEntries) {
            InputStream pomPropertiesInputStream;
            pomPropertiesInputStream = jarFile.getInputStream(zipEntry);
            Optional<ProjectCoordinate> projectCoordinate = parseProjectCoordinate(pomPropertiesInputStream,
                    zipEntry.getName());
            IOUtils.closeQuietly(pomPropertiesInputStream);
            if (projectCoordinate.isPresent()) {
                return projectCoordinate;
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

    private Set<ZipEntry> findPomPropertiesEntries(JarFile jarFile) {
        Set<ZipEntry> pomEntries = Sets.newHashSet();
        for (Enumeration<JarEntry> elements = jarFile.entries(); elements.hasMoreElements();) {
            ZipEntry entry = elements.nextElement();
            if (isPomPropertiesFile(entry.getName())) {
                pomEntries.add(entry);
            }
        }
        return pomEntries;
    }

    private boolean isPomPropertiesFile(String fileName) {
        return fileName.matches(POM_PROPERTIES_FILE_REGEX);
    }

    private Optional<JarFile> readJarFileIn(File file) {
        return jarFileConverter.createJarFile(file);
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
    public boolean isApplicable(DependencyType dependencyType) {
        return dependencyType == DependencyType.JAR;
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

}
