/**
 * Copyright (c) 2015 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Johannes Dorn - initial API and implementation.
 */
package org.eclipse.recommenders.internal.subwords.rcp;

import static org.eclipse.jface.viewers.StyledString.DECORATIONS_STYLER;
import static org.eclipse.recommenders.completion.rcp.tips.ConfigureContentAssistInformationControl.*;
import static org.eclipse.recommenders.internal.completion.rcp.EnabledCompletionProposal.*;
import static org.eclipse.recommenders.internal.subwords.rcp.Constants.*;
import static org.eclipse.recommenders.rcp.utils.PreferencesHelper.createLinkLabelToPreferencePage;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.recommenders.completion.rcp.IRecommendersCompletionContext;
import org.eclipse.recommenders.completion.rcp.processable.SessionProcessorDescriptor;
import org.eclipse.recommenders.completion.rcp.tips.AbstractCompletionTipProposal;
import org.eclipse.recommenders.completion.rcp.tips.ConfigureContentAssistInformationControl;
import org.eclipse.recommenders.internal.completion.rcp.CompletionRcpPreferences;
import org.eclipse.recommenders.rcp.SharedImages;
import org.eclipse.recommenders.rcp.SharedImages.Images;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.google.common.collect.ImmutableList;

@SuppressWarnings("restriction")
public class DisableSubwordsCompletionProposal extends AbstractCompletionTipProposal {

    private static final List<SessionProcessorDescriptor> NONE = Collections.<SessionProcessorDescriptor>emptyList();
    private static final String ABOUT_PREFERENCES = X_PREFERENCES + SUBWORDS_COMPLETION_PREFERENCE_PAGE_ID;
    private static final String ABOUT_DISABLE = X_SESSION_PROCESSOR + SWITCH_OFF + SESSION_PROCESSOR_ID;
    private static final String HTTP_MANUAL = "https://www.eclipse.org/recommenders/manual/#completion-engines"; //$NON-NLS-1$
    private static final String PAGE_NAME = createLinkLabelToPreferencePage(SUBWORDS_COMPLETION_PREFERENCE_PAGE_ID);

    private static final String INFO = MessageFormat.format(Messages.PROPOSAL_TOOLTIP_ENABLE_SUBWORDS_COMPLETION,
            PAGE_NAME, ABOUT_PREFERENCES, ABOUT_DISABLE, HTTP_MANUAL);

    // this proposal should appear below the enable code completion proposal
    private static final int ENABLE_TYPE_COMPLETION_RELEVANCE = ENABLE_CODE_COMPLETION_RELEVANCE - RELEVANCE_STEP_SIZE;

    private final CompletionRcpPreferences preferences;

    @Inject
    public DisableSubwordsCompletionProposal(SharedImages images, CompletionRcpPreferences completionPreferences) {
        this.preferences = completionPreferences;
        Image image = images.getImage(Images.OBJ_LIGHTBULB);
        setImage(image);
        setRelevance(ENABLE_TYPE_COMPLETION_RELEVANCE);
        StyledString text = new StyledString(Messages.PROPOSAL_LABEL_ENABLE_SUBWORDS_COMPLETION, DECORATIONS_STYLER);
        setStyledDisplayString(text);
        setSortString(text.getString());
    }

    @Override
    public boolean isApplicable(IRecommendersCompletionContext context) {
        SessionProcessorDescriptor descriptor = preferences.getSessionProcessorDescriptor(SESSION_PROCESSOR_ID);
        if (descriptor == null) {
            return false;
        }

        if (!preferences.isEnabled(descriptor)) {
            return false;
        }

        return true;
    }

    @Override
    public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
        SessionProcessorDescriptor descriptor = preferences.getSessionProcessorDescriptor(SESSION_PROCESSOR_ID);
        if (descriptor != null) {
            preferences.setSessionProcessorEnabled(NONE, ImmutableList.of(descriptor));
        }
    }

    @Override
    protected IInformationControl createInformationControl(Shell parent, String statusLineText) {
        return new ConfigureContentAssistInformationControl(parent, statusLineText, INFO, preferences);
    }
}
