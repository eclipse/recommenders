package org.eclipse.recommenders.tests.completion.rcp;

import static org.eclipse.recommenders.tests.CodeBuilder.classDeclaration;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.Closeable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.recommenders.completion.rcp.IRecommendersCompletionContext;
import org.eclipse.recommenders.tests.jdt.JavaProjectFixture;
import org.eclipse.recommenders.utils.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Lists;

/**
 * Test that generic types and their bounds are taken into account when completion is triggered on a generic return
 * value.
 * 
 * @see <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=404751">Bug 404751</a>
 */
@RunWith(Parameterized.class)
public class Bug404751Test {

    private final String typeParameters;
    private final String typeArguments;
    private final String returnType;
    private final String expectedType;

    public Bug404751Test(String typeParameters, String typeArguments, String returnType, String expectedType) {
        this.typeParameters = typeParameters;
        this.typeArguments = typeArguments;
        this.returnType = returnType;
        this.expectedType = expectedType;
    }

    @Parameters
    public static Collection<Object[]> scenarios() {
        LinkedList<Object[]> scenarios = Lists.newLinkedList();

        scenarios.add(scenario(null, null, "TestClass", "TestClass")); // No generics. Sanity check

        scenarios.add(scenario("T", "?", "T", "Object"));
        scenarios.add(scenario("T", "Number", "T", "Number"));

        scenarios.add(scenario("T extends Number", "?", "T", "Number"));
        scenarios.add(scenario("T extends Number", "Integer", "T", "Integer"));

        scenarios.add(scenario("T extends List", "?", "T", "List"));
        scenarios.add(scenario("T extends List<?>", "?", "T", "List"));
        scenarios.add(scenario("T extends List<?>", "List", "T", "List"));
        scenarios.add(scenario("T extends List<?>", "ArrayList", "T", "ArrayList"));
        scenarios.add(scenario("T extends List<?>", "List<?>", "T", "List"));
        scenarios.add(scenario("T extends List<?>", "ArrayList<?>", "T", "ArrayList"));
        scenarios.add(scenario("T extends List<T>", "?", "T", "List"));

        scenarios.add(scenario("T extends List & Closeable", "?", "T", "List"));
        scenarios.add(scenario("T extends Closeable & List", "?", "T", "Closeable"));

        scenarios.add(scenario("T extends Number & List", "?", "T", "Number"));
        scenarios.add(scenario("T extends Number & List & Closeable", "?", "T", "Number"));

        return scenarios;
    }

    private static Object[] scenario(String typeParameters, String typeArguments, String returnType, String expectedType) {
        return new Object[] { typeParameters, typeArguments, returnType, expectedType };
    }

    @Test
    public void testReceiverType() throws CoreException {
        String producerMethod = returnType + " produce() { return null; }";
        String consumerMethod = "static void consume() { new TestClass" + asGeneric(typeArguments)
                + "().produce().$; }";
        CharSequence code = classDeclaration("class TestClass" + asGeneric(typeParameters), producerMethod
                + consumerMethod);

        IRecommendersCompletionContext sut = exercise(code);
        IType receiverType = sut.getReceiverType().get();

        assertThat(receiverType.getElementName(), is(equalTo(expectedType)));
    }

    private String asGeneric(String typeParameters) {
        return typeParameters == null ? "" : "<" + typeParameters + ">";
    }

    private IRecommendersCompletionContext exercise(CharSequence code) throws CoreException {
        JavaProjectFixture fixture = new JavaProjectFixture(ResourcesPlugin.getWorkspace(), "test");
        Tuple<ICompilationUnit, Set<Integer>> struct = fixture.createFileAndParseWithMarkers(code.toString());
        ICompilationUnit cu = struct.getFirst();
        int completionIndex = struct.getSecond().iterator().next();
        JavaContentAssistInvocationContext ctx = new JavaContentAssistContextMock(cu, completionIndex);

        return new RecommendersCompletionContextFactoryMock().create(ctx);
    }
}
