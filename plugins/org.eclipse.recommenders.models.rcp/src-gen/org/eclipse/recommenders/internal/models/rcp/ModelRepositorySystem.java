/**
 */
package org.eclipse.recommenders.internal.models.rcp;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Model Repository System</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.recommenders.internal.models.rcp.ModelRepositorySystem#getRepositories <em>Repositories</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.recommenders.internal.models.rcp.ModelsPackage#getModelRepositorySystem()
 * @model
 * @generated
 */
public interface ModelRepositorySystem extends EObject {
    /**
     * Returns the value of the '<em><b>Repositories</b></em>' reference list.
     * The list contents are of type {@link org.eclipse.recommenders.internal.models.rcp.ModelRepository}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Repositories</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Repositories</em>' reference list.
     * @see org.eclipse.recommenders.internal.models.rcp.ModelsPackage#getModelRepositorySystem_Repositories()
     * @model
     * @generated
     */
    EList<ModelRepository> getRepositories();

} // ModelRepositorySystem
