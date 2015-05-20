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
package org.eclipse.recommenders.internal.types.rcp;

import static org.eclipse.recommenders.internal.types.rcp.SimpleProposalProcessorMatcher.processorWithBoost;
import static org.eclipse.recommenders.internal.types.rcp.TypesCompletionSessionProcessor.BOOST;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.*;

import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.recommenders.completion.rcp.IRecommendersCompletionContext;
import org.eclipse.recommenders.completion.rcp.processable.IProcessableProposal;
import org.eclipse.recommenders.completion.rcp.processable.OverlayImageProposalProcessor;
import org.eclipse.recommenders.completion.rcp.processable.ProcessableJavaCompletionProposal;
import org.eclipse.recommenders.completion.rcp.processable.ProposalProcessorManager;
import org.eclipse.recommenders.completion.rcp.processable.ProposalTag;
import org.eclipse.recommenders.rcp.SharedImages;
import org.eclipse.recommenders.utils.names.ITypeName;
import org.eclipse.recommenders.utils.names.VmTypeName;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

public class TypesCompletionSessionProcessorTest {

    private static final ITypeName LIST = VmTypeName.get("Ljava/util/List");
    private static final ITypeName SET = VmTypeName.get("Ljava/util/Set");

    private static final String ARRAY_LIST_SIGNATURE = "Ljava.util.ArrayList;";
    private static final String ARRAY_LIST_GENERIC_SIGNATURE = "Ljava.util.ArrayList<TE;>;";
    private static final String ABSTRACT_LIST_SIGNATURE = "Ljava.util.AbstractList;";
    private static final String LINKED_LIST_SIGNATURE = "Ljava.util.LinkedList;";
    private static final String ABSTRACT_SET_SIGNATURE = "Ljava.util.AbstractSet;";

    private static final String ARRAY_LIST_QUALIFIED_NAME = "java.util.ArrayList";
    private static final String ABSTRACT_LIST_QUALIFIED_NAME = "java.util.AbstractList";
    private static final String LINKED_LIST_QUALIFIED_NAME = "java.util.LinkedList";
    private static final String ABSTRACT_SET_QUALIFIED_NAME = "java.util.AbstractSet";

    private ITypesIndexService service;

    @Before
    public void setUp() {
        service = mock(ITypesIndexService.class);

        when(service.subtypes(eq(LIST), startsWith("A"), any(IJavaProject.class)))
                .thenReturn(ImmutableSet.of(ABSTRACT_LIST_QUALIFIED_NAME, ARRAY_LIST_QUALIFIED_NAME));
        when(service.subtypes(eq(LIST), startsWith("B"), any(IJavaProject.class)))
                .thenReturn(ImmutableSet.<String>of());
        when(service.subtypes(eq(LIST), startsWith("L"), any(IJavaProject.class)))
                .thenReturn(ImmutableSet.of(LINKED_LIST_QUALIFIED_NAME));
        when(service.subtypes(eq(LIST), eq(""), any(IJavaProject.class)))
                .thenReturn(ImmutableSet.of(ABSTRACT_LIST_QUALIFIED_NAME, ARRAY_LIST_QUALIFIED_NAME));

        when(service.subtypes(eq(SET), startsWith("A"), any(IJavaProject.class)))
                .thenReturn(ImmutableSet.of(ABSTRACT_SET_QUALIFIED_NAME));
    }

    @Test
    public void testNoExpectedTypes() {
        IRecommendersCompletionContext context = setUpCompletionScenario("A");

        TypesCompletionSessionProcessor sut = new TypesCompletionSessionProcessor(service, new SharedImages());

        boolean shouldProcess = sut.startSession(context);

        assertThat(shouldProcess, is(equalTo(false)));

        verifyZeroInteractions(service);
    }

    @Test
    public void testNoProposals() throws Exception {
        IRecommendersCompletionContext context = setUpCompletionScenario("B", LIST);

        TypesCompletionSessionProcessor sut = new TypesCompletionSessionProcessor(service, new SharedImages());

        boolean shouldProcess = sut.startSession(context);

        assertThat(shouldProcess, is(equalTo(false)));
    }

