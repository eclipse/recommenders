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

import static com.google.common.base.Optional.absent;
import static org.eclipse.recommenders.utils.Versions.*;

import java.util.regex.Pattern;

import org.eclipse.recommenders.models.DependencyInfo;
import org.eclipse.recommenders.models.DependencyType;
import org.eclipse.recommenders.models.ProjectCoordinate;

import com.google.common.base.Optional;

/**
 * This advisor tries to extract the version out of the folder name of java_home.
 */
public class JREFolderNameAdvisor extends AbstractProjectCoordinateAdvisor {

    @Override
    protected Optional<ProjectCoordinate> doSuggest(DependencyInfo dependencyInfo) {
        String path = dependencyInfo.getFile().getAbsolutePath();
        String[] splittedPath = path.split(Pattern.quote(System.getProperty("file.separator")));

        for (int i = splittedPath.length - 1; i >= 0; i--) {
            String version = canonicalizeVersion(splittedPath[i]);
            if (isValidVersionString(version)) {
                return ProjectCoordinate.create("jre", "jre", version);
            }
        }
        return absent();
    }

    @Override
    public boolean isApplicable(DependencyType dependencyType) {
        return dependencyType == DependencyType.JRE;
    }

}
