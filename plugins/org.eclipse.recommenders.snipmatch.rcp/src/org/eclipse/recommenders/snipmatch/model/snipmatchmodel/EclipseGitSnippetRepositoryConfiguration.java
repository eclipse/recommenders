/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.snipmatch.model.snipmatchmodel;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Eclipse Git Snippet Repository Configuration</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.EclipseGitSnippetRepositoryConfiguration#getUrl <em>Url</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnipmatchPackage#getEclipseGitSnippetRepositoryConfiguration()
 * @model
 * @generated
 */
public interface EclipseGitSnippetRepositoryConfiguration extends SnippetRepositoryConfiguration {
    /**
     * Returns the value of the '<em><b>Url</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Url</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Url</em>' attribute.
     * @see #setUrl(String)
     * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnipmatchPackage#getEclipseGitSnippetRepositoryConfiguration_Url()
     * @model
     * @generated
     */
    String getUrl();

    /**
     * Sets the value of the '{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.EclipseGitSnippetRepositoryConfiguration#getUrl <em>Url</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Url</em>' attribute.
     * @see #getUrl()
     * @generated
     */
    void setUrl(String value);

} // EclipseGitSnippetRepositoryConfiguration
