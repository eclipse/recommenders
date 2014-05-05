package org.eclipse.recommenders.completion.rcp.processable;

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
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

public class SessionProcessorDescriptors {

    private static final Logger LOG = LoggerFactory.getLogger(SessionProcessorDescriptor.class);

    private static final String PREF_NODE_ID_SESSIONPROCESSORS = "org.eclipse.recommenders.completion.rcp.sessionprocessors"; //$NON-NLS-1$
    private static final String PREF_DISABLED = "disabled"; //$NON-NLS-1$
    private static final String EXT_POINT_SESSION_PROCESSORS = PREF_NODE_ID_SESSIONPROCESSORS;

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
}
