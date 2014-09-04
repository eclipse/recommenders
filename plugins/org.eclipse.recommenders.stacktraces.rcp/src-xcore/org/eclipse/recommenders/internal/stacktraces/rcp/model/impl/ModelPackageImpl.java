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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;

import org.eclipse.emf.ecore.impl.EPackageImpl;

import org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorLogEvent;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.ModelFactory;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.ModelPackage;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.Severity;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.StackTraceElement;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.Status;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ModelPackageImpl extends EPackageImpl implements ModelPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass errorLogEventEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass statusEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass throwableEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass stackTraceElementEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum severityEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType uuidEDataType = null;

    /**
     * Creates an instance of the model <b>Package</b>, registered with
     * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
     * package URI value.
     * <p>Note: the correct way to create the package is via the static
     * factory method {@link #init init()}, which also performs
     * initialization of the package, or returns the registered package,
     * if one already exists.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.ecore.EPackage.Registry
     * @see org.eclipse.recommenders.internal.stacktraces.rcp.model.ModelPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private ModelPackageImpl() {
        super(eNS_URI, ModelFactory.eINSTANCE);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private static boolean isInited = false;

    /**
     * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
     * 
     * <p>This method is used to initialize {@link ModelPackage#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static ModelPackage init() {
        if (isInited) return (ModelPackage)EPackage.Registry.INSTANCE.getEPackage(ModelPackage.eNS_URI);

        // Obtain or create and register package
        ModelPackageImpl theModelPackage = (ModelPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof ModelPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new ModelPackageImpl());

        isInited = true;

        // Initialize simple dependencies
        EcorePackage.eINSTANCE.eClass();

        // Create package meta-data objects
        theModelPackage.createPackageContents();

        // Initialize created meta-data
        theModelPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theModelPackage.freeze();

  
        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(ModelPackage.eNS_URI, theModelPackage);
        return theModelPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getErrorLogEvent() {
        return errorLogEventEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getErrorLogEvent_AnonymousId() {
        return (EAttribute)errorLogEventEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getErrorLogEvent_EventId() {
        return (EAttribute)errorLogEventEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getErrorLogEvent_Name() {
        return (EAttribute)errorLogEventEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getErrorLogEvent_Email() {
        return (EAttribute)errorLogEventEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getErrorLogEvent_Comment() {
        return (EAttribute)errorLogEventEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getErrorLogEvent_EclipseBuildId() {
        return (EAttribute)errorLogEventEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getErrorLogEvent_JavaRuntimeVersion() {
        return (EAttribute)errorLogEventEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getErrorLogEvent_OsgiWs() {
        return (EAttribute)errorLogEventEClass.getEStructuralFeatures().get(7);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getErrorLogEvent_OsgiOs() {
        return (EAttribute)errorLogEventEClass.getEStructuralFeatures().get(8);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getErrorLogEvent_OsgiOsVersion() {
        return (EAttribute)errorLogEventEClass.getEStructuralFeatures().get(9);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getErrorLogEvent_OsgiArch() {
        return (EAttribute)errorLogEventEClass.getEStructuralFeatures().get(10);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getErrorLogEvent_Status() {
        return (EReference)errorLogEventEClass.getEStructuralFeatures().get(11);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getStatus() {
        return statusEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getStatus_PluginId() {
        return (EAttribute)statusEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getStatus_PluginVersion() {
        return (EAttribute)statusEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getStatus_Code() {
        return (EAttribute)statusEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getStatus_Severity() {
        return (EAttribute)statusEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getStatus_Message() {
        return (EAttribute)statusEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getStatus_Exception() {
        return (EReference)statusEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getStatus_Children() {
        return (EReference)statusEClass.getEStructuralFeatures().get(6);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getThrowable() {
        return throwableEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getThrowable_Classname() {
        return (EAttribute)throwableEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getThrowable_Message() {
        return (EAttribute)throwableEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getThrowable_Cause() {
        return (EReference)throwableEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getThrowable_StackTrace() {
        return (EReference)throwableEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getStackTraceElement() {
        return stackTraceElementEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getStackTraceElement_Filename() {
        return (EAttribute)stackTraceElementEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getStackTraceElement_Classname() {
        return (EAttribute)stackTraceElementEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getStackTraceElement_Methodname() {
        return (EAttribute)stackTraceElementEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getStackTraceElement_Line() {
        return (EAttribute)stackTraceElementEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getStackTraceElement_Native() {
        return (EAttribute)stackTraceElementEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getSeverity() {
        return severityEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getUUID() {
        return uuidEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModelFactory getModelFactory() {
        return (ModelFactory)getEFactoryInstance();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isCreated = false;

    /**
     * Creates the meta-model objects for the package.  This method is
     * guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void createPackageContents() {
        if (isCreated) return;
        isCreated = true;

        // Create classes and their features
        errorLogEventEClass = createEClass(ERROR_LOG_EVENT);
        createEAttribute(errorLogEventEClass, ERROR_LOG_EVENT__ANONYMOUS_ID);
        createEAttribute(errorLogEventEClass, ERROR_LOG_EVENT__EVENT_ID);
        createEAttribute(errorLogEventEClass, ERROR_LOG_EVENT__NAME);
        createEAttribute(errorLogEventEClass, ERROR_LOG_EVENT__EMAIL);
        createEAttribute(errorLogEventEClass, ERROR_LOG_EVENT__COMMENT);
        createEAttribute(errorLogEventEClass, ERROR_LOG_EVENT__ECLIPSE_BUILD_ID);
        createEAttribute(errorLogEventEClass, ERROR_LOG_EVENT__JAVA_RUNTIME_VERSION);
        createEAttribute(errorLogEventEClass, ERROR_LOG_EVENT__OSGI_WS);
        createEAttribute(errorLogEventEClass, ERROR_LOG_EVENT__OSGI_OS);
        createEAttribute(errorLogEventEClass, ERROR_LOG_EVENT__OSGI_OS_VERSION);
        createEAttribute(errorLogEventEClass, ERROR_LOG_EVENT__OSGI_ARCH);
        createEReference(errorLogEventEClass, ERROR_LOG_EVENT__STATUS);

        statusEClass = createEClass(STATUS);
        createEAttribute(statusEClass, STATUS__PLUGIN_ID);
        createEAttribute(statusEClass, STATUS__PLUGIN_VERSION);
        createEAttribute(statusEClass, STATUS__CODE);
        createEAttribute(statusEClass, STATUS__SEVERITY);
        createEAttribute(statusEClass, STATUS__MESSAGE);
        createEReference(statusEClass, STATUS__EXCEPTION);
        createEReference(statusEClass, STATUS__CHILDREN);

        throwableEClass = createEClass(THROWABLE);
        createEAttribute(throwableEClass, THROWABLE__CLASSNAME);
        createEAttribute(throwableEClass, THROWABLE__MESSAGE);
        createEReference(throwableEClass, THROWABLE__CAUSE);
        createEReference(throwableEClass, THROWABLE__STACK_TRACE);

        stackTraceElementEClass = createEClass(STACK_TRACE_ELEMENT);
        createEAttribute(stackTraceElementEClass, STACK_TRACE_ELEMENT__FILENAME);
        createEAttribute(stackTraceElementEClass, STACK_TRACE_ELEMENT__CLASSNAME);
        createEAttribute(stackTraceElementEClass, STACK_TRACE_ELEMENT__METHODNAME);
        createEAttribute(stackTraceElementEClass, STACK_TRACE_ELEMENT__LINE);
        createEAttribute(stackTraceElementEClass, STACK_TRACE_ELEMENT__NATIVE);

        // Create enums
        severityEEnum = createEEnum(SEVERITY);

        // Create data types
        uuidEDataType = createEDataType(UUID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private boolean isInitialized = false;

    /**
     * Complete the initialization of the package and its meta-model.  This
     * method is guarded to have no affect on any invocation but its first.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void initializePackageContents() {
        if (isInitialized) return;
        isInitialized = true;

        // Initialize package
        setName(eNAME);
        setNsPrefix(eNS_PREFIX);
        setNsURI(eNS_URI);

        // Obtain other dependent packages
        EcorePackage theEcorePackage = (EcorePackage)EPackage.Registry.INSTANCE.getEPackage(EcorePackage.eNS_URI);

        // Create type parameters

        // Set bounds for type parameters

        // Add supertypes to classes

        // Initialize classes, features, and operations; add parameters
        initEClass(errorLogEventEClass, ErrorLogEvent.class, "ErrorLogEvent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getErrorLogEvent_AnonymousId(), this.getUUID(), "anonymousId", null, 0, 1, ErrorLogEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getErrorLogEvent_EventId(), this.getUUID(), "eventId", null, 0, 1, ErrorLogEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getErrorLogEvent_Name(), theEcorePackage.getEString(), "name", null, 0, 1, ErrorLogEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getErrorLogEvent_Email(), theEcorePackage.getEString(), "email", null, 0, 1, ErrorLogEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getErrorLogEvent_Comment(), theEcorePackage.getEString(), "comment", null, 0, 1, ErrorLogEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getErrorLogEvent_EclipseBuildId(), theEcorePackage.getEString(), "eclipseBuildId", null, 0, 1, ErrorLogEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getErrorLogEvent_JavaRuntimeVersion(), theEcorePackage.getEString(), "javaRuntimeVersion", null, 0, 1, ErrorLogEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getErrorLogEvent_OsgiWs(), theEcorePackage.getEString(), "osgiWs", null, 0, 1, ErrorLogEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getErrorLogEvent_OsgiOs(), theEcorePackage.getEString(), "osgiOs", null, 0, 1, ErrorLogEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getErrorLogEvent_OsgiOsVersion(), theEcorePackage.getEString(), "osgiOsVersion", null, 0, 1, ErrorLogEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getErrorLogEvent_OsgiArch(), theEcorePackage.getEString(), "osgiArch", null, 0, 1, ErrorLogEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getErrorLogEvent_Status(), this.getStatus(), null, "status", null, 0, 1, ErrorLogEvent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(statusEClass, Status.class, "Status", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getStatus_PluginId(), theEcorePackage.getEString(), "pluginId", null, 0, 1, Status.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getStatus_PluginVersion(), theEcorePackage.getEString(), "pluginVersion", null, 0, 1, Status.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getStatus_Code(), theEcorePackage.getEInt(), "code", null, 0, 1, Status.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getStatus_Severity(), this.getSeverity(), "severity", null, 0, 1, Status.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getStatus_Message(), theEcorePackage.getEString(), "message", null, 0, 1, Status.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getStatus_Exception(), this.getThrowable(), null, "exception", null, 0, 1, Status.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getStatus_Children(), this.getStatus(), null, "children", null, 0, -1, Status.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(throwableEClass, org.eclipse.recommenders.internal.stacktraces.rcp.model.Throwable.class, "Throwable", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getThrowable_Classname(), theEcorePackage.getEString(), "classname", null, 0, 1, org.eclipse.recommenders.internal.stacktraces.rcp.model.Throwable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getThrowable_Message(), theEcorePackage.getEString(), "message", null, 0, 1, org.eclipse.recommenders.internal.stacktraces.rcp.model.Throwable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getThrowable_Cause(), this.getThrowable(), null, "cause", null, 0, 1, org.eclipse.recommenders.internal.stacktraces.rcp.model.Throwable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getThrowable_StackTrace(), this.getStackTraceElement(), null, "stackTrace", null, 0, -1, org.eclipse.recommenders.internal.stacktraces.rcp.model.Throwable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(stackTraceElementEClass, StackTraceElement.class, "StackTraceElement", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getStackTraceElement_Filename(), theEcorePackage.getEString(), "filename", null, 0, 1, StackTraceElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getStackTraceElement_Classname(), theEcorePackage.getEString(), "classname", null, 0, 1, StackTraceElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getStackTraceElement_Methodname(), theEcorePackage.getEString(), "methodname", null, 0, 1, StackTraceElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getStackTraceElement_Line(), theEcorePackage.getEInt(), "line", null, 0, 1, StackTraceElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getStackTraceElement_Native(), theEcorePackage.getEBoolean(), "native", null, 0, 1, StackTraceElement.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        // Initialize enums and add enum literals
        initEEnum(severityEEnum, Severity.class, "Severity");
        addEEnumLiteral(severityEEnum, Severity.OK);
        addEEnumLiteral(severityEEnum, Severity.CANCEL);
        addEEnumLiteral(severityEEnum, Severity.INFO);
        addEEnumLiteral(severityEEnum, Severity.WARN);
        addEEnumLiteral(severityEEnum, Severity.ERROR);
        addEEnumLiteral(severityEEnum, Severity.UNKNOWN);

        // Initialize data types
        initEDataType(uuidEDataType, java.util.UUID.class, "UUID", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

        // Create resource
        createResource(eNS_URI);
    }

} //ModelPackageImpl
