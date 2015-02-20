/**
 * Copyright (c) 2014 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Haftstein - initial API and implementation.
 */
package org.eclipse.recommenders.internal.stacktraces.rcp.notifications;

import java.util.List;

import org.eclipse.jface.window.Window;
import org.eclipse.recommenders.internal.stacktraces.rcp.ConfigurationDialog;
import org.eclipse.recommenders.internal.stacktraces.rcp.Constants;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.RememberSendAction;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.SendAction;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.Settings;
import org.eclipse.swt.widgets.Shell;

import com.google.common.collect.Lists;

public class ConfigurationNotification extends ExecutableErrorReportUiNotification {

    private Settings settings;
    private Shell parentShell;

    public ConfigurationNotification(Settings settings, Shell parentShell) {
        super(Constants.NOTIFY_CONFIGURATION);
        this.settings = settings;
        this.parentShell = parentShell;
    }

    @Override
    public List<Action> getActions() {
        Action a1 = new Action("Configure") {

            @Override
            public void execute() {
                configure();
            }

        };
        Action a2 = new Action("Disable") {

            @Override
            public void execute() {
                disable();
            }

        };
        return Lists.newArrayList(a1, a2);
    }

    private void configure() {
        ConfigurationDialog configurationDialog = new ConfigurationDialog(parentShell, settings);
        configurationDialog.setBlockOnOpen(true);
        int status = configurationDialog.open();

        switch (status) {
        case Window.OK: {
            settings.setAction(SendAction.ASK);
            settings.setConfigured(true);
            break;
        }
        case Window.CANCEL: {
            settings.setAction(SendAction.IGNORE);
            settings.setConfigured(true);
            break;
        }
        case ConfigurationDialog.ESC_CANCEL: {
            settings.setAction(SendAction.IGNORE);
            settings.setRememberSendAction(RememberSendAction.RESTART);
            settings.setConfigured(false);
            break;
        }
        default:
            // nothing
        }
    }

    private void disable() {
        settings.setAction(SendAction.IGNORE);
        settings.setConfigured(true);
    }

    @Override
    protected String getTitle() {
        return "Welcome to the Automated Error Reporting";
    }

    @Override
    public String getLabel() {
        return "Do you want to enable Error Reporting in Eclipse?";
    }

    @Override
    public String getDescription() {
        return "Error events may reveal issues in Eclipse. Thus we ask you to report them to eclipse.org. To help improve Eclipse, please enable the reporter.";
    }

}
