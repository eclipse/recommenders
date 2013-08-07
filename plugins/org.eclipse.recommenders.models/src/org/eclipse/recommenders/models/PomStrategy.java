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

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;
import static org.eclipse.recommenders.models.DependencyType.PROJECT;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

public class PomStrategy extends AbstractStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(PomStrategy.class);

    @Override
    protected Optional<ProjectCoordinate> extractProjectCoordinateInternal(DependencyInfo dependencyInfo) {
        File pomfile = new File(dependencyInfo.getFile(), "pom.xml");
        if (!pomfile.exists()) {
            return absent();
        }
        try {
            return extractProjectCoordinateFromPom(pomfile);
        } catch (IOException e) {
            LOG.error("Couldn't read pom file of dependency :" + dependencyInfo, e);
            return absent();
        } catch (XmlPullParserException e) {
            LOG.error("Couldn't read pom file of dependency :" + dependencyInfo, e);
            return absent();
        }
    }

    private Optional<ProjectCoordinate> extractProjectCoordinateFromPom(File pomfile) throws IOException,
            XmlPullParserException {
        MavenXpp3Reader mavenreader = new MavenXpp3Reader();
        FileReader reader = new FileReader(pomfile);
        Model model = mavenreader.read(reader);

        String groupId = model.getGroupId();
        String artifactId = model.getArtifactId();
        String version = model.getVersion();

        Parent parent = model.getParent();
        if (parent != null) {
            if (groupId == null) {
                groupId = parent.getGroupId();
            }
            if (version == null) {
                version = parent.getVersion();
            }
        }

        if (groupId == null || artifactId == null || version == null) {
            return absent();
        }
        if (groupId.contains("$") || artifactId.contains("$") || version.contains("$")) {
            return absent();
        }

        int indexOf = version.indexOf("-");
        version = version.substring(0, indexOf == -1 ? version.length() : indexOf);
        return of(new ProjectCoordinate(groupId, artifactId, version));
    }

    @Override
    public boolean isApplicable(DependencyType type) {
        return PROJECT == type;
    }
}
