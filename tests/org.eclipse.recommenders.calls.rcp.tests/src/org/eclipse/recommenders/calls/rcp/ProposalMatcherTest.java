package org.eclipse.recommenders.calls.rcp;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.eclipse.recommenders.testing.CodeBuilder.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.recommenders.completion.rcp.IRecommendersCompletionContext;
import org.eclipse.recommenders.completion.rcp.RecommendersCompletionContext;
import org.eclipse.recommenders.internal.calls.rcp.ProposalMatcher;
import org.eclipse.recommenders.internal.rcp.CachingAstProvider;
import org.eclipse.recommenders.testing.jdt.JavaProjectFixture;
import org.eclipse.recommenders.testing.rcp.jdt.JavaContentAssistContextMock;
import org.eclipse.recommenders.utils.Pair;
import org.eclipse.recommenders.utils.names.IMethodName;
import org.eclipse.recommenders.utils.names.VmMethodName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
public class ProposalMatcherTest {

    private static IMethodName METHOD_VOID = VmMethodName.get("Lorg/example/Any.method()V");
    private static IMethodName METHOD_OBJECT = VmMethodName.get("Lorg/example/Any.method(Ljava/lang/Object;)V");
    private static IMethodName METHOD_COLLECTION = VmMethodName.get("Lorg/example/Any.method(Ljava/util/Collection;)V");

    private static IMethodName METHOD_INTS = VmMethodName.get("Lorg/example/Any.method([I)V");
    private static IMethodName METHOD_OBJECTS = VmMethodName.get("Lorg/example/Any.method([Ljava/lang/Object;)V");

    private final CharSequence code;
    private final IMethodName method;
    private final boolean matchExpected;

    public ProposalMatcherTest(CharSequence code, IMethodName method, boolean matchExpected) {
        this.code = code;
        this.method = method;
        this.matchExpected = matchExpected;
    }

    @Parameters
    public static Collection<Object[]> scenarios() {
        LinkedList<Object[]> scenarios = Lists.newLinkedList();

        scenarios.add(scenario(classbody("void method(int i) { this.method$ }"), METHOD_VOID, false));

        scenarios.add(scenario(classbody("void method() { this.method$ }"), METHOD_VOID, true));
        scenarios.add(scenario(classbody("void method(Object o) { this.method$ }"), METHOD_OBJECT, true));
        scenarios.add(scenario(classbody("void method(Collection c) { this.method$ }"), METHOD_COLLECTION, true));
        scenarios.add(scenario(classbody("void method(Collection<?> c) { this.method$ }"), METHOD_COLLECTION, true));
        scenarios.add(scenario(classbody("void method(Collection<? extends Object> c) { this.method$ }"),
                METHOD_COLLECTION, true));
        scenarios
        .add(scenario(classbody(classname() + "<T>", "void method(T t) { this.method$ }"), METHOD_OBJECT, true));
        scenarios.add(scenario(classbody(classname() + "<O extends Object>", "void method(O o) { this.method$ }"),
                METHOD_OBJECT, true));
        scenarios.add(scenario(classbody(classname() + "<C extends Collection>", "void method(C c) { this.method$ }"),
                METHOD_COLLECTION, true));

        scenarios.add(scenario(classbody("void method(int[] is) { this.method$ }"), METHOD_INTS, true));
        scenarios.add(scenario(classbody("void method(Object[] os) { this.method$ }"), METHOD_OBJECTS, true));

        scenarios.add(scenario(classbody("<O extends Object> void method(O o) { this.method$ }"), METHOD_OBJECT, true));
        scenarios.add(scenario(classbody("<C extends Collection> void method(C c) { this.method$ }"),
                METHOD_COLLECTION, true));

        return scenarios;
    }

    private static Object[] scenario(CharSequence compilationUnit, IMethodName method, boolean matchExpected) {
        return new Object[] { compilationUnit, method, matchExpected };
    }

    @Test
    public void testReceiverTypeOfInstanceMethod() throws Exception {
        Collection<CompletionProposal> proposals = extractProposals(code);

        ProposalMatcher sut = new ProposalMatcher(getOnlyElement(proposals));

        assertThat(sut.match(method), is(equalTo(matchExpected)));
    }

    private Collection<CompletionProposal> extractProposals(CharSequence code) throws CoreException {
        JavaProjectFixture fixture = new JavaProjectFixture(ResourcesPlugin.getWorkspace(), "test");
        Pair<ICompilationUnit, Set<Integer>> struct = fixture.createFileAndParseWithMarkers(code.toString());
        ICompilationUnit cu = struct.getFirst();
        int completionIndex = struct.getSecond().iterator().next();
        JavaContentAssistInvocationContext javaContext = new JavaContentAssistContextMock(cu, completionIndex);
        IRecommendersCompletionContext recommendersContext = new RecommendersCompletionContext(javaContext,
                new CachingAstProvider());
        return recommendersContext.getProposals().values();
    }
}
