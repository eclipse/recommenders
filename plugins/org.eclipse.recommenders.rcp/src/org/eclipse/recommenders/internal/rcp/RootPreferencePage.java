/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 *    Olav Lenz - externalize Strings.
 *    Yasser Aziza - preference contributions
 */
package org.eclipse.recommenders.internal.rcp;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.google.common.collect.Lists;

public class RootPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private final String PREF_CONTRIBUTION_ID = "org.eclipse.recommenders.preferenceContribution"; //$NON-NLS-1$
    private final String CONTRIBUTION_ELEMENT = "preferenceContribution"; //$NON-NLS-1$

    private final String PARAMETER_PATTERN = "\"(.*?)\""; //$NON-NLS-1$

    private final String LABEL_ATTRIBUTE = "label"; //$NON-NLS-1$
    private final String COMMAND_ID_ATTRIBUTE = "commandId"; //$NON-NLS-1$
    private final String PRIORITY_ATTRIBUTE = "priority"; //$NON-NLS-1$
    private final String ICON_ELEMENT = "icon"; //$NON-NLS-1$

    @Override
    public void init(final IWorkbench workbench) {
        setDescription(Messages.PREFPAGE_DESCRIPTION_EMPTY);
    }

    @Override
    protected Control createContents(final Composite parent) {
        noDefaultAndApplyButton();
        Composite composite = new Composite(parent, SWT.NONE);
        GridDataFactory.fillDefaults().applyTo(composite);
        GridLayoutFactory.swtDefaults().margins(0, 5).applyTo(composite);

        Group group = new Group(composite, SWT.NONE);
        group.setText("Useful links"); //$NON-NLS-1$
        GridDataFactory.fillDefaults().grab(true, false).applyTo(group);
        GridLayoutFactory.swtDefaults().numColumns(2).applyTo(group);

        for (RootPreferenceLink link : getContributionLinks(group)) {
            link.appendLink(group);
        }

        return parent;
    }

    private List<RootPreferenceLink> getContributionLinks(Group group) {
        final IConfigurationElement[] configurationElements = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(PREF_CONTRIBUTION_ID);
        return readRegisteredDatums(group, configurationElements);
    }

    private List<RootPreferenceLink> readRegisteredDatums(Group group, IConfigurationElement... configurationElements) {
        List<RootPreferenceLink> links = Lists.newArrayList();
        if (configurationElements == null) {
            return links;
        }
        for (final IConfigurationElement configurationElement : configurationElements) {
            if (CONTRIBUTION_ELEMENT.equals(configurationElement.getName())) {
                final String pluginId = configurationElement.getContributor().getName();
                final String label = configurationElement.getAttribute(LABEL_ATTRIBUTE);
                final String commandId = configurationElement.getAttribute(COMMAND_ID_ATTRIBUTE);
                final int priority = Integer.parseInt(configurationElement.getAttribute(PRIORITY_ATTRIBUTE));

                String icon = configurationElement.getAttribute(ICON_ELEMENT);
                Image image = null;
                if (icon != null) {
                    image = AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, icon).createImage();
                }

                links.add(new RootPreferenceLink(priority, commandId, label, extractParameter(label), image));
            }
        }
        Collections.sort(links);
        return links;
    }

    private String extractParameter(String label) {
        Pattern pattern = Pattern.compile(PARAMETER_PATTERN);
        Matcher matcher = pattern.matcher(label);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return "";
    }
}
