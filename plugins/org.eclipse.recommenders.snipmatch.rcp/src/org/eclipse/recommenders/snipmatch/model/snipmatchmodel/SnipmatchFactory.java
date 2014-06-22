/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.snipmatch.model.snipmatchmodel;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnipmatchPackage
 * @generated
 */
public interface SnipmatchFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    SnipmatchFactory eINSTANCE = org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl.SnipmatchFactoryImpl
            .init();

    /**
     * Returns a new object of class '<em>Eclipse Git Snippet Repository Configuration</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Eclipse Git Snippet Repository Configuration</em>'.
     * @generated
     */
    EclipseGitSnippetRepositoryConfiguration createEclipseGitSnippetRepositoryConfiguration();

    /**
     * Returns a new object of class '<em>Snippet Repository Configurations</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Snippet Repository Configurations</em>'.
     * @generated
     */
    SnippetRepositoryConfigurations createSnippetRepositoryConfigurations();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    SnipmatchPackage getSnipmatchPackage();

} //SnipmatchFactory
