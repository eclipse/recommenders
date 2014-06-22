/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.snipmatch.model.snipmatchmodel;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Snippet Repository Configurations</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfigurations#getRepos <em>Repos</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnipmatchPackage#getSnippetRepositoryConfigurations()
 * @model
 * @generated
 */
public interface SnippetRepositoryConfigurations extends EObject {
    /**
     * Returns the value of the '<em><b>Repos</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfiguration}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Repos</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Repos</em>' containment reference list.
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnipmatchPackage#getSnippetRepositoryConfigurations_Repos()
     * @model containment="true"
     * @generated
     */
    EList<SnippetRepositoryConfiguration> getRepos();

} // SnippetRepositoryConfigurations
