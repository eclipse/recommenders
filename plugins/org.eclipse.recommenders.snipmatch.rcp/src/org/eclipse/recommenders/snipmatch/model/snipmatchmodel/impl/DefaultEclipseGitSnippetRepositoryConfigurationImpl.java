/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.recommenders.internal.snipmatch.rcp.Messages;
import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.DefaultEclipseGitSnippetRepositoryConfiguration;
import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.EclipseGitSnippetRepositoryConfiguration;
import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnipmatchFactory;
import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnipmatchPackage;
import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfiguration;

/**
 * <!-- begin-user-doc --> An implementation of the model object '
 * <em><b>Default Eclipse Git Snippet Repository Configuration</b></em>'. <!-- end-user-doc -->
 * <p>
 * </p>
 * 
 * @generated
 */
public class DefaultEclipseGitSnippetRepositoryConfigurationImpl extends MinimalEObjectImpl.Container implements
        DefaultEclipseGitSnippetRepositoryConfiguration {
    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    protected DefaultEclipseGitSnippetRepositoryConfigurationImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return SnipmatchPackage.Literals.DEFAULT_ECLIPSE_GIT_SNIPPET_REPOSITORY_CONFIGURATION;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public EList<SnippetRepositoryConfiguration> getDefaultConfiguration() {
        BasicEList<SnippetRepositoryConfiguration> result = new BasicEList<SnippetRepositoryConfiguration>();

        EclipseGitSnippetRepositoryConfiguration configuration = SnipmatchFactory.eINSTANCE
                .createEclipseGitSnippetRepositoryConfiguration();
        configuration.setName(Messages.DEFAULT_REPO_NAME);
        configuration.setDescription(Messages.ECLIPSE_GIT_SNIPPET_REPOSITORY_CONFIGURATION_DESCRIPTION);
        configuration.setEnabled(true);
        configuration.setUrl("https://git.eclipse.org/r/recommenders/org.eclipse.recommenders.snipmatch.snippets"); //$NON-NLS-1$

        result.add(configuration);
        return result;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated
     */
    @Override
    public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
        switch (operationID) {
        case SnipmatchPackage.DEFAULT_ECLIPSE_GIT_SNIPPET_REPOSITORY_CONFIGURATION___GET_DEFAULT_CONFIGURATION:
            return getDefaultConfiguration();
        }
        return super.eInvoke(operationID, arguments);
    }

} // DefaultEclipseGitSnippetRepositoryConfigurationImpl
