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

import org.eclipse.recommenders.internal.stacktraces.rcp.model.ModelPackage;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.StackTraceElement;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Stack Trace Element</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.StackTraceElementImpl#getFilename <em>Filename</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.StackTraceElementImpl#getClassname <em>Classname</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.StackTraceElementImpl#getMethodname <em>Methodname</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.StackTraceElementImpl#getLine <em>Line</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.StackTraceElementImpl#isNative <em>Native</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class StackTraceElementImpl extends MinimalEObjectImpl.Container implements StackTraceElement {
    /**
     * The default value of the '{@link #getFilename() <em>Filename</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFilename()
     * @generated
     * @ordered
     */
    protected static final String FILENAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getFilename() <em>Filename</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getFilename()
     * @generated
     * @ordered
     */
    protected String filename = FILENAME_EDEFAULT;

    /**
     * The default value of the '{@link #getClassname() <em>Classname</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getClassname()
     * @generated
     * @ordered
     */
    protected static final String CLASSNAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getClassname() <em>Classname</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getClassname()
     * @generated
     * @ordered
     */
    protected String classname = CLASSNAME_EDEFAULT;

    /**
     * The default value of the '{@link #getMethodname() <em>Methodname</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMethodname()
     * @generated
     * @ordered
     */
    protected static final String METHODNAME_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getMethodname() <em>Methodname</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getMethodname()
     * @generated
     * @ordered
     */
    protected String methodname = METHODNAME_EDEFAULT;

    /**
     * The default value of the '{@link #getLine() <em>Line</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLine()
     * @generated
     * @ordered
     */
    protected static final int LINE_EDEFAULT = 0;

    /**
     * The cached value of the '{@link #getLine() <em>Line</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLine()
     * @generated
     * @ordered
     */
    protected int line = LINE_EDEFAULT;

    /**
     * The default value of the '{@link #isNative() <em>Native</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isNative()
     * @generated
     * @ordered
     */
    protected static final boolean NATIVE_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isNative() <em>Native</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #isNative()
     * @generated
     * @ordered
     */
    protected boolean native_ = NATIVE_EDEFAULT;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected StackTraceElementImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ModelPackage.Literals.STACK_TRACE_ELEMENT;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getFilename() {
        return filename;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setFilename(String newFilename) {
        String oldFilename = filename;
        filename = newFilename;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.STACK_TRACE_ELEMENT__FILENAME, oldFilename, filename));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getClassname() {
        return classname;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setClassname(String newClassname) {
        String oldClassname = classname;
        classname = newClassname;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.STACK_TRACE_ELEMENT__CLASSNAME, oldClassname, classname));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String getMethodname() {
        return methodname;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setMethodname(String newMethodname) {
        String oldMethodname = methodname;
        methodname = newMethodname;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.STACK_TRACE_ELEMENT__METHODNAME, oldMethodname, methodname));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public int getLine() {
        return line;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setLine(int newLine) {
        int oldLine = line;
        line = newLine;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.STACK_TRACE_ELEMENT__LINE, oldLine, line));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public boolean isNative() {
        return native_;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setNative(boolean newNative) {
        boolean oldNative = native_;
        native_ = newNative;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelPackage.STACK_TRACE_ELEMENT__NATIVE, oldNative, native_));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case ModelPackage.STACK_TRACE_ELEMENT__FILENAME:
                return getFilename();
            case ModelPackage.STACK_TRACE_ELEMENT__CLASSNAME:
                return getClassname();
            case ModelPackage.STACK_TRACE_ELEMENT__METHODNAME:
                return getMethodname();
            case ModelPackage.STACK_TRACE_ELEMENT__LINE:
                return getLine();
            case ModelPackage.STACK_TRACE_ELEMENT__NATIVE:
                return isNative();
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
            case ModelPackage.STACK_TRACE_ELEMENT__FILENAME:
                setFilename((String)newValue);
                return;
            case ModelPackage.STACK_TRACE_ELEMENT__CLASSNAME:
                setClassname((String)newValue);
                return;
            case ModelPackage.STACK_TRACE_ELEMENT__METHODNAME:
                setMethodname((String)newValue);
                return;
            case ModelPackage.STACK_TRACE_ELEMENT__LINE:
                setLine((Integer)newValue);
                return;
            case ModelPackage.STACK_TRACE_ELEMENT__NATIVE:
                setNative((Boolean)newValue);
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
            case ModelPackage.STACK_TRACE_ELEMENT__FILENAME:
                setFilename(FILENAME_EDEFAULT);
                return;
            case ModelPackage.STACK_TRACE_ELEMENT__CLASSNAME:
                setClassname(CLASSNAME_EDEFAULT);
                return;
            case ModelPackage.STACK_TRACE_ELEMENT__METHODNAME:
                setMethodname(METHODNAME_EDEFAULT);
                return;
            case ModelPackage.STACK_TRACE_ELEMENT__LINE:
                setLine(LINE_EDEFAULT);
                return;
            case ModelPackage.STACK_TRACE_ELEMENT__NATIVE:
                setNative(NATIVE_EDEFAULT);
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
            case ModelPackage.STACK_TRACE_ELEMENT__FILENAME:
                return FILENAME_EDEFAULT == null ? filename != null : !FILENAME_EDEFAULT.equals(filename);
            case ModelPackage.STACK_TRACE_ELEMENT__CLASSNAME:
                return CLASSNAME_EDEFAULT == null ? classname != null : !CLASSNAME_EDEFAULT.equals(classname);
            case ModelPackage.STACK_TRACE_ELEMENT__METHODNAME:
                return METHODNAME_EDEFAULT == null ? methodname != null : !METHODNAME_EDEFAULT.equals(methodname);
            case ModelPackage.STACK_TRACE_ELEMENT__LINE:
                return line != LINE_EDEFAULT;
            case ModelPackage.STACK_TRACE_ELEMENT__NATIVE:
                return native_ != NATIVE_EDEFAULT;
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
        result.append(" (filename: ");
        result.append(filename);
        result.append(", classname: ");
        result.append(classname);
        result.append(", methodname: ");
        result.append(methodname);
        result.append(", line: ");
        result.append(line);
        result.append(", native: ");
        result.append(native_);
        result.append(')');
        return result.toString();
    }

} //StackTraceElementImpl
