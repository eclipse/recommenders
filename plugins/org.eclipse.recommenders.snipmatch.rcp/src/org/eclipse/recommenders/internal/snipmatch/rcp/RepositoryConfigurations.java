/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Olav Lenz - initial API and implementation.
 */
package org.eclipse.recommenders.internal.snipmatch.rcp;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.recommenders.injection.InjectionService;
import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnipmatchFactory;
import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfigurations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.name.Names;

public class RepositoryConfigurations {

    private static Logger LOG = LoggerFactory.getLogger(RepositoryConfigurations.class);

    public static final File LOCATION = InjectionService.getInstance().requestAnnotatedInstance(File.class,
            Names.named(SnipmatchRcpModule.REPOSITORY_CONFIGURATION_FILE));

    public static SnippetRepositoryConfigurations loadConfigurations() {
        Resource resource = provideResource();

        SnippetRepositoryConfigurations configurations;
        if (!resource.getContents().isEmpty()) {
            configurations = (SnippetRepositoryConfigurations) resource.getContents().get(0);
        } else {
            configurations = SnipmatchFactory.eINSTANCE.createSnippetRepositoryConfigurations();
        }

        return configurations;
    }

    private static Resource provideResource() {
        Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
        Map<String, Object> m = reg.getExtensionToFactoryMap();
        m.put("snipmatch", new XMIResourceFactoryImpl()); //$NON-NLS-1$

        ResourceSet resSet = new ResourceSetImpl();
        Resource resource = resSet.createResource(URI.createFileURI(LOCATION.getAbsolutePath()));
        return resource;
    }

    public static void storeConfigurations(SnippetRepositoryConfigurations configurations) {
        Resource resource = provideResource();
        resource.getContents().add(configurations);

        try {
            resource.save(Collections.EMPTY_MAP);
        } catch (IOException e) {
            LOG.error("Exception while storing repository configurations.", e); //$NON-NLS-1$
        }
    }

}
