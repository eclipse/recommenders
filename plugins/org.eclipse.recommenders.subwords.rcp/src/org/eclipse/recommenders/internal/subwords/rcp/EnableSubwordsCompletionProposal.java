/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology. All rights reserved. This program and the accompanying
 * materials are made available under the terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Johannes Dorn - initial API and implementation.
 */
package org.eclipse.recommenders.internal.subwords.rcp;

import static org.eclipse.jdt.internal.ui.JavaPlugin.getActiveWorkbenchShell;
import static org.eclipse.jface.viewers.StyledString.DECORATIONS_STYLER;
import static org.eclipse.recommenders.internal.subwords.rcp.Constants.*;
import static org.eclipse.recommenders.rcp.utils.PreferencesHelper.createLinkLabelToPreferencePage;
import static org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn;

import java.net.URL;
import java.text.MessageFormat;
import java.util.Collections;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.ui.text.java.AbstractJavaCompletionProposal;
import org.eclipse.jface.text.AbstractInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.recommenders.completion.rcp.processable.SessionProcessorDescriptor;
import org.eclipse.recommenders.completion.rcp.tips.ICompletionTipProposal;
import org.eclipse.recommenders.internal.completion.rcp.CompletionRcpPreferences;
import org.eclipse.recommenders.rcp.SharedImages;
import org.eclipse.recommenders.rcp.SharedImages.Images;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

import com.google.common.collect.ImmutableList;

public class EnableSubwordsCompletionProposal extends AbstractJavaCompletionProposal implements ICompletionTipProposal {

    private static final Object DUMMY_INFO = new Object();

    private static final String ABOUT_PREFERENCES = "about:preferences"; //$NON-NLS-1$
    private static final String ABOUT_ENABLE = "about:enable"; //$NON-NLS-1$
    private static final String HTTP_MANUAL = "https://www.eclipse.org/recommenders/manual/#completion-engines"; //$NON-NLS-1$
    private static final String PAGE_NAME = createLinkLabelToPreferencePage(SUBWORDS_COMPLETION_PREFERENCE_PAGE_ID);

    private static final String INFO = MessageFormat.format(Messages.PROPOSAL_TOOLTIP_ENABLE_SUBWORDS_COMPLETION,
            PAGE_NAME, ABOUT_PREFERENCES, ABOUT_ENABLE, HTTP_MANUAL);

    // Place this proposal at the bottom of the list.
    // Use -1 as Integer.MIN_VALUE does not work (possibly due to underflow) and other proposals (e.g., package
    // subwords) can have a relevance of 0..
    private static final int RELEVANCE = 100;

    private final CompletionRcpPreferences preferences;

    @Inject
    public EnableSubwordsCompletionProposal(SharedImages images, CompletionRcpPreferences completionPreferences) {
        this.preferences = completionPreferences;
        Image image = images.getImage(Images.OBJ_LIGHTBULB);
        StyledString text = new StyledString(Messages.PROPOSAL_LABEL_ENABLE_SUBWORDS_COMPLETION, DECORATIONS_STYLER);
        setStyledDisplayString(text);
        setImage(image);
        setRelevance(RELEVANCE);
        setReplacementString(""); //$NON-NLS-1$
    }

    @Override
    public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
        return DUMMY_INFO;
    }

    @Override
    protected boolean isPrefix(String prefix, String string) {
        return true;
    }

    @Override
    protected boolean isValidPrefix(String prefix) {
        return true;
    }

    @Override
    public boolean isApplicable() {
        SessionProcessorDescriptor descriptor = preferences.getSessionProcessorDescriptor(SESSION_PROCESSOR_ID);
        return descriptor != null && !preferences.isEnabled(descriptor);
    }

    @Override
    public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
        enableSubwordsCompletion();
    }

    @Override
    public IInformationControlCreator getInformationControlCreator() {
        return new IInformationControlCreator() {

            @Override
            public IInformationControl createInformationControl(Shell parent) {
                return new ConfigureContentAssistInformationControl(parent);
            }
        };
    }

    private void enableSubwordsCompletion() {
        SessionProcessorDescriptor descriptor = preferences.getSessionProcessorDescriptor(SESSION_PROCESSOR_ID);
        if (descriptor != null) {
            preferences.setSessionProcessorEnabled(ImmutableList.of(descriptor),
                    Collections.<SessionProcessorDescriptor>emptyList());
        }
    }

    private void openPreferencePage() {
        createPreferenceDialogOn(getActiveWorkbenchShell(), SUBWORDS_COMPLETION_PREFERENCE_PAGE_ID, null, null).open();
    }

    private void openHomepageInBrowser(String url) {
        try {
            IWebBrowser browser = PlatformUI.getWorkbench().getBrowserSupport()
                    .createBrowser(SWT.NONE, "recommenders-homepage", Messages.BROWSER_LABEL_PROJECT_WEBSITE, //$NON-NLS-1$
                            Messages.BROWSER_TOOLTIP_PROJECT_WEBSITE);
            browser.openURL(new URL(url));
        } catch (Exception e1) {
        }
    }

    private final class ConfigureContentAssistInformationControl extends AbstractInformationControl {

        private ConfigureContentAssistInformationControl(Shell parentShell) {
            super(parentShell, Messages.PROPOSAL_CATEGORY_CODE_RECOMMENDERS);
            create();
        }

        @Override
        public boolean hasContents() {
            return true;
        }

        @Override
        protected void createContent(Composite parent) {
            Link link = new Link(parent, SWT.NONE);
            link.setBackground(parent.getBackground());
            link.setText(INFO);

            link.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    dispose();
                    if (ABOUT_ENABLE.equals(e.text)) {
                        enableSubwordsCompletion();
                    } else if (ABOUT_PREFERENCES.equals(e.text)) {
                        openPreferencePage();
                    } else if (HTTP_MANUAL.equals(e.text)) {
                        openHomepageInBrowser(e.text);
                    }
                }
            });
        }
    }
}
