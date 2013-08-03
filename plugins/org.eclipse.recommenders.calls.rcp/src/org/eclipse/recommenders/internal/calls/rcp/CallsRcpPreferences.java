package org.eclipse.recommenders.internal.calls.rcp;

import static org.eclipse.recommenders.internal.calls.rcp.Constants.*;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;

public class CallsRcpPreferences {

    @Inject
    @Preference(P_MIN_PROPOSAL_PROBABILITY)
    public int minProposalProbability;

    @Inject
    @Preference(P_MAX_NUMBER_OF_PROPOSALS)
    public int maxNumberOfProposals;

    @Inject
    @Preference(P_UPDATE_PROPOSAL_RELEVANCE)
    public boolean changeProposalRelevance;

    @Inject
    @Preference(P_DECORATE_PROPOSAL_ICON)
    public boolean decorateProposalIcon;

    @Inject
    @Preference(P_DECORATE_PROPOSAL_TEXT)
    public boolean decorateProposalText;

    @Inject
    @Preference
    public IEclipsePreferences store;
}
