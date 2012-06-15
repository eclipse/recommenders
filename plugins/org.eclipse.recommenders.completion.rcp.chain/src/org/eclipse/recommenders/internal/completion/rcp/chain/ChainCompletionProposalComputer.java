/**
 * Copyright (c) 2010, 2011 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.completion.rcp.chain;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.codeassist.InternalCompletionContext;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnMemberAccess;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnQualifiedNameReference;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnSingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.lookup.ArrayBinding;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.util.ObjectVector;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jdt.internal.ui.text.template.contentassist.TemplateProposal;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.jdt.ui.text.java.ContentAssistInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposalComputer;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.recommenders.completion.rcp.IRecommendersCompletionContext;
import org.eclipse.recommenders.completion.rcp.IRecommendersCompletionContextFactory;
import org.eclipse.recommenders.internal.completion.rcp.DisableContentAssistCategoryJob;
import org.eclipse.recommenders.internal.completion.rcp.chain.ChainCompletionModule.ChainCompletion;
import org.eclipse.recommenders.internal.completion.rcp.chain.ui.ChainPreferencePage;
import org.eclipse.recommenders.utils.rcp.internal.RecommendersUtilsPlugin;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.inject.Inject;

@SuppressWarnings("restriction")
public class ChainCompletionProposalComputer implements IJavaCompletionProposalComputer {

    static final String CATEGORY_ID = "org.eclipse.recommenders.completion.rcp.chain.category";

    private IRecommendersCompletionContext ctx;
    private TypeBinding expectedType;
    private List<MemberEdge> entrypoints;
    private String error;
    private final IRecommendersCompletionContextFactory ctxFactory;
    private final IPreferenceStore prefStore;

    @Inject
    public ChainCompletionProposalComputer(final IRecommendersCompletionContextFactory ctxFactory,
            @ChainCompletion final IPreferenceStore preferenceStore) {
        this.ctxFactory = ctxFactory;
        prefStore = preferenceStore;
    }

    @Override
    public List<ICompletionProposal> computeCompletionProposals(final ContentAssistInvocationContext context,
            final IProgressMonitor monitor) {
        if (!shouldMakeProposals()) {
            return Collections.emptyList();
        }
        initalizeContexts(context);
        if (!allRequiredContextsAvailable()) {
            return Collections.emptyList();
        }
        if (!findExpectedType()) {
            return Collections.emptyList();
        }
        if (!findEntrypoints()) {
            return Collections.emptyList();
        }
        try {
            return executeCallChainSearch();
        } catch (final Exception e) {
            logError(e);
            return Collections.emptyList();
        }
    }

    @VisibleForTesting
    protected boolean shouldMakeProposals() {
        // TODO: Why do we disable it all the time? :)
        if (true) {
            return true;
        }
        final String[] excluded = PreferenceConstants.getExcludedCompletionProposalCategories();
        final Set<String> ex = Sets.newHashSet(excluded);
        if (!ex.contains(CATEGORY_ID)) {
            new DisableContentAssistCategoryJob(CATEGORY_ID).schedule();
            return false;
        }
        // we are not on the default tab
        return true;
    }

    /**
     * @return true if the context could be initialized successfully, i.e., completion context is a java context, and
     *         the core context is an extended context
     */
    private void initalizeContexts(final ContentAssistInvocationContext context) {
        ctx = ctxFactory.create((JavaContentAssistInvocationContext) context);
    }

    private boolean allRequiredContextsAvailable() {
        return ctx != null;
    }

    private boolean findExpectedType() {
        final IType expected = ctx.getExpectedType().orNull();
        if (expected == null) {
            return false;
        }
        final String[] excludedTypes = prefStore.getString(ChainPreferencePage.ID_IGNORE_TYPES).split("\\|");
        final String fullyQualified = expected.getFullyQualifiedName();
        for (final String excludedType : excludedTypes) {
            if (excludedType.equals(fullyQualified)) {
                return false;
            }
        }
        expectedType = TypeBindingHack.resolveBindingForExpectedType(ctx);
        return true;
    }

    private boolean findEntrypoints() {
        entrypoints = new LinkedList<MemberEdge>();
        final ASTNode node = ctx.getCompletionNode();

        if (node instanceof CompletionOnQualifiedNameReference) {
            findEntrypointsForCompletionOnQualifiedName((CompletionOnQualifiedNameReference) node);
        } else if (node instanceof CompletionOnMemberAccess) {
            findEntrypointsForCompletionOnMemberAccess((CompletionOnMemberAccess) node);
        } else if (node instanceof CompletionOnSingleNameReference) {
            findEntrypointsForCompletionOnSingleName();
        }
        return !entrypoints.isEmpty();
    }

    private void findEntrypointsForCompletionOnQualifiedName(final CompletionOnQualifiedNameReference node) {
        final Binding b = node.binding;
        if (b == null) {
            return;
        }
        switch (b.kind()) {
        case Binding.TYPE:
            addPublicStaticMembersToEntrypoints((TypeBinding) b);
            break;
        case Binding.FIELD:
            addPublicInstanceMembersToEntrypoints(((FieldBinding) b).type);
            break;
        case Binding.LOCAL:
            addPublicInstanceMembersToEntrypoints((VariableBinding) b);
            break;
        }
    }

    private void addPublicStaticMembersToEntrypoints(final TypeBinding type) {
        for (final Binding m : ASTUtils.findAllPublicStaticFieldsAndNonVoidNonPrimitiveStaticMethods(type)) {
            if (matchesExpectedPrefix(m)) {
                final MemberEdge edge = new MemberEdge(m);
                entrypoints.add(edge);
            }
        }
    }

    private boolean matchesExpectedPrefix(final Binding m) {
        return String.valueOf(m.readableName()).startsWith(ctx.getPrefix());
    }

    private JavaElement findEnclosingElement() {
        return (JavaElement) ctx.getEnclosingElement().get();
    }

    private void addPublicInstanceMembersToEntrypoints(final VariableBinding var) {
        addPublicInstanceMembersToEntrypoints(var.type);
    }

    private void addPublicInstanceMembersToEntrypoints(final TypeBinding type) {
        for (final Binding m : ASTUtils.findAllPublicInstanceFieldsAndNonVoidNonPrimitiveInstanceMethods(type)) {
            if (matchesExpectedPrefix(m)) {
                entrypoints.add(new MemberEdge(m));
            }
        }
    }

    private void findEntrypointsForCompletionOnMemberAccess(final CompletionOnMemberAccess node) {
        final Binding b = node.actualReceiverType;
        if (b == null) {
            return;
        }
        switch (b.kind()) {
        case Binding.TYPE:
            // note: not static!
            addPublicInstanceMembersToEntrypoints((TypeBinding) b);
            break;
        case Binding.LOCAL:
            // TODO Review: could this be a field?
            addPublicInstanceMembersToEntrypoints((VariableBinding) b);
            break;
        }
    }

    private void findEntrypointsForCompletionOnSingleName() {
        final InternalCompletionContext context = (InternalCompletionContext) ctx.getJavaContext().getCoreContext();
        resolveEntrypoints(context.getVisibleFields());
        resolveEntrypoints(context.getVisibleLocalVariables());
        resolveEntrypoints(context.getVisibleMethods());
    }

    private void resolveEntrypoints(final ObjectVector elements) {
        for (int i = elements.size(); i-- > 0;) {
            final Binding decl = (Binding) elements.elementAt(i);
            if (!matchesPrefixToken(decl)) {
                continue;
            }
            final MemberEdge e = new MemberEdge(decl);
            if (e.getReturnType() != null) {
                entrypoints.add(e);
            }
        }
    }

    private boolean matchesPrefixToken(final Binding decl) {
        return String.valueOf(decl.readableName()).startsWith(ctx.getPrefix());
    }

    private List<ICompletionProposal> executeCallChainSearch() throws JavaModelException {
        final GraphBuilder b = new GraphBuilder(expectedType);
        try {
            new SimpleTimeLimiter().callWithTimeout(new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    b.startChainSearch(findEnclosingElement(), entrypoints,
                            prefStore.getInt(ChainPreferencePage.ID_MAX_CHAINS),
                            prefStore.getInt(ChainPreferencePage.ID_MAX_DEPTH));
                    return null;
                }
            }, prefStore.getInt(ChainPreferencePage.ID_TIMEOUT), TimeUnit.SECONDS, true);
        } catch (final Exception e) {
            e.printStackTrace();
            setError("Timeout limit hit during call chain computation.");
        }
        return buildCompletionProposals(b.getChains(),
                expectedType instanceof ArrayBinding ? ((ArrayBinding) expectedType).dimensions() : 0);
    }

    private List<ICompletionProposal> buildCompletionProposals(final List<List<MemberEdge>> chains,
            final int expectedDimension) {
        final List<ICompletionProposal> proposals = Lists.newLinkedList();
        for (final List<MemberEdge> chain : chains) {
            final TemplateProposal completion = new CompletionTemplateBuilder().create(chain, expectedDimension,
                    ctx.getJavaContext());
            final ChainCompletionProposal completionProposal = new ChainCompletionProposal(completion, chain);
            proposals.add(completionProposal);
        }
        return proposals;
    }

    private void setError(final String errorMessage) {
        error = errorMessage;
    }

    private void logError(final Exception e) {
        RecommendersUtilsPlugin
                .logError(e, "Chain completion failed in %s.", ctx.getCompilationUnit().getElementName());
    }

    @Override
    public List<IContextInformation> computeContextInformation(final ContentAssistInvocationContext context,
            final IProgressMonitor monitor) {
        return Collections.emptyList();
    }

    @Override
    public void sessionStarted() {
        setError(null);
    }

    @Override
    public String getErrorMessage() {
        return error;
    }

    @Override
    public void sessionEnded() {

    }
}
