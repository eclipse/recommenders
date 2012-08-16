package org.eclipse.recommenders.internal.completion.rcp;

import java.util.Set;

import javax.inject.Inject;

import org.eclipse.recommenders.completion.rcp.IRecommendersCompletionContextFactory;
import org.eclipse.recommenders.injection.Guice;
import org.eclipse.recommenders.internal.completion.rcp.proposals.ProcessableProposalFactory;

public class RecommendersAllCompletionProposalProcessor extends ProcessableCompletionProposalComputer {

    private SessionProcessorDescriptor[] descriptors;

    @Inject
    public RecommendersAllCompletionProposalProcessor(SessionProcessorDescriptor[] descriptors,
            ProcessableProposalFactory proposalFactory, IRecommendersCompletionContextFactory contextFactory) {
        super(new ProcessableProposalFactory(), contextFactory);
        this.descriptors = descriptors;
    }

    @Override
    public void sessionStarted() {
        super.sessionStarted();
        processors.clear();
        for (SessionProcessorDescriptor d : descriptors) {
            if (d.isEnabled()) {
                processors.add(d.getProcessor());
            }
        }
    }
}
