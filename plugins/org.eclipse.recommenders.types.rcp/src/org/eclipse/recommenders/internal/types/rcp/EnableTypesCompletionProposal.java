package org.eclipse.recommenders.internal.types.rcp;

import static org.eclipse.jface.viewers.StyledString.DECORATIONS_STYLER;
import static org.eclipse.recommenders.internal.types.rcp.Constants.SESSION_PROCESSOR_ID;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.AbstractInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.recommenders.completion.rcp.IRecommendersCompletionContext;
import org.eclipse.recommenders.completion.rcp.processable.SessionProcessorDescriptor;
import org.eclipse.recommenders.completion.rcp.tips.AbstractCompletionTipProposal;
import org.eclipse.recommenders.internal.completion.rcp.CompletionRcpPreferences;
import org.eclipse.recommenders.rcp.SharedImages;
import org.eclipse.recommenders.rcp.SharedImages.Images;
import org.eclipse.recommenders.rcp.utils.BrowserUtils;
import org.eclipse.recommenders.utils.names.ITypeName;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import com.google.common.collect.ImmutableList;

@SuppressWarnings("restriction")
public class EnableTypesCompletionProposal extends AbstractCompletionTipProposal {

    private static final List<SessionProcessorDescriptor> NONE = Collections.<SessionProcessorDescriptor>emptyList();
    private static final String ABOUT_ENABLE = "about:enable"; //$NON-NLS-1$
    private static final String HTTP_MANUAL = "https://www.eclipse.org/recommenders/manual/#completion-engines"; //$NON-NLS-1$

    private static final String INFO = MessageFormat.format(Messages.PROPOSAL_TOOLTIP_ENABLE_TYPES_COMPLETION,
            ABOUT_ENABLE, HTTP_MANUAL);

    private final CompletionRcpPreferences preferences;

    @Inject
    public EnableTypesCompletionProposal(SharedImages images, CompletionRcpPreferences completionPreferences) {
        this.preferences = completionPreferences;
        Image image = images.getImage(Images.OBJ_LIGHTBULB);
        setImage(image);
        StyledString text = new StyledString(Messages.PROPOSAL_LABEL_ENABLE_TYPES_COMPLETION, DECORATIONS_STYLER);
        setStyledDisplayString(text);
        setSortString(text.getString());
    }

    @Override
    public boolean isApplicable(IRecommendersCompletionContext context) {
        SessionProcessorDescriptor descriptor = preferences.getSessionProcessorDescriptor(SESSION_PROCESSOR_ID);
        if (descriptor == null) {
            return false;
        }

        if (preferences.isEnabled(descriptor)) {
            return false;
        }

        Set<ITypeName> expectedTypes = context.getExpectedTypeNames();
        if (expectedTypes.isEmpty()) {
            return false;
        }

        return true;
    }

    @Override
    public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
        enableTypesCompletion();
    }

    private void enableTypesCompletion() {
        SessionProcessorDescriptor descriptor = preferences.getSessionProcessorDescriptor(SESSION_PROCESSOR_ID);
        if (descriptor != null) {
            preferences.setSessionProcessorEnabled(ImmutableList.of(descriptor), NONE);
        }
    }

    @Override
    protected IInformationControl createInformationControl(Shell parent, String statusLineText) {
        return new ConfigureContentAssistInformationControl(parent, statusLineText);
    }

    private final class ConfigureContentAssistInformationControl extends AbstractInformationControl {

        private ConfigureContentAssistInformationControl(Shell parent, String statusLineText) {
            super(parent, statusLineText);
            create();
        }

        @Override
        public boolean hasContents() {
            return true;
        }

        @Override
        protected void createContent(Composite parent) {
            Link link = new Link(parent, SWT.NONE);
            Dialog.applyDialogFont(link);
            link.setForeground(parent.getForeground());
            link.setBackground(parent.getBackground());
            link.setText(INFO);
            link.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    dispose();
                    if (ABOUT_ENABLE.equals(e.text)) {
                        enableTypesCompletion();
                    } else if (StringUtils.startsWith(e.text, "http:") || StringUtils.startsWith(e.text, "https:")) {
                        BrowserUtils.openInExternalBrowser(e.text);
                    }
                }
            });
        }
    }
}
