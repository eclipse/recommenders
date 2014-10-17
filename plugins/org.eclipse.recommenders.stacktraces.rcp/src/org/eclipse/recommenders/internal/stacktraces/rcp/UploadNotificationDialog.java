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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

//TODO need something like AbstractNotificationPopup (fade in/out, auto close after 5 secs, keep open if clicked)
public class UploadNotificationDialog extends Dialog {

    private static class FadeJob extends Job {

        private int currentAlpha;
        private Shell shell;
        private int increasePerTick;
        private boolean finished;
        private int delayMs;

        public FadeJob(Shell shell, int delayMs, int increasePerTick) {
            super("Fade");
            this.delayMs = delayMs;
            this.increasePerTick = increasePerTick;
            this.currentAlpha = shell.getAlpha();
            this.shell = shell;
            schedule(delayMs);
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            currentAlpha += increasePerTick;
            if (currentAlpha <= 0) {
                currentAlpha = 0;
            } else if (currentAlpha >= 255) {
                currentAlpha = 255;
            }
            if (currentAlpha == 0 || currentAlpha == 255) {
                finished = true;
            }
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    if (shell.isDisposed()) {
                        return;
                    }
                    shell.setAlpha(currentAlpha);
                }
            });

            if (!finished) {
                schedule(delayMs);
            }
            return Status.OK_STATUS;
        }

    }

    public static final int WIDTH = 300;
    public static final int HEIGHT = 200;

    public UploadNotificationDialog(Shell parentShell) {
        super(parentShell);
        setShellStyle(SWT.TITLE | SWT.MODELESS);
    }

    @Override
    public void create() {
        super.create();
        getShell().setAlpha(0);
        new FadeJob(getShell(), 100, 25);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(WIDTH, HEIGHT);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        return super.createDialogArea(parent);
    }

    @Override
    protected boolean isResizable() {
        return false;
    }

    @Override
    protected void okPressed() {
        super.okPressed();
        try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("Search for Updates");
        } catch (PartInitException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
