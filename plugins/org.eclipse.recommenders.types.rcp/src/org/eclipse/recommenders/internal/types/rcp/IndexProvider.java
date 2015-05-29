/**
 * Copyright (c) 2015 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 *    Johannes Dorn - Refactoring
 */
package org.eclipse.recommenders.internal.types.rcp;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

public class IndexProvider implements IIndexProvider {

    private final Map<IJavaProject, IProjectTypesIndex> indexes = Maps.newHashMap();

    @Override
    public synchronized Optional<IProjectTypesIndex> findIndex(IJavaProject project) {
        return Optional.fromNullable(indexes.get(project));
    }

    @Override
    public synchronized Optional<IProjectTypesIndex> findOrCreateIndex(IJavaProject project) {
        IProjectTypesIndex index = findIndex(project).orNull();
        if (index == null) {
            index = new ProjectTypesIndex(project, computeIndexDir(project));
            indexes.put(project, index);
        }
        return Optional.of(index);
    }

    private static File computeIndexDir(IJavaProject project) {
        Bundle bundle = FrameworkUtil.getBundle(IndexProvider.class);
        File location = Platform.getStateLocation(bundle).toFile();
        String mangledProjectName = project.getElementName().replaceAll("\\W", "_"); //$NON-NLS-1$ //$NON-NLS-2$
        File indexDir = new File(new File(location, Constants.INDEX_DIR), mangledProjectName);
        return indexDir;
    }

    @Override
    public Collection<IProjectTypesIndex> getAllIndexes() {
        return indexes.values();
    }

    @Override
    public void close() throws IOException {
        for (IProjectTypesIndex index : indexes.values()) {
            index.close();
        }
    }

}
