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
package org.eclipse.recommenders.internal.stacktraces.rcp.model;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Settings</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Settings#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Settings#getEmail <em>Email</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Settings#isAnonymizeStrackTraceElements <em>Anonymize Strack Trace Elements</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Settings#isAnonymizeMessages <em>Anonymize Messages</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Settings#getAction <em>Action</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Settings#getPause <em>Pause</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ModelPackage#getSettings()
 * @model
 * @generated
 */
public interface Settings extends EObject {
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
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ModelPackage#getSettings_Name()
     * @model unique="false"
     * @generated
     */
    String getName();

    /**
     * Sets the value of the '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Settings#getName <em>Name</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Name</em>' attribute.
     * @see #getName()
     * @generated
     */
    void setName(String value);

    /**
     * Returns the value of the '<em><b>Email</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Email</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Email</em>' attribute.
     * @see #setEmail(String)
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ModelPackage#getSettings_Email()
     * @model unique="false"
     * @generated
     */
    String getEmail();

    /**
     * Sets the value of the '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Settings#getEmail <em>Email</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Email</em>' attribute.
     * @see #getEmail()
     * @generated
     */
    void setEmail(String value);

    /**
     * Returns the value of the '<em><b>Anonymize Strack Trace Elements</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Anonymize Strack Trace Elements</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Anonymize Strack Trace Elements</em>' attribute.
     * @see #setAnonymizeStrackTraceElements(boolean)
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ModelPackage#getSettings_AnonymizeStrackTraceElements()
     * @model unique="false"
     * @generated
     */
    boolean isAnonymizeStrackTraceElements();

    /**
     * Sets the value of the '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Settings#isAnonymizeStrackTraceElements <em>Anonymize Strack Trace Elements</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Anonymize Strack Trace Elements</em>' attribute.
     * @see #isAnonymizeStrackTraceElements()
     * @generated
     */
    void setAnonymizeStrackTraceElements(boolean value);

    /**
     * Returns the value of the '<em><b>Anonymize Messages</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Anonymize Messages</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Anonymize Messages</em>' attribute.
     * @see #setAnonymizeMessages(boolean)
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ModelPackage#getSettings_AnonymizeMessages()
     * @model unique="false"
     * @generated
     */
    boolean isAnonymizeMessages();

    /**
     * Sets the value of the '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Settings#isAnonymizeMessages <em>Anonymize Messages</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Anonymize Messages</em>' attribute.
     * @see #isAnonymizeMessages()
     * @generated
     */
    void setAnonymizeMessages(boolean value);

    /**
     * Returns the value of the '<em><b>Action</b></em>' attribute.
     * The literals are from the enumeration {@link org.eclipse.recommenders.internal.stacktraces.rcp.model.SendAction}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Action</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Action</em>' attribute.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.SendAction
     * @see #setAction(SendAction)
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ModelPackage#getSettings_Action()
     * @model unique="false"
     * @generated
     */
    SendAction getAction();

    /**
     * Sets the value of the '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Settings#getAction <em>Action</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Action</em>' attribute.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.SendAction
     * @see #getAction()
     * @generated
     */
    void setAction(SendAction value);

    /**
     * Returns the value of the '<em><b>Pause</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Pause</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Pause</em>' attribute.
     * @see #setPause(long)
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ModelPackage#getSettings_Pause()
     * @model unique="false"
     * @generated
     */
    long getPause();

    /**
     * Sets the value of the '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Settings#getPause <em>Pause</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Pause</em>' attribute.
     * @see #getPause()
     * @generated
     */
    void setPause(long value);

} // Settings
