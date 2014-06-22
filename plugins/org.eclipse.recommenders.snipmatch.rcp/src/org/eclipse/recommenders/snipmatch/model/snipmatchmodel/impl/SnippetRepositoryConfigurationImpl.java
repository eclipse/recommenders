/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.recommenders.snipmatch.ISnippetRepository;

import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnipmatchPackage;
import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnippetRepositoryConfiguration;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Snippet Repository Configuration</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl.SnippetRepositoryConfigurationImpl#isEnabled <em>Enabled</em>}</li>
 *   <li>{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl.SnippetRepositoryConfigurationImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.impl.SnippetRepositoryConfigurationImpl#getDescription <em>Description</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class SnippetRepositoryConfigurationImpl extends MinimalEObjectImpl.Container implements
        SnippetRepositoryConfiguration {
    /**
     * The default value of the '{@link #isEnabled() <em>Enabled</em>}' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see #isEnabled()
     * @generated
     * @ordered
     */
    protected static final boolean ENABLED_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isEnabled() <em>Enabled</em>}' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see #isEnabled()
     * @generated
     * @ordered
     */
    protected boolean enabled = ENABLED_EDEFAULT;

    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @see #getName()
     * @generated
     * @ordered
     */
    protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     * 
     * @see #getName()
     * @generated
     * @ordered
     */
    protected String name = NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getDescription() <em>Description</em>}' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see #getDescription()
     * @generated
     * @ordered
     */
    protected static final String DESCRIPTION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getDescription() <em>Description</em>}' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see #getDescription()
     * @generated
     * @ordered
     */
    protected String description = DESCRIPTION_EDEFAULT;

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected SnippetRepositoryConfigurationImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return SnipmatchPackage.Literals.SNIPPET_REPOSITORY_CONFIGURATION;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setEnabled(boolean newEnabled) {
        boolean oldEnabled = enabled;
        enabled = newEnabled;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    SnipmatchPackage.SNIPPET_REPOSITORY_CONFIGURATION__ENABLED, oldEnabled, enabled));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setName(String newName) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    SnipmatchPackage.SNIPPET_REPOSITORY_CONFIGURATION__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public String getDescription() {
        return description;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    public void setDescription(String newDescription) {
        String oldDescription = description;
        description = newDescription;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET,
                    SnipmatchPackage.SNIPPET_REPOSITORY_CONFIGURATION__DESCRIPTION, oldDescription, description));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * 
     * @generated NOT
     */
    public abstract ISnippetRepository createRepositoryInstance();

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
        case SnipmatchPackage.SNIPPET_REPOSITORY_CONFIGURATION__ENABLED:
            return isEnabled();
        case SnipmatchPackage.SNIPPET_REPOSITORY_CONFIGURATION__NAME:
            return getName();
        case SnipmatchPackage.SNIPPET_REPOSITORY_CONFIGURATION__DESCRIPTION:
            return getDescription();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
        case SnipmatchPackage.SNIPPET_REPOSITORY_CONFIGURATION__ENABLED:
            setEnabled((Boolean) newValue);
            return;
        case SnipmatchPackage.SNIPPET_REPOSITORY_CONFIGURATION__NAME:
            setName((String) newValue);
            return;
        case SnipmatchPackage.SNIPPET_REPOSITORY_CONFIGURATION__DESCRIPTION:
            setDescription((String) newValue);
            return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
        case SnipmatchPackage.SNIPPET_REPOSITORY_CONFIGURATION__ENABLED:
            setEnabled(ENABLED_EDEFAULT);
            return;
        case SnipmatchPackage.SNIPPET_REPOSITORY_CONFIGURATION__NAME:
            setName(NAME_EDEFAULT);
            return;
        case SnipmatchPackage.SNIPPET_REPOSITORY_CONFIGURATION__DESCRIPTION:
            setDescription(DESCRIPTION_EDEFAULT);
            return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
        case SnipmatchPackage.SNIPPET_REPOSITORY_CONFIGURATION__ENABLED:
            return enabled != ENABLED_EDEFAULT;
        case SnipmatchPackage.SNIPPET_REPOSITORY_CONFIGURATION__NAME:
            return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
        case SnipmatchPackage.SNIPPET_REPOSITORY_CONFIGURATION__DESCRIPTION:
            return DESCRIPTION_EDEFAULT == null ? description != null : !DESCRIPTION_EDEFAULT.equals(description);
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
        switch (operationID) {
        case SnipmatchPackage.SNIPPET_REPOSITORY_CONFIGURATION___CREATE_REPOSITORY_INSTANCE:
            return createRepositoryInstance();
        }
        return super.eInvoke(operationID, arguments);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy())
            return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (enabled: "); //$NON-NLS-1$
        result.append(enabled);
        result.append(", name: "); //$NON-NLS-1$
        result.append(name);
        result.append(", description: "); //$NON-NLS-1$
        result.append(description);
        result.append(')');
        return result.toString();
    }

} // SnippetRepositoryConfigurationImpl
