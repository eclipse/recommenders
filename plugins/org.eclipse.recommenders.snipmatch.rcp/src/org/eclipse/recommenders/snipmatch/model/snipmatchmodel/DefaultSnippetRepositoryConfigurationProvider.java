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
 * A representation of the model object '<em><b>Default Snippet Repository Configuration Provider</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnipmatchPackage#getDefaultSnippetRepositoryConfigurationProvider()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface DefaultSnippetRepositoryConfigurationProvider extends EObject {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model kind="operation" many="false"
     * @generated
     */
    EList<SnippetRepositoryConfiguration> getDefaultConfiguration();

} // DefaultSnippetRepositoryConfigurationProvider
