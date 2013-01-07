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

import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.models.mapping.DependencyType;
import org.eclipse.recommenders.models.mapping.IDependencyInfo;
import org.eclipse.recommenders.utils.Fingerprints;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import com.google.common.base.Optional;

public class FingerprintStrategy extends AbstractStrategy {

    private final IndexUtilities indexer;

    public FingerprintStrategy(IndexUtilities indexer) {
        this.indexer = indexer;
    }

    @Override
    public boolean isApplicable(DependencyType dependencyType) {
        return dependencyType == DependencyType.JAR;
    }

    @Override
    protected Optional<ProjectCoordinate> extractProjectCoordinateInternal(IDependencyInfo dependencyInfo) {
        String fingerprint = Fingerprints.sha1(dependencyInfo.getFile());
        indexer.open();
        Optional<String> optionalCoordinateString = indexer.searchByFingerprint(fingerprint);
        indexer.close();
        if (!optionalCoordinateString.isPresent()) {
            return absent();
        }
        return extractProjectCoordinate(optionalCoordinateString.get());
    }

    private Optional<ProjectCoordinate> extractProjectCoordinate(String string) {
        try {
            DefaultArtifact artifact = new DefaultArtifact(string);
            return fromNullable(new ProjectCoordinate(artifact.getGroupId(), artifact.getArtifactId(),
                    artifact.getVersion()));
        } catch (IllegalArgumentException e) {
            return absent();
        }
    }

}
