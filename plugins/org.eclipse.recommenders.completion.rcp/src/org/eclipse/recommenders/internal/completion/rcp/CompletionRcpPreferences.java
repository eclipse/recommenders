/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Dorn - initial API and implementation.
 */
package org.eclipse.recommenders.internal.completion.rcp;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.recommenders.completion.rcp.processable.SessionProcessorDescriptor;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@SuppressWarnings("restriction")
public class CompletionRcpPreferences extends AbstractPreferenceInitializer {

    private static final char DISABLED_FLAG = '!';
    private static final char SEPARATOR = ';';

    @Inject
    @Preference(Constants.PREF_SESSIONPROCESSORS)
    private String enabledSessionProcessorString;

    private final Set<SessionProcessorDescriptor> availableProcessors;

    public CompletionRcpPreferences() {
        availableProcessors = readExtensionPoint();
    }

    private Set<SessionProcessorDescriptor> readExtensionPoint() {
        IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                Constants.EXT_POINT_SESSION_PROCESSORS);

        Set<SessionProcessorDescriptor> descriptors = Sets.newHashSet();
        for (final IConfigurationElement element : elements) {
            SessionProcessorDescriptor descriptor = new SessionProcessorDescriptor(element);
            descriptors.add(descriptor);
        }
        return descriptors;
    }

    @Override
    public void initializeDefaultPreferences() {
        IPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, Constants.BUNDLE_NAME);
        store.setDefault(Constants.PREF_SESSIONPROCESSORS,
                toString(Maps.asMap(availableProcessors, new Function<SessionProcessorDescriptor, Boolean>() {

                    @Override
                    public Boolean apply(SessionProcessorDescriptor descriptor) {
                        return descriptor.isEnabledByDefault();
                    }
                })));
    }

    public Collection<SessionProcessorDescriptor> getEnabledSessionProcessors() {
        return Maps.filterValues(fromString(availableProcessors, enabledSessionProcessorString),
                new Predicate<Boolean>() {

            @Override
            public boolean apply(Boolean input) {
                return input;
            }
        }).keySet();
    }

    public Set<SessionProcessorDescriptor> getRegisteredProccessors() {
        return availableProcessors;
    }

    public SessionProcessorDescriptor getSessionProcessorDescriptor(String id) {
        return find(availableProcessors, id);
    }

    public void setSessionProcessorEnabled(Collection<SessionProcessorDescriptor> enabledDescriptors,
            Collection<SessionProcessorDescriptor> disabledDescriptors) {
        Map<SessionProcessorDescriptor, Boolean> result = fromString(availableProcessors, enabledSessionProcessorString);

        for (SessionProcessorDescriptor enabledDescriptor : enabledDescriptors) {
            result.put(enabledDescriptor, true);
        }
        for (SessionProcessorDescriptor disabledDescriptor : disabledDescriptors) {
            result.put(disabledDescriptor, false);
        }

        IPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, Constants.BUNDLE_NAME);
        store.setValue(Constants.PREF_SESSIONPROCESSORS, toString(result));
    }

    public boolean isEnabled(SessionProcessorDescriptor processor) {
        Map<SessionProcessorDescriptor, Boolean> map = fromString(availableProcessors, enabledSessionProcessorString);
        return map.containsKey(processor) ? map.get(processor) : false;
    }

    private static String toString(Map<SessionProcessorDescriptor, Boolean> descriptors) {
        StringBuilder sb = new StringBuilder();

        Iterator<Entry<SessionProcessorDescriptor, Boolean>> it = descriptors.entrySet().iterator();
        while (it.hasNext()) {
            Entry<SessionProcessorDescriptor, Boolean> entry = it.next();
            SessionProcessorDescriptor descriptor = entry.getKey();
            Boolean enabled = entry.getValue();
            if (!enabled) {
                sb.append(DISABLED_FLAG);
            }
            sb.append(descriptor.getId());
            if (it.hasNext()) {
                sb.append(SEPARATOR);
            }
        }
        return sb.toString();
    }

    private static Map<SessionProcessorDescriptor, Boolean> fromString(
            Iterable<SessionProcessorDescriptor> descriptors, String string) {
        Map<SessionProcessorDescriptor, Boolean> result = Maps.newHashMap();
        for (String id : StringUtils.split(string, SEPARATOR)) {
            final boolean enabled;
            if (id.charAt(0) == DISABLED_FLAG) {
                enabled = false;
                id = id.substring(1);
            } else {
                enabled = true;
            }

            SessionProcessorDescriptor found = find(descriptors, id);
            if (found != null) {
                result.put(found, enabled);
            }
        }
        return result;
    }

    private static SessionProcessorDescriptor find(Iterable<SessionProcessorDescriptor> descriptors, String id) {
        for (SessionProcessorDescriptor descriptor : descriptors) {
            if (descriptor.getId().equals(id)) {
                return descriptor;
            }
        }
        return null;
    }
}
