/**
 * Copyright (c) 2015 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Haftstein - initial API
 */
package org.eclipse.recommenders.internal.stacktraces.rcp;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

class Shells {

    /**
     * Return the modal shell that is currently open. If there isn't one then return null.
     *
     * @param shell
     *            A shell to exclude from the search. May be <code>null</code>.
     * @return Shell or <code>null</code>.
     */
    public static Shell getModalShellExcluding(Shell shell) {
        // initial implementation in org.eclipse.equinox.internal.p2.ui.discovery.util.WorkbenchUtil
        IWorkbench workbench = PlatformUI.getWorkbench();
        Shell[] shells = workbench.getDisplay().getShells();
        int modal = SWT.APPLICATION_MODAL | SWT.SYSTEM_MODAL | SWT.PRIMARY_MODAL;
        for (Shell shell2 : shells) {
            if (shell2.equals(shell)) {
                continue;
            }
            if (shell2.isVisible()) {
                int style = shell2.getStyle();
                if ((style & modal) != 0) {
                    return shell2;
                }
            }
        }
        return null;
    }
}
