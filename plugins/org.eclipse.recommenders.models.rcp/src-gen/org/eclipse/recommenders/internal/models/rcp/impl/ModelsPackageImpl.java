/**
 */
package org.eclipse.recommenders.internal.models.rcp.impl;

import java.io.File;
import java.net.URL;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import org.eclipse.recommenders.internal.models.rcp.DownloadEvent;
import org.eclipse.recommenders.internal.models.rcp.Handler;
import org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor;
import org.eclipse.recommenders.internal.models.rcp.ModelRepository;
import org.eclipse.recommenders.internal.models.rcp.ModelRepositorySystem;
import org.eclipse.recommenders.internal.models.rcp.ModelsFactory;
import org.eclipse.recommenders.internal.models.rcp.ModelsPackage;
import org.eclipse.recommenders.internal.models.rcp.State;
import org.eclipse.recommenders.models.IModelIndex;
import org.eclipse.recommenders.models.IModelRepository;
import org.eclipse.recommenders.models.ModelCoordinate;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ModelsPackageImpl extends EPackageImpl implements ModelsPackage {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass modelRepositorySystemEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass modelRepositoryEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass modelArchiveDescriptorEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass handlerEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EClass downloadEventEClass = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EEnum stateEEnum = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType eModelCoordinateEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType eurlEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType eFileEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType eModelRepositoryEDataType = null;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    private EDataType eModelIndexEDataType = null;

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
     * @see org.eclipse.recommenders.internal.models.rcp.ModelsPackage#eNS_URI
     * @see #init()
     * @generated
     */
    private ModelsPackageImpl() {
        super(eNS_URI, ModelsFactory.eINSTANCE);
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
     * <p>This method is used to initialize {@link ModelsPackage#eINSTANCE} when that field is accessed.
     * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #eNS_URI
     * @see #createPackageContents()
     * @see #initializePackageContents()
     * @generated
     */
    public static ModelsPackage init() {
        if (isInited) return (ModelsPackage)EPackage.Registry.INSTANCE.getEPackage(ModelsPackage.eNS_URI);

        // Obtain or create and register package
        ModelsPackageImpl theModelsPackage = (ModelsPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof ModelsPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new ModelsPackageImpl());

        isInited = true;

        // Create package meta-data objects
        theModelsPackage.createPackageContents();

        // Initialize created meta-data
        theModelsPackage.initializePackageContents();

        // Mark meta-data to indicate it can't be changed
        theModelsPackage.freeze();

  
        // Update the registry and return the package
        EPackage.Registry.INSTANCE.put(ModelsPackage.eNS_URI, theModelsPackage);
        return theModelsPackage;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getModelRepositorySystem() {
        return modelRepositorySystemEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getModelRepositorySystem_Repositories() {
        return (EReference)modelRepositorySystemEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getModelRepository() {
        return modelRepositoryEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelRepository_Url() {
        return (EAttribute)modelRepositoryEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelRepository_Index() {
        return (EAttribute)modelRepositoryEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelRepository_Repository() {
        return (EAttribute)modelRepositoryEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelRepository_Accessible() {
        return (EAttribute)modelRepositoryEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelRepository_Basedir() {
        return (EAttribute)modelRepositoryEClass.getEStructuralFeatures().get(4);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getModelRepository_Requests() {
        return (EReference)modelRepositoryEClass.getEStructuralFeatures().get(5);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EOperation getModelRepository__Find__ModelCoordinate() {
        return modelRepositoryEClass.getEOperations().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EOperation getModelRepository__Open() {
        return modelRepositoryEClass.getEOperations().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EOperation getModelRepository__Close() {
        return modelRepositoryEClass.getEOperations().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EOperation getModelRepository__Download__ModelArchiveDescriptor_Handler() {
        return modelRepositoryEClass.getEOperations().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getModelArchiveDescriptor() {
        return modelArchiveDescriptorEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelArchiveDescriptor_Location() {
        return (EAttribute)modelArchiveDescriptorEClass.getEStructuralFeatures().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelArchiveDescriptor_Coordinate() {
        return (EAttribute)modelArchiveDescriptorEClass.getEStructuralFeatures().get(1);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EAttribute getModelArchiveDescriptor_State() {
        return (EAttribute)modelArchiveDescriptorEClass.getEStructuralFeatures().get(2);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EReference getModelArchiveDescriptor_Origin() {
        return (EReference)modelArchiveDescriptorEClass.getEStructuralFeatures().get(3);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getHandler() {
        return handlerEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EOperation getHandler__Handle__DownloadEvent() {
        return handlerEClass.getEOperations().get(0);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EClass getDownloadEvent() {
        return downloadEventEClass;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EEnum getState() {
        return stateEEnum;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getEModelCoordinate() {
        return eModelCoordinateEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getEURL() {
        return eurlEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getEFile() {
        return eFileEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getEModelRepository() {
        return eModelRepositoryEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EDataType getEModelIndex() {
        return eModelIndexEDataType;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModelsFactory getModelsFactory() {
        return (ModelsFactory)getEFactoryInstance();
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
        modelRepositorySystemEClass = createEClass(MODEL_REPOSITORY_SYSTEM);
        createEReference(modelRepositorySystemEClass, MODEL_REPOSITORY_SYSTEM__REPOSITORIES);

        modelRepositoryEClass = createEClass(MODEL_REPOSITORY);
        createEAttribute(modelRepositoryEClass, MODEL_REPOSITORY__URL);
        createEAttribute(modelRepositoryEClass, MODEL_REPOSITORY__INDEX);
        createEAttribute(modelRepositoryEClass, MODEL_REPOSITORY__REPOSITORY);
        createEAttribute(modelRepositoryEClass, MODEL_REPOSITORY__ACCESSIBLE);
        createEAttribute(modelRepositoryEClass, MODEL_REPOSITORY__BASEDIR);
        createEReference(modelRepositoryEClass, MODEL_REPOSITORY__REQUESTS);
        createEOperation(modelRepositoryEClass, MODEL_REPOSITORY___FIND__MODELCOORDINATE);
        createEOperation(modelRepositoryEClass, MODEL_REPOSITORY___OPEN);
        createEOperation(modelRepositoryEClass, MODEL_REPOSITORY___CLOSE);
        createEOperation(modelRepositoryEClass, MODEL_REPOSITORY___DOWNLOAD__MODELARCHIVEDESCRIPTOR_HANDLER);

        modelArchiveDescriptorEClass = createEClass(MODEL_ARCHIVE_DESCRIPTOR);
        createEAttribute(modelArchiveDescriptorEClass, MODEL_ARCHIVE_DESCRIPTOR__LOCATION);
        createEAttribute(modelArchiveDescriptorEClass, MODEL_ARCHIVE_DESCRIPTOR__COORDINATE);
        createEAttribute(modelArchiveDescriptorEClass, MODEL_ARCHIVE_DESCRIPTOR__STATE);
        createEReference(modelArchiveDescriptorEClass, MODEL_ARCHIVE_DESCRIPTOR__ORIGIN);

        handlerEClass = createEClass(HANDLER);
        createEOperation(handlerEClass, HANDLER___HANDLE__DOWNLOADEVENT);

        downloadEventEClass = createEClass(DOWNLOAD_EVENT);

        // Create enums
        stateEEnum = createEEnum(STATE);

        // Create data types
        eModelCoordinateEDataType = createEDataType(EMODEL_COORDINATE);
        eurlEDataType = createEDataType(EURL);
        eFileEDataType = createEDataType(EFILE);
        eModelRepositoryEDataType = createEDataType(EMODEL_REPOSITORY);
        eModelIndexEDataType = createEDataType(EMODEL_INDEX);
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

        // Create type parameters

        // Set bounds for type parameters

        // Add supertypes to classes

        // Initialize classes, features, and operations; add parameters
        initEClass(modelRepositorySystemEClass, ModelRepositorySystem.class, "ModelRepositorySystem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEReference(getModelRepositorySystem_Repositories(), this.getModelRepository(), null, "repositories", null, 0, -1, ModelRepositorySystem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(modelRepositoryEClass, ModelRepository.class, "ModelRepository", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getModelRepository_Url(), this.getEURL(), "url", null, 0, 1, ModelRepository.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getModelRepository_Index(), this.getEModelIndex(), "index", null, 0, 1, ModelRepository.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getModelRepository_Repository(), this.getEModelRepository(), "repository", null, 0, 1, ModelRepository.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, !IS_ORDERED);
        initEAttribute(getModelRepository_Accessible(), ecorePackage.getEBoolean(), "accessible", "false", 1, 1, ModelRepository.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, IS_DERIVED, !IS_ORDERED);
        initEAttribute(getModelRepository_Basedir(), this.getEFile(), "basedir", "", 0, 1, ModelRepository.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getModelRepository_Requests(), this.getModelArchiveDescriptor(), null, "requests", null, 0, -1, ModelRepository.class, IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        EOperation op = initEOperation(getModelRepository__Find__ModelCoordinate(), this.getModelArchiveDescriptor(), "find", 1, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, this.getEModelCoordinate(), "mc", 0, 1, IS_UNIQUE, IS_ORDERED);

        initEOperation(getModelRepository__Open(), null, "open", 0, 1, IS_UNIQUE, IS_ORDERED);

        initEOperation(getModelRepository__Close(), null, "close", 0, 1, IS_UNIQUE, IS_ORDERED);

        op = initEOperation(getModelRepository__Download__ModelArchiveDescriptor_Handler(), null, "download", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, this.getModelArchiveDescriptor(), "descriptor", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, this.getHandler(), "listener", 1, 1, IS_UNIQUE, IS_ORDERED);

        initEClass(modelArchiveDescriptorEClass, ModelArchiveDescriptor.class, "ModelArchiveDescriptor", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
        initEAttribute(getModelArchiveDescriptor_Location(), this.getEFile(), "location", null, 0, 1, ModelArchiveDescriptor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getModelArchiveDescriptor_Coordinate(), this.getEModelCoordinate(), "coordinate", null, 0, 1, ModelArchiveDescriptor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getModelArchiveDescriptor_State(), this.getState(), "state", null, 0, 1, ModelArchiveDescriptor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getModelArchiveDescriptor_Origin(), this.getModelRepository(), null, "origin", null, 0, 1, ModelArchiveDescriptor.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

        initEClass(handlerEClass, Handler.class, "Handler", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        op = initEOperation(getHandler__Handle__DownloadEvent(), null, "handle", 0, 1, IS_UNIQUE, IS_ORDERED);
        addEParameter(op, this.getDownloadEvent(), "event", 0, 1, IS_UNIQUE, IS_ORDERED);

        initEClass(downloadEventEClass, DownloadEvent.class, "DownloadEvent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

        // Initialize enums and add enum literals
        initEEnum(stateEEnum, State.class, "State");
        addEEnumLiteral(stateEEnum, State.ABSENT);
        addEEnumLiteral(stateEEnum, State.DOWNLOADING);
        addEEnumLiteral(stateEEnum, State.CACHED);
        addEEnumLiteral(stateEEnum, State.ACTIVE);

        // Initialize data types
        initEDataType(eModelCoordinateEDataType, ModelCoordinate.class, "EModelCoordinate", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
        initEDataType(eurlEDataType, URL.class, "EURL", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
        initEDataType(eFileEDataType, File.class, "EFile", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
        initEDataType(eModelRepositoryEDataType, IModelRepository.class, "EModelRepository", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
        initEDataType(eModelIndexEDataType, IModelIndex.class, "EModelIndex", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);

        // Create resource
        createResource(eNS_URI);
    }

} //ModelsPackageImpl
