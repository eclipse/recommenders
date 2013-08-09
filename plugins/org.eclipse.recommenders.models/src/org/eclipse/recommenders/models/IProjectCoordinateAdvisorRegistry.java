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

import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * The Mapping interface provide the functionality for the mapping between IDependencyInfo and ProjectCoordinate
 */
public interface IProjectCoordinateAdvisorRegistry {

    /**
     * Returns all advisors this registry is configured with.
     */
    ImmutableList<IProjectCoordinateAdvisor> getAdvisors();

    /**
     * adds an advisor to the list of currently configured advisors.
     */
    void addAdvisor(IProjectCoordinateAdvisor advisor);

    /**
     * Sets a advisors for this registry. Overwrites any previously configured advisors.
     */
    void setAdvisors(List<IProjectCoordinateAdvisor> advisors);

}
