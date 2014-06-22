/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.recommenders.snipmatch.ISnippetRepository;

import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.EclipseGitSnippetRepositoryConfiguration;
import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnipmatchFactory;
import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnipmatchPackage;
import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfiguration;
import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfigurations;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class SnipmatchPackageImpl extends EPackageImpl implements SnipmatchPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass snippetRepositoryConfigurationEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass eclipseGitSnippetRepositoryConfigurationEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass snippetRepositoryConfigurationsEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType eSnippetRepositoryEDataType = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnipmatchPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private SnipmatchPackageImpl() {
        super(eNS_URI, SnipmatchFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
     * 
     * <p>This method is used to initialize {@link SnipmatchPackage#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static SnipmatchPackage init() {
        if (isInited)
            return (SnipmatchPackage) EPackage.Registry.INSTANCE.getEPackage(SnipmatchPackage.eNS_URI);

        // Obtain or create and register package
        SnipmatchPackageImpl theSnipmatchPackage = (SnipmatchPackageImpl) (EPackage.Registry.INSTANCE.get(eNS_URI) instanceof SnipmatchPackageImpl ? EPackage.Registry.INSTANCE
                .get(eNS_URI) : new SnipmatchPackageImpl());

        isInited = true;

        // Create package meta-data objects
        theSnipmatchPackage.createPackageContents();

        // Initialize created meta-data
        theSnipmatchPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theSnipmatchPackage.freeze();

        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(SnipmatchPackage.eNS_URI, theSnipmatchPackage);
        return theSnipmatchPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getSnippetRepositoryConfiguration() {
        return snippetRepositoryConfigurationEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getSnippetRepositoryConfiguration_Enabled() {
        return (EAttribute) snippetRepositoryConfigurationEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getSnippetRepositoryConfiguration_Name() {
        return (EAttribute) snippetRepositoryConfigurationEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getSnippetRepositoryConfiguration_Description() {
        return (EAttribute) snippetRepositoryConfigurationEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EOperation getSnippetRepositoryConfiguration__CreateRepositoryInstance() {
        return snippetRepositoryConfigurationEClass.getEOperations().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getEclipseGitSnippetRepositoryConfiguration() {
        return eclipseGitSnippetRepositoryConfigurationEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getEclipseGitSnippetRepositoryConfiguration_Url() {
        return (EAttribute) eclipseGitSnippetRepositoryConfigurationEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getSnippetRepositoryConfigurations() {
        return snippetRepositoryConfigurationsEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getSnippetRepositoryConfigurations_Repos() {
        return (EReference) snippetRepositoryConfigurationsEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getESnippetRepository() {
        return eSnippetRepositoryEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SnipmatchFactory getSnipmatchFactory() {
        return (SnipmatchFactory) getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void createPackageContents() {
        if (isCreated)
            return;
        isCreated = true;

        // Create classes and their features
        snippetRepositoryConfigurationEClass = createEClass(SNIPPET_REPOSITORY_CONFIGURATION);
        createEAttribute(snippetRepositoryConfigurationEClass, SNIPPET_REPOSITORY_CONFIGURATION__ENABLED);
        createEAttribute(snippetRepositoryConfigurationEClass, SNIPPET_REPOSITORY_CONFIGURATION__NAME);
        createEAttribute(snippetRepositoryConfigurationEClass, SNIPPET_REPOSITORY_CONFIGURATION__DESCRIPTION);
        createEOperation(snippetRepositoryConfigurationEClass,
                SNIPPET_REPOSITORY_CONFIGURATION___CREATE_REPOSITORY_INSTANCE);

        eclipseGitSnippetRepositoryConfigurationEClass = createEClass(ECLIPSE_GIT_SNIPPET_REPOSITORY_CONFIGURATION);
        createEAttribute(eclipseGitSnippetRepositoryConfigurationEClass,
                ECLIPSE_GIT_SNIPPET_REPOSITORY_CONFIGURATION__URL);

        snippetRepositoryConfigurationsEClass = createEClass(SNIPPET_REPOSITORY_CONFIGURATIONS);
        createEReference(snippetRepositoryConfigurationsEClass, SNIPPET_REPOSITORY_CONFIGURATIONS__REPOS);

        // Create data types
        eSnippetRepositoryEDataType = createEDataType(ESNIPPET_REPOSITORY);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void initializePackageContents() {
        if (isInitialized)
            return;
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Create type parameters

        // Set bounds for type parameters

        // Add supertypes to classes
        eclipseGitSnippetRepositoryConfigurationEClass.getESuperTypes().add(this.getSnippetRepositoryConfiguration());

        // Initialize classes, features, and operations; add parameters
        initEClass(snippetRepositoryConfigurationEClass, SnippetRepositoryConfiguration.class,
                "SnippetRepositoryConfiguration", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(
                getSnippetRepositoryConfiguration_Enabled(),
                ecorePackage.getEBoolean(),
                "enabled", null, 0, 1, SnippetRepositoryConfiguration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, !IS_ORDERED); //$NON-NLS-1$
        initEAttribute(
                getSnippetRepositoryConfiguration_Name(),
                ecorePackage.getEString(),
                "name", null, 0, 1, SnippetRepositoryConfiguration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
        initEAttribute(
                getSnippetRepositoryConfiguration_Description(),
                ecorePackage.getEString(),
                "description", null, 0, 1, SnippetRepositoryConfiguration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEOperation(getSnippetRepositoryConfiguration__CreateRepositoryInstance(), this.getESnippetRepository(),
                "createRepositoryInstance", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

        initEClass(eclipseGitSnippetRepositoryConfigurationEClass, EclipseGitSnippetRepositoryConfiguration.class,
                "EclipseGitSnippetRepositoryConfiguration", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEAttribute(
                getEclipseGitSnippetRepositoryConfiguration_Url(),
                ecorePackage.getEString(),
                "url", null, 0, 1, EclipseGitSnippetRepositoryConfiguration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        initEClass(snippetRepositoryConfigurationsEClass, SnippetRepositoryConfigurations.class,
                "SnippetRepositoryConfigurations", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
        initEReference(
                getSnippetRepositoryConfigurations_Repos(),
                this.getSnippetRepositoryConfiguration(),
                null,
                "repos", null, 0, -1, SnippetRepositoryConfigurations.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

        // Initialize data types
        initEDataType(eSnippetRepositoryEDataType, ISnippetRepository.class,
                "ESnippetRepository", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$

        // Create resource
        createResource(eNS_URI);
    }

} //SnipmatchPackageImpl