    @Test
    public void testPrefixWithSingleProposal() throws Exception {
        IRecommendersCompletionContext context = setUpCompletionScenario("L", LIST);

        TypesCompletionSessionProcessor sut = new TypesCompletionSessionProcessor(service, new SharedImages());

        boolean shouldProcess = sut.startSession(context);

        assertThat(shouldProcess, is(equalTo(true)));

        ProposalProcessorManager manager = mock(ProposalProcessorManager.class);
        IProcessableProposal linkedListProcessableProposal = mockIProcessableProposal(manager,
                CompletionProposal.TYPE_REF, LINKED_LIST_SIGNATURE);

        sut.process(linkedListProcessableProposal);

        verify(linkedListProcessableProposal).setTag(ProposalTag.RECOMMENDERS_SCORE, BOOST);
        verify(manager, times(1)).addProcessor(processorWithBoost(BOOST));
        verify(manager, times(1)).addProcessor(isA(OverlayImageProposalProcessor.class));
        verifyNoMoreInteractions(manager);
    }

    @Test
    public void testEmptyPrefix() throws Exception {
        IRecommendersCompletionContext context = setUpCompletionScenario("", LIST);

        TypesCompletionSessionProcessor sut = new TypesCompletionSessionProcessor(service, new SharedImages());

        boolean shouldProcess = sut.startSession(context);

        assertThat(shouldProcess, is(equalTo(true)));

        ProposalProcessorManager abstractListProposalManager = mock(ProposalProcessorManager.class);
        IProcessableProposal abstractListProcessableProposal = mockIProcessableProposal(abstractListProposalManager,
                CompletionProposal.TYPE_REF, ABSTRACT_LIST_SIGNATURE);

        ProposalProcessorManager arrayListProposalManager = mock(ProposalProcessorManager.class);
        IProcessableProposal arrayListProcessableProposal = mockIProcessableProposal(arrayListProposalManager,
                CompletionProposal.TYPE_REF, ARRAY_LIST_SIGNATURE);

        sut.process(abstractListProcessableProposal);

        verify(abstractListProcessableProposal).setTag(ProposalTag.RECOMMENDERS_SCORE, BOOST);
        verify(abstractListProposalManager, times(1)).addProcessor(processorWithBoost(BOOST));
        verify(abstractListProposalManager, times(1)).addProcessor(isA(OverlayImageProposalProcessor.class));
        verifyNoMoreInteractions(abstractListProposalManager);

        sut.process(arrayListProcessableProposal);

        verify(arrayListProcessableProposal).setTag(ProposalTag.RECOMMENDERS_SCORE, BOOST);
        verify(arrayListProposalManager, times(1)).addProcessor(processorWithBoost(BOOST));
        verify(arrayListProposalManager, times(1)).addProcessor(isA(OverlayImageProposalProcessor.class));
        verifyNoMoreInteractions(arrayListProposalManager);
    }

    @Test
    public void testMultipleExpectedTypes() throws Exception {
        IRecommendersCompletionContext context = setUpCompletionScenario("A", LIST, SET);

        TypesCompletionSessionProcessor sut = new TypesCompletionSessionProcessor(service, new SharedImages());

        boolean shouldProcess = sut.startSession(context);

        assertThat(shouldProcess, is(equalTo(true)));

        ProposalProcessorManager abstractListProposalManager = mock(ProposalProcessorManager.class);
        IProcessableProposal abstractListProcessableProposal = mockIProcessableProposal(abstractListProposalManager,
                CompletionProposal.TYPE_REF, ABSTRACT_LIST_SIGNATURE);

        ProposalProcessorManager arrayListProposalManager = mock(ProposalProcessorManager.class);
        IProcessableProposal arrayListProcessableProposal = mockIProcessableProposal(arrayListProposalManager,
                CompletionProposal.TYPE_REF, ARRAY_LIST_SIGNATURE);

        ProposalProcessorManager abstractSetProposalManager = mock(ProposalProcessorManager.class);
        IProcessableProposal abstractSetProcessableProposal = mockIProcessableProposal(abstractSetProposalManager,
                CompletionProposal.TYPE_REF, ABSTRACT_SET_SIGNATURE);

        sut.process(abstractListProcessableProposal);

        verify(abstractListProcessableProposal).setTag(ProposalTag.RECOMMENDERS_SCORE, BOOST);
        verify(abstractListProposalManager, times(1)).addProcessor(processorWithBoost(BOOST));
        verify(abstractListProposalManager, times(1)).addProcessor(isA(OverlayImageProposalProcessor.class));
        verifyNoMoreInteractions(abstractListProposalManager);

        sut.process(arrayListProcessableProposal);

        verify(arrayListProcessableProposal).setTag(ProposalTag.RECOMMENDERS_SCORE, BOOST);
        verify(arrayListProposalManager, times(1)).addProcessor(processorWithBoost(BOOST));
        verify(arrayListProposalManager, times(1)).addProcessor(isA(OverlayImageProposalProcessor.class));
        verifyNoMoreInteractions(arrayListProposalManager);

        sut.process(abstractSetProcessableProposal);

        verify(abstractSetProcessableProposal).setTag(ProposalTag.RECOMMENDERS_SCORE, BOOST);
        verify(abstractSetProposalManager, times(1)).addProcessor(processorWithBoost(BOOST));
        verify(abstractSetProposalManager, times(1)).addProcessor(isA(OverlayImageProposalProcessor.class));
        verifyNoMoreInteractions(abstractSetProposalManager);
    }

