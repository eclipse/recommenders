package org.eclipse.recommenders.internal.completion.rcp.proposals;

import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.jface.viewers.StyledString;

/**
 * ProposalProcessor can manipulate different aspects of a proposal such as prefix matching, display string and
 * relevance. They are typically registered at {@link ProposalProcessorManager#addProcessor(ProposalProcessor)} in
 * {@link SessionProcessor#process(IProcessableProposal)}.
 */
public abstract class ProposalProcessor {

    /**
     * returns whether this prefix could work as prefix for this completion. This method is tought to enable other
     * matching strategies like subwords - or even more obscure token/proposal matchers.
     */
    public boolean isPrefix(String prefix) {
        return false;
    }

    /**
     * Enables processorts to modify the given display string. It's always a fresh display string, but shared between
     * all processors.
     */
    public void modifyDisplayString(StyledString displayString) {
    }

    /**
     * used to update the default relevance of this proposal by some increment. The initial relevance value is JDT's
     * default value.
     */
    public void modifyRelevance(AtomicInteger relevance) {
    }

}