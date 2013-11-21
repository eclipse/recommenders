package org.eclipse.recommenders.internal.completion.rcp;

import static org.eclipse.jdt.internal.ui.JavaPlugin.getActiveWorkbenchShell;
import static org.eclipse.jface.viewers.StyledString.DECORATIONS_STYLER;
import static org.eclipse.recommenders.internal.completion.rcp.Constants.*;
import static org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.ui.text.java.AbstractJavaCompletionProposal;
import org.eclipse.jface.text.AbstractInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.recommenders.completion.rcp.DisableContentAssistCategoryJob;
import org.eclipse.recommenders.rcp.SharedImages;
import org.eclipse.recommenders.rcp.SharedImages.Images;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

@SuppressWarnings("restriction")
public class ConfigureCompletionProposal extends AbstractJavaCompletionProposal {

    private static final int RELEVANCE = 5000;

    public ConfigureCompletionProposal(SharedImages images) {
        Image image = images.getImage(Images.OBJ_LIGHTBULB);
        StyledString text = new StyledString("Enable intelligent code completion?", DECORATIONS_STYLER);
        setStyledDisplayString(text);
        setImage(image);
        setRelevance(RELEVANCE);
    }

    @Override
    public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
        // cannot be null or an empty string. Otherwise the information control is not shown.
        return new Object();
    }

    @Override
    public IInformationControlCreator getInformationControlCreator() {
        return new IInformationControlCreator() {

            @Override
            public IInformationControl createInformationControl(Shell parent) {
                return new ConfigureContentAssistInformationControl(parent, "Status");
            }
        };
    }

    /**
     * Disables Mylyn and JDT content assists.
     */
    @Override
    public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
        new DisableContentAssistCategoryJob(MYLYN_ALL_CATEGORY).schedule();
        new DisableContentAssistCategoryJob(JDT_ALL_CATEGORY).schedule();
    }

    private final class ConfigureContentAssistInformationControl extends AbstractInformationControl {

        private ConfigureContentAssistInformationControl(Shell parentShell, String statusFieldText) {
            super(parentShell, statusFieldText);
            create();
        }

        @Override
        public boolean hasContents() {
            return true;
        }

        @Override
        protected void createContent(Composite parent) {

            Display display = parent.getDisplay();
            Color bg = display.getSystemColor(SWT.COLOR_INFO_BACKGROUND);
            Link link = new Link(parent, SWT.NONE);
            link.setBackground(bg);
            link.setText("I've recognized you have enabled Code Recommenders but didn't configure it properly yet.\n\n"
                    + "To enable Code Recommenders with its defaults, simply press return. "
                    + "If you want to configure Recommenders now, go to <a>Preferences</a>.\n\n"
                    + "Click <a>here</a> to disable Code Recommenders in your content assist.");
            link.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    dispose();
                    String text = e.text;
                    if ("here".equalsIgnoreCase(text)) {
                        new DisableContentAssistCategoryJob(RECOMMENDERS_ALL_CATEGORY_ID).schedule();
                    } else if ("preferences".equalsIgnoreCase(text)) {
                        createPreferenceDialogOn(getActiveWorkbenchShell(), COMPLETION_PREFERENCE_PAGE_ID, null, null)
                                .open();
                    }
                }
            });
        }
    }
}
