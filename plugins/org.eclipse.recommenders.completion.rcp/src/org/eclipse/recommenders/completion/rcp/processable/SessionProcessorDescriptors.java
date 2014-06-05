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

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.recommenders.internal.completion.rcp.Constants;

import com.google.common.collect.Lists;

public class SessionProcessorDescriptors {

    private static final char DISABLED_FLAG = '!';
    private static final char SEPARATOR = ';';

    private static List<SessionProcessorDescriptor> registeredProcessors;

    public static List<SessionProcessorDescriptor> getRegisteredProcessors() {
        if (registeredProcessors == null) {
            registeredProcessors = loadRegisteredProcessors();
        }
        return registeredProcessors;
    }

    private static List<SessionProcessorDescriptor> loadRegisteredProcessors() {
        final IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                Constants.EXT_POINT_SESSION_PROCESSORS);

        final List<SessionProcessorDescriptor> descriptors = Lists.newLinkedList();
        for (final IConfigurationElement element : elements) {
            SessionProcessorDescriptor descriptor = new SessionProcessorDescriptor(element);
            descriptors.add(descriptor);
        }
        return descriptors;
    }

    public static String toString(List<SessionProcessorDescriptor> descriptors) {
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

    public static List<SessionProcessorDescriptor> fromString(String string, List<SessionProcessorDescriptor> available) {
        List<SessionProcessorDescriptor> result = Lists.newArrayList();
        for (String id : StringUtils.split(string, SEPARATOR)) {
            final boolean enabled;
            if (id.charAt(0) == DISABLED_FLAG) {
                enabled = false;
                id = id.substring(1);
            } else {
                enabled = true;
            }

            SessionProcessorDescriptor found = find(available, id);
            if (found != null) {
                found.setEnabled(enabled);
                result.add(found);
            }
        }

        for (SessionProcessorDescriptor descriptor : available) {
            if (find(result, descriptor.getId()) == null) {
                result.add(descriptor);
            }
        }

        return result;
    }

    private static SessionProcessorDescriptor find(List<SessionProcessorDescriptor> descriptors, String id) {
        for (SessionProcessorDescriptor descriptor : descriptors) {
            if (descriptor.getId().equals(id)) {
                return descriptor;
            }
        }
        return null;
    }
}
