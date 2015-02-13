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

import static org.eclipse.recommenders.internal.rcp.Constants.COMMAND_HREF_ID;
import static org.eclipse.recommenders.internal.rcp.LogMessages.LOG_ERROR_FAILED_TO_EXECUTE_COMMAND;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.recommenders.utils.Logs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.plugin.AbstractUIPlugin;

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

        addContributionLinks(group);

        return parent;
    }

    private void addContributionLinks(Group group) {
        final IConfigurationElement[] configurationElements = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(PREF_CONTRIBUTION_ID);
        readRegisteredDatums(group, configurationElements);
    }

    private void readRegisteredDatums(Group group, IConfigurationElement... configurationElements) {
        if (configurationElements == null) {
            return;
        }
        for (final IConfigurationElement configurationElement : configurationElements) {
            if (CONTRIBUTION_ELEMENT.equals(configurationElement.getName())) {
                final String pluginId = configurationElement.getContributor().getName();
                final String label = configurationElement.getAttribute(LABEL_ATTRIBUTE);
                final String commandId = configurationElement.getAttribute(COMMAND_ID_ATTRIBUTE);
                final String priority = configurationElement.getAttribute(PRIORITY_ATTRIBUTE);

                String icon = configurationElement.getAttribute(ICON_ELEMENT);
                ImageDescriptor imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, icon);

                Link link = createLink(group, imageDescriptor.createImage(), label, extractParameter(label));
                link.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        executeCommand(commandId, extractParameter(label));
                    }
                });
            }
        }
    }

    private void executeCommand(String commandId, String value) {
        ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
        IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);

        if (value.isEmpty()) {
            try {
                handlerService.executeCommand(commandId, null);
            } catch (Exception e) {
                Logs.log(LOG_ERROR_FAILED_TO_EXECUTE_COMMAND, commandId, e);
            }
        }

        try {
            Command command = commandService.getCommand(commandId);
            IParameter commandParmeter = command.getParameter(COMMAND_HREF_ID);
            Parameterization parameterization = new Parameterization(commandParmeter, value);
            ParameterizedCommand parameterizedCommand = new ParameterizedCommand(command,
                    new Parameterization[] { parameterization });
            handlerService.executeCommand(parameterizedCommand, null);
        } catch (Exception e) {
            Logs.log(LOG_ERROR_FAILED_TO_EXECUTE_COMMAND, commandId, e);
        }
    }

    private Link createLink(Composite content, Image icon, String urlLabel, String url) {
        Label label = new Label(content, SWT.BEGINNING);
        label.setImage(icon);

        Link link = new Link(content, SWT.BEGINNING);
        link.setText(MessageFormat.format(urlLabel, url));
        return link;
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
