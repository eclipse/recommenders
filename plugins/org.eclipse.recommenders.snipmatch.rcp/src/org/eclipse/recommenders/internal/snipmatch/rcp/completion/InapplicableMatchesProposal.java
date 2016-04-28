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

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension3;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension5;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension6;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.recommenders.internal.snipmatch.rcp.l10n.Messages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;

public class InapplicableMatchesProposal implements ICompletionProposal, ICompletionProposalExtension3,
        ICompletionProposalExtension5, ICompletionProposalExtension6 {

    private final int matches;
    private final Image image;

    public InapplicableMatchesProposal(int matches, Image image) {
        this.matches = matches;
        this.image = image;
    }

    @Override
    public StyledString getStyledDisplayString() {
        StyledString styledString = new StyledString();
        styledString.append(MessageFormat.format(Messages.COMPLETION_ENGINE_INAPPLICABLE_MATCHES_PROPOSAL, matches),
                StyledString.DECORATIONS_STYLER);
        return styledString;
    }

    @Override
    public IInformationControlCreator getInformationControlCreator() {
        return new IInformationControlCreator() {
            @Override
            public IInformationControl createInformationControl(Shell parent) {
                return new InapplicableMatchesInformationControl(parent, Messages.PROPOSAL_CATEGORY_CODE_RECOMMENDERS,
                        MessageFormat.format(Messages.COMPLETION_ENGINE_INAPPLICABLE_MATCHES_PROPOSAL_TOOLTIP,
                                matches));
            }
        };
    }

    @Override
    public CharSequence getPrefixCompletionText(IDocument document, int completionOffset) {
        return null;
    }

    @Override
    public int getPrefixCompletionStart(IDocument document, int completionOffset) {
        return 0;
    }

    @Override
    public void apply(IDocument document) {
    }

    @Override
    public Point getSelection(IDocument document) {
        return null;
    }

    @Override
    public String getDisplayString() {
        return getStyledDisplayString().toString();
    }

    @Override
    public Image getImage() {
        return image;
    }

    @Override
    public IContextInformation getContextInformation() {
        return null;
    }

    @Override
    public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
        return MessageFormat.format(Messages.COMPLETION_ENGINE_INAPPLICABLE_MATCHES_PROPOSAL_TOOLTIP, matches);
    }

    @Override
    public String getAdditionalProposalInfo() {
        return null;
    }

}
