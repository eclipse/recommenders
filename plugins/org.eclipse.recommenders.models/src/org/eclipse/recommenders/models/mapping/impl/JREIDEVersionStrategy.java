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

import com.google.common.base.Optional;

public class JREIDEVersionStrategy extends AbstractStrategy {

    @Override
    protected Optional<ProjectCoordinate> extractProjectCoordinateInternal(IDependencyInfo dependencyInfo) {
        Optional<String> optionalVersion = dependencyInfo.getAttribute(DependencyInfo.JRE_VERSION_IDE);
        if (!optionalVersion.isPresent()) {
            return absent();
        }
        String version = optionalVersion.get();

        ProjectCoordinate projectCoordinate = new ProjectCoordinate("jre", "jre", version);
        return fromNullable(projectCoordinate);
    }

    @Override
    public boolean isApplicable(DependencyType dependencyType) {
        return dependencyType == DependencyType.JRE;
    }

}
