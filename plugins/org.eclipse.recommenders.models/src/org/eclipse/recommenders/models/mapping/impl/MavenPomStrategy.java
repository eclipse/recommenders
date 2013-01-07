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
import org.eclipse.recommenders.models.mapping.IElementInfo;
import org.eclipse.recommenders.models.mapping.IMappingStrategy;
import org.eclipse.recommenders.models.mapping.ElementType;

import com.google.common.base.Optional;

/**
 * Implementation based on {@link MavenPomJarIdExtractor}.
 */
public class MavenPomStrategy implements IMappingStrategy {

    private static final String POM_FILE_ENDING = "pom.properties";

    private static final String PROPERTY_KEY_VERSION = "version";
    private static final String PROPERTY_KEY_ARTIFACT_ID = "artifactId";
    private static final String PROPERTY_KEY_GROUP_ID = "groupID";

    @Override
    public Optional<ProjectCoordinate> extractProjectCoordinate(IElementInfo elementInfo) {
        if (!isAddressableElementTypes(elementInfo.getType())) {
            return Optional.absent();
        }
        Optional<JarFile> optionalJarFile = readJarFileIn(elementInfo.getFile());
        if (!optionalJarFile.isPresent()) {
            return Optional.absent();
        }
        JarFile jarFile = optionalJarFile.get();
        Optional<InputStream> optionalFileName = findPomFile(jarFile);
        if (!optionalFileName.isPresent()) {
            return Optional.absent();
        }
        InputStream pomInputStream = optionalFileName.get();
        Optional<ProjectCoordinate> optionalProjectCoordinate = parseProjectCoordinate(pomInputStream);
        return optionalProjectCoordinate;
    }

    private Optional<ProjectCoordinate> parseProjectCoordinate(InputStream inputStream) {
        final Properties properties = new Properties();
        try {
            properties.load(inputStream);
            ProjectCoordinate pc = new ProjectCoordinate(parseGroupID(properties), parseArtifactID(properties),
                    parseVersion(properties));
            return Optional.of(pc);
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
        return fileName.endsWith(POM_FILE_ENDING);
    }

    private Optional<JarFile> readJarFileIn(File file) {
        try {
            JarFile jarFile = new JarFile(file.getAbsolutePath());
            return Optional.fromNullable(jarFile);
        } catch (IOException e) {
            return Optional.absent();
        }
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
    public boolean isAddressableElementTypes(ElementType type) {
        return type.equals(ElementType.JAR);
    }
}
