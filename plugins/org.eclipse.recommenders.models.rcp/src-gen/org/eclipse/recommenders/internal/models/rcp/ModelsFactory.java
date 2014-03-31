/**
 */
package org.eclipse.recommenders.internal.models.rcp;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.recommenders.internal.models.rcp.ModelsPackage
 * @generated
 */
public interface ModelsFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    ModelsFactory eINSTANCE = org.eclipse.recommenders.internal.models.rcp.impl.ModelsFactoryImpl.init();

    /**
     * Returns a new object of class '<em>Model Repository System</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Model Repository System</em>'.
     * @generated
     */
    ModelRepositorySystem createModelRepositorySystem();

    /**
     * Returns a new object of class '<em>Model Repository</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Model Repository</em>'.
     * @generated
     */
    ModelRepository createModelRepository();

    /**
     * Returns a new object of class '<em>Model Archive Descriptor</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Model Archive Descriptor</em>'.
     * @generated
     */
    ModelArchiveDescriptor createModelArchiveDescriptor();

    /**
     * Returns a new object of class '<em>Download Event</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Download Event</em>'.
     * @generated
     */
    DownloadEvent createDownloadEvent();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    ModelsPackage getModelsPackage();

} //ModelsFactory
