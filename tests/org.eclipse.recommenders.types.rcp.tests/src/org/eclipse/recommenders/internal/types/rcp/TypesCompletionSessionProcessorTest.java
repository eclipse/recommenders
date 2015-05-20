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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.*;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.recommenders.completion.rcp.IRecommendersCompletionContext;
import org.eclipse.recommenders.rcp.SharedImages;
import org.eclipse.recommenders.utils.names.ITypeName;
import org.eclipse.recommenders.utils.names.VmTypeName;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

public class TypesCompletionSessionProcessorTest {

    private static final ITypeName LIST = VmTypeName.get("Ljava/util/List");
    private static final ITypeName SET = VmTypeName.get("Ljava/util/Set");

    private ITypesIndexService service;

    @Before
    public void setUp() {
        service = mock(ITypesIndexService.class);

        when(service.subtypes(eq(LIST), startsWith("A"), any(IJavaProject.class)))
                .thenReturn(ImmutableSet.of("java.util.AbstractList", "java.util.ArrayList"));
        when(service.subtypes(eq(LIST), startsWith("B"), any(IJavaProject.class)))
                .thenReturn(ImmutableSet.<String>of());
        when(service.subtypes(eq(LIST), eq(""), any(IJavaProject.class)))
                .thenReturn(ImmutableSet.of("java.util.AbstractList", "java.util.ArrayList"));

        when(service.subtypes(eq(SET), startsWith("A"), any(IJavaProject.class)))
                .thenReturn(ImmutableSet.of("java.util.AbstractSet"));
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
    public void testEmptyPrefix() {
        IRecommendersCompletionContext context = setUpCompletionScenario("", LIST);

        TypesCompletionSessionProcessor sut = new TypesCompletionSessionProcessor(service, new SharedImages());

        boolean shouldProcess = sut.startSession(context);

        assertThat(shouldProcess, is(equalTo(true)));

        // TODO
    }

    private IRecommendersCompletionContext setUpCompletionScenario(String prefix, ITypeName... expectedTypeNames) {
        IRecommendersCompletionContext context = mock(IRecommendersCompletionContext.class);

        when(context.getPrefix()).thenReturn(prefix);

        when(context.getExpectedTypeNames()).thenReturn(ImmutableSet.copyOf(expectedTypeNames));

        IJavaProject project = mock(IJavaProject.class);
        when(context.getProject()).thenReturn(project);

        return context;
    }
}
