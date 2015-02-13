/**
 * Copyright (c) 2105 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Sewe - initial API and implementation.
 */
package org.eclipse.recommenders.internal.completion.rcp.tips;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.AbstractInformationControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

public class CompletionTipProposalInformationControl extends AbstractInformationControl {

    private final String tooltip;

    CompletionTipProposalInformationControl(Shell parent, String statusLineText, String tooltip) {
        super(parent, statusLineText);
        this.tooltip = tooltip;
        create();
    }

    @Override
    public boolean hasContents() {
        return true;
    }

    @Override
    protected void createContent(Composite parent) {
        Link link = new Link(parent, SWT.NONE);
        Dialog.applyDialogFont(link);
        link.setForeground(parent.getForeground());
        link.setBackground(parent.getBackground());
        link.setText(tooltip);
    }
}
