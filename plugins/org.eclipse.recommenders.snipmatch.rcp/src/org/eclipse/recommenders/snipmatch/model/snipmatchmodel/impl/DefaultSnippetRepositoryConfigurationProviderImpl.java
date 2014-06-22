/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.DefaultSnippetRepositoryConfigurationProvider;
import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnipmatchPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Default Snippet Repository Configuration Provider</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public abstract class DefaultSnippetRepositoryConfigurationProviderImpl extends MinimalEObjectImpl.Container implements
        DefaultSnippetRepositoryConfigurationProvider {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected DefaultSnippetRepositoryConfigurationProviderImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return SnipmatchPackage.Literals.DEFAULT_SNIPPET_REPOSITORY_CONFIGURATION_PROVIDER;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
        switch (operationID) {
        case SnipmatchPackage.DEFAULT_SNIPPET_REPOSITORY_CONFIGURATION_PROVIDER___GET_DEFAULT_CONFIGURATION:
            return getDefaultConfiguration();
        }
        return super.eInvoke(operationID, arguments);
    }

} //DefaultSnippetRepositoryConfigurationProviderImpl
