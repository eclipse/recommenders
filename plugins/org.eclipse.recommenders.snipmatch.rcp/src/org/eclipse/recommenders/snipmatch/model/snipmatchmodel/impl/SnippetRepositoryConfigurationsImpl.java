/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl;

import java.util.Collection;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;
import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnipmatchPackage;
import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfiguration;
import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfigurations;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Snippet Repository Configurations</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl.SnippetRepositoryConfigurationsImpl#getRepos <em>Repos</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SnippetRepositoryConfigurationsImpl extends MinimalEObjectImpl.Container implements
        SnippetRepositoryConfigurations {
    /**
     * The cached value of the '{@link #getRepos() <em>Repos</em>}' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRepos()
     * @generated
     * @ordered
     */
    protected EList<SnippetRepositoryConfiguration> repos;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected SnippetRepositoryConfigurationsImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return SnipmatchPackage.Literals.SNIPPET_REPOSITORY_CONFIGURATIONS;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<SnippetRepositoryConfiguration> getRepos() {
        if (repos == null) {
            repos = new EObjectContainmentEList<SnippetRepositoryConfiguration>(SnippetRepositoryConfiguration.class,
                    this, SnipmatchPackage.SNIPPET_REPOSITORY_CONFIGURATIONS__REPOS);
        }
        return repos;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
        switch (featureID) {
        case SnipmatchPackage.SNIPPET_REPOSITORY_CONFIGURATIONS__REPOS:
            return ((InternalEList<?>) getRepos()).basicRemove(otherEnd, msgs);
        }
        return super.eInverseRemove(otherEnd, featureID, msgs);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case SnipmatchPackage.SNIPPET_REPOSITORY_CONFIGURATIONS__REPOS:
            return getRepos();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
        case SnipmatchPackage.SNIPPET_REPOSITORY_CONFIGURATIONS__REPOS:
            getRepos().clear();
            getRepos().addAll((Collection<? extends SnippetRepositoryConfiguration>) newValue);
            return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
        case SnipmatchPackage.SNIPPET_REPOSITORY_CONFIGURATIONS__REPOS:
            getRepos().clear();
            return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
        case SnipmatchPackage.SNIPPET_REPOSITORY_CONFIGURATIONS__REPOS:
            return repos != null && !repos.isEmpty();
        }
        return super.eIsSet(featureID);
    }

} //SnippetRepositoryConfigurationsImpl
