/**
 */
package org.eclipse.recommenders.internal.models.rcp;

import java.io.File;
import java.net.URL;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.recommenders.models.IModelIndex;
import org.eclipse.recommenders.models.IModelRepository;
import org.eclipse.recommenders.models.ModelCoordinate;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Model Repository</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.recommenders.internal.models.rcp.ModelRepository#getUrl <em>Url</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.models.rcp.ModelRepository#getIndex <em>Index</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.models.rcp.ModelRepository#getRepository <em>Repository</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.models.rcp.ModelRepository#isAccessible <em>Accessible</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.models.rcp.ModelRepository#getBasedir <em>Basedir</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.models.rcp.ModelRepository#getRequests <em>Requests</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.recommenders.internal.models.rcp.ModelsPackage#getModelRepository()
 * @model
 * @generated
 */
public interface ModelRepository extends EObject {
    /**
     * Returns the value of the '<em><b>Url</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Url</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Url</em>' attribute.
     * @see #setUrl(URL)
     * @see org.eclipse.recommenders.internal.models.rcp.ModelsPackage#getModelRepository_Url()
     * @model dataType="org.eclipse.recommenders.internal.models.rcp.EURL"
     * @generated
     */
    URL getUrl();

    /**
     * Sets the value of the '{@link org.eclipse.recommenders.internal.models.rcp.ModelRepository#getUrl <em>Url</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Url</em>' attribute.
     * @see #getUrl()
     * @generated
     */
    void setUrl(URL value);

    /**
     * Returns the value of the '<em><b>Index</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Index</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Index</em>' attribute.
     * @see #setIndex(IModelIndex)
     * @see org.eclipse.recommenders.internal.models.rcp.ModelsPackage#getModelRepository_Index()
     * @model dataType="org.eclipse.recommenders.internal.models.rcp.EModelIndex"
     * @generated
     */
    IModelIndex getIndex();

    /**
     * Sets the value of the '{@link org.eclipse.recommenders.internal.models.rcp.ModelRepository#getIndex <em>Index</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Index</em>' attribute.
     * @see #getIndex()
     * @generated
     */
    void setIndex(IModelIndex value);

    /**
     * Returns the value of the '<em><b>Repository</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Repository</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Repository</em>' attribute.
     * @see #setRepository(IModelRepository)
     * @see org.eclipse.recommenders.internal.models.rcp.ModelsPackage#getModelRepository_Repository()
     * @model unique="false" dataType="org.eclipse.recommenders.internal.models.rcp.EModelRepository" ordered="false"
     * @generated
     */
    IModelRepository getRepository();

    /**
     * Sets the value of the '{@link org.eclipse.recommenders.internal.models.rcp.ModelRepository#getRepository <em>Repository</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Repository</em>' attribute.
     * @see #getRepository()
     * @generated
     */
    void setRepository(IModelRepository value);

    /**
     * Returns the value of the '<em><b>Accessible</b></em>' attribute.
     * The default value is <code>"false"</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Accessible</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Accessible</em>' attribute.
     * @see #setAccessible(boolean)
     * @see org.eclipse.recommenders.internal.models.rcp.ModelsPackage#getModelRepository_Accessible()
     * @model default="false" unique="false" required="true" transient="true" derived="true" ordered="false"
     * @generated
     */
    boolean isAccessible();

    /**
     * Sets the value of the '{@link org.eclipse.recommenders.internal.models.rcp.ModelRepository#isAccessible <em>Accessible</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Accessible</em>' attribute.
     * @see #isAccessible()
     * @generated
     */
    void setAccessible(boolean value);

    /**
     * Returns the value of the '<em><b>Basedir</b></em>' attribute.
     * The default value is <code>""</code>.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Basedir</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Basedir</em>' attribute.
     * @see #setBasedir(File)
     * @see org.eclipse.recommenders.internal.models.rcp.ModelsPackage#getModelRepository_Basedir()
     * @model default="" dataType="org.eclipse.recommenders.internal.models.rcp.EFile"
     * @generated
     */
    File getBasedir();

    /**
     * Sets the value of the '{@link org.eclipse.recommenders.internal.models.rcp.ModelRepository#getBasedir <em>Basedir</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Basedir</em>' attribute.
     * @see #getBasedir()
     * @generated
     */
    void setBasedir(File value);

    /**
     * Returns the value of the '<em><b>Requests</b></em>' reference list.
     * The list contents are of type {@link org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Requests</em>' reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Requests</em>' reference list.
     * @see org.eclipse.recommenders.internal.models.rcp.ModelsPackage#getModelRepository_Requests()
     * @model transient="true"
     * @generated
     */
    EList<ModelArchiveDescriptor> getRequests();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model required="true" mcDataType="org.eclipse.recommenders.internal.models.rcp.EModelCoordinate"
     * @generated
     */
    ModelArchiveDescriptor find(ModelCoordinate mc);

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model
     * @generated
     */
    void open();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model
     * @generated
     */
    void close();

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @model listenerRequired="true"
     * @generated
     */
    void download(ModelArchiveDescriptor descriptor, Handler listener);

} // ModelRepository
