/**
 * Copyright (c) 2016 Yasett Acurana.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Yasett Acurana - initial API and implementation.
 */
package org.eclipse.recommenders.internal.snipmatch.rcp.completion;

import static java.util.Objects.requireNonNull;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.AbstractInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class InapplicableMatchesInformationControl extends AbstractInformationControl {
    private final String description;
    private final String statusLineText;

    public InapplicableMatchesInformationControl(Shell parent, String statusLineText, String description) {
        super(parent, statusLineText);
        this.description = requireNonNull(description);
        this.statusLineText = statusLineText;
        create();
    }

    @Override
    public boolean hasContents() {
        return true;
    }

    @Override
    public IInformationControlCreator getInformationPresenterControlCreator() {
        return new IInformationControlCreator() {

            @Override
            public IInformationControl createInformationControl(Shell parent) {
                return new InapplicableMatchesInformationControl(parent, statusLineText, description);
            }
        };
    }

    @Override
    protected void createContent(Composite parent) {
        Text text = new Text(parent, SWT.WRAP);
        text.setText(description);
        text.setForeground(parent.getForeground());
        text.setBackground(parent.getBackground());
        Dialog.applyDialogFont(text);
    }
}
