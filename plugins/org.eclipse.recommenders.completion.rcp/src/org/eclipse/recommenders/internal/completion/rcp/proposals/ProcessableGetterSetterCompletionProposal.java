package org.eclipse.recommenders.internal.completion.rcp.proposals;

import static com.google.common.base.Optional.fromNullable;

import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.text.java.GetterSetterCompletionProposal;

import com.google.common.base.Optional;

public class ProcessableGetterSetterCompletionProposal extends GetterSetterCompletionProposal implements
        IProcessableProposal {

    private String lastPrefix;
    private ProposalProcessorManager contrib;
    private CompletionProposal coreProposal;

    public ProcessableGetterSetterCompletionProposal(CompletionProposal coreProposal, IField field, boolean isGetter,
            int relevance) throws JavaModelException {
        super(field, coreProposal.getReplaceStart(), coreProposal.getReplaceEnd() - coreProposal.getReplaceStart(),
                isGetter, relevance);
    }

    // ===========

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
    }

    @Override
    public ProposalProcessorManager getContributionManager() {
        return contrib;
    }

    @Override
    public void setContributionManager(ProposalProcessorManager mgr) {
        this.contrib = mgr;
    }

}
