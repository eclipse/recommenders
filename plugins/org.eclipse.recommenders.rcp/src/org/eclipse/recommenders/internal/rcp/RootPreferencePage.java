/**
 * Copyright (c) 2010, 2013, 2015 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 *    Olav Lenz - externalize Strings.
 *    Yasser Aziza - contribution link
 */
package org.eclipse.recommenders.internal.rcp;

import static org.eclipse.recommenders.internal.rcp.LogMessages.LOG_ERROR_FAILED_TO_READ_EXTENSION_ELEMENT;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.recommenders.utils.Logs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class RootPreferencePage extends AbstractPreferencePage {

    private final String PREF_CONTRIBUTION_ID = "org.eclipse.recommenders.rcp.linkContribution"; //$NON-NLS-1$
    private final String CONTRIBUTION_ELEMENT = "linkContribution"; //$NON-NLS-1$

    private final String LABEL_ATTRIBUTE = "label"; //$NON-NLS-1$
    private final String COMMAND_ID_ATTRIBUTE = "commandId"; //$NON-NLS-1$
    private final String PRIORITY_ATTRIBUTE = "priority"; //$NON-NLS-1$
    private final String ICON_ELEMENT = "icon"; //$NON-NLS-1$

    public RootPreferencePage() {
        super(Messages.PREFPAGE_DESCRIPTION_EMPTY);
    }

    @Override
    protected Control createContents(final Composite parent) {
        noDefaultAndApplyButton();
        Composite composite = new Composite(parent, SWT.NONE);
        GridDataFactory.fillDefaults().applyTo(composite);
        GridLayoutFactory.swtDefaults().margins(0, 5).applyTo(composite);

        Group group = new Group(composite, SWT.NONE);
        group.setText(Messages.PREFPAGE_LINKS_DESCRIPTION);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(group);
        GridLayoutFactory.swtDefaults().numColumns(2).applyTo(group);

        for (ContributionLink link : getContributionLinks(group)) {
            link.appendLink(group);
        }

        return parent;
    }

    private List<ContributionLink> getContributionLinks(Group group) {
        final IConfigurationElement[] configurationElements = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(PREF_CONTRIBUTION_ID);
        return readRegisteredDatums(group, configurationElements);
    }

    private List<ContributionLink> readRegisteredDatums(Group group, IConfigurationElement... configurationElements) {
        List<ContributionLink> links = Lists.newArrayList();
        if (configurationElements == null) {
            return links;
        }
        for (final IConfigurationElement configurationElement : configurationElements) {
            if (CONTRIBUTION_ELEMENT.equals(configurationElement.getName())) {
                final String pluginId = configurationElement.getContributor().getName();
                final String label = configurationElement.getAttribute(LABEL_ATTRIBUTE);
                final String commandId = configurationElement.getAttribute(COMMAND_ID_ATTRIBUTE);
                final String priority = configurationElement.getAttribute(PRIORITY_ATTRIBUTE);

                String icon = configurationElement.getAttribute(ICON_ELEMENT);
                Image image = null;
                if (icon != null) {
                    image = AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, icon).createImage();
                }

                try {
                    if (isValidAttribute(label) && isValidAttribute(commandId) && isValidAttribute(priority)) {
                        links.add(new ContributionLink(Integer.parseInt(priority), commandId, label, image));
                    } else {
                        Logs.log(LOG_ERROR_FAILED_TO_READ_EXTENSION_ELEMENT, CONTRIBUTION_ELEMENT);
                    }
                } catch (Exception e) {
                    Logs.log(LOG_ERROR_FAILED_TO_READ_EXTENSION_ELEMENT, CONTRIBUTION_ELEMENT);
                }
            }
        }
        Collections.sort(links);
        return links;
    }

    private boolean isValidAttribute(String attribute) {
        return !Strings.isNullOrEmpty(attribute);
    }
}
