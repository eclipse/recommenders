/**
 */
package org.eclipse.recommenders.internal.models.rcp;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Handler</b></em>'.
 * <!-- end-user-doc -->
 *
 *
 * @see org.eclipse.recommenders.internal.models.rcp.ModelsPackage#getHandler()
 * @model interface="true" abstract="true"
 * @generated
 */
public interface Handler extends EObject {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model
     * @generated
     */
    void handle(DownloadEvent event);

} // Handler
