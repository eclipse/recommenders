/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.models.rcp;

import static com.google.common.base.Optional.*;
import static org.eclipse.jdt.core.IJavaElement.PACKAGE_FRAGMENT_ROOT;
import static org.eclipse.recommenders.models.DependencyType.*;
import static org.eclipse.recommenders.rcp.utils.JdtUtils.getLocation;
import static org.eclipse.recommenders.utils.Checks.cast;

import java.io.File;

import javax.inject.Inject;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.recommenders.models.BasedTypeName;
import org.eclipse.recommenders.models.DependencyInfo;
import org.eclipse.recommenders.models.DependencyType;
import org.eclipse.recommenders.models.IMappingProvider;
import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.models.rcp.IProjectCoordinateProvider;
import org.eclipse.recommenders.rcp.JavaElementResolver;
import org.eclipse.recommenders.rcp.utils.JdtUtils;
import org.eclipse.recommenders.utils.names.IMethodName;
import org.eclipse.recommenders.utils.names.ITypeName;

import com.google.common.base.Optional;

public class ProjectCoordinateProvider implements IProjectCoordinateProvider {

    @Inject
    IMappingProvider mappingProvider;
    @Inject
    JavaElementResolver javaElementResolver;

    public ProjectCoordinateProvider() {
        // injection constructor
    }

    public ProjectCoordinateProvider(IMappingProvider mappingProvider) {
        this.mappingProvider = mappingProvider;
    }

    @Override
    public Optional<ProjectCoordinate> resolve(ITypeBinding binding) {
        if (binding == null) {
            return absent();
        }
        IType type = cast(binding.getJavaElement());
        return resolve(type);
    }

    @Override
    public Optional<ProjectCoordinate> resolve(IType type) {
        if (type == null) {
            return absent();
        }
        IPackageFragmentRoot root = cast(type.getAncestor(PACKAGE_FRAGMENT_ROOT));
        return resolve(root);
    }

    @Override
    public Optional<ProjectCoordinate> resolve(IMethodBinding binding) {
        if (binding == null) {
            return absent();
        }
        IMethod method = cast(binding.getJavaElement());
        return resolve(method);
    }

    @Override
    public Optional<ProjectCoordinate> resolve(IMethod method) {
        if (method == null) {
            return absent();
        }
        IPackageFragmentRoot root = cast(method.getAncestor(PACKAGE_FRAGMENT_ROOT));
        return resolve(root);
    }

    @Override
    public Optional<ProjectCoordinate> resolve(IPackageFragmentRoot root) {
        if (root == null) {
            return absent();
        }
        if (!root.isArchive()) {
            return resolve(root.getJavaProject());
        }
        File location = JdtUtils.getLocation(root).orNull();
        if (location == null) {
            return absent();
        }

        // TODO ProjectCoordinateProvider needs to cache this kind of data. Likely a LoadingCache that maps from
        // Package Fragment Root to a project coordinate.
        IJavaProject project = root.getJavaProject();
        try {
            for (IClasspathEntry entry : project.getRawClasspath()) {
                boolean jre = entry.getPath().toString().contains("org.eclipse.jdt.launching.JRE_CONTAINER");
                for (IPackageFragmentRoot sub : project.findPackageFragmentRoots(entry)) {
                    if (sub.equals(root)) {
                        DependencyInfo request = new DependencyInfo(location, jre ? JRE : JAR);
                        return resolve(request);
                    }
                }
            }
            return resolve(new DependencyInfo(location, DependencyType.JAR));
        } catch (JavaModelException e) {
            return absent();
        }
    }

    @Override
    public Optional<ProjectCoordinate> resolve(IJavaProject javaProject) {
        File location = getLocation(javaProject).orNull();
        DependencyInfo request = new DependencyInfo(location, DependencyType.PROJECT);
        return resolve(request);
    }

    @Override
    public Optional<ProjectCoordinate> resolve(DependencyInfo info) {
        return mappingProvider.searchForProjectCoordinate(info);
    }

    @Override
    public Optional<BasedTypeName> toBasedName(IType type) {
        ProjectCoordinate base = resolve(type).orNull();
        if (null == base) {
            return absent();
        }
        return of(new BasedTypeName(base, toName(type)));
    }

    @Override
    public ITypeName toName(IType type) {
        return javaElementResolver.toRecType(type);
    }

    @Override
    public Optional<IMethodName> toName(IMethod method) {
        return javaElementResolver.toRecMethod(method);
    }

}
