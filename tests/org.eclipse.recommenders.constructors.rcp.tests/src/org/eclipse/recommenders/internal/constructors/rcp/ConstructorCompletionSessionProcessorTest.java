/**
 * Copyright (c) 2015 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Sewe - initial API and implementation.
 */
package org.eclipse.recommenders.internal.constructors.rcp;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnSingleNameReference;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnSingleTypeReference;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.recommenders.completion.rcp.CompletionContextKey;
import org.eclipse.recommenders.completion.rcp.IRecommendersCompletionContext;
import org.eclipse.recommenders.completion.rcp.processable.IProcessableProposal;
import org.eclipse.recommenders.completion.rcp.processable.OverlayImageProposalProcessor;
import org.eclipse.recommenders.completion.rcp.processable.ProcessableJavaCompletionProposal;
import org.eclipse.recommenders.completion.rcp.processable.ProposalProcessorManager;
import org.eclipse.recommenders.completion.rcp.processable.ProposalTag;
import org.eclipse.recommenders.completion.rcp.processable.SimpleProposalProcessor;
import org.eclipse.recommenders.coordinates.ProjectCoordinate;
import org.eclipse.recommenders.models.UniqueTypeName;
import org.eclipse.recommenders.models.rcp.IProjectCoordinateProvider;
import org.eclipse.recommenders.rcp.SharedImages;
import org.eclipse.recommenders.utils.Nullable;
import org.eclipse.recommenders.utils.Result;
import org.eclipse.recommenders.utils.names.IMethodName;
import org.eclipse.recommenders.utils.names.ITypeName;
import org.eclipse.recommenders.utils.names.VmMethodName;
import org.eclipse.recommenders.utils.names.VmTypeName;
import org.hamcrest.Description;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

@SuppressWarnings("restriction")
public class ConstructorCompletionSessionProcessorTest {

    private static final ProjectCoordinate JRE_1_6_0 = new ProjectCoordinate("jre", "jre", "1.6.0");

    private static final ITypeName OBJECT = VmTypeName.get("Ljava/lang/Object");
    private static final IType OBJECT_TYPE = mock(IType.class);

    private static final IMethodName OBJECT_INIT = VmMethodName.get("Ljava/lang/Object.<init>()V");
    private static final IJavaCompletionProposal OBJECT_INIT_PROPOSAL = mock(IJavaCompletionProposal.class);
    private static final CompletionProposal OBJECT_INIT_CORE_PROPOSAL = mock(CompletionProposal.class);
    private static final IProcessableProposal OBJECT_INIT_PROCESSABLE_PROPOSAL = mock(ProcessableJavaCompletionProposal.class);

    private static final ConstructorModel NULL_MODEL = null;

    private IProjectCoordinateProvider pcProvider;
    private IConstructorModelProvider modelProvider;
    private IMethodNameProvider methodNameProvider;
    private IRecommendersCompletionContext context;

    @Test
    public void testCompletionWithoutModel() {
        setUp(CompletionOnSingleTypeReference.class, new UniqueTypeName(JRE_1_6_0, OBJECT), OBJECT_TYPE, NULL_MODEL,
                ImmutableMap.of(OBJECT_INIT_PROPOSAL, OBJECT_INIT_CORE_PROPOSAL));

        ConstructorCompletionSessionProcessor sut = new ConstructorCompletionSessionProcessor(pcProvider,
                modelProvider, methodNameProvider, createDefaultPreferences(), new SharedImages());

        boolean shouldProcess = sut.startSession(context);

        assertThat(shouldProcess, is(equalTo(false)));
        verify(modelProvider).releaseModel(NULL_MODEL);
    }

    @Test
    public void testUnsupportedCompletion() {
        ConstructorModel model = new ConstructorModel(OBJECT, Collections.<IMethodName, Integer>emptyMap());
        setUp(CompletionOnSingleNameReference.class, new UniqueTypeName(JRE_1_6_0, OBJECT), OBJECT_TYPE, model,
                ImmutableMap.of(OBJECT_INIT_PROPOSAL, OBJECT_INIT_CORE_PROPOSAL));

        ConstructorCompletionSessionProcessor sut = new ConstructorCompletionSessionProcessor(pcProvider,
                modelProvider, methodNameProvider, createDefaultPreferences(), new SharedImages());

        boolean shouldProcess = sut.startSession(context);

        assertThat(shouldProcess, is(equalTo(false)));
    }

