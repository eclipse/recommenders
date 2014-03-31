/**
 */
package org.eclipse.recommenders.internal.models.rcp.impl;

import static org.eclipse.recommenders.internal.models.rcp.State.*;
import static org.eclipse.recommenders.utils.Checks.ensureEquals;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;
import org.eclipse.emf.ecore.util.EObjectResolvingEList;
import org.eclipse.recommenders.internal.models.rcp.Handler;
import org.eclipse.recommenders.internal.models.rcp.ModelArchiveDescriptor;
import org.eclipse.recommenders.internal.models.rcp.ModelRepository;
import org.eclipse.recommenders.internal.models.rcp.ModelsFactory;
import org.eclipse.recommenders.internal.models.rcp.ModelsPackage;
import org.eclipse.recommenders.models.DownloadCallback;
import org.eclipse.recommenders.models.IModelIndex;
import org.eclipse.recommenders.models.IModelRepository;
import org.eclipse.recommenders.models.ModelCoordinate;
import org.eclipse.recommenders.models.ModelIndex;
import com.google.common.collect.MapMaker;

/**
 * <!-- begin-user-doc --> An implementation of the model object '<em><b>Model Repository</b></em>'. <!-- end-user-doc
 * -->
 * <p>
 * The following features are implemented:
 * <ul>
 * <li>{@link org.eclipse.recommenders.internal.models.rcp.impl.ModelRepositoryImpl#getUrl <em>Url</em>}</li>
 * <li>{@link org.eclipse.recommenders.internal.models.rcp.impl.ModelRepositoryImpl#getIndex <em>Index</em>}</li>
 * <li>{@link org.eclipse.recommenders.internal.models.rcp.impl.ModelRepositoryImpl#getRepository <em>Repository</em>}</li>
 * <li>{@link org.eclipse.recommenders.internal.models.rcp.impl.ModelRepositoryImpl#isAccessible <em>Accessible</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class ModelRepositoryImpl extends MinimalEObjectImpl.Container implements ModelRepository {
    /**
     * The default value of the '{@link #getUrl() <em>Url</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     *
     * @see #getUrl()
     * @generated
     * @ordered
     */
    protected static final URL URL_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getUrl() <em>Url</em>}' attribute.
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @see #getUrl()
     * @generated
     * @ordered
     */
    protected URL url = URL_EDEFAULT;

    /**
     * The default value of the '{@link #getIndex() <em>Index</em>}' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see #getIndex()
     * @generated
     * @ordered
     */
    protected static final IModelIndex INDEX_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getIndex() <em>Index</em>}' attribute. <!-- begin-user-doc --> <!-- end-user-doc
     * -->
     *
     * @see #getIndex()
     * @generated
     * @ordered
     */
    protected IModelIndex index = INDEX_EDEFAULT;

    /**
     * The default value of the '{@link #getRepository() <em>Repository</em>}' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see #getRepository()
     * @generated
     * @ordered
     */
    protected static final IModelRepository REPOSITORY_EDEFAULT = null;

    /**
     * The cached value of the '{@link #getRepository() <em>Repository</em>}' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see #getRepository()
     * @generated
     * @ordered
     */
    protected IModelRepository repository = REPOSITORY_EDEFAULT;

    /**
     * The default value of the '{@link #isAccessible() <em>Accessible</em>}' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see #isAccessible()
     * @generated
     * @ordered
     */
    protected static final boolean ACCESSIBLE_EDEFAULT = false;

    /**
     * The cached value of the '{@link #isAccessible() <em>Accessible</em>}' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see #isAccessible()
     * @generated
     * @ordered
     */
    protected boolean accessible = ACCESSIBLE_EDEFAULT;

    /**
     * The default value of the '{@link #getBasedir() <em>Basedir</em>}' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see #getBasedir()
     * @generated
     * @ordered
     */
    protected static final File BASEDIR_EDEFAULT = (File)ModelsFactory.eINSTANCE.createFromString(ModelsPackage.eINSTANCE.getEFile(), "");

    /**
     * The cached value of the '{@link #getBasedir() <em>Basedir</em>}' attribute.
     * <!-- begin-user-doc --> <!--
     * end-user-doc -->
     * @see #getBasedir()
     * @generated
     * @ordered
     */
    protected File basedir = BASEDIR_EDEFAULT;

    /**
     * The cached value of the '{@link #getRequests() <em>Requests</em>}' reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see #getRequests()
     * @generated
     * @ordered
     */
    protected EList<ModelArchiveDescriptor> requests;

    protected ConcurrentMap<ModelCoordinate, ModelArchiveDescriptor> descriptors = new MapMaker().weakValues()
            .makeMap();

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    protected ModelRepositoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ModelsPackage.Literals.MODEL_REPOSITORY;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public URL getUrl() {
        return url;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setUrl(URL newUrl) {
        URL oldUrl = url;
        url = newUrl;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelsPackage.MODEL_REPOSITORY__URL, oldUrl, url));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public IModelIndex getIndex() {
        return index;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setIndex(IModelIndex newIndex) {
        IModelIndex oldIndex = index;
        index = newIndex;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelsPackage.MODEL_REPOSITORY__INDEX, oldIndex, index));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public IModelRepository getRepository() {
        return repository;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setRepository(IModelRepository newRepository) {
        IModelRepository oldRepository = repository;
        repository = newRepository;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelsPackage.MODEL_REPOSITORY__REPOSITORY, oldRepository, repository));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean isAccessible() {
        return accessible;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setAccessible(boolean newAccessible) {
        boolean oldAccessible = accessible;
        accessible = newAccessible;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelsPackage.MODEL_REPOSITORY__ACCESSIBLE, oldAccessible, accessible));
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public File getBasedir() {
        return basedir;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void setBasedir(File newBasedir) {
        File oldBasedir = basedir;
        basedir = newBasedir;
        if (eNotificationRequired())
            eNotify(new ENotificationImpl(this, Notification.SET, ModelsPackage.MODEL_REPOSITORY__BASEDIR, oldBasedir, basedir));
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public EList<ModelArchiveDescriptor> getRequests() {
        if (requests == null) {
            requests = new EObjectResolvingEList<ModelArchiveDescriptor>(ModelArchiveDescriptor.class, this, ModelsPackage.MODEL_REPOSITORY__REQUESTS);
        }
        return requests;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     * @category not generated
     */
    @Override
    public ModelArchiveDescriptor find(ModelCoordinate mc) {
        ModelArchiveDescriptor res = descriptors.get(mc);
        if (res == null) {
            File location = repository.getLocation(mc, false).orNull();
            res = ModelsFactoryImpl.eINSTANCE.createModelArchiveDescriptor();
            res.setCoordinate(mc);
            res.setOrigin(this);
            res.setLocation(location);
            res.setState(location == null ? ABSENT : CACHED);
            descriptors.put(mc, res);
        }
        return res;
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     * @category time consuming
     */
    @Override
    public void open() {
        ModelArchiveDescriptor find = find(ModelIndex.INDEX);
        download(find, null);
        try {
            index.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void close() {
        // TODO: implement this method
        // Ensure that you remove @generated or mark it @generated NOT
        throw new UnsupportedOperationException();
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     *
     * @generated NOT
     * @category not generated
     */
    @Override
    public void download(ModelArchiveDescriptor d, Handler listener) {
        ensureEquals(d.getOrigin(), this, "Trying to download a model archive which does not belong to this repo");
        d.setState(DOWNLOADING);
        File location = repository.resolve(d.getCoordinate(), false, new DownloadCallback()).orNull();
        d.setLocation(location);
        d.setState(location == null ? ABSENT : CACHED);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eGet(int featureID, boolean resolve, boolean coreType) {
        switch (featureID) {
            case ModelsPackage.MODEL_REPOSITORY__URL:
                return getUrl();
            case ModelsPackage.MODEL_REPOSITORY__INDEX:
                return getIndex();
            case ModelsPackage.MODEL_REPOSITORY__REPOSITORY:
                return getRepository();
            case ModelsPackage.MODEL_REPOSITORY__ACCESSIBLE:
                return isAccessible();
            case ModelsPackage.MODEL_REPOSITORY__BASEDIR:
                return getBasedir();
            case ModelsPackage.MODEL_REPOSITORY__REQUESTS:
                return getRequests();
        }
        return super.eGet(featureID, resolve, coreType);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    @Override
    public void eSet(int featureID, Object newValue) {
        switch (featureID) {
            case ModelsPackage.MODEL_REPOSITORY__URL:
                setUrl((URL)newValue);
                return;
            case ModelsPackage.MODEL_REPOSITORY__INDEX:
                setIndex((IModelIndex)newValue);
                return;
            case ModelsPackage.MODEL_REPOSITORY__REPOSITORY:
                setRepository((IModelRepository)newValue);
                return;
            case ModelsPackage.MODEL_REPOSITORY__ACCESSIBLE:
                setAccessible((Boolean)newValue);
                return;
            case ModelsPackage.MODEL_REPOSITORY__BASEDIR:
                setBasedir((File)newValue);
                return;
            case ModelsPackage.MODEL_REPOSITORY__REQUESTS:
                getRequests().clear();
                getRequests().addAll((Collection<? extends ModelArchiveDescriptor>)newValue);
                return;
        }
        super.eSet(featureID, newValue);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public void eUnset(int featureID) {
        switch (featureID) {
            case ModelsPackage.MODEL_REPOSITORY__URL:
                setUrl(URL_EDEFAULT);
                return;
            case ModelsPackage.MODEL_REPOSITORY__INDEX:
                setIndex(INDEX_EDEFAULT);
                return;
            case ModelsPackage.MODEL_REPOSITORY__REPOSITORY:
                setRepository(REPOSITORY_EDEFAULT);
                return;
            case ModelsPackage.MODEL_REPOSITORY__ACCESSIBLE:
                setAccessible(ACCESSIBLE_EDEFAULT);
                return;
            case ModelsPackage.MODEL_REPOSITORY__BASEDIR:
                setBasedir(BASEDIR_EDEFAULT);
                return;
            case ModelsPackage.MODEL_REPOSITORY__REQUESTS:
                getRequests().clear();
                return;
        }
        super.eUnset(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public boolean eIsSet(int featureID) {
        switch (featureID) {
            case ModelsPackage.MODEL_REPOSITORY__URL:
                return URL_EDEFAULT == null ? url != null : !URL_EDEFAULT.equals(url);
            case ModelsPackage.MODEL_REPOSITORY__INDEX:
                return INDEX_EDEFAULT == null ? index != null : !INDEX_EDEFAULT.equals(index);
            case ModelsPackage.MODEL_REPOSITORY__REPOSITORY:
                return REPOSITORY_EDEFAULT == null ? repository != null : !REPOSITORY_EDEFAULT.equals(repository);
            case ModelsPackage.MODEL_REPOSITORY__ACCESSIBLE:
                return accessible != ACCESSIBLE_EDEFAULT;
            case ModelsPackage.MODEL_REPOSITORY__BASEDIR:
                return BASEDIR_EDEFAULT == null ? basedir != null : !BASEDIR_EDEFAULT.equals(basedir);
            case ModelsPackage.MODEL_REPOSITORY__REQUESTS:
                return requests != null && !requests.isEmpty();
        }
        return super.eIsSet(featureID);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public Object eInvoke(int operationID, EList<?> arguments) throws InvocationTargetException {
        switch (operationID) {
            case ModelsPackage.MODEL_REPOSITORY___FIND__MODELCOORDINATE:
                return find((ModelCoordinate)arguments.get(0));
            case ModelsPackage.MODEL_REPOSITORY___OPEN:
                open();
                return null;
            case ModelsPackage.MODEL_REPOSITORY___CLOSE:
                close();
                return null;
            case ModelsPackage.MODEL_REPOSITORY___DOWNLOAD__MODELARCHIVEDESCRIPTOR_HANDLER:
                download((ModelArchiveDescriptor)arguments.get(0), (Handler)arguments.get(1));
                return null;
        }
        return super.eInvoke(operationID, arguments);
    }

    /**
     * <!-- begin-user-doc --> <!-- end-user-doc -->
     * @generated
     */
    @Override
    public String toString() {
        if (eIsProxy()) return super.toString();

        StringBuffer result = new StringBuffer(super.toString());
        result.append(" (url: ");
        result.append(url);
        result.append(", index: ");
        result.append(index);
        result.append(", repository: ");
        result.append(repository);
        result.append(", accessible: ");
        result.append(accessible);
        result.append(", basedir: ");
        result.append(basedir);
        result.append(')');
        return result.toString();
    }

} // ModelRepositoryImpl
