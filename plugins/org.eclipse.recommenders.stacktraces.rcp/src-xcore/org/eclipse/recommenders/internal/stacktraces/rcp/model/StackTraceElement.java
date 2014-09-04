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
 * A representation of the model object '<em><b>Stack Trace Element</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.StackTraceElement#getFilename <em>Filename</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.StackTraceElement#getClassname <em>Classname</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.StackTraceElement#getMethodname <em>Methodname</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.StackTraceElement#getLine <em>Line</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.StackTraceElement#isNative <em>Native</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ModelPackage#getStackTraceElement()
 * @model
 * @generated
 */
public interface StackTraceElement extends EObject {
    /**
     * Returns the value of the '<em><b>Filename</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Filename</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Filename</em>' attribute.
     * @see #setFilename(String)
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ModelPackage#getStackTraceElement_Filename()
     * @model unique="false"
     * @generated
     */
    String getFilename();

    /**
     * Sets the value of the '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.StackTraceElement#getFilename <em>Filename</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Filename</em>' attribute.
     * @see #getFilename()
     * @generated
     */
    void setFilename(String value);

    /**
     * Returns the value of the '<em><b>Classname</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Classname</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Classname</em>' attribute.
     * @see #setClassname(String)
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ModelPackage#getStackTraceElement_Classname()
     * @model unique="false"
     * @generated
     */
    String getClassname();

    /**
     * Sets the value of the '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.StackTraceElement#getClassname <em>Classname</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Classname</em>' attribute.
     * @see #getClassname()
     * @generated
     */
    void setClassname(String value);

    /**
     * Returns the value of the '<em><b>Methodname</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Methodname</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Methodname</em>' attribute.
     * @see #setMethodname(String)
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ModelPackage#getStackTraceElement_Methodname()
     * @model unique="false"
     * @generated
     */
    String getMethodname();

    /**
     * Sets the value of the '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.StackTraceElement#getMethodname <em>Methodname</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Methodname</em>' attribute.
     * @see #getMethodname()
     * @generated
     */
    void setMethodname(String value);

    /**
     * Returns the value of the '<em><b>Line</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Line</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Line</em>' attribute.
     * @see #setLine(int)
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ModelPackage#getStackTraceElement_Line()
     * @model unique="false"
     * @generated
     */
    int getLine();

    /**
     * Sets the value of the '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.StackTraceElement#getLine <em>Line</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Line</em>' attribute.
     * @see #getLine()
     * @generated
     */
    void setLine(int value);

    /**
     * Returns the value of the '<em><b>Native</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Native</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Native</em>' attribute.
     * @see #setNative(boolean)
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ModelPackage#getStackTraceElement_Native()
     * @model unique="false"
     * @generated
     */
    boolean isNative();

    /**
     * Sets the value of the '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.StackTraceElement#isNative <em>Native</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Native</em>' attribute.
     * @see #isNative()
     * @generated
     */
    void setNative(boolean value);

} // StackTraceElement