    @Test
    public void testNoExpectedType() {
        ConstructorModel model = new ConstructorModel(OBJECT, Collections.<IMethodName, Integer>emptyMap());
        setUp(CompletionOnSingleTypeReference.class, new UniqueTypeName(JRE_1_6_0, OBJECT), null, model,
                ImmutableMap.of(OBJECT_INIT_PROPOSAL, OBJECT_INIT_CORE_PROPOSAL));

        ConstructorCompletionSessionProcessor sut = new ConstructorCompletionSessionProcessor(pcProvider,
                modelProvider, methodNameProvider, createDefaultPreferences(), new SharedImages());

        boolean shouldProcess = sut.startSession(context);

        assertThat(shouldProcess, is(equalTo(false)));
    }

    @Test
    public void testCompletionWithoutRecommendations() {
        ConstructorModel model = new ConstructorModel(OBJECT, Collections.<IMethodName, Integer>emptyMap());
        setUp(CompletionOnSingleTypeReference.class, new UniqueTypeName(JRE_1_6_0, OBJECT), OBJECT_TYPE, model,
                ImmutableMap.of(OBJECT_INIT_PROPOSAL, OBJECT_INIT_CORE_PROPOSAL));

        ConstructorCompletionSessionProcessor sut = new ConstructorCompletionSessionProcessor(pcProvider,
                modelProvider, methodNameProvider, createDefaultPreferences(), new SharedImages());

        boolean shouldProcess = sut.startSession(context);

        assertThat(shouldProcess, is(equalTo(false)));
        verify(modelProvider).releaseModel(model);
    }

    @Test
    public void testCompletionWithRelevanceBoostAndLabelAndImageDecoration() throws Exception {
        ConstructorModel model = new ConstructorModel(OBJECT, ImmutableMap.of(OBJECT_INIT, 1));
        setUp(CompletionOnSingleTypeReference.class, new UniqueTypeName(JRE_1_6_0, OBJECT), OBJECT_TYPE, model,
                ImmutableMap.of(OBJECT_INIT_PROPOSAL, OBJECT_INIT_CORE_PROPOSAL));

        ProposalProcessorManager mgr = mock(ProposalProcessorManager.class);
        when(OBJECT_INIT_PROCESSABLE_PROPOSAL.getProposalProcessorManager()).thenReturn(mgr);

        ConstructorCompletionSessionProcessor sut = new ConstructorCompletionSessionProcessor(pcProvider,
                modelProvider, methodNameProvider, createDefaultPreferences(), new SharedImages());

        boolean shouldProcess = sut.startSession(context);

        assertThat(shouldProcess, is(equalTo(true)));
        verify(modelProvider).releaseModel(model);

        sut.process(OBJECT_INIT_PROCESSABLE_PROPOSAL);

        verify(OBJECT_INIT_PROCESSABLE_PROPOSAL).setTag(ProposalTag.RECOMMENDERS_SCORE, 100.0);
        verify(mgr, times(1)).addProcessor(ProcessorMatcher.withBoostAndLabel(200, "100%"));
        verify(mgr, times(1)).addProcessor(Mockito.any(OverlayImageProposalProcessor.class));
    }

