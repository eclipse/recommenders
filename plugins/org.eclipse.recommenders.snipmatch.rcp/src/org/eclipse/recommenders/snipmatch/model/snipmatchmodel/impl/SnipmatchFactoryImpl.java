/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.recommenders.snipmatch.ISnippetRepository;

import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.*;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class SnipmatchFactoryImpl extends EFactoryImpl implements SnipmatchFactory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static SnipmatchFactory init() {
        try {
            SnipmatchFactory theSnipmatchFactory = (SnipmatchFactory) EPackage.Registry.INSTANCE
                    .getEFactory(SnipmatchPackage.eNS_URI);
            if (theSnipmatchFactory != null) {
                return theSnipmatchFactory;
            }
        } catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new SnipmatchFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SnipmatchFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
        case SnipmatchPackage.ECLIPSE_GIT_SNIPPET_REPOSITORY_CONFIGURATION:
            return createEclipseGitSnippetRepositoryConfiguration();
        case SnipmatchPackage.SNIPPET_REPOSITORY_CONFIGURATIONS:
            return createSnippetRepositoryConfigurations();
        case SnipmatchPackage.DEFAULT_ECLIPSE_GIT_SNIPPET_REPOSITORY_CONFIGURATION:
            return createDefaultEclipseGitSnippetRepositoryConfiguration();
        default:
            throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object createFromString(EDataType eDataType, String initialValue) {
        switch (eDataType.getClassifierID()) {
        case SnipmatchPackage.ESNIPPET_REPOSITORY:
            return createESnippetRepositoryFromString(eDataType, initialValue);
        default:
            throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String convertToString(EDataType eDataType, Object instanceValue) {
        switch (eDataType.getClassifierID()) {
        case SnipmatchPackage.ESNIPPET_REPOSITORY:
            return convertESnippetRepositoryToString(eDataType, instanceValue);
        default:
            throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EclipseGitSnippetRepositoryConfiguration createEclipseGitSnippetRepositoryConfiguration() {
        EclipseGitSnippetRepositoryConfigurationImpl eclipseGitSnippetRepositoryConfiguration = new EclipseGitSnippetRepositoryConfigurationImpl();
        return eclipseGitSnippetRepositoryConfiguration;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SnippetRepositoryConfigurations createSnippetRepositoryConfigurations() {
        SnippetRepositoryConfigurationsImpl snippetRepositoryConfigurations = new SnippetRepositoryConfigurationsImpl();
        return snippetRepositoryConfigurations;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DefaultEclipseGitSnippetRepositoryConfiguration createDefaultEclipseGitSnippetRepositoryConfiguration() {
        DefaultEclipseGitSnippetRepositoryConfigurationImpl defaultEclipseGitSnippetRepositoryConfiguration = new DefaultEclipseGitSnippetRepositoryConfigurationImpl();
        return defaultEclipseGitSnippetRepositoryConfiguration;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ISnippetRepository createESnippetRepositoryFromString(EDataType eDataType, String initialValue) {
        return (ISnippetRepository) super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertESnippetRepositoryToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SnipmatchPackage getSnipmatchPackage() {
        return (SnipmatchPackage) getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static SnipmatchPackage getPackage() {
        return SnipmatchPackage.eINSTANCE;
    }

} //SnipmatchFactoryImpl
