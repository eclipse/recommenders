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

import static org.eclipse.recommenders.internal.completion.rcp.chain.TypeBindingAnalyzer.findAllPublicStaticFieldsAndNonVoidNonPrimitiveStaticMethods;
import static org.eclipse.recommenders.internal.completion.rcp.chain.TypeBindingAnalyzer.findVisibleInstanceFieldsAndRelevantInstanceMethods;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
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
import org.eclipse.recommenders.internal.completion.rcp.chain.ChainCompletionModule.ChainCompletion;
import org.eclipse.recommenders.internal.completion.rcp.chain.ui.ChainPreferencePage;
import org.eclipse.recommenders.utils.rcp.internal.RecommendersUtilsPlugin;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.inject.Inject;

@SuppressWarnings("restriction")
public class ChainCompletionProposalComputer implements IJavaCompletionProposalComputer {

    static final String CATEGORY_ID = "org.eclipse.recommenders.completion.rcp.chain.category";

    private IRecommendersCompletionContext ctx;
    private List<ChainElement> entrypoints;
    private String error;
    private final IRecommendersCompletionContextFactory ctxFactory;
    private final IPreferenceStore prefStore;
    private IType enclosingType;

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
        if (!initializeRequiredContext(context)) {
            return Collections.emptyList();
        }
        if (!shouldPerformCompletionOnExpectedType()) {
            return Collections.emptyList();
        }
        if (!findEntrypoints()) {
            return Collections.emptyList();
        }
        return executeCallChainSearch();
    }

    private boolean shouldMakeProposals() {
        // TODO: Why do we check this at all? Doesn't JDT take care of this?
        for (final String excluded : PreferenceConstants.getExcludedCompletionProposalCategories()) {
            if (excluded.equals(CATEGORY_ID)) {
                return false;
            }
        }
        return true;
    }

    private boolean initializeRequiredContext(final ContentAssistInvocationContext context) {
        ctx = ctxFactory.create((JavaContentAssistInvocationContext) context);
        return ctx != null;
    }

    private boolean shouldPerformCompletionOnExpectedType() {
        final IType expected = ctx.getExpectedType().orNull();
        if (expected == null) {
            return false;
        }
        return !expectedTypeIsIgnoredByUser(expected);
    }

    private boolean expectedTypeIsIgnoredByUser(final IType expected) {
        final String[] excludedTypes = prefStore.getString(ChainPreferencePage.ID_IGNORE_TYPES).split("\\|");
        final String fullyQualified = expected.getFullyQualifiedName();
        for (final String excludedType : excludedTypes) {
            if (excludedType.equals(fullyQualified)) {
                return true;
            }
        }
        return false;
    }

    private boolean findEntrypoints() {
        entrypoints = new LinkedList<ChainElement>();
        final ASTNode node = ctx.getCompletionNode();
        enclosingType = (IType) ((JavaElement) ctx.getEnclosingElement().get()).getAncestor(IJavaElement.TYPE);

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
        default:
            throw new IllegalStateException();
        }
    }

    private void addPublicStaticMembersToEntrypoints(final TypeBinding type) {
        for (final Binding m : findAllPublicStaticFieldsAndNonVoidNonPrimitiveStaticMethods(type, enclosingType)) {
            if (matchesExpectedPrefix(m)) {
                final ChainElement edge = new ChainElement(m);
                entrypoints.add(edge);
            }
        }
    }

    private void addPublicInstanceMembersToEntrypoints(final VariableBinding var) {
        addPublicInstanceMembersToEntrypoints(var.type);
    }

    private void addPublicInstanceMembersToEntrypoints(final TypeBinding type) {
        for (final Binding m : findVisibleInstanceFieldsAndRelevantInstanceMethods(type, enclosingType)) {
            if (matchesExpectedPrefix(m)) {
                entrypoints.add(new ChainElement(m));
            }
        }
    }

    private boolean matchesExpectedPrefix(final Binding binding) {
        return String.valueOf(binding.readableName()).startsWith(ctx.getPrefix());
    }

    private void findEntrypointsForCompletionOnMemberAccess(final CompletionOnMemberAccess node) {
        final Binding b = node.actualReceiverType;
        if (b == null) {
            return;
        }
        switch (b.kind()) {
        case Binding.TYPE:
            addPublicInstanceMembersToEntrypoints((TypeBinding) b);
            break;
        case Binding.LOCAL:
            // TODO Review: could this be a field?
            addPublicInstanceMembersToEntrypoints((VariableBinding) b);
            break;
        default:
            throw new IllegalStateException();
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
            final String key = String.valueOf(decl.computeUniqueKey());
            if (key.startsWith("Ljava/lang/Object;")) {
                continue;
            }
            final ChainElement e = new ChainElement(decl);
            if (e.getReturnType() != null) {
                entrypoints.add(e);
            }
        }
    }

    private boolean matchesPrefixToken(final Binding decl) {
        return String.valueOf(decl.readableName()).startsWith(ctx.getPrefix());
    }

    private List<ICompletionProposal> executeCallChainSearch() {
        final TypeBinding expectedType = TypeBindingAnalyzer.resolveBindingForExpectedType(ctx);
        final ChainFinder finder = new ChainFinder(expectedType);
        try {
            new SimpleTimeLimiter().callWithTimeout(new Callable<Void>() {
                @Override
                public Void call() throws Exception {
                    final int maxChains = prefStore.getInt(ChainPreferencePage.ID_MAX_CHAINS);
                    final int maxDepth = prefStore.getInt(ChainPreferencePage.ID_MAX_DEPTH);
                    finder.startChainSearch(enclosingType, entrypoints, maxChains, maxDepth);
                    return null;
                }
            }, prefStore.getInt(ChainPreferencePage.ID_TIMEOUT), TimeUnit.SECONDS, true);
        } catch (final Exception e) {
            setError("Timeout limit hit during call chain computation.");
        }
        final int dimensions = expectedType instanceof ArrayBinding ? ((ArrayBinding) expectedType).dimensions() : 0;
        return buildCompletionProposals(finder.getChains(), dimensions);
    }

    private List<ICompletionProposal> buildCompletionProposals(final List<List<ChainElement>> chains,
            final int expectedDimension) {
        final List<ICompletionProposal> proposals = Lists.newLinkedList();
        for (final List<ChainElement> chain : chains) {
            final TemplateProposal proposal = new CompletionTemplateBuilder().create(chain, expectedDimension,
                    ctx.getJavaContext());
            final ChainCompletionProposal completionProposal = new ChainCompletionProposal(proposal, chain);
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
