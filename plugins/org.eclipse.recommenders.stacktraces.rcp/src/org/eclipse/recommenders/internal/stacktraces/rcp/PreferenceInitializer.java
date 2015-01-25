/**
 * Copyright (c) 2014 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.stacktraces.rcp;

import static org.eclipse.recommenders.internal.stacktraces.rcp.Constants.*;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.ModelFactory;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.RememberSendAction;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.SendAction;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.Settings;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IEclipsePreferences s = DefaultScope.INSTANCE.getNode(PLUGIN_ID);
        s.put(PROP_SERVER, SERVER_URL);
        s.put(PROP_NAME, "");
        s.put(PROP_EMAIL, "");
        s.putBoolean(PROP_SKIP_SIMILAR_ERRORS, true);
        s.putBoolean(PROP_CONFIGURED, false);
        s.put(PROP_WHITELISTED_PLUGINS, Constants.WHITELISTED_PLUGINS);
        s.put(PROP_WHITELISTED_PACKAGES, Constants.WHITELISTED_PACKAGES);
        s.put(PROP_SEND_ACTION, SendAction.ASK.name());
        s.put(PROP_REMEMBER_SEND_ACTION, RememberSendAction.NONE.name());
        s.putBoolean(PROP_ANONYMIZE_STACKTRACES, true);
        s.putBoolean(PROP_ANONYMIZE_MESSAGES, false);
    }

    public static Settings readSettings() {
        final ScopedPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, PLUGIN_ID);
        final Settings settings = ModelFactory.eINSTANCE.createSettings();
        settings.eSetDeliver(false);
        final EClass eClass = settings.eClass();
        for (EAttribute attr : eClass.getEAllAttributes()) {
            EDataType type = attr.getEAttributeType();
            String key = attr.getName();
            String value = store.getString(key);
            try {
                Object data = EcoreUtil.createFromString(type, value);
                settings.eSet(attr, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        settings.eSetDeliver(true);

        store.addPropertyChangeListener(new IPropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent event) {
                String property = event.getProperty();
                EStructuralFeature feature = eClass.getEStructuralFeature(property);
                if (feature != null && feature instanceof EAttribute) {
                    EAttribute attr = (EAttribute) feature;
                    EDataType type = attr.getEAttributeType();
                    String string = EcoreUtil.convertToString(type, event.getNewValue());
                    Object value = EcoreUtil.createFromString(type, string);
                    settings.eSet(feature, value);
                }
            }
        });

        settings.eAdapters().add(new AdapterImpl() {
            @Override
            public void notifyChanged(Notification msg) {
                for (EAttribute attr : eClass.getEAllAttributes()) {
                    EDataType type = attr.getEAttributeType();
                    Object value = settings.eGet(attr);
                    String key = attr.getName();
                    String data = EcoreUtil.convertToString(type, value);
                    try {
                        store.putValue(key, data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return settings;
    }
}
