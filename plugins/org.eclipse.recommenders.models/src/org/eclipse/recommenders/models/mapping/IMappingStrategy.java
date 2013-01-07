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
package org.eclipse.recommenders.models.mapping;

import org.eclipse.recommenders.models.ProjectCoordinate;

import com.google.common.base.Optional;

/**
 * Strategy for extract the ProjectCoordinate of a element.
 */
public interface IMappingStrategy {

    public Optional<ProjectCoordinate> searchForProjectCoordinate(IDependencyInfo dependencyInfo);

    public boolean isApplicable(DependencyType dependencyType);

}
