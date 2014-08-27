package org.eclipse.recommenders.completion.rcp;

import static org.eclipse.recommenders.testing.CodeBuilder.*;
import static org.eclipse.recommenders.utils.names.VmTypeName.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.recommenders.internal.rcp.CachingAstProvider;
import org.eclipse.recommenders.testing.jdt.JavaProjectFixture;
import org.eclipse.recommenders.testing.rcp.jdt.JavaContentAssistContextMock;
import org.eclipse.recommenders.utils.Pair;
import org.eclipse.recommenders.utils.names.ITypeName;
import org.eclipse.recommenders.utils.names.VmTypeName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
public class ExpectedTypeNamesContextFunctionTest {

    private static ITypeName OBJECT_ARRAY = VmTypeName.get("[Ljava/lang/Object");
    private static ITypeName STRING = VmTypeName.get("Ljava/lang/String");
    private static ITypeName STRING_ARRAY = VmTypeName.get("[Ljava/lang/String");
    private static ITypeName FILE = VmTypeName.get("Ljava/io/File");
    private static ITypeName COLLECTION = VmTypeName.get("Ljava/util/Collection");
    private static ITypeName URI = VmTypeName.get("Ljava/net/URI");

    private final CharSequence code;
    private final ITypeName[] expectedTypes;

    public ExpectedTypeNamesContextFunctionTest(CharSequence code, ITypeName... expectedTypes) {
        this.code = code;
        this.expectedTypes = expectedTypes;
    }

    @Parameters
    public static Collection<Object[]> scenarios() {
        LinkedList<Object[]> scenarios = Lists.newLinkedList();

        scenarios.add(scenario(method("new File($);"), FILE, STRING, URI));
        scenarios.add(scenario(method("File f = $;"), FILE));
        scenarios.add(scenario(classbody("File method() { return $; }"), FILE));

        scenarios.add(scenario(method("List<String> l = new ArrayList<String>($);"), COLLECTION, INT));
        scenarios.add(scenario(method("List<String> l = new ArrayList<String>(); l.add($)"), STRING, INT));
        scenarios.add(scenario(method("List<String> l = new ArrayList<String>(); l.toArray($)"), STRING_ARRAY));

        scenarios.add(scenario(method("Arrays.asList($);"), OBJECT_ARRAY));

        scenarios.add(scenario(method("if ($) {}"), BOOLEAN));
        scenarios.add(scenario(method("while ($) {}"), BOOLEAN));

        return scenarios;
    }

    private static Object[] scenario(CharSequence compilationUnit, ITypeName... expectedTypes) {
        return new Object[] { compilationUnit, expectedTypes };
    }

    @Test
    public void test() throws Exception {
        IRecommendersCompletionContext context = getContext(code);
        Set<ITypeName> actual = context.getExpectedTypeNames();

        assertThat(actual, hasItems(expectedTypes));
        assertThat(actual.size(), is(equalTo(expectedTypes.length)));
    }

    private IRecommendersCompletionContext getContext(CharSequence code) throws CoreException {
        JavaProjectFixture fixture = new JavaProjectFixture(ResourcesPlugin.getWorkspace(), "test");
        Pair<ICompilationUnit, Set<Integer>> struct = fixture.createFileAndParseWithMarkers(code.toString());
        ICompilationUnit cu = struct.getFirst();
        int completionIndex = struct.getSecond().iterator().next();
        JavaContentAssistInvocationContext javaContext = new JavaContentAssistContextMock(cu, completionIndex);
        IRecommendersCompletionContext recommendersContext = new RecommendersCompletionContext(javaContext,
                new CachingAstProvider());
        return recommendersContext;
    }
}
