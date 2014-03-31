/**
 */
package org.eclipse.recommenders.internal.models.rcp.impl;

import java.io.File;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor;
import org.eclipse.recommenders.internal.models.rcp.ModelRepository;
import org.eclipse.recommenders.internal.models.rcp.ModelsPackage;
import org.eclipse.recommenders.internal.models.rcp.State;
import org.eclipse.recommenders.models.ModelCoordinate;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Model Archive Descriptor</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.recommenders.internal.models.rcp.impl.ModelArchiveDescriptorImpl#getLocation <em>Location</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.models.rcp.impl.ModelArchiveDescriptorImpl#getCoordinate <em>Coordinate</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.models.rcp.impl.ModelArchiveDescriptorImpl#getState <em>State</em>}</li>
 *   <li>{@link org.eclipse.recommenders.internal.models.rcp.impl.ModelArchiveDescriptorImpl#getOrigin <em>Origin</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ModelArchiveDescriptorImpl extends MinimalEObjectImpl.Container implements ModelArchiveDescriptor {
    /**
     * The default value of the '{@link #getLocation() <em>Location</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLocation()
     * @generated
     * @ordered
     */
    protected static final File LOCATION_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getLocation() <em>Location</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getLocation()
     * @generated
     * @ordered
     */
    protected File location = LOCATION_EDEFAULT;

    /**
     * The default value of the '{@link #getCoordinate() <em>Coordinate</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCoordinate()
     * @generated
     * @ordered
     */
    protected static final ModelCoordinate COORDINATE_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getCoordinate() <em>Coordinate</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getCoordinate()
     * @generated
     * @ordered
     */
    protected ModelCoordinate coordinate = COORDINATE_EDEFAULT;

    /**
     * The default value of the '{@link #getState() <em>State</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getState()
     * @generated
     * @ordered
     */
    protected static final State STATE_EDEFAULT = State.ABSENT;

    /**
     * The cached value of the '{@link #getState() <em>State</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getState()
     * @generated
     * @ordered
     */
    protected State state = STATE_EDEFAULT;

    /**
     * The cached value of the '{@link #getOrigin() <em>Origin</em>}' reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getOrigin()
     * @generated
     * @ordered
     */
    protected ModelRepository origin;

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected ModelArchiveDescriptorImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ModelsPackage.Literals.MODEL_ARCHIVE_DESCRIPTOR;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public File getLocation() {
        return location;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setLocation(File newLocation) {
        File oldLocation = location;
        location = newLocation;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelsPackage.MODEL_ARCHIVE_DESCRIPTOR__LOCATION, oldLocation, location));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModelCoordinate getCoordinate() {
        return coordinate;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setCoordinate(ModelCoordinate newCoordinate) {
        ModelCoordinate oldCoordinate = coordinate;
        coordinate = newCoordinate;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelsPackage.MODEL_ARCHIVE_DESCRIPTOR__COORDINATE, oldCoordinate, coordinate));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public State getState() {
        return state;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setState(State newState) {
        State oldState = state;
        state = newState == null ? STATE_EDEFAULT : newState;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelsPackage.MODEL_ARCHIVE_DESCRIPTOR__STATE, oldState, state));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModelRepository getOrigin() {
        if (origin != null && origin.eIsProxy()) {
            InternalEObject oldOrigin = (InternalEObject)origin;
            origin = (ModelRepository)eResolveProxy(oldOrigin);
            if (origin != oldOrigin) {
                if (eNotificationRequired())
                    eNotify(new ENotificationImpl(this, Notification.RESOLVE, ModelsPackage.MODEL_ARCHIVE_DESCRIPTOR__ORIGIN, oldOrigin, origin));
            }
        }
        return origin;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModelRepository basicGetOrigin() {
        return origin;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setOrigin(ModelRepository newOrigin) {
        ModelRepository oldOrigin = origin;
        origin = newOrigin;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelsPackage.MODEL_ARCHIVE_DESCRIPTOR__ORIGIN, oldOrigin, origin));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case ModelsPackage.MODEL_ARCHIVE_DESCRIPTOR__LOCATION:
                return getLocation();
            case ModelsPackage.MODEL_ARCHIVE_DESCRIPTOR__COORDINATE:
                return getCoordinate();
            case ModelsPackage.MODEL_ARCHIVE_DESCRIPTOR__STATE:
                return getState();
            case ModelsPackage.MODEL_ARCHIVE_DESCRIPTOR__ORIGIN:
                if (resolve) return getOrigin();
                return basicGetOrigin();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case ModelsPackage.MODEL_ARCHIVE_DESCRIPTOR__LOCATION:
                setLocation((File)newValue);
                return;
            case ModelsPackage.MODEL_ARCHIVE_DESCRIPTOR__COORDINATE:
                setCoordinate((ModelCoordinate)newValue);
                return;
            case ModelsPackage.MODEL_ARCHIVE_DESCRIPTOR__STATE:
                setState((State)newValue);
                return;
            case ModelsPackage.MODEL_ARCHIVE_DESCRIPTOR__ORIGIN:
                setOrigin((ModelRepository)newValue);
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
            case ModelsPackage.MODEL_ARCHIVE_DESCRIPTOR__LOCATION:
                setLocation(LOCATION_EDEFAULT);
                return;
            case ModelsPackage.MODEL_ARCHIVE_DESCRIPTOR__COORDINATE:
                setCoordinate(COORDINATE_EDEFAULT);
                return;
            case ModelsPackage.MODEL_ARCHIVE_DESCRIPTOR__STATE:
                setState(STATE_EDEFAULT);
                return;
            case ModelsPackage.MODEL_ARCHIVE_DESCRIPTOR__ORIGIN:
                setOrigin((ModelRepository)null);
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
            case ModelsPackage.MODEL_ARCHIVE_DESCRIPTOR__LOCATION:
                return LOCATION_EDEFAULT == null ? location != null : !LOCATION_EDEFAULT.equals(location);
            case ModelsPackage.MODEL_ARCHIVE_DESCRIPTOR__COORDINATE:
                return COORDINATE_EDEFAULT == null ? coordinate != null : !COORDINATE_EDEFAULT.equals(coordinate);
            case ModelsPackage.MODEL_ARCHIVE_DESCRIPTOR__STATE:
                return state != STATE_EDEFAULT;
            case ModelsPackage.MODEL_ARCHIVE_DESCRIPTOR__ORIGIN:
                return origin != null;
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (location: ");
        result.append(location);
        result.append(", coordinate: ");
        result.append(coordinate);
        result.append(", state: ");
        result.append(state);
        result.append(')');
        return result.toString();
    }

} //ModelArchiveDescriptorImpl
