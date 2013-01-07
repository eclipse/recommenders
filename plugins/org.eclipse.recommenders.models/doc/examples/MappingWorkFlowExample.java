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
package examples;

import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.models.mapping.IDependencyInfo;
import org.eclipse.recommenders.models.mapping.IMappingProvider;
import org.eclipse.recommenders.models.mapping.IMappingStrategy;
import org.eclipse.recommenders.models.mapping.impl.MavenPomPropertiesStrategy;

import com.google.common.base.Optional;

public class MappingWorkFlowExample {

    public static void useOfMapping(IMappingProvider mapping) {
        IDependencyInfo ed = null;

        mapping.addStrategy(new MavenPomPropertiesStrategy());

        IMappingStrategy mappingStrategy = mapping;

        Optional<ProjectCoordinate> optionalProjectCoordinate = mappingStrategy.searchForProjectCoordinate(ed);

        ProjectCoordinate projectCoordinate = null;
        if (optionalProjectCoordinate.isPresent()) {
            projectCoordinate = optionalProjectCoordinate.get();
        }
    }

}
