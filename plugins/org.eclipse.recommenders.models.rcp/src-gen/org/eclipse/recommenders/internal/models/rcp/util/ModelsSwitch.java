/**
 */
package org.eclipse.recommenders.internal.models.rcp.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

import org.eclipse.recommenders.internal.models.rcp.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see org.eclipse.recommenders.internal.models.rcp.ModelsPackage
 * @generated
 */
public class ModelsSwitch<T> extends Switch<T> {
    /**
     * The cached model package
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static ModelsPackage modelPackage;

    /**
     * Creates an instance of the switch.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModelsSwitch() {
        if (modelPackage == null) {
            modelPackage = ModelsPackage.eINSTANCE;
        }
    }

    /**
     * Checks whether this is a switch for the given package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @parameter ePackage the package in question.
     * @return whether this is a switch for the given package.
     * @generated
     */
    @Override
    protected boolean isSwitchFor(EPackage ePackage) {
        return ePackage == modelPackage;
    }

    /**
     * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the first non-null result returned by a <code>caseXXX</code> call.
     * @generated
     */
    @Override
    protected T doSwitch(int classifierID, EObject theEObject) {
        switch (classifierID) {
            case ModelsPackage.MODEL_REPOSITORY_SYSTEM: {
                ModelRepositorySystem modelRepositorySystem = (ModelRepositorySystem)theEObject;
                T result = caseModelRepositorySystem(modelRepositorySystem);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelsPackage.MODEL_REPOSITORY: {
                ModelRepository modelRepository = (ModelRepository)theEObject;
                T result = caseModelRepository(modelRepository);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelsPackage.MODEL_ARCHIVE_DESCRIPTOR: {
                ModelArchiveDescriptor modelArchiveDescriptor = (ModelArchiveDescriptor)theEObject;
                T result = caseModelArchiveDescriptor(modelArchiveDescriptor);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelsPackage.HANDLER: {
                Handler handler = (Handler)theEObject;
                T result = caseHandler(handler);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            case ModelsPackage.DOWNLOAD_EVENT: {
                DownloadEvent downloadEvent = (DownloadEvent)theEObject;
                T result = caseDownloadEvent(downloadEvent);
                if (result == null) result = defaultCase(theEObject);
                return result;
            }
            default: return defaultCase(theEObject);
        }
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Model Repository System</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Model Repository System</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseModelRepositorySystem(ModelRepositorySystem object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Model Repository</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Model Repository</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseModelRepository(ModelRepository object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Model Archive Descriptor</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Model Archive Descriptor</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseModelArchiveDescriptor(ModelArchiveDescriptor object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Handler</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Handler</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseHandler(Handler object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>Download Event</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>Download Event</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
     * @generated
     */
    public T caseDownloadEvent(DownloadEvent object) {
        return null;
    }

    /**
     * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
     * <!-- begin-user-doc -->
     * This implementation returns null;
     * returning a non-null result will terminate the switch, but this is the last case anyway.
     * <!-- end-user-doc -->
     * @param object the target of the switch.
     * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
     * @see #doSwitch(org.eclipse.emf.ecore.EObject)
     * @generated
     */
    @Override
    public T defaultCase(EObject object) {
        return null;
    }

} //ModelsSwitch
