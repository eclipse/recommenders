/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.completion.rcp.tips;

import static org.eclipse.jface.viewers.StyledString.DECORATIONS_STYLER;

import java.util.Date;

import javax.inject.Inject;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.AbstractInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.recommenders.completion.rcp.tips.AbstractCompletionTipProposal;
import org.eclipse.recommenders.internal.completion.rcp.Messages;
import org.eclipse.recommenders.rcp.SharedImages;
import org.eclipse.recommenders.rcp.SharedImages.Images;
import org.eclipse.recommenders.rcp.utils.Dialogs;
import org.eclipse.recommenders.utils.Nullable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("restriction")
public class DiscoveryCompletionProposal extends AbstractCompletionTipProposal {

    private static final String DISCOVERY_URL = "http://download.eclipse.org/recommenders/discovery/2.0/completion/directory.xml"; //$NON-NLS-1$

    @Inject
    public DiscoveryCompletionProposal(SharedImages images) {
        Image image = images.getImage(Images.OBJ_LIGHTBULB);
        setImage(image);
        StyledString text = new StyledString(Messages.PROPOSAL_LABEL_DISCOVER_EXTENSIONS, DECORATIONS_STYLER);
        setStyledDisplayString(text);
        setSortString(text.getString());
    }

    @Override
    public boolean isApplicable(@Nullable Date lastSeen) {
        return lastSeen == null; // Don't show this twice
    }

    @Override
    public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
        Dialogs.newExtensionsDiscoveryDialog(DISCOVERY_URL).open();
    }

    @Override
    protected IInformationControl createInformationControl(Shell parent, String statusLineText) {
        return new ConfigureContentAssistInformationControl(parent, statusLineText);
    }

    private static final class ConfigureContentAssistInformationControl extends AbstractInformationControl {

        private ConfigureContentAssistInformationControl(Shell parent, String statusLineText) {
            super(parent, statusLineText);
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
            link.setText(Messages.PROPOSAL_TOOLTIP_DISCOVER_EXTENSIONS);
        }
    }
}
