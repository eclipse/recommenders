/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.snipmatch.model.snipmatchmodel;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.recommenders.snipmatch.ISnippetRepository;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Snippet Repository Configuration</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfiguration#isEnabled <em>Enabled</em>}</li>
 *   <li>{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfiguration#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfiguration#getDescription <em>Description</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnipmatchPackage#getSnippetRepositoryConfiguration()
 * @model abstract="true"
 * @generated
 */
public interface SnippetRepositoryConfiguration extends EObject {
    /**
     * Returns the value of the '<em><b>Enabled</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Enabled</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Enabled</em>' attribute.
     * @see #setEnabled(boolean)
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnipmatchPackage#getSnippetRepositoryConfiguration_Enabled()
     * @model ordered="false"
     * @generated
     */
    boolean isEnabled();

    /**
     * Sets the value of the '{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfiguration#isEnabled <em>Enabled</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Enabled</em>' attribute.
     * @see #isEnabled()
     * @generated
     */
    void setEnabled(boolean value);

    /**
     * Returns the value of the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Name</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Name</em>' attribute.
     * @see #setName(String)
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnipmatchPackage#getSnippetRepositoryConfiguration_Name()
     * @model
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfiguration#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Description</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Description</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Description</em>' attribute.
     * @see #setDescription(String)
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnipmatchPackage#getSnippetRepositoryConfiguration_Description()
     * @model
     * @generated
     */
    String getDescription();

    /**
     * Sets the value of the '{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfiguration#getDescription <em>Description</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Description</em>' attribute.
     * @see #getDescription()
     * @generated
     */
    void setDescription(String value);

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model dataType="org.eclipse.recommenders.snipmatch.model.snipmatchmodel.ESnippetRepository"
     * @generated
     */
    ISnippetRepository createRepositoryInstance();

} // SnippetRepositoryConfiguration
