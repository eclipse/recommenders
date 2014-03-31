/**
 */
package org.eclipse.recommenders.internal.models.rcp.impl;

import java.util.Collection;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectResolvingEList;

import org.eclipse.recommenders.internal.models.rcp.ModelRepository;
import org.eclipse.recommenders.internal.models.rcp.ModelRepositorySystem;
import org.eclipse.recommenders.internal.models.rcp.ModelsPackage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Model Repository System</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.recommenders.internal.models.rcp.impl.ModelRepositorySystemImpl#getRepositories <em>Repositories</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ModelRepositorySystemImpl extends MinimalEObjectImpl.Container implements ModelRepositorySystem {
    /**
     * The cached value of the '{@link #getRepositories() <em>Repositories</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRepositories()
     * @generated
     * @ordered
     */
    protected EList<ModelRepository> repositories;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ModelRepositorySystemImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ModelsPackage.Literals.MODEL_REPOSITORY_SYSTEM;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<ModelRepository> getRepositories() {
        if (repositories == null) {
            repositories = new EObjectResolvingEList<ModelRepository>(ModelRepository.class, this, ModelsPackage.MODEL_REPOSITORY_SYSTEM__REPOSITORIES);
        }
        return repositories;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case ModelsPackage.MODEL_REPOSITORY_SYSTEM__REPOSITORIES:
                return getRepositories();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case ModelsPackage.MODEL_REPOSITORY_SYSTEM__REPOSITORIES:
                getRepositories().clear();
                getRepositories().addAll((Collection<? extends ModelRepository>)newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case ModelsPackage.MODEL_REPOSITORY_SYSTEM__REPOSITORIES:
                getRepositories().clear();
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case ModelsPackage.MODEL_REPOSITORY_SYSTEM__REPOSITORIES:
                return repositories != null && !repositories.isEmpty();
        }
        return super.eIsSet(featureID);
    }

} //ModelRepositorySystemImpl
