/**
 * Copyright (c) 2010, 2012 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.completion.rcp.processable;

import static org.eclipse.recommenders.completion.rcp.CompletionContextKey.ACTIVE_PROCESSORS;
import static org.eclipse.recommenders.completion.rcp.processable.ProcessableProposalFactory.create;
import static org.eclipse.recommenders.completion.rcp.processable.ProposalTag.*;
import static org.eclipse.recommenders.internal.completion.rcp.Constants.*;
import static org.eclipse.recommenders.internal.completion.rcp.l10n.LogMessages.ERROR_SESSION_PROCESSOR_FAILED;
import static org.eclipse.recommenders.utils.Checks.cast;
import static org.eclipse.recommenders.utils.Logs.log;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.text.java.CompletionProposalCategory;
import org.eclipse.jdt.internal.ui.text.java.CompletionProposalComputerRegistry;
import org.eclipse.jdt.internal.ui.text.java.JavaAllCompletionProposalComputer;
import org.eclipse.jdt.internal.ui.text.java.JavaMethodCompletionProposal;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionListenerExtension2;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.ContentAssistantFacade;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.recommenders.completion.rcp.CompletionContextKey;
import org.eclipse.recommenders.completion.rcp.DisableContentAssistCategoryJob;
import org.eclipse.recommenders.completion.rcp.ICompletionContextFunction;
import org.eclipse.recommenders.completion.rcp.IRecommendersCompletionContext;
import org.eclipse.recommenders.completion.rcp.RecommendersCompletionContext;
import org.eclipse.recommenders.internal.completion.rcp.CompletionRcpPreferences;
import org.eclipse.recommenders.internal.completion.rcp.EmptyCompletionProposal;
import org.eclipse.recommenders.internal.completion.rcp.EnabledCompletionProposal;
import org.eclipse.recommenders.rcp.IAstProvider;
import org.eclipse.recommenders.rcp.SharedImages;
import org.eclipse.recommenders.utils.Logs;
import org.eclipse.ui.IEditorPart;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

@SuppressWarnings({ "restriction", "rawtypes" })
public class IntelligentCompletionProposalComputer extends JavaAllCompletionProposalComputer
        implements ICompletionListener, ICompletionListenerExtension2 {

    /**
     * A whitelist ensuring that the CompilationUnitEditor we are dealing with actually contains Java code. This is
     * necessary as certain plugins (Groovy Eclipse, Scala IDE) extend the JDT's CompilationUnitEditor.
     *
     * @see <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=470372">Bug 470372</a>
     * @see <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=470406">Bug 470406</a>
     * @see <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=474318">Bug 474318</a>
     */
    private static final List<String> JAVA_EDITOR_WHITELIST = ImmutableList.of(CompilationUnitEditor.class.getName(),
            "org.eclipse.wb.internal.core.editor.multi.DesignerEditor");

    private final CompletionRcpPreferences preferences;
    private final IAstProvider astProvider;
    private final SharedImages images;
    private final Map<CompletionContextKey, ICompletionContextFunction> functions;
    private final Provider<IEditorPart> editorProvider;
    private final IProcessableProposalFactory proposalFactory = new ProcessableProposalFactory();

    private final Set<SessionProcessor> processors = new LinkedHashSet<>();
    private final Set<SessionProcessor> activeProcessors = new LinkedHashSet<>();

    // Set in storeContext
    public JavaContentAssistInvocationContext jdtContext;
    public IRecommendersCompletionContext crContext;

    public ContentAssistantFacade contentAssist;

    @Inject
    public IntelligentCompletionProposalComputer(CompletionRcpPreferences preferences, IAstProvider astProvider,
            SharedImages images, Map<CompletionContextKey, ICompletionContextFunction> functions,
            Provider<IEditorPart> editorRetriever) {
        this.preferences = preferences;
        this.astProvider = astProvider;
        this.images = images;
        this.functions = functions;
        this.editorProvider = editorRetriever;
    }

    @Override
    public void sessionStarted() {
        processors.clear();
        for (SessionProcessorDescriptor d : preferences.getEnabledSessionProcessors()) {
            try {
                processors.add(d.getProcessor());
            } catch (Throwable e) {
                log(ERROR_SESSION_PROCESSOR_FAILED, e, d.getId());
            }
        }
        activeProcessors.clear();
        activeProcessors.addAll(processors);
        // code looks odd? This method unregisters this instance from the last(!) source viewer see
        // unregisterCompletionListener for details
        unregisterCompletionListener();
    }

    @Override
    public List<ICompletionProposal> computeCompletionProposals(ContentAssistInvocationContext context,
            IProgressMonitor monitor) {

        if (!(context instanceof JavaContentAssistInvocationContext)) {
            return Collections.emptyList();
        }

        storeContext(context);

        if (!isTriggeredInJavaProject()) {
            // We can't make recommendations. Fall back to JDT.
            if (!isContentAssistConfigurationOkay()) {
                // JDT is still active, so don't add any proposals.
                return Collections.emptyList();
            } else {
                // JDT is inactive. Return all proposals JDT would have made.
                List<ICompletionProposal> res = new LinkedList<>();
                for (Entry<IJavaCompletionProposal, CompletionProposal> pair : crContext.getProposals().entrySet()) {
                    IJavaCompletionProposal jdtProposal = create(pair.getValue(), pair.getKey(), jdtContext,
                            proposalFactory);
                    res.add(jdtProposal);
                }
                return res;
            }
        }

        if (!isContentAssistConfigurationOkay()) {
            enableRecommenders();
            int offset = context.getInvocationOffset();
            EnabledCompletionProposal info = new EnabledCompletionProposal(images, offset);
            boolean hasOtherProposals = !crContext.getProposals().isEmpty();
            if (hasOtherProposals) {
                // Return the configure proposal
                return ImmutableList.<ICompletionProposal>of(info);
            } else {
                return ImmutableList.<ICompletionProposal>of(info, new EmptyCompletionProposal(offset));
            }
        } else {
            List<ICompletionProposal> res = new LinkedList<>();
            registerCompletionListener();
            crContext.set(ACTIVE_PROCESSORS, ImmutableSet.copyOf(activeProcessors));
            fireInitializeContext(crContext);
            fireStartSession(crContext);
            for (Entry<IJavaCompletionProposal, CompletionProposal> pair : crContext.getProposals().entrySet()) {
                IJavaCompletionProposal jdtProposal = create(pair.getValue(), pair.getKey(), jdtContext,
                        proposalFactory);
                res.add(jdtProposal);
                if (jdtProposal instanceof JavaMethodCompletionProposal) {
                    int position = guessContextInformationPosition(jdtContext);
                    JavaMethodCompletionProposal jmcp = (JavaMethodCompletionProposal) jdtProposal;
                    jmcp.setContextInformationPosition(position);
                }
                if (jdtProposal instanceof IProcessableProposal) {
                    IProcessableProposal crProposal = (IProcessableProposal) jdtProposal;
                    crProposal.setTag(CONTEXT, crContext);
                    crProposal.setTag(IS_VISIBLE, true);
                    crProposal.setTag(JDT_UI_PROPOSAL, pair.getKey());
                    crProposal.setTag(JDT_CORE_PROPOSAL, pair.getValue());
                    crProposal.setTag(JDT_SCORE, jdtProposal.getRelevance());
                    fireProcessProposal(crProposal);
                }
            }
            fireEndComputation(res);
            fireAboutToShow(res);

            return res;
        }
    }

    private boolean isTriggeredInJavaProject() {
        if (jdtContext == null) {
            return false;
        }

        IEditorPart editor = editorProvider.get();
        if (editor == null) {
            return false;
        }

        if (!JAVA_EDITOR_WHITELIST.contains(editor.getClass().getName())) {
            return false;
        }

        IJavaProject project = jdtContext.getProject();
        if (project == null) {
            return false;
        }
        return project.exists();
    }

    private void enableRecommenders() {
        new DisableContentAssistCategoryJob(MYLYN_ALL_CATEGORY).schedule();
        new DisableContentAssistCategoryJob(JDT_ALL_CATEGORY).schedule();
        new DisableContentAssistCategoryJob(JDT_TYPE_CATEGORY).schedule();
        new DisableContentAssistCategoryJob(JDT_NON_TYPE_CATEGORY).schedule();
    }

    @Override
    public void sessionEnded() {
        fireAboutToClose();
    }

    private void storeContext(ContentAssistInvocationContext context) {
        jdtContext = cast(context);
        crContext = new RecommendersCompletionContext(jdtContext, astProvider, functions);
    }

    protected boolean isContentAssistConfigurationOkay() {
        Set<String> excludedCategories = Sets.newHashSet(PreferenceConstants.getExcludedCompletionProposalCategories());
        if (excludedCategories.contains(RECOMMENDERS_ALL_CATEGORY_ID)) {
            // If we are excluded on the default tab, then we cannot be on the default tab now, as we are executing.
            // Hence, we must be on a subsequent tab.
            return true;
        }
        if (isJdtJavaProposalsEnabled(excludedCategories) || isMylynJavaProposalsEnabled(excludedCategories)) {
            return false;
        }
        return true;
    }

    private boolean isMylynJavaProposalsEnabled(Set<String> excludedCategories) {
        return isMylynInstalled() && !excludedCategories.contains(MYLYN_ALL_CATEGORY);
    }

    private boolean isJdtJavaProposalsEnabled(Set<String> excludedCategories) {
        return !excludedCategories.contains(JDT_ALL_CATEGORY) || !excludedCategories.contains(JDT_TYPE_CATEGORY)
                || !excludedCategories.contains(JDT_NON_TYPE_CATEGORY);
    }

    private boolean isMylynInstalled() {
        CompletionProposalComputerRegistry reg = CompletionProposalComputerRegistry.getDefault();
        for (CompletionProposalCategory cat : reg.getProposalCategories()) {
            if (cat.getId().equals(MYLYN_ALL_CATEGORY)) {
                return true;
            }
        }
        return false;
    }

    private void registerCompletionListener() {
        ITextViewer v = jdtContext.getViewer();
        if (!(v instanceof SourceViewer)) {
            return;
        }
        SourceViewer sv = (SourceViewer) v;
        contentAssist = sv.getContentAssistantFacade();
        contentAssist.addCompletionListener(this);
    }

    /*
     * Unregisters this computer from the last known content assist facade. This method is called in some unexpected
     * places (i.e., not in sessionEnded and similar methods) because unregistering in these methods would be too early
     * to get notified about apply events.
     */
    private void unregisterCompletionListener() {
        if (contentAssist != null) {
            contentAssist.removeCompletionListener(this);
        }
    }

    protected void fireInitializeContext(IRecommendersCompletionContext crContext) {
        for (Iterator<SessionProcessor> it = activeProcessors.iterator(); it.hasNext();) {
            SessionProcessor p = it.next();
            try {
                p.initializeContext(crContext);
            } catch (Throwable e) {
                it.remove();
                Logs.log(ERROR_SESSION_PROCESSOR_FAILED, e, p.getClass());
            }
        }
    }

    protected void fireStartSession(IRecommendersCompletionContext crContext) {
        for (Iterator<SessionProcessor> it = activeProcessors.iterator(); it.hasNext();) {
            SessionProcessor p = it.next();
            try {
                boolean interested = p.startSession(crContext);
                if (!interested) {
                    it.remove();
                }
            } catch (Throwable e) {
                it.remove();
                Logs.log(ERROR_SESSION_PROCESSOR_FAILED, e, p.getClass());
            }
        }
    }

    protected void fireProcessProposal(IProcessableProposal proposal) {
        for (SessionProcessor p : activeProcessors) {
            try {
                proposal.getRelevance();
                p.process(proposal);
            } catch (Throwable e) {
                log(ERROR_SESSION_PROCESSOR_FAILED, e, p.getClass());
            }
        }
        proposal.getProposalProcessorManager().prefixChanged(crContext.getPrefix());
    }

    protected void fireEndComputation(List<ICompletionProposal> proposals) {
        for (SessionProcessor p : activeProcessors) {
            try {
                p.endSession(proposals);
            } catch (Throwable e) {
                log(ERROR_SESSION_PROCESSOR_FAILED, e, p.getClass());
            }
        }
    }

    protected void fireAboutToShow(List<ICompletionProposal> proposals) {
        for (SessionProcessor p : activeProcessors) {
            try {
                p.aboutToShow(proposals);
            } catch (Throwable e) {
                log(ERROR_SESSION_PROCESSOR_FAILED, e, p.getClass());
            }
        }
    }

    protected void fireAboutToClose() {
        for (SessionProcessor p : activeProcessors) {
            try {
                p.aboutToClose();
            } catch (Throwable e) {
                log(ERROR_SESSION_PROCESSOR_FAILED, e, p.getClass());
            }
        }
    }

    @Override
    public void assistSessionStarted(ContentAssistEvent event) {
        // ignore
    }

    @Override
    public void assistSessionEnded(ContentAssistEvent event) {
        // ignore

        // Calling unregister here seems like a good choice here but unfortunately isn't. "proposal applied" events are
        // fired after the sessionEnded event, and thus, we cannot use this method to unsubscribe from the current
        // editor. See unregisterCompletionListern for details.
    }

    @Override
    public void selectionChanged(ICompletionProposal proposal, boolean smartToggle) {
        for (SessionProcessor p : activeProcessors) {
            try {
                p.selected(proposal);
            } catch (Throwable e) {
                log(ERROR_SESSION_PROCESSOR_FAILED, e, p.getClass());
            }
        }
    }

    @Override
    public void applied(ICompletionProposal proposal) {
        for (SessionProcessor p : activeProcessors) {
            try {
                p.applied(proposal);
            } catch (Throwable e) {
                log(ERROR_SESSION_PROCESSOR_FAILED, e, p.getClass());
            }
        }
        unregisterCompletionListener();
    }
}
