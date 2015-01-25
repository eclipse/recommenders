/**
 * Copyright (c) 2014 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.stacktraces.rcp;

import org.eclipse.jface.window.Window;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.RememberSendAction;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.SendAction;
import org.eclipse.swt.widgets.Shell;

class Configurator {

    public static void configureWithDialog(Settings settings, Shell parentShell) {
        ConfigurationDialog configurationDialog = new ConfigurationDialog(parentShell, settings);
        configurationDialog.setBlockOnOpen(true);
        int status = configurationDialog.open();

        switch (status) {
        case Window.OK: {
            settings.setSendAction(SendAction.ASK);
            settings.setConfigured(true);
            break;
        }
        case Window.CANCEL: {
            settings.setSendAction(SendAction.IGNORE);
            settings.setConfigured(true);
            break;
        }
        case ConfigurationDialog.ESC_CANCEL: {
            settings.setSendAction(SendAction.IGNORE);
            settings.setRememberSendAction(RememberSendAction.RESTART);
            settings.setConfigured(false);
            break;
        }
        default:
            // nothing
        }
    }
}