    @Test
    public void testConstructorInvocationProposalWithGenerics() throws Exception {
        IRecommendersCompletionContext context = setUpCompletionScenario("A", LIST);

        TypesCompletionSessionProcessor sut = new TypesCompletionSessionProcessor(service, new SharedImages());

        boolean shouldProcess = sut.startSession(context);

        assertThat(shouldProcess, is(equalTo(true)));

        ProposalProcessorManager manager = mock(ProposalProcessorManager.class);
        IProcessableProposal genericArrayListProcessableProposal = mockIProcessableProposal(manager,
                CompletionProposal.CONSTRUCTOR_INVOCATION, ARRAY_LIST_GENERIC_SIGNATURE);

        sut.process(genericArrayListProcessableProposal);

        verify(genericArrayListProcessableProposal).setTag(ProposalTag.RECOMMENDERS_SCORE, BOOST);
        verify(manager, times(1)).addProcessor(processorWithBoost(BOOST));
        verify(manager, times(1)).addProcessor(isA(OverlayImageProposalProcessor.class));
        verifyNoMoreInteractions(manager);
    }

    @Test
    public void testAnonymousClassConstructorInvocationProposal() throws Exception {
        IRecommendersCompletionContext context = setUpCompletionScenario("A", SET);

        TypesCompletionSessionProcessor sut = new TypesCompletionSessionProcessor(service, new SharedImages());

        boolean shouldProcess = sut.startSession(context);

        assertThat(shouldProcess, is(equalTo(true)));

        ProposalProcessorManager manager = mock(ProposalProcessorManager.class);
        IProcessableProposal abstractSetProcessableProposal = mockIProcessableProposal(manager,
                CompletionProposal.ANONYMOUS_CLASS_CONSTRUCTOR_INVOCATION, ABSTRACT_SET_SIGNATURE);

        sut.process(abstractSetProcessableProposal);

        verify(abstractSetProcessableProposal).setTag(ProposalTag.RECOMMENDERS_SCORE, BOOST);
        verify(manager, times(1)).addProcessor(processorWithBoost(BOOST));
        verify(manager, times(1)).addProcessor(isA(OverlayImageProposalProcessor.class));
        verifyNoMoreInteractions(manager);
    }

    private IRecommendersCompletionContext setUpCompletionScenario(String prefix, ITypeName... expectedTypeNames) {
        IRecommendersCompletionContext context = mock(IRecommendersCompletionContext.class);

        when(context.getPrefix()).thenReturn(prefix);

        when(context.getExpectedTypeNames()).thenReturn(ImmutableSet.copyOf(expectedTypeNames));

        IJavaProject project = mock(IJavaProject.class);
        when(context.getProject()).thenReturn(project);

        return context;
    }

    private IProcessableProposal mockIProcessableProposal(ProposalProcessorManager manager, int coreProposalKind,
            String coreProposalSig) {
        IProcessableProposal processableProposal = mock(ProcessableJavaCompletionProposal.class);
        when(processableProposal.getProposalProcessorManager()).thenReturn(manager);

        CompletionProposal coreProposal = mock(CompletionProposal.class);
        when(coreProposal.getKind()).thenReturn(coreProposalKind);
        when(processableProposal.getCoreProposal()).thenReturn(Optional.fromNullable(coreProposal));

        switch (coreProposalKind) {
        case CompletionProposal.TYPE_REF: {
            when(coreProposal.getSignature()).thenReturn(coreProposalSig.toCharArray());
            break;
        }
        case CompletionProposal.ANONYMOUS_CLASS_CONSTRUCTOR_INVOCATION:
        case CompletionProposal.CONSTRUCTOR_INVOCATION:
            when(coreProposal.getDeclarationSignature()).thenReturn(coreProposalSig.toCharArray());
        }

        return processableProposal;
    }
}
