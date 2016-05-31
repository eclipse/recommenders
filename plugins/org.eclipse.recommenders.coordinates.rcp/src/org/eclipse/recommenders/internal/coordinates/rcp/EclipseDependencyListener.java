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
package org.eclipse.recommenders.internal.coordinates.rcp;

import static com.google.common.base.Optional.fromNullable;
import static org.eclipse.recommenders.coordinates.rcp.DependencyInfos.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.recommenders.coordinates.DependencyInfo;
import org.eclipse.recommenders.coordinates.DependencyType;
import org.eclipse.recommenders.coordinates.IDependencyListener;
import org.eclipse.recommenders.coordinates.rcp.DependencyInfos;
import org.eclipse.recommenders.internal.coordinates.rcp.l10n.LogMessages;
import org.eclipse.recommenders.rcp.JavaModelEvents;
import org.eclipse.recommenders.utils.Logs;

import com.google.common.base.Optional;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

@SuppressWarnings("restriction")
public class EclipseDependencyListener implements IDependencyListener {

    private final HashMultimap<DependencyInfo, DependencyInfo> workspaceDependenciesByProject = HashMultimap.create();
    private final HashMultimap<DependencyInfo, IPackageFragmentRoot> jrePackageFragmentRoots = HashMultimap.create();

    private final Map<IJavaProject, DependencyInfo> projectDependencyInfos = Maps.newHashMap();

    public EclipseDependencyListener(final EventBus bus) {
        bus.register(this);
        parseWorkspaceForDependencies();
    }

    private void parseWorkspaceForDependencies() {
        IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        for (IProject project : projects) {
            try {
                if (project.isOpen() && project.hasNature(JavaCore.NATURE_ID)) {
                    IJavaProject javaProject = JavaCore.create(project);
                    registerDependenciesForJavaProject(javaProject);
                }
            } catch (CoreException e) {
                Logs.log(LogMessages.ERROR_FAILED_TO_REGISTER_PROJECT_DEPENDENCIES, e, project);
            }
        }
    }

    @Subscribe
    public void onEvent(final JavaModelEvents.JavaProjectOpened e) {
        registerDependenciesForJavaProject(e.project);
    }

    @Subscribe
    public void onEvent(final JavaModelEvents.JavaProjectClosed e) {
        deregisterDependenciesForJavaProject(e.project);
    }

    @Subscribe
    public void onEvent(final JavaModelEvents.JarPackageFragmentRootAdded e) {
        registerDependencyForJAR(e.root);
    }

    @Subscribe
    public void onEvent(final JavaModelEvents.JarPackageFragmentRootRemoved e) {
        deregisterDependencyForJAR(e.root);
    }

    private void registerDependenciesForJavaProject(final IJavaProject javaProject) {
        DependencyInfo projectDependencyInfo = getProjectDependencyInfo(javaProject).orNull();
        if (projectDependencyInfo == null) {
            return;
        }

        DependencyInfo jreDependencyInfo = DependencyInfos.createJreDependencyInfo(javaProject).orNull();
        if (jreDependencyInfo == null) {
            return;
        }

        workspaceDependenciesByProject.put(projectDependencyInfo, jreDependencyInfo);
        jrePackageFragmentRoots.putAll(projectDependencyInfo, detectJREPackageFragementRoots(javaProject));
        workspaceDependenciesByProject.putAll(projectDependencyInfo, searchForAllDependenciesOfProject(javaProject));
    }

    private Set<DependencyInfo> searchForAllDependenciesOfProject(final IJavaProject javaProject) {
        DependencyInfo projectDependencyInfo = getProjectDependencyInfo(javaProject).orNull();
        if (projectDependencyInfo == null) {
            Collections.emptySet();
        }

        Set<DependencyInfo> dependencies = Sets.newHashSet();
        Set<IPackageFragmentRoot> jreRoots = jrePackageFragmentRoots.get(projectDependencyInfo);
        try {
            for (final IPackageFragmentRoot packageFragmentRoot : javaProject.getAllPackageFragmentRoots()) {
                if (!jreRoots.contains(packageFragmentRoot) && packageFragmentRoot instanceof JarPackageFragmentRoot) {
                    DependencyInfo dependencyInfo = createJarDependencyInfo(packageFragmentRoot);
                    dependencies.add(dependencyInfo);
                } else if (packageFragmentRoot.getKind() == IPackageFragmentRoot.K_SOURCE
                        && packageFragmentRoot.getJavaProject() != null) {
                    DependencyInfo dependencyInfo = DependencyInfos
                            .createProjectDependencyInfo(packageFragmentRoot.getJavaProject()).orNull();
                    if (dependencyInfo != null) {
                        dependencies.add(dependencyInfo);
                    }
                }
            }
        } catch (JavaModelException e) {
            Logs.log(LogMessages.ERROR_FAILED_TO_SEARCH_FOR_PROJECT_DEPENDENCIES, e, javaProject);
        }

        return dependencies;
    }

