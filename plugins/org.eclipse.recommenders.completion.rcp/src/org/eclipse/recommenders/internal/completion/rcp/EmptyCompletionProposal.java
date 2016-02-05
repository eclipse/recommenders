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
package org.eclipse.recommenders.internal.completion.rcp;

import static org.eclipse.jface.viewers.StyledString.QUALIFIER_STYLER;

import org.eclipse.jdt.internal.ui.text.java.AbstractJavaCompletionProposal;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.viewers.StyledString;
// leave a bit space for other, maybe more important proposals
// don't do anything. In particular do not call the super implementation.
import org.eclipse.recommenders.internal.completion.rcp.l10n.Messages;

@SuppressWarnings("restriction")
public class EmptyCompletionProposal extends AbstractJavaCompletionProposal {

    private static final int RELEVANCE = Integer.MAX_VALUE;

    /**
     * @see {@linkplain org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal#JavaCompletionProposal(String, int, int, org.eclipse.swt.graphics.Image, StyledString, int, boolean, org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext)
     *      The constructor of JavaCompletionProposal} for hints on the setters to call here.
     */
    public EmptyCompletionProposal(int invocationOffset) {
        setReplacementOffset(invocationOffset);
        setReplacementString(""); //$NON-NLS-1$
        setReplacementLength(0);

        setStyledDisplayString(new StyledString(Messages.PROPOSAL_LABEL_NO_PROPOSALS, QUALIFIER_STYLER));

        setRelevance(RELEVANCE);

        // Don't sort this proposal based on its label, but always show it before all other proposals.
        setSortString("");

        setCursorPosition(0);
    }

    @Override
    public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
        // don't do anything. In particular do not call the super implementation.
    }

    @Override
    protected boolean isValidPrefix(String prefix) {
        return true;
    }
}
