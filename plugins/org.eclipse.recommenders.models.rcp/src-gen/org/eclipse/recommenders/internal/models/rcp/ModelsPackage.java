/**
 */
package org.eclipse.recommenders.internal.models.rcp;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
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
 * @see org.eclipse.recommenders.internal.models.rcp.ModelsFactory
 * @model kind="package"
 * @generated
 */
public interface ModelsPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "rcp";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://eclipse.org/recommenders/models";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "org.eclipse.recommenders.internal.models";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    ModelsPackage eINSTANCE = org.eclipse.recommenders.internal.models.rcp.impl.ModelsPackageImpl.init();

    /**
     * The meta object id for the '{@link org.eclipse.recommenders.internal.models.rcp.impl.ModelRepositorySystemImpl <em>Model Repository System</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelRepositorySystemImpl
     * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelsPackageImpl#getModelRepositorySystem()
     * @generated
     */
    int MODEL_REPOSITORY_SYSTEM = 0;

    /**
     * The feature id for the '<em><b>Repositories</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REPOSITORY_SYSTEM__REPOSITORIES = 0;

    /**
     * The number of structural features of the '<em>Model Repository System</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REPOSITORY_SYSTEM_FEATURE_COUNT = 1;

    /**
     * The number of operations of the '<em>Model Repository System</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REPOSITORY_SYSTEM_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.recommenders.internal.models.rcp.impl.ModelRepositoryImpl <em>Model Repository</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelRepositoryImpl
     * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelsPackageImpl#getModelRepository()
     * @generated
     */
    int MODEL_REPOSITORY = 1;

    /**
     * The feature id for the '<em><b>Url</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REPOSITORY__URL = 0;

    /**
     * The feature id for the '<em><b>Index</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REPOSITORY__INDEX = 1;

    /**
     * The feature id for the '<em><b>Repository</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REPOSITORY__REPOSITORY = 2;

    /**
     * The feature id for the '<em><b>Accessible</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REPOSITORY__ACCESSIBLE = 3;

    /**
     * The feature id for the '<em><b>Basedir</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REPOSITORY__BASEDIR = 4;

    /**
     * The feature id for the '<em><b>Requests</b></em>' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REPOSITORY__REQUESTS = 5;

    /**
     * The number of structural features of the '<em>Model Repository</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REPOSITORY_FEATURE_COUNT = 6;

    /**
     * The operation id for the '<em>Find</em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REPOSITORY___FIND__MODELCOORDINATE = 0;

    /**
     * The operation id for the '<em>Open</em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REPOSITORY___OPEN = 1;

    /**
     * The operation id for the '<em>Close</em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REPOSITORY___CLOSE = 2;

    /**
     * The operation id for the '<em>Download</em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REPOSITORY___DOWNLOAD__MODELARCHIVEDESCRIPTOR_HANDLER = 3;

    /**
     * The number of operations of the '<em>Model Repository</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_REPOSITORY_OPERATION_COUNT = 4;

    /**
     * The meta object id for the '{@link org.eclipse.recommenders.internal.models.rcp.impl.ModelArchiveDescriptorImpl <em>Model Archive Descriptor</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelArchiveDescriptorImpl
     * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelsPackageImpl#getModelArchiveDescriptor()
     * @generated
     */
    int MODEL_ARCHIVE_DESCRIPTOR = 2;

    /**
     * The feature id for the '<em><b>Location</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_ARCHIVE_DESCRIPTOR__LOCATION = 0;

    /**
     * The feature id for the '<em><b>Coordinate</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_ARCHIVE_DESCRIPTOR__COORDINATE = 1;

    /**
     * The feature id for the '<em><b>State</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_ARCHIVE_DESCRIPTOR__STATE = 2;

    /**
     * The feature id for the '<em><b>Origin</b></em>' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_ARCHIVE_DESCRIPTOR__ORIGIN = 3;

    /**
     * The number of structural features of the '<em>Model Archive Descriptor</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_ARCHIVE_DESCRIPTOR_FEATURE_COUNT = 4;

    /**
     * The number of operations of the '<em>Model Archive Descriptor</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int MODEL_ARCHIVE_DESCRIPTOR_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.recommenders.internal.models.rcp.Handler <em>Handler</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.recommenders.internal.models.rcp.Handler
     * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelsPackageImpl#getHandler()
     * @generated
     */
    int HANDLER = 3;

    /**
     * The number of structural features of the '<em>Handler</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HANDLER_FEATURE_COUNT = 0;

    /**
     * The operation id for the '<em>Handle</em>' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HANDLER___HANDLE__DOWNLOADEVENT = 0;

    /**
     * The number of operations of the '<em>Handler</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int HANDLER_OPERATION_COUNT = 1;

    /**
     * The meta object id for the '{@link org.eclipse.recommenders.internal.models.rcp.impl.DownloadEventImpl <em>Download Event</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.recommenders.internal.models.rcp.impl.DownloadEventImpl
     * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelsPackageImpl#getDownloadEvent()
     * @generated
     */
    int DOWNLOAD_EVENT = 4;

    /**
     * The number of structural features of the '<em>Download Event</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOWNLOAD_EVENT_FEATURE_COUNT = 0;

    /**
     * The number of operations of the '<em>Download Event</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int DOWNLOAD_EVENT_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.recommenders.internal.models.rcp.State <em>State</em>}' enum.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.recommenders.internal.models.rcp.State
     * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelsPackageImpl#getState()
     * @generated
     */
    int STATE = 5;

    /**
     * The meta object id for the '<em>EModel Coordinate</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.recommenders.models.ModelCoordinate
     * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelsPackageImpl#getEModelCoordinate()
     * @generated
     */
    int EMODEL_COORDINATE = 6;

    /**
     * The meta object id for the '<em>EURL</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.net.URL
     * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelsPackageImpl#getEURL()
     * @generated
     */
    int EURL = 7;

    /**
     * The meta object id for the '<em>EFile</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see java.io.File
     * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelsPackageImpl#getEFile()
     * @generated
     */
    int EFILE = 8;

    /**
     * The meta object id for the '<em>EModel Repository</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.recommenders.models.IModelRepository
     * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelsPackageImpl#getEModelRepository()
     * @generated
     */
    int EMODEL_REPOSITORY = 9;

    /**
     * The meta object id for the '<em>EModel Index</em>' data type.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.recommenders.models.IModelIndex
     * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelsPackageImpl#getEModelIndex()
     * @generated
     */
    int EMODEL_INDEX = 10;


    /**
     * Returns the meta object for class '{@link org.eclipse.recommenders.internal.models.rcp.ModelRepositorySystem <em>Model Repository System</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Model Repository System</em>'.
     * @see org.eclipse.recommenders.internal.models.rcp.ModelRepositorySystem
     * @generated
     */
    EClass getModelRepositorySystem();

    /**
     * Returns the meta object for the reference list '{@link org.eclipse.recommenders.internal.models.rcp.ModelRepositorySystem#getRepositories <em>Repositories</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Repositories</em>'.
     * @see org.eclipse.recommenders.internal.models.rcp.ModelRepositorySystem#getRepositories()
     * @see #getModelRepositorySystem()
     * @generated
     */
    EReference getModelRepositorySystem_Repositories();

    /**
     * Returns the meta object for class '{@link org.eclipse.recommenders.internal.models.rcp.ModelRepository <em>Model Repository</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Model Repository</em>'.
     * @see org.eclipse.recommenders.internal.models.rcp.ModelRepository
     * @generated
     */
    EClass getModelRepository();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.models.rcp.ModelRepository#getUrl <em>Url</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Url</em>'.
     * @see org.eclipse.recommenders.internal.models.rcp.ModelRepository#getUrl()
     * @see #getModelRepository()
     * @generated
     */
    EAttribute getModelRepository_Url();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.models.rcp.ModelRepository#getIndex <em>Index</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Index</em>'.
     * @see org.eclipse.recommenders.internal.models.rcp.ModelRepository#getIndex()
     * @see #getModelRepository()
     * @generated
     */
    EAttribute getModelRepository_Index();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.models.rcp.ModelRepository#getRepository <em>Repository</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Repository</em>'.
     * @see org.eclipse.recommenders.internal.models.rcp.ModelRepository#getRepository()
     * @see #getModelRepository()
     * @generated
     */
    EAttribute getModelRepository_Repository();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.models.rcp.ModelRepository#isAccessible <em>Accessible</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Accessible</em>'.
     * @see org.eclipse.recommenders.internal.models.rcp.ModelRepository#isAccessible()
     * @see #getModelRepository()
     * @generated
     */
    EAttribute getModelRepository_Accessible();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.models.rcp.ModelRepository#getBasedir <em>Basedir</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Basedir</em>'.
     * @see org.eclipse.recommenders.internal.models.rcp.ModelRepository#getBasedir()
     * @see #getModelRepository()
     * @generated
     */
    EAttribute getModelRepository_Basedir();

    /**
     * Returns the meta object for the reference list '{@link org.eclipse.recommenders.internal.models.rcp.ModelRepository#getRequests <em>Requests</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference list '<em>Requests</em>'.
     * @see org.eclipse.recommenders.internal.models.rcp.ModelRepository#getRequests()
     * @see #getModelRepository()
     * @generated
     */
    EReference getModelRepository_Requests();

    /**
     * Returns the meta object for the '{@link org.eclipse.recommenders.internal.models.rcp.ModelRepository#find(org.eclipse.recommenders.models.ModelCoordinate) <em>Find</em>}' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the '<em>Find</em>' operation.
     * @see org.eclipse.recommenders.internal.models.rcp.ModelRepository#find(org.eclipse.recommenders.models.ModelCoordinate)
     * @generated
     */
    EOperation getModelRepository__Find__ModelCoordinate();

    /**
     * Returns the meta object for the '{@link org.eclipse.recommenders.internal.models.rcp.ModelRepository#open() <em>Open</em>}' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the '<em>Open</em>' operation.
     * @see org.eclipse.recommenders.internal.models.rcp.ModelRepository#open()
     * @generated
     */
    EOperation getModelRepository__Open();

    /**
     * Returns the meta object for the '{@link org.eclipse.recommenders.internal.models.rcp.ModelRepository#close() <em>Close</em>}' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the '<em>Close</em>' operation.
     * @see org.eclipse.recommenders.internal.models.rcp.ModelRepository#close()
     * @generated
     */
    EOperation getModelRepository__Close();

    /**
     * Returns the meta object for the '{@link org.eclipse.recommenders.internal.models.rcp.ModelRepository#download(org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor, org.eclipse.recommenders.internal.models.rcp.Handler) <em>Download</em>}' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the '<em>Download</em>' operation.
     * @see org.eclipse.recommenders.internal.models.rcp.ModelRepository#download(org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor, org.eclipse.recommenders.internal.models.rcp.Handler)
     * @generated
     */
    EOperation getModelRepository__Download__ModelArchiveDescriptor_Handler();

    /**
     * Returns the meta object for class '{@link org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor <em>Model Archive Descriptor</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Model Archive Descriptor</em>'.
     * @see org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor
     * @generated
     */
    EClass getModelArchiveDescriptor();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor#getLocation <em>Location</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Location</em>'.
     * @see org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor#getLocation()
     * @see #getModelArchiveDescriptor()
     * @generated
     */
    EAttribute getModelArchiveDescriptor_Location();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor#getCoordinate <em>Coordinate</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>Coordinate</em>'.
     * @see org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor#getCoordinate()
     * @see #getModelArchiveDescriptor()
     * @generated
     */
    EAttribute getModelArchiveDescriptor_Coordinate();

    /**
     * Returns the meta object for the attribute '{@link org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor#getState <em>State</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute '<em>State</em>'.
     * @see org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor#getState()
     * @see #getModelArchiveDescriptor()
     * @generated
     */
    EAttribute getModelArchiveDescriptor_State();

    /**
     * Returns the meta object for the reference '{@link org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor#getOrigin <em>Origin</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the reference '<em>Origin</em>'.
     * @see org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor#getOrigin()
     * @see #getModelArchiveDescriptor()
     * @generated
     */
    EReference getModelArchiveDescriptor_Origin();

    /**
     * Returns the meta object for class '{@link org.eclipse.recommenders.internal.models.rcp.Handler <em>Handler</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Handler</em>'.
     * @see org.eclipse.recommenders.internal.models.rcp.Handler
     * @generated
     */
    EClass getHandler();

    /**
     * Returns the meta object for the '{@link org.eclipse.recommenders.internal.models.rcp.Handler#handle(org.eclipse.recommenders.internal.models.rcp.DownloadEvent) <em>Handle</em>}' operation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the '<em>Handle</em>' operation.
     * @see org.eclipse.recommenders.internal.models.rcp.Handler#handle(org.eclipse.recommenders.internal.models.rcp.DownloadEvent)
     * @generated
     */
    EOperation getHandler__Handle__DownloadEvent();

    /**
     * Returns the meta object for class '{@link org.eclipse.recommenders.internal.models.rcp.DownloadEvent <em>Download Event</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Download Event</em>'.
     * @see org.eclipse.recommenders.internal.models.rcp.DownloadEvent
     * @generated
     */
    EClass getDownloadEvent();

    /**
     * Returns the meta object for enum '{@link org.eclipse.recommenders.internal.models.rcp.State <em>State</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for enum '<em>State</em>'.
     * @see org.eclipse.recommenders.internal.models.rcp.State
     * @generated
     */
    EEnum getState();

    /**
     * Returns the meta object for data type '{@link org.eclipse.recommenders.models.ModelCoordinate <em>EModel Coordinate</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>EModel Coordinate</em>'.
     * @see org.eclipse.recommenders.models.ModelCoordinate
     * @model instanceClass="org.eclipse.recommenders.models.ModelCoordinate"
     * @generated
     */
    EDataType getEModelCoordinate();

    /**
     * Returns the meta object for data type '{@link java.net.URL <em>EURL</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>EURL</em>'.
     * @see java.net.URL
     * @model instanceClass="java.net.URL"
     * @generated
     */
    EDataType getEURL();

    /**
     * Returns the meta object for data type '{@link java.io.File <em>EFile</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>EFile</em>'.
     * @see java.io.File
     * @model instanceClass="java.io.File"
     * @generated
     */
    EDataType getEFile();

    /**
     * Returns the meta object for data type '{@link org.eclipse.recommenders.models.IModelRepository <em>EModel Repository</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>EModel Repository</em>'.
     * @see org.eclipse.recommenders.models.IModelRepository
     * @model instanceClass="org.eclipse.recommenders.models.IModelRepository"
     * @generated
     */
    EDataType getEModelRepository();

    /**
     * Returns the meta object for data type '{@link org.eclipse.recommenders.models.IModelIndex <em>EModel Index</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for data type '<em>EModel Index</em>'.
     * @see org.eclipse.recommenders.models.IModelIndex
     * @model instanceClass="org.eclipse.recommenders.models.IModelIndex"
     * @generated
     */
    EDataType getEModelIndex();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    ModelsFactory getModelsFactory();

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
         * The meta object literal for the '{@link org.eclipse.recommenders.internal.models.rcp.impl.ModelRepositorySystemImpl <em>Model Repository System</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelRepositorySystemImpl
         * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelsPackageImpl#getModelRepositorySystem()
         * @generated
         */
        EClass MODEL_REPOSITORY_SYSTEM = eINSTANCE.getModelRepositorySystem();

        /**
         * The meta object literal for the '<em><b>Repositories</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MODEL_REPOSITORY_SYSTEM__REPOSITORIES = eINSTANCE.getModelRepositorySystem_Repositories();

        /**
         * The meta object literal for the '{@link org.eclipse.recommenders.internal.models.rcp.impl.ModelRepositoryImpl <em>Model Repository</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelRepositoryImpl
         * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelsPackageImpl#getModelRepository()
         * @generated
         */
        EClass MODEL_REPOSITORY = eINSTANCE.getModelRepository();

        /**
         * The meta object literal for the '<em><b>Url</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MODEL_REPOSITORY__URL = eINSTANCE.getModelRepository_Url();

        /**
         * The meta object literal for the '<em><b>Index</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MODEL_REPOSITORY__INDEX = eINSTANCE.getModelRepository_Index();

        /**
         * The meta object literal for the '<em><b>Repository</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MODEL_REPOSITORY__REPOSITORY = eINSTANCE.getModelRepository_Repository();

        /**
         * The meta object literal for the '<em><b>Accessible</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MODEL_REPOSITORY__ACCESSIBLE = eINSTANCE.getModelRepository_Accessible();

        /**
         * The meta object literal for the '<em><b>Basedir</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MODEL_REPOSITORY__BASEDIR = eINSTANCE.getModelRepository_Basedir();

        /**
         * The meta object literal for the '<em><b>Requests</b></em>' reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MODEL_REPOSITORY__REQUESTS = eINSTANCE.getModelRepository_Requests();

        /**
         * The meta object literal for the '<em><b>Find</b></em>' operation.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EOperation MODEL_REPOSITORY___FIND__MODELCOORDINATE = eINSTANCE.getModelRepository__Find__ModelCoordinate();

        /**
         * The meta object literal for the '<em><b>Open</b></em>' operation.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EOperation MODEL_REPOSITORY___OPEN = eINSTANCE.getModelRepository__Open();

        /**
         * The meta object literal for the '<em><b>Close</b></em>' operation.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EOperation MODEL_REPOSITORY___CLOSE = eINSTANCE.getModelRepository__Close();

        /**
         * The meta object literal for the '<em><b>Download</b></em>' operation.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EOperation MODEL_REPOSITORY___DOWNLOAD__MODELARCHIVEDESCRIPTOR_HANDLER = eINSTANCE.getModelRepository__Download__ModelArchiveDescriptor_Handler();

        /**
         * The meta object literal for the '{@link org.eclipse.recommenders.internal.models.rcp.impl.ModelArchiveDescriptorImpl <em>Model Archive Descriptor</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelArchiveDescriptorImpl
         * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelsPackageImpl#getModelArchiveDescriptor()
         * @generated
         */
        EClass MODEL_ARCHIVE_DESCRIPTOR = eINSTANCE.getModelArchiveDescriptor();

        /**
         * The meta object literal for the '<em><b>Location</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MODEL_ARCHIVE_DESCRIPTOR__LOCATION = eINSTANCE.getModelArchiveDescriptor_Location();

        /**
         * The meta object literal for the '<em><b>Coordinate</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MODEL_ARCHIVE_DESCRIPTOR__COORDINATE = eINSTANCE.getModelArchiveDescriptor_Coordinate();

        /**
         * The meta object literal for the '<em><b>State</b></em>' attribute feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute MODEL_ARCHIVE_DESCRIPTOR__STATE = eINSTANCE.getModelArchiveDescriptor_State();

        /**
         * The meta object literal for the '<em><b>Origin</b></em>' reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference MODEL_ARCHIVE_DESCRIPTOR__ORIGIN = eINSTANCE.getModelArchiveDescriptor_Origin();

        /**
         * The meta object literal for the '{@link org.eclipse.recommenders.internal.models.rcp.Handler <em>Handler</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.recommenders.internal.models.rcp.Handler
         * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelsPackageImpl#getHandler()
         * @generated
         */
        EClass HANDLER = eINSTANCE.getHandler();

        /**
         * The meta object literal for the '<em><b>Handle</b></em>' operation.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EOperation HANDLER___HANDLE__DOWNLOADEVENT = eINSTANCE.getHandler__Handle__DownloadEvent();

        /**
         * The meta object literal for the '{@link org.eclipse.recommenders.internal.models.rcp.impl.DownloadEventImpl <em>Download Event</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.recommenders.internal.models.rcp.impl.DownloadEventImpl
         * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelsPackageImpl#getDownloadEvent()
         * @generated
         */
        EClass DOWNLOAD_EVENT = eINSTANCE.getDownloadEvent();

        /**
         * The meta object literal for the '{@link org.eclipse.recommenders.internal.models.rcp.State <em>State</em>}' enum.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.recommenders.internal.models.rcp.State
         * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelsPackageImpl#getState()
         * @generated
         */
        EEnum STATE = eINSTANCE.getState();

        /**
         * The meta object literal for the '<em>EModel Coordinate</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.recommenders.models.ModelCoordinate
         * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelsPackageImpl#getEModelCoordinate()
         * @generated
         */
        EDataType EMODEL_COORDINATE = eINSTANCE.getEModelCoordinate();

        /**
         * The meta object literal for the '<em>EURL</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.net.URL
         * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelsPackageImpl#getEURL()
         * @generated
         */
        EDataType EURL = eINSTANCE.getEURL();

        /**
         * The meta object literal for the '<em>EFile</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see java.io.File
         * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelsPackageImpl#getEFile()
         * @generated
         */
        EDataType EFILE = eINSTANCE.getEFile();

        /**
         * The meta object literal for the '<em>EModel Repository</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.recommenders.models.IModelRepository
         * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelsPackageImpl#getEModelRepository()
         * @generated
         */
        EDataType EMODEL_REPOSITORY = eINSTANCE.getEModelRepository();

        /**
         * The meta object literal for the '<em>EModel Index</em>' data type.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.recommenders.models.IModelIndex
         * @see org.eclipse.recommenders.internal.models.rcp.impl.ModelsPackageImpl#getEModelIndex()
         * @generated
         */
        EDataType EMODEL_INDEX = eINSTANCE.getEModelIndex();

    }

} //ModelsPackage
