/**
 */
package org.eclipse.recommenders.internal.models.rcp;

import java.io.File;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.recommenders.models.ModelCoordinate;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Model Archive Descriptor</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor#getLocation <em>Location</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor#getCoordinate <em>Coordinate</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor#getState <em>State</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor#getOrigin <em>Origin</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.recommenders.internal.models.rcp.ModelsPackage#getModelArchiveDescriptor()
 * @model
 * @generated
 */
public interface ModelArchiveDescriptor extends EObject {
    /**
     * Returns the value of the '<em><b>Location</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Location</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Location</em>' attribute.
     * @see #setLocation(File)
     * @see org.eclipse.recommenders.internal.models.rcp.ModelsPackage#getModelArchiveDescriptor_Location()
     * @model dataType="org.eclipse.recommenders.internal.models.rcp.EFile"
     * @generated
     */
    File getLocation();

    /**
     * Sets the value of the '{@link org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor#getLocation <em>Location</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Location</em>' attribute.
     * @see #getLocation()
     * @generated
     */
    void setLocation(File value);

    /**
     * Returns the value of the '<em><b>Coordinate</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Coordinate</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Coordinate</em>' attribute.
     * @see #setCoordinate(ModelCoordinate)
     * @see org.eclipse.recommenders.internal.models.rcp.ModelsPackage#getModelArchiveDescriptor_Coordinate()
     * @model dataType="org.eclipse.recommenders.internal.models.rcp.EModelCoordinate"
     * @generated
     */
    ModelCoordinate getCoordinate();

    /**
     * Sets the value of the '{@link org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor#getCoordinate <em>Coordinate</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Coordinate</em>' attribute.
     * @see #getCoordinate()
     * @generated
     */
    void setCoordinate(ModelCoordinate value);

    /**
     * Returns the value of the '<em><b>State</b></em>' attribute.
     * The literals are from the enumeration {@link org.eclipse.recommenders.internal.models.rcp.State}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>State</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>State</em>' attribute.
     * @see org.eclipse.recommenders.internal.models.rcp.State
     * @see #setState(State)
     * @see org.eclipse.recommenders.internal.models.rcp.ModelsPackage#getModelArchiveDescriptor_State()
     * @model
     * @generated
     */
    State getState();

    /**
     * Sets the value of the '{@link org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor#getState <em>State</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>State</em>' attribute.
     * @see org.eclipse.recommenders.internal.models.rcp.State
     * @see #getState()
     * @generated
     */
    void setState(State value);

    /**
     * Returns the value of the '<em><b>Origin</b></em>' reference.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Origin</em>' reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * <!-- begin-model-doc -->
     * The model repository that created this descriptor
     * <!-- end-model-doc -->
     * @return the value of the '<em>Origin</em>' reference.
     * @see #setOrigin(ModelRepository)
     * @see org.eclipse.recommenders.internal.models.rcp.ModelsPackage#getModelArchiveDescriptor_Origin()
     * @model
     * @generated
     */
    ModelRepository getOrigin();

    /**
     * Sets the value of the '{@link org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor#getOrigin <em>Origin</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Origin</em>' reference.
     * @see #getOrigin()
     * @generated
     */
    void setOrigin(ModelRepository value);

} // ModelArchiveDescriptor