    public static Set<IPackageFragmentRoot> detectJREPackageFragementRoots(final IJavaProject javaProject) {
        // Please note that this is merely a heuristic to detect if a Jar is part of the JRE or not:
        // All Jars in the JRE_Container which are not located in the ext folder are considered part of the JRE.
        Set<IPackageFragmentRoot> jreRoots = new HashSet<IPackageFragmentRoot>();
        try {
            for (IClasspathEntry entry : javaProject.getRawClasspath()) {
                if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
                    if (entry.getPath().toString().contains("org.eclipse.jdt.launching.JRE_CONTAINER")) { //$NON-NLS-1$
                        for (IPackageFragmentRoot packageFragmentRoot : javaProject.findPackageFragmentRoots(entry)) {
                            if (!packageFragmentRoot.getPath().toFile().getParentFile().getName().equals("ext")) { //$NON-NLS-1$
                                jreRoots.add(packageFragmentRoot);
                            }
                        }
                    }
                }
            }
        } catch (JavaModelException e) {
            Logs.log(LogMessages.ERROR_FAILED_TO_DETECT_PROJECT_JRE, e, javaProject);
        }
        return jreRoots;
    }

    private void deregisterDependenciesForJavaProject(final IJavaProject javaProject) {
        DependencyInfo projectDependencyInfo = getProjectDependencyInfo(javaProject).orNull();
        if (projectDependencyInfo == null) {
            return;
        }

        workspaceDependenciesByProject.removeAll(projectDependencyInfo);
        jrePackageFragmentRoots.removeAll(projectDependencyInfo);
        synchronized (projectDependencyInfos) {
            projectDependencyInfos.remove(javaProject);
        }
    }

    private void registerDependencyForJAR(final JarPackageFragmentRoot root) {
        IJavaProject javaProject = getJavaProjectForPackageFragmentRoot(root).orNull();
        if (javaProject == null) {
            return;
        }

        DependencyInfo projectDependencyInfo = getProjectDependencyInfo(javaProject).orNull();
        if (projectDependencyInfo == null) {
            return;
        }

        if (!isJREOfProjectIsKnown(projectDependencyInfo)) {
            workspaceDependenciesByProject.removeAll(projectDependencyInfo);
            registerDependenciesForJavaProject(javaProject);
        }
        if (!isPartOfTheJRE(root)) {
            DependencyInfo dependencyInfo = createJarDependencyInfo(root);
            workspaceDependenciesByProject.put(projectDependencyInfo, dependencyInfo);
        }
    }

    private boolean isJREOfProjectIsKnown(final DependencyInfo projectDependencyInfo) {
        for (DependencyInfo dependencyInfo : workspaceDependenciesByProject.get(projectDependencyInfo)) {
            if (dependencyInfo.getType() == DependencyType.JRE) {
                return true;
            }
        }
        return false;
    }

    private boolean isPartOfTheJRE(final IPackageFragmentRoot pfr) {
        IJavaProject javaProject = getJavaProjectForPackageFragmentRoot(pfr).orNull();
        if (javaProject == null) {
            return false;
        }

        DependencyInfo projectDependencyInfo = createProjectDependencyInfo(javaProject).orNull();
        if (projectDependencyInfo == null) {
            return false;
        }

        if (!jrePackageFragmentRoots.containsEntry(projectDependencyInfo, pfr)) {
            return false;
        }

        return true;
    }

    private void deregisterDependencyForJAR(final JarPackageFragmentRoot pfr) {
        IJavaProject javaProject = getJavaProjectForPackageFragmentRoot(pfr).orNull();
        if (javaProject == null) {
            return;
        }

        if (isPartOfTheJRE(pfr)) {
            deregisterJREDependenciesForProject(javaProject);
        } else {
            DependencyInfo dependencyInfo = createJarDependencyInfo(pfr);
            DependencyInfo projectDependencyInfo = getProjectDependencyInfo(javaProject).orNull();
            if (projectDependencyInfo == null) {
                return;
            }

            workspaceDependenciesByProject.remove(projectDependencyInfo, dependencyInfo);
            if (!workspaceDependenciesByProject.containsKey(projectDependencyInfo)) {
                jrePackageFragmentRoots.removeAll(projectDependencyInfo);
            }
        }
    }

    private void deregisterJREDependenciesForProject(final IJavaProject javaProject) {
        DependencyInfo projectDependencyInfo = getProjectDependencyInfo(javaProject).orNull();
        if (projectDependencyInfo == null) {
            return;
        }

        for (DependencyInfo dependencyInfo : workspaceDependenciesByProject.get(projectDependencyInfo)) {
            if (dependencyInfo.getType() == DependencyType.JRE) {
                workspaceDependenciesByProject.remove(projectDependencyInfo, dependencyInfo);
                return;
            }
        }
    }

    private Optional<IJavaProject> getJavaProjectForPackageFragmentRoot(final IPackageFragmentRoot pfr) {
        IJavaProject parent = (IJavaProject) pfr.getAncestor(IJavaElement.JAVA_PROJECT);
        return fromNullable(parent);
    }

    @Override
    public ImmutableSet<DependencyInfo> getDependencies() {
        ImmutableSet.Builder<DependencyInfo> res = ImmutableSet.builder();
        for (DependencyInfo javaProjects : workspaceDependenciesByProject.keySet()) {
            Set<DependencyInfo> dependenciesForProject = workspaceDependenciesByProject.get(javaProjects);
            res.addAll(dependenciesForProject);
        }
        return res.build();
    }

    @Override
    public ImmutableSet<DependencyInfo> getDependenciesForProject(final DependencyInfo project) {
        Set<DependencyInfo> infos = workspaceDependenciesByProject.get(project);
        return ImmutableSet.copyOf(infos);
    }

    private Optional<DependencyInfo> getProjectDependencyInfo(final IJavaProject javaProject) {
        synchronized (projectDependencyInfos) {
            DependencyInfo dependencyInfo = projectDependencyInfos.get(javaProject);
            if (dependencyInfo != null) {
                return Optional.of(dependencyInfo);
            }

            dependencyInfo = createProjectDependencyInfo(javaProject).orNull();
            if (dependencyInfo != null) {
                projectDependencyInfos.put(javaProject, dependencyInfo);
                return Optional.of(dependencyInfo);
            }

            return Optional.absent();
        }
    }

    @Override
    public ImmutableSet<DependencyInfo> getProjects() {
        Set<DependencyInfo> infos = workspaceDependenciesByProject.keySet();
        return ImmutableSet.copyOf(infos);
    }
}
