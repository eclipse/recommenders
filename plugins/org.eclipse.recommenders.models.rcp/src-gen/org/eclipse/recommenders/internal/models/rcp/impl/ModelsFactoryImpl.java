/**
 */
package org.eclipse.recommenders.internal.models.rcp.impl;

import java.io.File;

import java.net.URL;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

import org.eclipse.recommenders.internal.models.rcp.*;

import org.eclipse.recommenders.models.IModelIndex;
import org.eclipse.recommenders.models.IModelRepository;
import org.eclipse.recommenders.models.ModelCoordinate;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ModelsFactoryImpl extends EFactoryImpl implements ModelsFactory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ModelsFactory init() {
        try {
            ModelsFactory theModelsFactory = (ModelsFactory)EPackage.Registry.INSTANCE.getEFactory(ModelsPackage.eNS_URI);
            if (theModelsFactory != null) {
                return theModelsFactory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new ModelsFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModelsFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
            case ModelsPackage.MODEL_REPOSITORY_SYSTEM: return createModelRepositorySystem();
            case ModelsPackage.MODEL_REPOSITORY: return createModelRepository();
            case ModelsPackage.MODEL_ARCHIVE_DESCRIPTOR: return createModelArchiveDescriptor();
            case ModelsPackage.DOWNLOAD_EVENT: return createDownloadEvent();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object createFromString(EDataType eDataType, String initialValue) {
        switch (eDataType.getClassifierID()) {
            case ModelsPackage.STATE:
                return createStateFromString(eDataType, initialValue);
            case ModelsPackage.EMODEL_COORDINATE:
                return createEModelCoordinateFromString(eDataType, initialValue);
            case ModelsPackage.EURL:
                return createEURLFromString(eDataType, initialValue);
            case ModelsPackage.EFILE:
                return createEFileFromString(eDataType, initialValue);
            case ModelsPackage.EMODEL_REPOSITORY:
                return createEModelRepositoryFromString(eDataType, initialValue);
            case ModelsPackage.EMODEL_INDEX:
                return createEModelIndexFromString(eDataType, initialValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String convertToString(EDataType eDataType, Object instanceValue) {
        switch (eDataType.getClassifierID()) {
            case ModelsPackage.STATE:
                return convertStateToString(eDataType, instanceValue);
            case ModelsPackage.EMODEL_COORDINATE:
                return convertEModelCoordinateToString(eDataType, instanceValue);
            case ModelsPackage.EURL:
                return convertEURLToString(eDataType, instanceValue);
            case ModelsPackage.EFILE:
                return convertEFileToString(eDataType, instanceValue);
            case ModelsPackage.EMODEL_REPOSITORY:
                return convertEModelRepositoryToString(eDataType, instanceValue);
            case ModelsPackage.EMODEL_INDEX:
                return convertEModelIndexToString(eDataType, instanceValue);
            default:
                throw new IllegalArgumentException("The datatype '" + eDataType.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModelRepositorySystem createModelRepositorySystem() {
        ModelRepositorySystemImpl modelRepositorySystem = new ModelRepositorySystemImpl();
        return modelRepositorySystem;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModelRepository createModelRepository() {
        ModelRepositoryImpl modelRepository = new ModelRepositoryImpl();
        return modelRepository;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModelArchiveDescriptor createModelArchiveDescriptor() {
        ModelArchiveDescriptorImpl modelArchiveDescriptor = new ModelArchiveDescriptorImpl();
        return modelArchiveDescriptor;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public DownloadEvent createDownloadEvent() {
        DownloadEventImpl downloadEvent = new DownloadEventImpl();
        return downloadEvent;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public State createStateFromString(EDataType eDataType, String initialValue) {
        State result = State.get(initialValue);
        if (result == null) throw new IllegalArgumentException("The value '" + initialValue + "' is not a valid enumerator of '" + eDataType.getName() + "'");
        return result;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertStateToString(EDataType eDataType, Object instanceValue) {
        return instanceValue == null ? null : instanceValue.toString();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModelCoordinate createEModelCoordinateFromString(EDataType eDataType, String initialValue) {
        return (ModelCoordinate)super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertEModelCoordinateToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public URL createEURLFromString(EDataType eDataType, String initialValue) {
        return (URL)super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertEURLToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public File createEFileFromString(EDataType eDataType, String initialValue) {
        return (File)super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertEFileToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public IModelRepository createEModelRepositoryFromString(EDataType eDataType, String initialValue) {
        return (IModelRepository)super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertEModelRepositoryToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public IModelIndex createEModelIndexFromString(EDataType eDataType, String initialValue) {
        return (IModelIndex)super.createFromString(eDataType, initialValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public String convertEModelIndexToString(EDataType eDataType, Object instanceValue) {
        return super.convertToString(eDataType, instanceValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModelsPackage getModelsPackage() {
        return (ModelsPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static ModelsPackage getPackage() {
        return ModelsPackage.eINSTANCE;
    }

} //ModelsFactoryImpl