    @Test
    public void testCompletionWithoutRelevanceBoostOrLabelDecoration() throws Exception {
        ConstructorModel model = new ConstructorModel(OBJECT, ImmutableMap.of(OBJECT_INIT, 1));
        setUp(CompletionOnSingleTypeReference.class, new UniqueTypeName(JRE_1_6_0, OBJECT), OBJECT_TYPE, model,
                ImmutableMap.of(OBJECT_INIT_PROPOSAL, OBJECT_INIT_CORE_PROPOSAL));

        ProposalProcessorManager mgr = mock(ProposalProcessorManager.class);
        when(OBJECT_INIT_PROCESSABLE_PROPOSAL.getProposalProcessorManager()).thenReturn(mgr);

        ConstructorsRcpPreferences preferences = createDefaultPreferences();
        preferences.changeProposalRelevance = false;
        preferences.decorateProposalText = false;

        ConstructorCompletionSessionProcessor sut = new ConstructorCompletionSessionProcessor(pcProvider,
                modelProvider, methodNameProvider, preferences, new SharedImages());

        boolean shouldProcess = sut.startSession(context);

        assertThat(shouldProcess, is(equalTo(true)));
        verify(modelProvider).releaseModel(model);

        sut.process(OBJECT_INIT_PROCESSABLE_PROPOSAL);

        verify(OBJECT_INIT_PROCESSABLE_PROPOSAL, never()).setTag(Mockito.any(ProposalTag.class), Mockito.anyDouble());
        verify(mgr, times(1)).addProcessor(ProcessorMatcher.withBoostAndLabel(0, ""));
        
    }

    private void setUp(Class<? extends ASTNode> completionType, UniqueTypeName uniqueTypeName,
            @Nullable IType expectedType, @Nullable ConstructorModel model,
            Map<IJavaCompletionProposal, CompletionProposal> proposals) {
        LookupEnvironment lookupEnvironment = mock(LookupEnvironment.class);
        context = mock(IRecommendersCompletionContext.class);
        when(context.get(CompletionContextKey.LOOKUP_ENVIRONMENT)).thenReturn(Optional.of(lookupEnvironment));
        Optional<ASTNode> completionNode = completionType == null ? Optional.<ASTNode>absent() : Optional
                .<ASTNode>of(mock(completionType));
        when(context.getCompletionNode()).thenReturn(completionNode);
        when(context.getExpectedType()).thenReturn(Optional.fromNullable(expectedType));
        when(context.getProposals()).thenReturn(proposals);

        pcProvider = Mockito.mock(IProjectCoordinateProvider.class);
        when(pcProvider.tryToUniqueName(expectedType)).thenReturn(Result.fromNullable(uniqueTypeName));

        modelProvider = Mockito.mock(IConstructorModelProvider.class);

        if (uniqueTypeName != null) {
            when(modelProvider.acquireModel(uniqueTypeName)).thenReturn(Optional.fromNullable(model));
        }

        methodNameProvider = Mockito.mock(IMethodNameProvider.class);
        when(methodNameProvider.toMethodName(OBJECT_INIT_CORE_PROPOSAL, lookupEnvironment)).thenReturn(
                Optional.of(OBJECT_INIT));

        when(OBJECT_INIT_CORE_PROPOSAL.getKind()).thenReturn(CompletionProposal.CONSTRUCTOR_INVOCATION);

        when(OBJECT_INIT_PROCESSABLE_PROPOSAL.getCoreProposal()).thenReturn(Optional.of(OBJECT_INIT_CORE_PROPOSAL));

    }

    private ConstructorsRcpPreferences createDefaultPreferences() {
        return createPreferences(0, 7, true, true, false);
    }

    private ConstructorsRcpPreferences createPreferences(int minProposalProbability, int maxNumberOfProposals,
            boolean changeProposalRelevance, boolean decorateProposalText, boolean decorateProposalIcon) {
        ConstructorsRcpPreferences pref = new ConstructorsRcpPreferences();
        pref.maxNumberOfProposals = maxNumberOfProposals;
        pref.minProposalProbability = minProposalProbability;
        pref.changeProposalRelevance = changeProposalRelevance;
        pref.decorateProposalText = decorateProposalText;
        pref.decorateProposalIcon = decorateProposalIcon;
        return pref;
    }

    private static class ProcessorMatcher extends ArgumentMatcher<SimpleProposalProcessor> {

        private final int boost;
        private final String label;

        public static SimpleProposalProcessor withBoostAndLabel(int boost, String label) {
            return argThat(new ProcessorMatcher(boost, label));
        }

        public ProcessorMatcher(Integer boost, String label) {
            this.boost = boost;
            this.label = label;
        }

        @Override
        public boolean matches(Object argument) {
            SimpleProposalProcessor processor = (SimpleProposalProcessor) argument;
            if (boost != processor.getIncrement()) {
                return false;
            }
            return label.equals(processor.getAddon());
        }
    }

}
