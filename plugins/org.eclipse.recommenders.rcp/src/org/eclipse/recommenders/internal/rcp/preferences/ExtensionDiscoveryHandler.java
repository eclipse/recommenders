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
package org.eclipse.recommenders.internal.rcp.preferences;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.recommenders.rcp.utils.Dialogs;
import org.eclipse.recommenders.utils.rcp.preferences.AbstractLinkContributionPage;
import org.eclipse.ui.PlatformUI;

public class ExtensionDiscoveryHandler extends AbstractHandler {

    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

            @Override
            public void run() {
                Dialogs.newExtensionsDiscoveryDialog(event.getParameter(AbstractLinkContributionPage.COMMAND_HREF_ID))
                        .open();
            }
        });
        return null;
    }
}
