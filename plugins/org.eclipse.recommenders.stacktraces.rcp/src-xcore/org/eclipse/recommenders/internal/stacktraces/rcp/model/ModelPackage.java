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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ModelFactory
 * @model kind="package"
 *        annotation="http://www.eclipse.org/emf/2002/GenModel modelDirectory='/org.eclipse.recommenders.stacktraces.rcp/src-xcore' copyrightText='Copyright (c) 2014 Codetrails GmbH.\nAll rights reserved. This program and the accompanying materials\nare made available under the terms of the Eclipse Public License v1.0\nwhich accompanies this distribution, and is available at\nhttp://www.eclipse.org/legal/epl-v10.html\n\nContributors:\n    Marcel Bruch - initial API and implementation.' bundleManifest='false' basePackage='org.eclipse.recommenders.internal.stacktraces.rcp'"
 * @generated
 */
public interface ModelPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "model";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "org.eclipse.recommenders.internal.stacktraces.rcp.model";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "model";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    ModelPackage eINSTANCE = org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.ModelPackageImpl.init();

    /**
     * The meta object id for the '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.ErrorLogEventImpl <em>Error Log Event</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.ErrorLogEventImpl
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.ModelPackageImpl#getErrorLogEvent()
     * @generated
     */
    int ERROR_LOG_EVENT = 0;

    /**
     * The feature id for the '<em><b>Anonymous Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ERROR_LOG_EVENT__ANONYMOUS_ID = 0;

    /**
     * The feature id for the '<em><b>Event Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ERROR_LOG_EVENT__EVENT_ID = 1;

    /**
     * The feature id for the '<em><b>Name</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ERROR_LOG_EVENT__NAME = 2;

    /**
     * The feature id for the '<em><b>Email</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ERROR_LOG_EVENT__EMAIL = 3;

    /**
     * The feature id for the '<em><b>Comment</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ERROR_LOG_EVENT__COMMENT = 4;

    /**
     * The feature id for the '<em><b>Eclipse Build Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ERROR_LOG_EVENT__ECLIPSE_BUILD_ID = 5;

    /**
     * The feature id for the '<em><b>Java Runtime Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ERROR_LOG_EVENT__JAVA_RUNTIME_VERSION = 6;

    /**
     * The feature id for the '<em><b>Osgi Ws</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ERROR_LOG_EVENT__OSGI_WS = 7;

    /**
     * The feature id for the '<em><b>Osgi Os</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ERROR_LOG_EVENT__OSGI_OS = 8;

    /**
     * The feature id for the '<em><b>Osgi Os Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ERROR_LOG_EVENT__OSGI_OS_VERSION = 9;

    /**
     * The feature id for the '<em><b>Osgi Arch</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ERROR_LOG_EVENT__OSGI_ARCH = 10;

    /**
     * The feature id for the '<em><b>Status</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ERROR_LOG_EVENT__STATUS = 11;

    /**
     * The number of structural features of the '<em>Error Log Event</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ERROR_LOG_EVENT_FEATURE_COUNT = 12;

    /**
     * The number of operations of the '<em>Error Log Event</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ERROR_LOG_EVENT_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.StatusImpl <em>Status</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.StatusImpl
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.ModelPackageImpl#getStatus()
     * @generated
     */
    int STATUS = 1;

    /**
     * The feature id for the '<em><b>Plugin Id</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATUS__PLUGIN_ID = 0;

    /**
     * The feature id for the '<em><b>Plugin Version</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATUS__PLUGIN_VERSION = 1;

    /**
     * The feature id for the '<em><b>Code</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATUS__CODE = 2;

    /**
     * The feature id for the '<em><b>Severity</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATUS__SEVERITY = 3;

    /**
     * The feature id for the '<em><b>Message</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATUS__MESSAGE = 4;

    /**
     * The feature id for the '<em><b>Exception</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATUS__EXCEPTION = 5;

    /**
     * The feature id for the '<em><b>Children</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATUS__CHILDREN = 6;

    /**
     * The number of structural features of the '<em>Status</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATUS_FEATURE_COUNT = 7;

    /**
     * The number of operations of the '<em>Status</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STATUS_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.ThrowableImpl <em>Throwable</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.ThrowableImpl
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.ModelPackageImpl#getThrowable()
     * @generated
     */
    int THROWABLE = 2;

    /**
     * The feature id for the '<em><b>Classname</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int THROWABLE__CLASSNAME = 0;

    /**
     * The feature id for the '<em><b>Message</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int THROWABLE__MESSAGE = 1;

    /**
     * The feature id for the '<em><b>Cause</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int THROWABLE__CAUSE = 2;

    /**
     * The feature id for the '<em><b>Stack Trace</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int THROWABLE__STACK_TRACE = 3;

    /**
     * The number of structural features of the '<em>Throwable</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int THROWABLE_FEATURE_COUNT = 4;

    /**
     * The number of operations of the '<em>Throwable</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int THROWABLE_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.StackTraceElementImpl <em>Stack Trace Element</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.StackTraceElementImpl
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.ModelPackageImpl#getStackTraceElement()
     * @generated
     */
    int STACK_TRACE_ELEMENT = 3;

    /**
     * The feature id for the '<em><b>Filename</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STACK_TRACE_ELEMENT__FILENAME = 0;

    /**
     * The feature id for the '<em><b>Classname</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STACK_TRACE_ELEMENT__CLASSNAME = 1;

    /**
     * The feature id for the '<em><b>Methodname</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STACK_TRACE_ELEMENT__METHODNAME = 2;

    /**
     * The feature id for the '<em><b>Line</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STACK_TRACE_ELEMENT__LINE = 3;

    /**
     * The feature id for the '<em><b>Native</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STACK_TRACE_ELEMENT__NATIVE = 4;

    /**
     * The number of structural features of the '<em>Stack Trace Element</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STACK_TRACE_ELEMENT_FEATURE_COUNT = 5;

    /**
     * The number of operations of the '<em>Stack Trace Element</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int STACK_TRACE_ELEMENT_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Severity <em>Severity</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.Severity
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.ModelPackageImpl#getSeverity()
     * @generated
     */
    int SEVERITY = 4;

    /**
     * The meta object id for the '<em>UUID</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.util.UUID
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.ModelPackageImpl#getUUID()
     * @generated
     */
    int UUID = 5;


    /**
     * Returns the meta object for class '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent <em>Error Log Event</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Error Log Event</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent
     * @generated
     */
    EClass getErrorLogEvent();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent#getAnonymousId <em>Anonymous Id</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Anonymous Id</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent#getAnonymousId()
     * @see #getErrorLogEvent()
     * @generated
     */
    EAttribute getErrorLogEvent_AnonymousId();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent#getEventId <em>Event Id</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Event Id</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent#getEventId()
     * @see #getErrorLogEvent()
     * @generated
     */
    EAttribute getErrorLogEvent_EventId();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent#getName <em>Name</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Name</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent#getName()
     * @see #getErrorLogEvent()
     * @generated
     */
    EAttribute getErrorLogEvent_Name();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent#getEmail <em>Email</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Email</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent#getEmail()
     * @see #getErrorLogEvent()
     * @generated
     */
    EAttribute getErrorLogEvent_Email();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent#getComment <em>Comment</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Comment</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent#getComment()
     * @see #getErrorLogEvent()
     * @generated
     */
    EAttribute getErrorLogEvent_Comment();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent#getEclipseBuildId <em>Eclipse Build Id</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Eclipse Build Id</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent#getEclipseBuildId()
     * @see #getErrorLogEvent()
     * @generated
     */
    EAttribute getErrorLogEvent_EclipseBuildId();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent#getJavaRuntimeVersion <em>Java Runtime Version</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Java Runtime Version</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent#getJavaRuntimeVersion()
     * @see #getErrorLogEvent()
     * @generated
     */
    EAttribute getErrorLogEvent_JavaRuntimeVersion();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent#getOsgiWs <em>Osgi Ws</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Osgi Ws</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent#getOsgiWs()
     * @see #getErrorLogEvent()
     * @generated
     */
    EAttribute getErrorLogEvent_OsgiWs();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent#getOsgiOs <em>Osgi Os</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Osgi Os</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent#getOsgiOs()
     * @see #getErrorLogEvent()
     * @generated
     */
    EAttribute getErrorLogEvent_OsgiOs();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent#getOsgiOsVersion <em>Osgi Os Version</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Osgi Os Version</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent#getOsgiOsVersion()
     * @see #getErrorLogEvent()
     * @generated
     */
    EAttribute getErrorLogEvent_OsgiOsVersion();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent#getOsgiArch <em>Osgi Arch</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Osgi Arch</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent#getOsgiArch()
     * @see #getErrorLogEvent()
     * @generated
     */
    EAttribute getErrorLogEvent_OsgiArch();

    /**
     * Returns the meta object for the containment reference '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent#getStatus <em>Status</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Status</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent#getStatus()
     * @see #getErrorLogEvent()
     * @generated
     */
    EReference getErrorLogEvent_Status();

    /**
     * Returns the meta object for class '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Status <em>Status</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Status</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.Status
     * @generated
     */
    EClass getStatus();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Status#getPluginId <em>Plugin Id</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Plugin Id</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.Status#getPluginId()
     * @see #getStatus()
     * @generated
     */
    EAttribute getStatus_PluginId();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Status#getPluginVersion <em>Plugin Version</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Plugin Version</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.Status#getPluginVersion()
     * @see #getStatus()
     * @generated
     */
    EAttribute getStatus_PluginVersion();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Status#getCode <em>Code</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Code</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.Status#getCode()
     * @see #getStatus()
     * @generated
     */
    EAttribute getStatus_Code();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Status#getSeverity <em>Severity</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Severity</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.Status#getSeverity()
     * @see #getStatus()
     * @generated
     */
    EAttribute getStatus_Severity();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Status#getMessage <em>Message</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Message</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.Status#getMessage()
     * @see #getStatus()
     * @generated
     */
    EAttribute getStatus_Message();

    /**
     * Returns the meta object for the containment reference '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Status#getException <em>Exception</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Exception</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.Status#getException()
     * @see #getStatus()
     * @generated
     */
    EReference getStatus_Exception();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Status#getChildren <em>Children</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Children</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.Status#getChildren()
     * @see #getStatus()
     * @generated
     */
    EReference getStatus_Children();

    /**
     * Returns the meta object for class '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Throwable <em>Throwable</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Throwable</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.Throwable
     * @generated
     */
    EClass getThrowable();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Throwable#getClassname <em>Classname</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Classname</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.Throwable#getClassname()
     * @see #getThrowable()
     * @generated
     */
    EAttribute getThrowable_Classname();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Throwable#getMessage <em>Message</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Message</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.Throwable#getMessage()
     * @see #getThrowable()
     * @generated
     */
    EAttribute getThrowable_Message();

    /**
     * Returns the meta object for the containment reference '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Throwable#getCause <em>Cause</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Cause</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.Throwable#getCause()
     * @see #getThrowable()
     * @generated
     */
    EReference getThrowable_Cause();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Throwable#getStackTrace <em>Stack Trace</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Stack Trace</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.Throwable#getStackTrace()
     * @see #getThrowable()
     * @generated
     */
    EReference getThrowable_StackTrace();

    /**
     * Returns the meta object for class '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.StackTraceElement <em>Stack Trace Element</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Stack Trace Element</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.StackTraceElement
     * @generated
     */
    EClass getStackTraceElement();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.StackTraceElement#getFilename <em>Filename</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Filename</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.StackTraceElement#getFilename()
     * @see #getStackTraceElement()
     * @generated
     */
    EAttribute getStackTraceElement_Filename();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.StackTraceElement#getClassname <em>Classname</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Classname</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.StackTraceElement#getClassname()
     * @see #getStackTraceElement()
     * @generated
     */
    EAttribute getStackTraceElement_Classname();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.StackTraceElement#getMethodname <em>Methodname</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Methodname</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.StackTraceElement#getMethodname()
     * @see #getStackTraceElement()
     * @generated
     */
    EAttribute getStackTraceElement_Methodname();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.StackTraceElement#getLine <em>Line</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Line</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.StackTraceElement#getLine()
     * @see #getStackTraceElement()
     * @generated
     */
    EAttribute getStackTraceElement_Line();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.StackTraceElement#isNative <em>Native</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Native</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.StackTraceElement#isNative()
     * @see #getStackTraceElement()
     * @generated
     */
    EAttribute getStackTraceElement_Native();

    /**
     * Returns the meta object for enum '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Severity <em>Severity</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>Severity</em>'.
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.Severity
     * @generated
     */
    EEnum getSeverity();

    /**
     * Returns the meta object for data type '{@link java.util.UUID <em>UUID</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>UUID</em>'.
     * @see java.util.UUID
     * @model instanceClass="java.util.UUID"
     * @generated
     */
    EDataType getUUID();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    ModelFactory getModelFactory();

    /**
     * <!-- begin-user-doc -->
     * Defines literals for the meta objects that represent
     * <ul>
     *   <li>each class,</li>
     *   <li>each feature of each class,</li>
     *   <li>each operation of each class,</li>
     *   <li>each enum,</li>
     *   <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * @generated
     */
    interface Literals {
        /**
         * The meta object literal for the '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.ErrorLogEventImpl <em>Error Log Event</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.ErrorLogEventImpl
         * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.ModelPackageImpl#getErrorLogEvent()
         * @generated
         */
        EClass ERROR_LOG_EVENT = eINSTANCE.getErrorLogEvent();

        /**
         * The meta object literal for the '<em><b>Anonymous Id</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ERROR_LOG_EVENT__ANONYMOUS_ID = eINSTANCE.getErrorLogEvent_AnonymousId();

        /**
         * The meta object literal for the '<em><b>Event Id</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ERROR_LOG_EVENT__EVENT_ID = eINSTANCE.getErrorLogEvent_EventId();

        /**
         * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ERROR_LOG_EVENT__NAME = eINSTANCE.getErrorLogEvent_Name();

        /**
         * The meta object literal for the '<em><b>Email</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ERROR_LOG_EVENT__EMAIL = eINSTANCE.getErrorLogEvent_Email();

        /**
         * The meta object literal for the '<em><b>Comment</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ERROR_LOG_EVENT__COMMENT = eINSTANCE.getErrorLogEvent_Comment();

        /**
         * The meta object literal for the '<em><b>Eclipse Build Id</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ERROR_LOG_EVENT__ECLIPSE_BUILD_ID = eINSTANCE.getErrorLogEvent_EclipseBuildId();

        /**
         * The meta object literal for the '<em><b>Java Runtime Version</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ERROR_LOG_EVENT__JAVA_RUNTIME_VERSION = eINSTANCE.getErrorLogEvent_JavaRuntimeVersion();

        /**
         * The meta object literal for the '<em><b>Osgi Ws</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ERROR_LOG_EVENT__OSGI_WS = eINSTANCE.getErrorLogEvent_OsgiWs();

        /**
         * The meta object literal for the '<em><b>Osgi Os</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ERROR_LOG_EVENT__OSGI_OS = eINSTANCE.getErrorLogEvent_OsgiOs();

        /**
         * The meta object literal for the '<em><b>Osgi Os Version</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ERROR_LOG_EVENT__OSGI_OS_VERSION = eINSTANCE.getErrorLogEvent_OsgiOsVersion();

        /**
         * The meta object literal for the '<em><b>Osgi Arch</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ERROR_LOG_EVENT__OSGI_ARCH = eINSTANCE.getErrorLogEvent_OsgiArch();

        /**
         * The meta object literal for the '<em><b>Status</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference ERROR_LOG_EVENT__STATUS = eINSTANCE.getErrorLogEvent_Status();

        /**
         * The meta object literal for the '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.StatusImpl <em>Status</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.StatusImpl
         * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.ModelPackageImpl#getStatus()
         * @generated
         */
        EClass STATUS = eINSTANCE.getStatus();

        /**
         * The meta object literal for the '<em><b>Plugin Id</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STATUS__PLUGIN_ID = eINSTANCE.getStatus_PluginId();

        /**
         * The meta object literal for the '<em><b>Plugin Version</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STATUS__PLUGIN_VERSION = eINSTANCE.getStatus_PluginVersion();

        /**
         * The meta object literal for the '<em><b>Code</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STATUS__CODE = eINSTANCE.getStatus_Code();

        /**
         * The meta object literal for the '<em><b>Severity</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STATUS__SEVERITY = eINSTANCE.getStatus_Severity();

        /**
         * The meta object literal for the '<em><b>Message</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STATUS__MESSAGE = eINSTANCE.getStatus_Message();

        /**
         * The meta object literal for the '<em><b>Exception</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference STATUS__EXCEPTION = eINSTANCE.getStatus_Exception();

        /**
         * The meta object literal for the '<em><b>Children</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference STATUS__CHILDREN = eINSTANCE.getStatus_Children();

        /**
         * The meta object literal for the '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.ThrowableImpl <em>Throwable</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.ThrowableImpl
         * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.ModelPackageImpl#getThrowable()
         * @generated
         */
        EClass THROWABLE = eINSTANCE.getThrowable();

        /**
         * The meta object literal for the '<em><b>Classname</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute THROWABLE__CLASSNAME = eINSTANCE.getThrowable_Classname();

        /**
         * The meta object literal for the '<em><b>Message</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute THROWABLE__MESSAGE = eINSTANCE.getThrowable_Message();

        /**
         * The meta object literal for the '<em><b>Cause</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference THROWABLE__CAUSE = eINSTANCE.getThrowable_Cause();

        /**
         * The meta object literal for the '<em><b>Stack Trace</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference THROWABLE__STACK_TRACE = eINSTANCE.getThrowable_StackTrace();

        /**
         * The meta object literal for the '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.StackTraceElementImpl <em>Stack Trace Element</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.StackTraceElementImpl
         * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.ModelPackageImpl#getStackTraceElement()
         * @generated
         */
        EClass STACK_TRACE_ELEMENT = eINSTANCE.getStackTraceElement();

        /**
         * The meta object literal for the '<em><b>Filename</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STACK_TRACE_ELEMENT__FILENAME = eINSTANCE.getStackTraceElement_Filename();

        /**
         * The meta object literal for the '<em><b>Classname</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STACK_TRACE_ELEMENT__CLASSNAME = eINSTANCE.getStackTraceElement_Classname();

        /**
         * The meta object literal for the '<em><b>Methodname</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STACK_TRACE_ELEMENT__METHODNAME = eINSTANCE.getStackTraceElement_Methodname();

        /**
         * The meta object literal for the '<em><b>Line</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STACK_TRACE_ELEMENT__LINE = eINSTANCE.getStackTraceElement_Line();

        /**
         * The meta object literal for the '<em><b>Native</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute STACK_TRACE_ELEMENT__NATIVE = eINSTANCE.getStackTraceElement_Native();

        /**
         * The meta object literal for the '{@link org.eclipse.recommenders.internal.stacktraces.rcp.model.Severity <em>Severity</em>}' enum.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.Severity
         * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.ModelPackageImpl#getSeverity()
         * @generated
         */
        EEnum SEVERITY = eINSTANCE.getSeverity();

        /**
         * The meta object literal for the '<em>UUID</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.util.UUID
         * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.ModelPackageImpl#getUUID()
         * @generated
         */
        EDataType UUID = eINSTANCE.getUUID();

    }

} //ModelPackage
