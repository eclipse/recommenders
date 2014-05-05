/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Sewe- Initial API
 */
package org.eclipse.recommenders.completion.rcp.processable;

import static java.lang.Boolean.TRUE;

import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.service.prefs.BackingStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class SessionProcessorDescriptors {

    private static final Logger LOG = LoggerFactory.getLogger(SessionProcessorDescriptor.class);

    public static final String PREF_NODE_ID_SESSIONPROCESSORS = "org.eclipse.recommenders.completion.rcp.sessionprocessors"; //$NON-NLS-1$
    private static final String ENABLED_BY_DEFAULT_ATTRIBUTE = "enabledByDefault"; //$NON-NLS-1$
    private static final String PREF_DISABLED = "disabled"; //$NON-NLS-1$
    private static final String EXT_POINT_SESSION_PROCESSORS = PREF_NODE_ID_SESSIONPROCESSORS;

    private static final char DISABLED_FLAG = '!';
    private static final char SEPARATOR = ';';

    public static SessionProcessorDescriptor[] parseExtensions() {
        IExtensionRegistry registry = Platform.getExtensionRegistry();
        IExtensionPoint point = registry.getExtensionPoint(EXT_POINT_SESSION_PROCESSORS);
        Set<String> disabledProcessors = getDisabledProcessors();
        PriorityQueue<SessionProcessorDescriptor> queue = new PriorityQueue<SessionProcessorDescriptor>();
        try {
            for (IConfigurationElement elem : point.getConfigurationElements()) {
                try {
                    final String pluginId = elem.getContributor().getName();
                    String id = elem.getAttribute("id"); //$NON-NLS-1$
                    String name = elem.getAttribute("name"); //$NON-NLS-1$
                    String description = elem.getAttribute("description"); //$NON-NLS-1$
                    final String iconPath = elem.getAttribute("icon"); //$NON-NLS-1$
                    String priorityString = elem.getAttribute("priority"); //$NON-NLS-1$
                    String preferencePageId = elem.getAttribute("preferencePage"); //$NON-NLS-1$
                    int priority = priorityString == null ? 10 : Integer.parseInt(priorityString);
                    final Image icon = AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, iconPath).createImage();
                    SessionProcessor processor = (SessionProcessor) elem.createExecutableExtension("class"); //$NON-NLS-1$
                    boolean enable = !disabledProcessors.contains(id);
                    SessionProcessorDescriptor d = new SessionProcessorDescriptor(id, name, description, icon,
                            priority, enable, preferencePageId, processor);
                    queue.add(d);
                } catch (Exception e) {
                    LOG.error("Exception during extension point parsing.", e); //$NON-NLS-1$
                }
            }
        } catch (Exception e) {
            LOG.error("Exception during extension point parsing", e); //$NON-NLS-1$
        }
        SessionProcessorDescriptor[] res = queue.toArray(new SessionProcessorDescriptor[0]);
        return res;
    }

    public static List<SessionProcessorDescriptor> getRegisteredProcessors() {
        final IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                EXT_POINT_SESSION_PROCESSORS);

        final List<SessionProcessorDescriptor> descriptors = Lists.newLinkedList();
        for (final IConfigurationElement element : elements) {
            try {
            boolean enabled = Boolean.valueOf(Objects.firstNonNull(element.getAttribute(ENABLED_BY_DEFAULT_ATTRIBUTE),
                    TRUE.toString()));

            final String pluginId = element.getContributor().getName();
            String id = element.getAttribute("id"); //$NON-NLS-1$
            String name = element.getAttribute("name"); //$NON-NLS-1$
            String description = element.getAttribute("description"); //$NON-NLS-1$
            final String iconPath = element.getAttribute("icon"); //$NON-NLS-1$
            String priorityString = element.getAttribute("priority"); //$NON-NLS-1$
            String preferencePageId = element.getAttribute("preferencePage"); //$NON-NLS-1$
            int priority = priorityString == null ? 10 : Integer.parseInt(priorityString);
            final Image icon = AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, iconPath).createImage();
            SessionProcessor processor = (SessionProcessor) element.createExecutableExtension("class"); //$NON-NLS-1$
            SessionProcessorDescriptor d = new SessionProcessorDescriptor(id, name, description, icon, priority,
                    enabled, preferencePageId, processor);
            descriptors.add(d);
            } catch (Exception e) {
                LOG.error("Exception during extension point parsing.", e); //$NON-NLS-1$
            }
        }
        return descriptors;
    }

    static Set<String> getDisabledProcessors() {
        String prefs = getSessionProcessorPreferences().get(PREF_DISABLED, ""); //$NON-NLS-1$
        Iterable<String> split = Splitter.on(';').omitEmptyStrings().split(prefs);
        return Sets.newHashSet(split);
    }

    static void saveDisabledProcessors(Set<String> disabledProcessors) {
        String join = Joiner.on(';').skipNulls().join(disabledProcessors);
        IEclipsePreferences store = getSessionProcessorPreferences();
        store.put(PREF_DISABLED, join);
        try {
            store.flush();
        } catch (BackingStoreException e) {
            LOG.error("Failed to flush preferences", e); //$NON-NLS-1$
        }
    }

    public static final class EnabledSessionProcessorPredicate implements Predicate<SessionProcessorDescriptor> {
        @Override
        public boolean apply(SessionProcessorDescriptor p) {
            return p.isEnabled();
        }
    }

    private static IEclipsePreferences getSessionProcessorPreferences() {
        return InstanceScope.INSTANCE.getNode(PREF_NODE_ID_SESSIONPROCESSORS);
    }

    public static String store(List<SessionProcessorDescriptor> descriptors) {
        StringBuilder sb = new StringBuilder();

        Iterator<SessionProcessorDescriptor> it = descriptors.iterator();
        while (it.hasNext()) {
            SessionProcessorDescriptor descriptor = it.next();
            if (!descriptor.isEnabled()) {
                sb.append(DISABLED_FLAG);
            }
            sb.append(descriptor.getId());
            if (it.hasNext()) {
                sb.append(SEPARATOR);
            }
        }

        return sb.toString();
    }
}
