package org.eclipse.recommenders.internal.completion.rcp.proposals;

import java.util.List;

import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.recommenders.completion.rcp.IRecommendersCompletionContext;

public abstract class SessionProcessor {

    /**
     * called after a new completion session was started. The given context already contains the initial jdt proposal.
     * SessionProcessors may add additional proposals here if required.
     */
    public void startSession(IRecommendersCompletionContext context) {
    }

    /**
     * Called for every {@link IProcessableProposal} to allow adding individual {@link ProposalProcessor}s to a
     * proposal.
     */
    public void process(IProcessableProposal proposal) {
    }

    /**
     * Called when the proposal computation is done. Processors may add additional proposal now if required. But be
     * careful not interfere badly with other processors.
     */
    public void endSession(List<ICompletionProposal> proposals) {
    }

    /**
     * Presents the final list of proposals to all interested parties.
     * 
     * @param proposals
     *            the final list
     */
    public void aboutToShow(List<ICompletionProposal> proposals) {
    }

    /**
     * Called whenever a proposal was selected in the ui. Note that this method may be called repeatedly with the same
     * proposal.
     */
    public void selected(ICompletionProposal proposal) {
    }

    /**
     * Called when a proposal was applied on the code.
     */
    public void applied(ICompletionProposal proposal) {
    }

}
