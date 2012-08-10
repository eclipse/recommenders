package org.eclipse.recommenders.internal.completion.rcp.proposals;

import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jface.viewers.StyledString;

import com.google.common.base.Optional;

public interface IProcessableProposal extends IJavaCompletionProposal {


    public void setRelevance(int newRelevance);

    StyledString getStyledDisplayString();

    void setStyledDisplayString(StyledString styledDisplayString);

    public ProposalProcessorManager getProposalProcessorManager();

    void setProposalProcessorManager(ProposalProcessorManager mgr);

    Optional<CompletionProposal> getCoreProposal();

    // boolean isPrefix(String prefix, String completion);
    String getPrefix();

}
