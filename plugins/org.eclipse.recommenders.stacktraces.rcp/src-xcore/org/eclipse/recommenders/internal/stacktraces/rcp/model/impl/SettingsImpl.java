/**
 * Copyright (c) 2014 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.stacktraces.rcp.model.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.recommenders.internal.stacktraces.rcp.model.SendAction;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.ModelPackage;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.Settings;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Settings</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.SettingsImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.SettingsImpl#getEmail <em>Email</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.SettingsImpl#isAnonymizeStrackTraceElements <em>Anonymize Strack Trace Elements</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.SettingsImpl#isAnonymizeMessages <em>Anonymize Messages</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.SettingsImpl#getAction <em>Action</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.SettingsImpl#getPause <em>Pause</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SettingsImpl extends MinimalEObjectImpl.Container implements Settings {
    /**
     * The default value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected static final String NAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getName()
     * @generated
     * @ordered
     */
    protected String name = NAME_EDEFAULT;

    /**
     * The default value of the '{@link #getEmail() <em>Email</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmail()
     * @generated
     * @ordered
     */
    protected static final String EMAIL_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getEmail() <em>Email</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getEmail()
     * @generated
     * @ordered
     */
    protected String email = EMAIL_EDEFAULT;

    /**
     * The default value of the '{@link #isAnonymizeStrackTraceElements() <em>Anonymize Strack Trace Elements</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isAnonymizeStrackTraceElements()
     * @generated
     * @ordered
     */
    protected static final boolean ANONYMIZE_STRACK_TRACE_ELEMENTS_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isAnonymizeStrackTraceElements() <em>Anonymize Strack Trace Elements</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isAnonymizeStrackTraceElements()
     * @generated
     * @ordered
     */
    protected boolean anonymizeStrackTraceElements = ANONYMIZE_STRACK_TRACE_ELEMENTS_EDEFAULT;

    /**
     * The default value of the '{@link #isAnonymizeMessages() <em>Anonymize Messages</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isAnonymizeMessages()
     * @generated
     * @ordered
     */
    protected static final boolean ANONYMIZE_MESSAGES_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isAnonymizeMessages() <em>Anonymize Messages</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isAnonymizeMessages()
     * @generated
     * @ordered
     */
    protected boolean anonymizeMessages = ANONYMIZE_MESSAGES_EDEFAULT;

    /**
     * The default value of the '{@link #getAction() <em>Action</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAction()
     * @generated
     * @ordered
     */
    protected static final SendAction ACTION_EDEFAULT = SendAction.ASK;

    /**
     * The cached value of the '{@link #getAction() <em>Action</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getAction()
     * @generated
     * @ordered
     */
    protected SendAction action = ACTION_EDEFAULT;

    /**
     * The default value of the '{@link #getPause() <em>Pause</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPause()
     * @generated
     * @ordered
     */
    protected static final long PAUSE_EDEFAULT = 0L;

    /**
     * The cached value of the '{@link #getPause() <em>Pause</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getPause()
     * @generated
     * @ordered
     */
    protected long pause = PAUSE_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected SettingsImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ModelPackage.Literals.SETTINGS;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getName() {
        return name;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setName(String newName) {
        String oldName = name;
        name = newName;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.SETTINGS__NAME, oldName, name));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getEmail() {
        return email;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setEmail(String newEmail) {
        String oldEmail = email;
        email = newEmail;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.SETTINGS__EMAIL, oldEmail, email));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isAnonymizeStrackTraceElements() {
        return anonymizeStrackTraceElements;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAnonymizeStrackTraceElements(boolean newAnonymizeStrackTraceElements) {
        boolean oldAnonymizeStrackTraceElements = anonymizeStrackTraceElements;
        anonymizeStrackTraceElements = newAnonymizeStrackTraceElements;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.SETTINGS__ANONYMIZE_STRACK_TRACE_ELEMENTS, oldAnonymizeStrackTraceElements, anonymizeStrackTraceElements));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isAnonymizeMessages() {
        return anonymizeMessages;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAnonymizeMessages(boolean newAnonymizeMessages) {
        boolean oldAnonymizeMessages = anonymizeMessages;
        anonymizeMessages = newAnonymizeMessages;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.SETTINGS__ANONYMIZE_MESSAGES, oldAnonymizeMessages, anonymizeMessages));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SendAction getAction() {
        return action;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setAction(SendAction newAction) {
        SendAction oldAction = action;
        action = newAction == null ? ACTION_EDEFAULT : newAction;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.SETTINGS__ACTION, oldAction, action));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public long getPause() {
        return pause;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setPause(long newPause) {
        long oldPause = pause;
        pause = newPause;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.SETTINGS__PAUSE, oldPause, pause));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case ModelPackage.SETTINGS__NAME:
                return getName();
            case ModelPackage.SETTINGS__EMAIL:
                return getEmail();
            case ModelPackage.SETTINGS__ANONYMIZE_STRACK_TRACE_ELEMENTS:
                return isAnonymizeStrackTraceElements();
            case ModelPackage.SETTINGS__ANONYMIZE_MESSAGES:
                return isAnonymizeMessages();
            case ModelPackage.SETTINGS__ACTION:
                return getAction();
            case ModelPackage.SETTINGS__PAUSE:
                return getPause();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case ModelPackage.SETTINGS__NAME:
                setName((String)newValue);
                return;
            case ModelPackage.SETTINGS__EMAIL:
                setEmail((String)newValue);
                return;
            case ModelPackage.SETTINGS__ANONYMIZE_STRACK_TRACE_ELEMENTS:
                setAnonymizeStrackTraceElements((Boolean)newValue);
                return;
            case ModelPackage.SETTINGS__ANONYMIZE_MESSAGES:
                setAnonymizeMessages((Boolean)newValue);
                return;
            case ModelPackage.SETTINGS__ACTION:
                setAction((SendAction)newValue);
                return;
            case ModelPackage.SETTINGS__PAUSE:
                setPause((Long)newValue);
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
            case ModelPackage.SETTINGS__NAME:
                setName(NAME_EDEFAULT);
                return;
            case ModelPackage.SETTINGS__EMAIL:
                setEmail(EMAIL_EDEFAULT);
                return;
            case ModelPackage.SETTINGS__ANONYMIZE_STRACK_TRACE_ELEMENTS:
                setAnonymizeStrackTraceElements(ANONYMIZE_STRACK_TRACE_ELEMENTS_EDEFAULT);
                return;
            case ModelPackage.SETTINGS__ANONYMIZE_MESSAGES:
                setAnonymizeMessages(ANONYMIZE_MESSAGES_EDEFAULT);
                return;
            case ModelPackage.SETTINGS__ACTION:
                setAction(ACTION_EDEFAULT);
                return;
            case ModelPackage.SETTINGS__PAUSE:
                setPause(PAUSE_EDEFAULT);
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
            case ModelPackage.SETTINGS__NAME:
                return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
            case ModelPackage.SETTINGS__EMAIL:
                return EMAIL_EDEFAULT == null ? email != null : !EMAIL_EDEFAULT.equals(email);
            case ModelPackage.SETTINGS__ANONYMIZE_STRACK_TRACE_ELEMENTS:
                return anonymizeStrackTraceElements != ANONYMIZE_STRACK_TRACE_ELEMENTS_EDEFAULT;
            case ModelPackage.SETTINGS__ANONYMIZE_MESSAGES:
                return anonymizeMessages != ANONYMIZE_MESSAGES_EDEFAULT;
            case ModelPackage.SETTINGS__ACTION:
                return action != ACTION_EDEFAULT;
            case ModelPackage.SETTINGS__PAUSE:
                return pause != PAUSE_EDEFAULT;
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (name: ");
        result.append(name);
        result.append(", email: ");
        result.append(email);
        result.append(", anonymizeStrackTraceElements: ");
        result.append(anonymizeStrackTraceElements);
        result.append(", anonymizeMessages: ");
        result.append(anonymizeMessages);
        result.append(", action: ");
        result.append(action);
        result.append(", pause: ");
        result.append(pause);
        result.append(')');
        return result.toString();
    }

} //SettingsImpl
