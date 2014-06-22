/**
 */
package org.eclipse.recommenders.snipmatch.model.snipmatchmodel.provider;

import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ViewerNotification;

import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.EclipseGitSnippetRepositoryConfiguration;
import org.eclipse.recommenders.snipmatch.model.snipmatchmodel.SnipmatchPackage;

/**
 * This is the item provider adapter for a {@link org.eclipse.recommenders.snipmatch.model.snipmatchmodel.EclipseGitSnippetRepositoryConfiguration} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class EclipseGitSnippetRepositoryConfigurationItemProvider extends
		SnippetRepositoryConfigurationItemProvider {
	/**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EclipseGitSnippetRepositoryConfigurationItemProvider(
			AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * This returns the property descriptors for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

			addUrlPropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Url feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addUrlPropertyDescriptor(Object object) {
		itemPropertyDescriptors
				.add(createItemPropertyDescriptor(
						((ComposeableAdapterFactory) adapterFactory)
								.getRootAdapterFactory(),
						getResourceLocator(),
						getString("_UI_EclipseGitSnippetRepositoryConfiguration_url_feature"), //$NON-NLS-1$
						getString(
								"_UI_PropertyDescriptor_description", "_UI_EclipseGitSnippetRepositoryConfiguration_url_feature", "_UI_EclipseGitSnippetRepositoryConfiguration_type"), //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						SnipmatchPackage.Literals.ECLIPSE_GIT_SNIPPET_REPOSITORY_CONFIGURATION__URL,
						true, false, false,
						ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
	}

	/**
	 * This returns EclipseGitSnippetRepositoryConfiguration.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(
				object,
				getResourceLocator().getImage(
						"full/obj16/EclipseGitSnippetRepositoryConfiguration")); //$NON-NLS-1$
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String getText(Object object) {
		String label = ((EclipseGitSnippetRepositoryConfiguration) object)
				.getName();
		return label == null || label.length() == 0 ? getString("_UI_EclipseGitSnippetRepositoryConfiguration_type") : //$NON-NLS-1$
				getString("_UI_EclipseGitSnippetRepositoryConfiguration_type") + " " + label; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached
	 * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification) {
		updateChildren(notification);

		switch (notification
				.getFeatureID(EclipseGitSnippetRepositoryConfiguration.class)) {
		case SnipmatchPackage.ECLIPSE_GIT_SNIPPET_REPOSITORY_CONFIGURATION__URL:
			fireNotifyChanged(new ViewerNotification(notification,
					notification.getNotifier(), false, true));
			return;
		}
		super.notifyChanged(notification);
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
	 * that can be created under this object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(
			Collection<Object> newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);
	}

}
