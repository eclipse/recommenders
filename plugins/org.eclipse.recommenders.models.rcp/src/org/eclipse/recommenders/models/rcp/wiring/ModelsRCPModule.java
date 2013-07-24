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
package org.eclipse.recommenders.models.rcp.wiring;

import java.io.File;
import java.io.IOException;

import javax.inject.Singleton;

import org.eclipse.core.runtime.Platform;
import org.eclipse.recommenders.internal.rcp.wiring.RecommendersModule.ModelRepositoryIndexLocation;
import org.eclipse.recommenders.models.IModelRepository;
import org.eclipse.recommenders.models.dependencies.IMappingProvider;
import org.eclipse.recommenders.models.dependencies.impl.FingerprintStrategy;
import org.eclipse.recommenders.models.dependencies.impl.JREExecutionEnvironmentStrategy;
import org.eclipse.recommenders.models.dependencies.impl.JREReleaseFileStrategy;
import org.eclipse.recommenders.models.dependencies.impl.MappingProvider;
import org.eclipse.recommenders.models.dependencies.impl.MavenPomPropertiesStrategy;
import org.eclipse.recommenders.models.dependencies.impl.OsgiManifestStrategy;
import org.eclipse.recommenders.models.dependencies.impl.SimpleIndexSearcher;
import org.eclipse.recommenders.models.dependencies.rcp.EclipseDependencyListener;
import org.eclipse.recommenders.models.rcp.EclipseModelRepository;
import org.eclipse.recommenders.models.rcp.ProjectCoordinateProvider;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.google.common.eventbus.EventBus;
import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.name.Names;

public class ModelsRCPModule extends AbstractModule implements Module {

    public static final String IDENTIFIED_PACKAGE_FRAGMENT_ROOTS = "IDENTIFIED_PACKAGE_FRAGMENT_ROOTS";

    @Override
    protected void configure() {
        Bundle bundle = FrameworkUtil.getBundle(getClass());
        File stateLocation = Platform.getStateLocation(bundle).toFile();

        File cachePersistence = new File(stateLocation, "cache/identified-package-fragment-roots.json");
        try {
            Files.createParentDirs(cachePersistence);
        } catch (IOException e) {
            e.printStackTrace();
        }

        bind(File.class).annotatedWith(Names.named(IDENTIFIED_PACKAGE_FRAGMENT_ROOTS)).toInstance(cachePersistence);
        bind(ProjectCoordinateProvider.class).in(Scopes.SINGLETON);
        bind(IModelRepository.class).to(EclipseModelRepository.class).in(Scopes.SINGLETON);
    }

    @Singleton
    @Provides
    protected EclipseDependencyListener provideMappingProvider(EventBus bus) {
        return new EclipseDependencyListener(bus);
    }

    @Singleton
    @Provides
    public SimpleIndexSearcher provideSearcher(@ModelRepositoryIndexLocation File localRepositoryFile) {
        return new SimpleIndexSearcher(localRepositoryFile);
    }

    @Singleton
    @Provides
    protected IMappingProvider provideMappingProvider(SimpleIndexSearcher searcher) {
        MappingProvider mappingProvider = new MappingProvider();
        mappingProvider.addStrategy(new MavenPomPropertiesStrategy());
        mappingProvider.addStrategy(new JREExecutionEnvironmentStrategy());
        mappingProvider.addStrategy(new JREReleaseFileStrategy());
        mappingProvider.addStrategy(new OsgiManifestStrategy());
        mappingProvider.addStrategy(new FingerprintStrategy(searcher));
        return mappingProvider;
    }

}
