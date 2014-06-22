/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.snipmatch.model.snipmatchmodel;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnipmatchFactory
 * @model kind="package"
 * @generated
 */
public interface SnipmatchPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "snipmatchmodel";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "org.eclips.recommenders.snipmatch.rcp";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "org.eclipse.recommenders";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    SnipmatchPackage eINSTANCE = org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl.SnipmatchPackageImpl
            .init();

    /**
     * The meta object id for the '{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl.SnippetRepositoryConfigurationImpl <em>Snippet Repository Configuration</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl.SnippetRepositoryConfigurationImpl
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl.SnipmatchPackageImpl#getSnippetRepositoryConfiguration()
     * @generated
     */
    int SNIPPET_REPOSITORY_CONFIGURATION = 0;

    /**
     * The feature id for the '<em><b>Enabled</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SNIPPET_REPOSITORY_CONFIGURATION__ENABLED = 0;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SNIPPET_REPOSITORY_CONFIGURATION__NAME = 1;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SNIPPET_REPOSITORY_CONFIGURATION__DESCRIPTION = 2;

    /**
     * The number of structural features of the '<em>Snippet Repository Configuration</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SNIPPET_REPOSITORY_CONFIGURATION_FEATURE_COUNT = 3;

    /**
     * The operation id for the '<em>Create Repository Instance</em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SNIPPET_REPOSITORY_CONFIGURATION___CREATE_REPOSITORY_INSTANCE = 0;

    /**
     * The number of operations of the '<em>Snippet Repository Configuration</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SNIPPET_REPOSITORY_CONFIGURATION_OPERATION_COUNT = 1;

    /**
     * The meta object id for the '{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl.EclipseGitSnippetRepositoryConfigurationImpl <em>Eclipse Git Snippet Repository Configuration</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl.EclipseGitSnippetRepositoryConfigurationImpl
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl.SnipmatchPackageImpl#getEclipseGitSnippetRepositoryConfiguration()
     * @generated
     */
    int ECLIPSE_GIT_SNIPPET_REPOSITORY_CONFIGURATION = 1;

    /**
     * The feature id for the '<em><b>Enabled</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ECLIPSE_GIT_SNIPPET_REPOSITORY_CONFIGURATION__ENABLED = SNIPPET_REPOSITORY_CONFIGURATION__ENABLED;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ECLIPSE_GIT_SNIPPET_REPOSITORY_CONFIGURATION__NAME = SNIPPET_REPOSITORY_CONFIGURATION__NAME;

    /**
     * The feature id for the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ECLIPSE_GIT_SNIPPET_REPOSITORY_CONFIGURATION__DESCRIPTION = SNIPPET_REPOSITORY_CONFIGURATION__DESCRIPTION;

    /**
     * The feature id for the '<em><b>Url</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ECLIPSE_GIT_SNIPPET_REPOSITORY_CONFIGURATION__URL = SNIPPET_REPOSITORY_CONFIGURATION_FEATURE_COUNT + 0;

    /**
     * The number of structural features of the '<em>Eclipse Git Snippet Repository Configuration</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ECLIPSE_GIT_SNIPPET_REPOSITORY_CONFIGURATION_FEATURE_COUNT = SNIPPET_REPOSITORY_CONFIGURATION_FEATURE_COUNT + 1;

    /**
     * The operation id for the '<em>Create Repository Instance</em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ECLIPSE_GIT_SNIPPET_REPOSITORY_CONFIGURATION___CREATE_REPOSITORY_INSTANCE = SNIPPET_REPOSITORY_CONFIGURATION___CREATE_REPOSITORY_INSTANCE;

    /**
     * The number of operations of the '<em>Eclipse Git Snippet Repository Configuration</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ECLIPSE_GIT_SNIPPET_REPOSITORY_CONFIGURATION_OPERATION_COUNT = SNIPPET_REPOSITORY_CONFIGURATION_OPERATION_COUNT + 0;

    /**
     * The meta object id for the '{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl.SnippetRepositoryConfigurationsImpl <em>Snippet Repository Configurations</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl.SnippetRepositoryConfigurationsImpl
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl.SnipmatchPackageImpl#getSnippetRepositoryConfigurations()
     * @generated
     */
    int SNIPPET_REPOSITORY_CONFIGURATIONS = 2;

    /**
     * The feature id for the '<em><b>Repos</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SNIPPET_REPOSITORY_CONFIGURATIONS__REPOS = 0;

    /**
     * The number of structural features of the '<em>Snippet Repository Configurations</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SNIPPET_REPOSITORY_CONFIGURATIONS_FEATURE_COUNT = 1;

    /**
     * The number of operations of the '<em>Snippet Repository Configurations</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SNIPPET_REPOSITORY_CONFIGURATIONS_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '<em>ESnippet Repository</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.recommenders.snipmatch.ISnippetRepository
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl.SnipmatchPackageImpl#getESnippetRepository()
     * @generated
     */
    int ESNIPPET_REPOSITORY = 3;

    /**
     * Returns the meta object for class '{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfiguration <em>Snippet Repository Configuration</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Snippet Repository Configuration</em>'.
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfiguration
     * @generated
     */
    EClass getSnippetRepositoryConfiguration();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfiguration#isEnabled <em>Enabled</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Enabled</em>'.
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfiguration#isEnabled()
     * @see #getSnippetRepositoryConfiguration()
     * @generated
     */
    EAttribute getSnippetRepositoryConfiguration_Enabled();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfiguration#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfiguration#getName()
     * @see #getSnippetRepositoryConfiguration()
     * @generated
     */
    EAttribute getSnippetRepositoryConfiguration_Name();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfiguration#getDescription <em>Description</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Description</em>'.
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfiguration#getDescription()
     * @see #getSnippetRepositoryConfiguration()
     * @generated
     */
    EAttribute getSnippetRepositoryConfiguration_Description();

    /**
     * Returns the meta object for the '{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfiguration#createRepositoryInstance() <em>Create Repository Instance</em>}' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the '<em>Create Repository Instance</em>' operation.
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfiguration#createRepositoryInstance()
     * @generated
     */
    EOperation getSnippetRepositoryConfiguration__CreateRepositoryInstance();

    /**
     * Returns the meta object for class '{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.EclipseGitSnippetRepositoryConfiguration <em>Eclipse Git Snippet Repository Configuration</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Eclipse Git Snippet Repository Configuration</em>'.
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.EclipseGitSnippetRepositoryConfiguration
     * @generated
     */
    EClass getEclipseGitSnippetRepositoryConfiguration();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.EclipseGitSnippetRepositoryConfiguration#getUrl <em>Url</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Url</em>'.
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.EclipseGitSnippetRepositoryConfiguration#getUrl()
     * @see #getEclipseGitSnippetRepositoryConfiguration()
     * @generated
     */
    EAttribute getEclipseGitSnippetRepositoryConfiguration_Url();

    /**
     * Returns the meta object for class '{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfigurations <em>Snippet Repository Configurations</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Snippet Repository Configurations</em>'.
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfigurations
     * @generated
     */
    EClass getSnippetRepositoryConfigurations();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfigurations#getRepos <em>Repos</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Repos</em>'.
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfigurations#getRepos()
     * @see #getSnippetRepositoryConfigurations()
     * @generated
     */
    EReference getSnippetRepositoryConfigurations_Repos();

    /**
     * Returns the meta object for data type '{@link org.eclipse.recommenders.snipmatch.ISnippetRepository <em>ESnippet Repository</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>ESnippet Repository</em>'.
     * @see org.eclipse.recommenders.snipmatch.ISnippetRepository
     * @model instanceClass="org.eclipse.recommenders.snipmatch.ISnippetRepository"
     * @generated
     */
    EDataType getESnippetRepository();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    SnipmatchFactory getSnipmatchFactory();

    /**
     * <!-- begin-user-doc -->
     * Defines literals for the meta objects that represent
     * <ul>
     *   <li>each class,</li>
     *   <li>each feature of each class,</li>
     *   <li>each operation of each class,</li>
     *   <li>each enum,</li>
     *   <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * @generated
     */
    interface Literals {
        /**
         * The meta object literal for the '{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl.SnippetRepositoryConfigurationImpl <em>Snippet Repository Configuration</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl.SnippetRepositoryConfigurationImpl
         * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl.SnipmatchPackageImpl#getSnippetRepositoryConfiguration()
         * @generated
         */
        EClass SNIPPET_REPOSITORY_CONFIGURATION = eINSTANCE.getSnippetRepositoryConfiguration();

        /**
         * The meta object literal for the '<em><b>Enabled</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute SNIPPET_REPOSITORY_CONFIGURATION__ENABLED = eINSTANCE.getSnippetRepositoryConfiguration_Enabled();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute SNIPPET_REPOSITORY_CONFIGURATION__NAME = eINSTANCE.getSnippetRepositoryConfiguration_Name();

        /**
         * The meta object literal for the '<em><b>Description</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute SNIPPET_REPOSITORY_CONFIGURATION__DESCRIPTION = eINSTANCE
                .getSnippetRepositoryConfiguration_Description();

        /**
         * The meta object literal for the '<em><b>Create Repository Instance</b></em>' operation.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EOperation SNIPPET_REPOSITORY_CONFIGURATION___CREATE_REPOSITORY_INSTANCE = eINSTANCE
                .getSnippetRepositoryConfiguration__CreateRepositoryInstance();

        /**
         * The meta object literal for the '{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl.EclipseGitSnippetRepositoryConfigurationImpl <em>Eclipse Git Snippet Repository Configuration</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl.EclipseGitSnippetRepositoryConfigurationImpl
         * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl.SnipmatchPackageImpl#getEclipseGitSnippetRepositoryConfiguration()
         * @generated
         */
        EClass ECLIPSE_GIT_SNIPPET_REPOSITORY_CONFIGURATION = eINSTANCE.getEclipseGitSnippetRepositoryConfiguration();

        /**
         * The meta object literal for the '<em><b>Url</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ECLIPSE_GIT_SNIPPET_REPOSITORY_CONFIGURATION__URL = eINSTANCE
                .getEclipseGitSnippetRepositoryConfiguration_Url();

        /**
         * The meta object literal for the '{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl.SnippetRepositoryConfigurationsImpl <em>Snippet Repository Configurations</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl.SnippetRepositoryConfigurationsImpl
         * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl.SnipmatchPackageImpl#getSnippetRepositoryConfigurations()
         * @generated
         */
        EClass SNIPPET_REPOSITORY_CONFIGURATIONS = eINSTANCE.getSnippetRepositoryConfigurations();

        /**
         * The meta object literal for the '<em><b>Repos</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference SNIPPET_REPOSITORY_CONFIGURATIONS__REPOS = eINSTANCE.getSnippetRepositoryConfigurations_Repos();

        /**
         * The meta object literal for the '<em>ESnippet Repository</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.recommenders.snipmatch.ISnippetRepository
         * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl.SnipmatchPackageImpl#getESnippetRepository()
         * @generated
         */
        EDataType ESNIPPET_REPOSITORY = eINSTANCE.getESnippetRepository();

    }

} //SnipmatchPackage
