/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.snipmatch.model.snipmatchmodel.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnipmatchPackage
 * @generated
 */
public class SnipmatchAdapterFactory extends AdapterFactoryImpl {
    /**
     * The cached model package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static SnipmatchPackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SnipmatchAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = SnipmatchPackage.eINSTANCE;
        }
    }

    /**
     * Returns whether this factory is applicable for the type of the object.
     * <!-- begin-user-doc -->
     * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
     * <!-- end-user-doc -->
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
    @Override
    public boolean isFactoryForType(Object object) {
        if (object == modelPackage) {
            return true;
        }
        if (object instanceof EObject) {
            return ((EObject) object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

    /**
     * The switch that delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected SnipmatchSwitch<Adapter> modelSwitch = new SnipmatchSwitch<Adapter>() {
        @Override
        public Adapter caseSnippetRepositoryConfiguration(SnippetRepositoryConfiguration object) {
            return createSnippetRepositoryConfigurationAdapter();
        }

        @Override
        public Adapter caseEclipseGitSnippetRepositoryConfiguration(EclipseGitSnippetRepositoryConfiguration object) {
            return createEclipseGitSnippetRepositoryConfigurationAdapter();
        }

        @Override
        public Adapter caseSnippetRepositoryConfigurations(SnippetRepositoryConfigurations object) {
            return createSnippetRepositoryConfigurationsAdapter();
        }

        @Override
        public Adapter caseDefaultSnippetRepositoryConfigurationProvider(
                DefaultSnippetRepositoryConfigurationProvider object) {
            return createDefaultSnippetRepositoryConfigurationProviderAdapter();
        }

        @Override
        public Adapter caseDefaultEclipseGitSnippetRepositoryConfiguration(
                DefaultEclipseGitSnippetRepositoryConfiguration object) {
            return createDefaultEclipseGitSnippetRepositoryConfigurationAdapter();
        }

        @Override
        public Adapter defaultCase(EObject object) {
            return createEObjectAdapter();
        }
    };

    /**
     * Creates an adapter for the <code>target</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
    @Override
    public Adapter createAdapter(Notifier target) {
        return modelSwitch.doSwitch((EObject) target);
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfiguration <em>Snippet Repository Configuration</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfiguration
     * @generated
     */
    public Adapter createSnippetRepositoryConfigurationAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.EclipseGitSnippetRepositoryConfiguration <em>Eclipse Git Snippet Repository Configuration</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.EclipseGitSnippetRepositoryConfiguration
     * @generated
     */
    public Adapter createEclipseGitSnippetRepositoryConfigurationAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfigurations <em>Snippet Repository Configurations</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfigurations
     * @generated
     */
    public Adapter createSnippetRepositoryConfigurationsAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.DefaultSnippetRepositoryConfigurationProvider <em>Default Snippet Repository Configuration Provider</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.DefaultSnippetRepositoryConfigurationProvider
     * @generated
     */
    public Adapter createDefaultSnippetRepositoryConfigurationProviderAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.DefaultEclipseGitSnippetRepositoryConfiguration <em>Default Eclipse Git Snippet Repository Configuration</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.DefaultEclipseGitSnippetRepositoryConfiguration
     * @generated
     */
    public Adapter createDefaultEclipseGitSnippetRepositoryConfigurationAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for the default case.
     * <!-- begin-user-doc -->
     * This default implementation returns null.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @generated
     */
    public Adapter createEObjectAdapter() {
        return null;
    }

} //SnipmatchAdapterFactory
