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
package org.eclipse.recommenders.internal.models.rcp;

import static com.google.common.base.Optional.absent;
import static org.eclipse.jdt.core.IJavaElement.JAVA_PROJECT;
import static org.eclipse.recommenders.models.DependencyInfo.PACKAGE_FRAGMENT_ROOT;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.recommenders.injection.InjectionService;
import org.eclipse.recommenders.models.DependencyInfo;
import org.eclipse.recommenders.models.IProjectCoordinateAdvisor;
import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.models.rcp.IProjectCoordinateProvider;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;

public class NestedJarProjectCoordinateAdvisor implements IProjectCoordinateAdvisor {

    // can not use @Inject here. See below.
    @VisibleForTesting
    protected IProjectCoordinateProvider pcProvider;

    public Optional<ProjectCoordinate> suggest(DependencyInfo dependencyInfo) {
        String handle = dependencyInfo.getHint(PACKAGE_FRAGMENT_ROOT).orNull();
        if (StringUtils.isEmpty(handle)) {
            return absent();
        }
        IJavaElement pkg = resolveHandleIdentifier(handle);
        // we only expect package fragments.
        if (!(pkg instanceof IPackageFragmentRoot)) {
            return absent();
        }
        IJavaProject project = (IJavaProject) pkg.getAncestor(JAVA_PROJECT);
        if (project == null) {
            return absent();
        }
        initializePcProvider();
        return pcProvider.resolve(project);
    }

    @VisibleForTesting
    protected IJavaElement resolveHandleIdentifier(String handle) {
        return JavaCore.create(handle);
    }

    private void initializePcProvider() {
        // not great but if not initializing this lazily I get into circular dependencies when the
        // ProjectCoordinatesView is open.
        if (pcProvider == null) {
            pcProvider = InjectionService.getInstance().requestInstance(IProjectCoordinateProvider.class);
        }
    }
}
