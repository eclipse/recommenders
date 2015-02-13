/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Sewe - initial API and implementation.
 */
package org.eclipse.recommenders.internal.completion.rcp.tips;

import static org.eclipse.recommenders.internal.completion.rcp.Constants.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.recommenders.completion.rcp.IRecommendersCompletionContext;
import org.eclipse.recommenders.completion.rcp.processable.SessionProcessor;
import org.eclipse.recommenders.completion.rcp.tips.ICompletionTipProposal;
import org.eclipse.recommenders.internal.completion.rcp.Constants;
import org.eclipse.recommenders.internal.completion.rcp.LogMessages;
import org.eclipse.recommenders.utils.Logs;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;

public class TipsSessionProcessor extends SessionProcessor {

    private static final String COMPLETION_TIP_ID = "id"; //$NON-NLS-1$
    private static final String COMPLETION_TIP_CLASS = "class"; //$NON-NLS-1$

    private static final IConfigurationElement[] CONFIGURATION_ELEMENTS = RegistryFactory.getRegistry()
            .getConfigurationElementsFor(EXT_POINT_COMPLETION_TIPS);

    private final IConfigurationElement[] configurationElements;
    private final Map<ICompletionTipProposal, String> proposalsToShow = Maps.newHashMap();
    private final Map<String, Date> seenTips = Maps.newHashMap();

    public TipsSessionProcessor() {
        this(CONFIGURATION_ELEMENTS);
    }

    @VisibleForTesting
    TipsSessionProcessor(IConfigurationElement[] configurationElements) {
        this.configurationElements = configurationElements;
    }

    @Override
    public boolean startSession(IRecommendersCompletionContext context) {
        if (preventsAutoComplete(context)) {
            return false;
        }

        proposalsToShow.clear();
        seenTips.clear();

        initializeProposalsToShow(context.getInvocationOffset());

        return !proposalsToShow.isEmpty();
    }

    private void initializeProposalsToShow(int cursorPosition) {
        for (final IConfigurationElement configurationElement : configurationElements) {
            String id = configurationElement.getAttribute(COMPLETION_TIP_ID);
            if (id == null) {
                continue;
            }

            if (!getEnabledPreference(id)) {
                continue;
            }

            ICompletionTipProposal proposal;
            try {
                proposal = (ICompletionTipProposal) configurationElement.createExecutableExtension(COMPLETION_TIP_CLASS);
            } catch (CoreException e) {
                Logs.log(LogMessages.LOG_ERROR_CANNOT_INSTANTIATE_COMPLETION_TIP_PROPOSAL, e, id);
                continue;
            }
            proposal.setCursorPosition(cursorPosition);

            boolean isApplicable = proposal.isApplicable(getLastSeenPreference(id).orNull());

            if (isApplicable) {
                proposalsToShow.put(proposal, id);
            }
        }
    }

    private boolean preventsAutoComplete(IRecommendersCompletionContext context) {
        return context.getProposals().size() <= 1;
    }

    @Override
    public void endSession(List<ICompletionProposal> proposals) {
        proposals.addAll(proposalsToShow.keySet());
    }

    @Override
    public void selected(ICompletionProposal proposal) {
        if (proposalsToShow.containsKey(proposal)) {
            String tipId = proposalsToShow.get(proposal);
            Date now = new java.util.Date();
            setLastSeenPreference(tipId, now);
        }
    }

    private static boolean getEnabledPreference(String tipId) {
        return getTipsPreferences().node(PREF_TIPS).node(tipId).getBoolean(PREF_ENABLED, true);
    }

    private static Optional<Date> getLastSeenPreference(String tipId) {
        long lastSeen = getTipsPreferences().node(PREF_TIPS).node(tipId).getLong(PREF_LAST_SEEN, -1);
        return Optional.fromNullable(lastSeen > 0 ? new Date(lastSeen) : null);
    }

    private static void setLastSeenPreference(String tipId, Date lastSeen) {
        getTipsPreferences().node(PREF_TIPS).node(tipId).putLong(PREF_LAST_SEEN, lastSeen.getTime());
    }

    private static IEclipsePreferences getTipsPreferences() {
        return InstanceScope.INSTANCE.getNode(Constants.BUNDLE_NAME);
    }
}
