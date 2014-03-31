/**
 */
package org.eclipse.recommenders.internal.models.rcp.util;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

import org.eclipse.recommenders.internal.models.rcp.*;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.recommenders.internal.models.rcp.ModelsPackage
 * @generated
 */
public class ModelsAdapterFactory extends AdapterFactoryImpl {
    /**
     * The cached model package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected static ModelsPackage modelPackage;

    /**
     * Creates an instance of the adapter factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModelsAdapterFactory() {
        if (modelPackage == null) {
            modelPackage = ModelsPackage.eINSTANCE;
        }
    }

    /**
     * Returns whether this factory is applicable for the type of the object.
     * <!-- begin-user-doc -->
     * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
     * <!-- end-user-doc -->
     * @return whether this factory is applicable for the type of the object.
     * @generated
     */
    @Override
    public boolean isFactoryForType(Object object) {
        if (object == modelPackage) {
            return true;
        }
        if (object instanceof EObject) {
            return ((EObject)object).eClass().getEPackage() == modelPackage;
        }
        return false;
    }

    /**
     * The switch that delegates to the <code>createXXX</code> methods.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ModelsSwitch<Adapter> modelSwitch =
        new ModelsSwitch<Adapter>() {
            @Override
            public Adapter caseModelRepositorySystem(ModelRepositorySystem object) {
                return createModelRepositorySystemAdapter();
            }
            @Override
            public Adapter caseModelRepository(ModelRepository object) {
                return createModelRepositoryAdapter();
            }
            @Override
            public Adapter caseModelArchiveDescriptor(ModelArchiveDescriptor object) {
                return createModelArchiveDescriptorAdapter();
            }
            @Override
            public Adapter caseHandler(Handler object) {
                return createHandlerAdapter();
            }
            @Override
            public Adapter caseDownloadEvent(DownloadEvent object) {
                return createDownloadEventAdapter();
            }
            @Override
            public Adapter defaultCase(EObject object) {
                return createEObjectAdapter();
            }
        };

    /**
     * Creates an adapter for the <code>target</code>.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param target the object to adapt.
     * @return the adapter for the <code>target</code>.
     * @generated
     */
    @Override
    public Adapter createAdapter(Notifier target) {
        return modelSwitch.doSwitch((EObject)target);
    }


    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.recommenders.internal.models.rcp.ModelRepositorySystem <em>Model Repository System</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.recommenders.internal.models.rcp.ModelRepositorySystem
     * @generated
     */
    public Adapter createModelRepositorySystemAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.recommenders.internal.models.rcp.ModelRepository <em>Model Repository</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.recommenders.internal.models.rcp.ModelRepository
     * @generated
     */
    public Adapter createModelRepositoryAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor <em>Model Archive Descriptor</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor
     * @generated
     */
    public Adapter createModelArchiveDescriptorAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.recommenders.internal.models.rcp.Handler <em>Handler</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.recommenders.internal.models.rcp.Handler
     * @generated
     */
    public Adapter createHandlerAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for an object of class '{@link org.eclipse.recommenders.internal.models.rcp.DownloadEvent <em>Download Event</em>}'.
     * <!-- begin-user-doc -->
     * This default implementation returns null so that we can easily ignore cases;
     * it's useful to ignore a case when inheritance will catch all the cases anyway.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @see org.eclipse.recommenders.internal.models.rcp.DownloadEvent
     * @generated
     */
    public Adapter createDownloadEventAdapter() {
        return null;
    }

    /**
     * Creates a new adapter for the default case.
     * <!-- begin-user-doc -->
     * This default implementation returns null.
     * <!-- end-user-doc -->
     * @return the new adapter.
     * @generated
     */
    public Adapter createEObjectAdapter() {
        return null;
    }

} //ModelsAdapterFactory
