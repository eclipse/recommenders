/**
 * Copyright (c) 2015 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Yasser Aziza - initial API and implementation.
 */
package org.eclipse.recommenders.internal.rcp.links;

import static org.eclipse.recommenders.internal.rcp.LogMessages.LOG_ERROR_FAILED_TO_READ_EXTENSION_ELEMENT;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.recommenders.utils.Logs;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class ContributionExtensionReader {

    private final String PREF_CONTRIBUTION_ID = "org.eclipse.recommenders.rcp.linkContribution"; //$NON-NLS-1$
    private final String CONTRIBUTION_ELEMENT = "linkContribution"; //$NON-NLS-1$

    private final String PREF_PAGE_ID_ATTRIBUTE = "preferencePageId"; //$NON-NLS-1$
    private final String LABEL_ATTRIBUTE = "label"; //$NON-NLS-1$
    private final String COMMAND_ID_ATTRIBUTE = "commandId"; //$NON-NLS-1$
    private final String PRIORITY_ATTRIBUTE = "priority"; //$NON-NLS-1$
    private final String ICON_ELEMENT = "icon"; //$NON-NLS-1$

    private final String preferencePageId;

    List<ContributionLink> links;

    public ContributionExtensionReader(String preferencePageId) {
        this.preferencePageId = preferencePageId;
        readContributionLinks();
    }

    public List<ContributionLink> getContributionLinks() {
        return links;
    }

    private void readContributionLinks() {
        final IConfigurationElement[] configurationElements = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(PREF_CONTRIBUTION_ID);
        readContributionLinks(configurationElements);
    }

    @VisibleForTesting
    void readContributionLinks(IConfigurationElement... configurationElements) {
        List<ContributionLink> links = this.links = Lists.newArrayList();

        if (configurationElements == null) {
            return;
        }

        for (final IConfigurationElement configurationElement : configurationElements) {
            if (CONTRIBUTION_ELEMENT.equals(configurationElement.getName())) {
                final String pluginId = configurationElement.getContributor().getName();
                final String pageId = configurationElement.getAttribute(PREF_PAGE_ID_ATTRIBUTE);
                if (!preferencePageId.equals(pageId)) {
                    continue;
                }
                final String label = configurationElement.getAttribute(LABEL_ATTRIBUTE);
                final String commandId = configurationElement.getAttribute(COMMAND_ID_ATTRIBUTE);
                final String priority = configurationElement.getAttribute(PRIORITY_ATTRIBUTE);

                String icon = configurationElement.getAttribute(ICON_ELEMENT);
                Image image = null;
                if (icon != null) {
                    image = AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, icon).createImage();
                }

                try {
                    if (isValidAttribute(label) && isValidAttribute(commandId)) {
                        links.add(new ContributionLink(label, commandId, priority, image));
                    } else {
                        Logs.log(LOG_ERROR_FAILED_TO_READ_EXTENSION_ELEMENT, CONTRIBUTION_ELEMENT);
                    }
                } catch (Exception e) {
                    Logs.log(LOG_ERROR_FAILED_TO_READ_EXTENSION_ELEMENT, CONTRIBUTION_ELEMENT);
                }
            }
        }
        Collections.sort(links);
        this.links = links;
    }

    private boolean isValidAttribute(String attribute) {
        return !Strings.isNullOrEmpty(attribute);
    }
}
