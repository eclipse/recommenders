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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.models.mapping.DependencyType;
import org.eclipse.recommenders.models.mapping.IDependencyInfo;
import org.eclipse.recommenders.models.mapping.IMappingProvider;
import org.eclipse.recommenders.models.mapping.IMappingStrategy;

import com.google.common.base.Optional;

public class MappingProvider implements IMappingProvider {

    List<IMappingStrategy> strategies = new ArrayList<IMappingStrategy>();

    // TODO: Store and reload mapping when IDE close/start (via .JSON)

    @Override
    public List<IMappingStrategy> getStrategies() {
        return strategies;
    }

    @Override
    public void addStrategy(IMappingStrategy strategy) {
        strategies.add(strategy);
    }

    @Override
    public Optional<ProjectCoordinate> extractProjectCoordinate(IDependencyInfo dependencyInfo) {
        // TODO: Check if mapping exists in cache is missing right now
        for (IMappingStrategy strategy : strategies) {
            Optional<ProjectCoordinate> optionalProjectCoordinate = strategy.extractProjectCoordinate(dependencyInfo);
            if (optionalProjectCoordinate.isPresent()) {
                return optionalProjectCoordinate;
            }
        }
        return Optional.absent();
    }

    @Override
    public boolean isApplicable(DependencyType dependencyTyp) {
        for (IMappingStrategy mappingStrategy : strategies) {
            if (mappingStrategy.isApplicable(dependencyTyp)) {
                return true;
            }
        }
        return false;
    }

}
