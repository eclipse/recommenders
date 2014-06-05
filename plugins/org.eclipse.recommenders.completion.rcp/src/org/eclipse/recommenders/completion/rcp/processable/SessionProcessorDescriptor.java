/**
 * Copyright (c) 2010, 2012 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.completion.rcp.processable;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.base.Optional.fromNullable;
import static java.lang.Boolean.TRUE;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.google.common.base.Optional;

public class SessionProcessorDescriptor {

    private static final String ENABLED_BY_DEFAULT_ATTRIBUTE = "enabledByDefault"; //$NON-NLS-1$

    private final IConfigurationElement config;
    private boolean enabled;
    private SessionProcessor processor;

    public SessionProcessorDescriptor(IConfigurationElement config) {
        this.config = config;
        enabled = Boolean.valueOf(firstNonNull(config.getAttribute(ENABLED_BY_DEFAULT_ATTRIBUTE), TRUE.toString()));
    }

    public String getId() {
        return config.getAttribute("id"); //$NON-NLS-1$
    }

    public String getName() {
        return config.getAttribute("name"); //$NON-NLS-1$
    }

    public String getDescription() {
        return firstNonNull(config.getAttribute("description"), ""); //$NON-NLS-1$
    }

    public Image getIcon() {
        String pluginId = config.getContributor().getName();
        String iconPath = config.getAttribute("icon"); //$NON-NLS-1$
        return AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, iconPath).createImage();
    }

    public int getPriority() {
        String priority = config.getAttribute("priority"); //$NON-NLS-1$
        return priority == null ? 10 : Integer.parseInt(priority);
    }

    public synchronized SessionProcessor getProcessor() throws CoreException {
        if (processor == null) {
            processor = (SessionProcessor) config.createExecutableExtension("class"); //$NON-NLS-1$
        }
        return processor;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Optional<String> getPreferencePage() {
        return fromNullable(config.getAttribute("preferencePage")); //$NON-NLS-1$
    }

    @Override
    public String toString() {
        return getId();
    }
}
