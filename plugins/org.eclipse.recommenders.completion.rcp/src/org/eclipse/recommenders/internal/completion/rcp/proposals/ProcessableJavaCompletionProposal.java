/**
 * Copyright (c) 2010, 2012 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - Initial API
 */
package org.eclipse.recommenders.internal.completion.rcp.proposals;

import static com.google.common.base.Optional.fromNullable;

import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;

import com.google.common.base.Optional;

public class ProcessableJavaCompletionProposal extends org.eclipse.jdt.internal.ui.text.java.JavaCompletionProposal implements
        IProcessableProposal {

    private ProposalProcessorManager contrib;
    private CompletionProposal coreProposal;
    private String lastPrefix;

    protected ProcessableJavaCompletionProposal(CompletionProposal coreProposal, JavaCompletionProposal uiProposal,
            JavaContentAssistInvocationContext context) throws JavaModelException {
        super(uiProposal.getReplacementString(), coreProposal.getReplaceStart(), uiProposal.getReplacementLength(),
                uiProposal.getImage(), uiProposal.getStyledDisplayString(), coreProposal.getRelevance(), true, context);
        this.coreProposal = coreProposal;
    }

    @Override
    public boolean isPrefix(final String prefix, final String completion) {
        lastPrefix = prefix;
        if (contrib.prefixChanged(prefix)) {
            return true;
        }
        return super.isPrefix(prefix, completion);
    }

    @Override
    public String getPrefix() {
        return lastPrefix;
    }

    @Override
    public Optional<CompletionProposal> getCoreProposal() {
        return fromNullable(coreProposal);
    }    @Override
    public ProposalProcessorManager getContributionManager() {
        return contrib;
    }
    @Override
    public void setContributionManager(ProposalProcessorManager mgr) {
        this.contrib = mgr;
    }

}
