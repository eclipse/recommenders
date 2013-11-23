/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.completion.rcp;

import static org.eclipse.jdt.internal.ui.JavaPlugin.getActiveWorkbenchShell;
import static org.eclipse.jface.viewers.StyledString.DECORATIONS_STYLER;
import static org.eclipse.recommenders.internal.completion.rcp.Constants.*;
import static org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn;

import java.util.Dictionary;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.equinox.internal.p2.discovery.Catalog;
import org.eclipse.equinox.internal.p2.discovery.DiscoveryCore;
import org.eclipse.equinox.internal.p2.discovery.compatibility.RemoteBundleDiscoveryStrategy;
import org.eclipse.equinox.internal.p2.ui.discovery.util.WorkbenchUtil;
import org.eclipse.equinox.internal.p2.ui.discovery.wizards.CatalogConfiguration;
import org.eclipse.equinox.internal.p2.ui.discovery.wizards.DiscoveryWizard;
import org.eclipse.jdt.internal.ui.text.java.AbstractJavaCompletionProposal;
import org.eclipse.jface.text.AbstractInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.recommenders.completion.rcp.DisableContentAssistCategoryJob;
import org.eclipse.recommenders.completion.rcp.RecommendersCompletionContext;
import org.eclipse.recommenders.rcp.SharedImages;
import org.eclipse.recommenders.rcp.SharedImages.Images;
import org.eclipse.recommenders.rcp.utils.PreferencesHelper;
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
public class DiscoverCompletionProposal extends AbstractJavaCompletionProposal {

    private static final Object DUMMY_INFO = new Object();

    // leave a bit space for other, maybe more important proposals
    private static final int RELEVANCE = Integer.MAX_VALUE - 10001;

    public DiscoverCompletionProposal(SharedImages images, int invocationOffset) {
        Image image = images.getImage(Images.OBJ_LIGHTBULB);
        StyledString text = new StyledString("Nothing found? Discover how crowdsourcing can help!", DECORATIONS_STYLER);
        setStyledDisplayString(text);
        setImage(image);
        setRelevance(RELEVANCE);
        setSortString(text.getString());
        setCursorPosition(invocationOffset);
    }

    @Override
    public Object getAdditionalProposalInfo(IProgressMonitor monitor) {
        return DUMMY_INFO;
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
    
    @Override
    protected boolean isValidPrefix(String prefix) {
        return true;
    }
    
    @Override
    public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {

        Catalog catalog = new Catalog();
        Dictionary<Object, Object> env = DiscoveryCore.createEnvironment();
        catalog.setEnvironment(env);
        catalog.setVerifyUpdateSiteAvailability(false);

        // add strategy for retrieving remote catalog

        // look for remote descriptor
        RemoteBundleDiscoveryStrategy remoteDiscoveryStrategy = new RemoteBundleDiscoveryStrategy();
        remoteDiscoveryStrategy
                .setDirectoryUrl("file:///Users/Marcel/Repositories/org.eclipse.recommenders/repositories/discovery/directory.xml");
        catalog.getDiscoveryStrategies().add(remoteDiscoveryStrategy);

        CatalogConfiguration configuration = new CatalogConfiguration();
        configuration.setShowTagFilter(false);

        DiscoveryWizard wizard = new DiscoveryWizard(catalog, configuration);
        WizardDialog dialog = new WizardDialog(WorkbenchUtil.getShell(), wizard);
        dialog.open();
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
            final String prefPageLabel = PreferencesHelper
                    .createLinkLabelToPreferencePage(COMPLETION_PREFERENCE_PAGE_ID);

            Link link = new Link(parent, SWT.NONE);
            link.setBackground(bg);
            link.setText("What is crowdsourcing in your IDE?\n\n"
                    + "In a nutshell, crowdsourcing helps you to find the proposal you need much faster by "
                    + "highlighting and sorting proposals to the top that have been used frequently by other developers before. "
                    + "It does much more than that. Click <a>here</a> to learn more about how crowdsourcing in your IDE gives "
                    + "you a extra boost in productivity.\n\n"
                    + "But be aware: Crowdsourcing means that you share data too. In the simplest case you just share code completion "
                    + "events how you use classes of the Java standard library. You may also share ratings for code snippets, "
                    + "provide your own snippets, or even share stacktraces with your community - it's all up to you!\n\n"
                    + "May the crowd be with you, my young padawan!");
            link.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    dispose();
                    String text = e.text;
                    if ("here".equalsIgnoreCase(text)) {
                        new DisableContentAssistCategoryJob(RECOMMENDERS_ALL_CATEGORY_ID).schedule();
                    } else if (prefPageLabel.equalsIgnoreCase(text)) {
                        createPreferenceDialogOn(getActiveWorkbenchShell(), COMPLETION_PREFERENCE_PAGE_ID, null, null)
                                .open();
                    }
                }
            });
        }
    }
}
