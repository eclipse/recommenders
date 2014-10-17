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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.recommenders.internal.stacktraces.rcp.FadeJob.FadeJobProgressListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class UploadNotificationPopup extends Dialog implements FadeJobProgressListener {

    public static final int WIDTH = 300;
    public static final int HEIGHT = 200;
    private String text;

    public UploadNotificationPopup(Shell parentShell, String text) {
        super(parentShell);
        this.text = text;
        setShellStyle(SWT.TITLE | SWT.MODELESS);
    }

    @Override
    public void create() {
        super.create();
        setBlockOnOpen(false);
        getShell().setAlpha(0);
        new FadeJob(getShell(), 50, 12, this).schedule();
        new FadeJob(getShell(), 50, -40, this).schedule(3000);

    }

    @Override
    protected Point getInitialSize() {
        return new Point(WIDTH, HEIGHT);
    }

    @Override
    public void fadeJobFinished(int currentAlpha) {
        if (currentAlpha == 0) {
            close();
        }
    }

    @Override
    public boolean close() {
        return super.close();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        Label messageLabel = new Label(container, SWT.NONE);
        messageLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
        messageLabel.setText(this.text);
        return container;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Error Reporting");
    }

    @Override
    protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
        return null;
    }

}
