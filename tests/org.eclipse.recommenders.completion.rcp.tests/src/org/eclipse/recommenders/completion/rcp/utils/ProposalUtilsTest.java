package org.eclipse.recommenders.completion.rcp.utils;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.eclipse.recommenders.testing.CodeBuilder.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.recommenders.completion.rcp.IRecommendersCompletionContext;
import org.eclipse.recommenders.testing.rcp.completion.rules.TemporaryProject;
import org.eclipse.recommenders.testing.rcp.completion.rules.TemporaryWorkspace;
import org.eclipse.recommenders.utils.names.IMethodName;
import org.eclipse.recommenders.utils.names.VmMethodName;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
public class ProposalUtilsTest {

    @ClassRule
    public static final TemporaryWorkspace WORKSPACE = new TemporaryWorkspace();

    private static final IMethodName METHOD_VOID = VmMethodName.get("LExample.method()V");
    private static final IMethodName METHOD_OBJECT = VmMethodName.get("LExample.method(Ljava/lang/Object;)V");
    private static final IMethodName METHOD_NUMBER = VmMethodName.get("LExample.method(Ljava/lang/Number;)V");
    private static final IMethodName METHOD_COLLECTION = VmMethodName.get("LExample.method(Ljava/util/Collection;)V");
    private static final IMethodName SET_INT_STRING = VmMethodName
            .get("Ljava/util/List.set(ILjava/lang/Object;)Ljava/lang/Object;");

    private static final IMethodName NESTED_METHOD_VOID = VmMethodName.get("LExample$Nested.method()V");

    private static final IMethodName METHOD_INTS = VmMethodName.get("LExample.method([I)V");
    private static final IMethodName METHOD_OBJECTS = VmMethodName.get("LExample.method([Ljava/lang/Object;)V");

    private static final IMethodName INIT = VmMethodName.get("LExample.<init>()V");
    private static final IMethodName INIT_OBJECT = VmMethodName.get("LExample.<init>(Ljava/lang/Object;)V");
    private static final IMethodName INIT_NUMBER = VmMethodName.get("LExample.<init>(Ljava/lang/Number;)V");
    private static final IMethodName INIT_COLLECTION = VmMethodName.get("LExample.<init>(Ljava/util/Collection;)V");

    private static final IMethodName NESTED_INIT = VmMethodName.get("LExample$Nested.<init>()V");
    private static final IMethodName NESTED_INIT_OBJECT = VmMethodName
            .get("LExample$Nested.<init>(Ljava/lang/Object;)V");
    private static final IMethodName NESTED_INIT_NUMBER = VmMethodName
            .get("LExample$Nested.<init>(Ljava/lang/Number;)V");
    private static final IMethodName NESTED_INIT_COLLECTION = VmMethodName
            .get("LExample$Nested.<init>(Ljava/util/Collection;)V");
    private static final IMethodName INNER_INIT_EXAMPLE = VmMethodName.get("LExample$Inner.<init>(LExample;)V");
    private static final IMethodName INNER_INIT_EXAMPLE_OBJECT = VmMethodName
            .get("LExample$Inner.<init>(LExample;Ljava/lang/Object;)V");
    private static final IMethodName INNER_INIT_EXAMPLE_NUMBER = VmMethodName
            .get("LExample$Inner.<init>(LExample;Ljava/lang/Number;)V");
    private static final IMethodName INNER_INIT_EXAMPLE_COLLECTION = VmMethodName
            .get("LExample$Inner.<init>(LExample;Ljava/util/Collection;)V");

    private static final IMethodName COMPARE_TO_BOOLEAN = VmMethodName
            .get("Ljava/lang/Boolean.compareTo(Ljava/lang/Boolean;)I");
    private static final IMethodName COMPARABLE_COMPARE_TO_OBJECT = VmMethodName
            .get("Ljava/lang/Comparable.compareTo(Ljava/lang/Object;)I");
    private static final IMethodName COMPARE_TO_OBJECT = VmMethodName.get("LExample.compareTo(Ljava/lang/Object;)I");
    private static final IMethodName COMPARE_TO_EXAMPLE = VmMethodName.get("LExample.compareTo(LExample;)I");

    private static final IMethodName OBJECT_HASH_CODE = VmMethodName.get("Ljava/lang/Object.hashCode()I");
    private static final IMethodName EXAMPLE_HASH_CODE = VmMethodName.get("LExample.hashCode()I");
    private static final IMethodName SUBEXAMPLE_HASH_CODE = VmMethodName.get("LSubExample.hashCode()I");

    private static final IMethodName OBJECT_CLONE = VmMethodName.get("Ljava/lang/Object.clone()Ljava/lang/Object;");

    private final boolean ignore;
    private final CharSequence exampleCode;
    private final CharSequence scenarioCode;
    private final IMethodName expectedMethod;

    public ProposalUtilsTest(boolean ignore, String description, CharSequence exampleCode, CharSequence scenarioCode,
            IMethodName expectedMethod) {
        this.ignore = ignore;
        this.exampleCode = exampleCode;
        this.scenarioCode = scenarioCode;
        this.expectedMethod = expectedMethod;
    }

    @Parameters(name = "{index}: {1}")
    public static Collection<Object[]> scenarios() {
        LinkedList<Object[]> scenarios = Lists.newLinkedList();

        // @formatter:off
        scenarios.add(scenario("Method",
                classbody("Example", "void method() {}"),
                method("new Example().method$ "),
                METHOD_VOID));
        scenarios.add(scenario("Method With Object Argument",
                classbody("Example", "void method(Object o) {}"),
                method("new Example().method$ "),
                METHOD_OBJECT));
        scenarios.add(scenario("Method With Collection Argument",
                classbody("Example", "void method(Collection c) {}"),
                method("new Example().method$ "),
                METHOD_COLLECTION));
        scenarios.add(scenario("Method With Primitive Array Argument",
                classbody("Example", "void method(int[] is) {}"),
                method("new Example().method$ "),
                METHOD_INTS));
        scenarios.add(scenario("Method With Object Array Argument",
                classbody("Example", "void method(Object[] os) {}"),
                method("new Example().method$ "),
                METHOD_OBJECTS));

        scenarios.add(scenario("Method Of Static Nested Class",
                classbody("Example", "static class Nested { void method() {} }"),
                method("new Example.Nested().method$ "),
                NESTED_METHOD_VOID));

        scenarios.add(scenario("Method of Static Nested Class of Parameterized Class",
                classbody("Example<T>", "static class Nested { void method() {} }"),
                method("new Example.Nested().method$ "),
                NESTED_METHOD_VOID));
        scenarios.add(scenario("Method of Parameterized Static Nested Class",
                classbody("Example", "static class Nested<T> { void method() {} }"),
                method("new Example.Nested<String>().method$ "),
                NESTED_METHOD_VOID));

        scenarios.add(scenario("Method With Parameterized Collection Argument",
                classbody("Example", "void method(Collection<Number> c) {}"),
                method("new Example().method$ "),
                METHOD_COLLECTION));
        scenarios.add(scenario("Method With Wildcard Parameterized Collection Argument",
                classbody("Example", "void method(Collection<?> c) {}"),
                method("new Example().method$ "),
                METHOD_COLLECTION));
        scenarios.add(scenario("Method With Bounded Above Wildcard Parameterized Collection Argument",
                classbody("Example", "void method(Collection<? extends Number> c) {}"),
                method("new Example().method$ "),
                METHOD_COLLECTION));
        scenarios.add(scenario("Method With Bounded Below Wildcard Parameterized Collection Argument",
                classbody("Example", "void method(Collection<? super Number> c) {}"),
                method("new Example().method$ "),
                METHOD_COLLECTION));

        scenarios.add(scenario("Method With Unspecified Class Parameter As Argument",
                classbody("Example<T>", "void method(T t) {}"),
                method("new Example().method$ "),
                METHOD_OBJECT));
        scenarios.add(scenario("Method With Specified Class Parameter As Argument",
                classbody("Example<T>", "void method(T t) {}"),
                method("new Example<Number>().method$ "),
                METHOD_OBJECT));
        scenarios.add(scenario("Method With Class Bounded Above Parameter As Argument",
                classbody("Example<O extends Object>", "void method(O o) {}"),
                method("new Example().method$ "),
                METHOD_OBJECT));
        scenarios.add(ignoredScenario("Method With Bounded Class Parameter As Argument",
                classbody("Example<N extends Number>", "void method(N n) {}"),
                method("new Example().method$ "),
                METHOD_NUMBER));
        scenarios.add(ignoredScenario("Method With Bounded And Specified Class Parameter As Argument",
                classbody("Example<N extends Number>", "void method(N n) {}"),
                method("new Example<Integer>().method$ "),
                METHOD_NUMBER));
        scenarios.add(ignoredScenario("Method With Multiple Inheritance Class Parameter As Argument",
                classbody("Example<N extends Number & Comparable>", "void method(N n) {}"),
                method("new Example().method$ "),
                METHOD_NUMBER));

        scenarios.add(scenario("Bounded Parameter Field Method Call with Nested Parameterization",
                classbody("Example<L extends List<String>>", "L l;"),
                method("new Example().l.set$ "),
                SET_INT_STRING));

        String auxiliaryDefinition = "class Auxiliary<L extends List<String>> { <N extends L> void method(N n) { } }";
        scenarios.add(ignoredScenario("Secondary Class With Nested, Bounded Parameters And Method With Bounded Parameter",
                classbody("Example", "void method(Auxiliary a) {}") + auxiliaryDefinition,
                classbody("SubExample extends Example", "void method(Auxiliary a) { a.method$ }"),
                VmMethodName.get("LAuxiliary.method(Ljava/util/List;)V")));

        scenarios.add(scenario("Method With Class Parameter Array Argument",
                classbody("Example<T>", "void method(T[] t) {}"),
                method("new Example().method$ "),
                METHOD_OBJECTS));
        scenarios.add(scenario("Method With Class Wildcard Parameter Array As Argument",
                classbody("Example<O extends Object>", "void method(O[] o) {}"),
                method("new Example().method$ "),
                METHOD_OBJECTS));
        scenarios.add(scenario("Method With Class Wildcard Parameter Collection As Argument",
                classbody("Example<N extends Number>", "void method(Collection<N> c) {}"),
                method("new Example().method$ "),
                METHOD_COLLECTION));

        scenarios.add(scenario("Method With Method Parameter",
                classbody("Example", "<T> void method(T t) {}"),
                method("new Example().method$ "),
                METHOD_OBJECT));
        scenarios.add(scenario("Method With Object Bounded Parameter",
                classbody("Example", "<O extends Object> void method(O o) {}"),
                method("new Example().method$ "),
                METHOD_OBJECT));
        scenarios.add(ignoredScenario("Method with Bounded Parameter",
                classbody("Example", "<N extends Number> void method(N n) {}"),
                method("new Example().method$ "),
                METHOD_NUMBER));
        scenarios.add(ignoredScenario("Method with Multiple Inheritance Parameter",
                classbody("Example", "<N extends Number & Comparable> void method(N n) {}"),
                method("new Example().method$ "),
                METHOD_NUMBER));

        scenarios.add(scenario("Static Method With Method Parameter Argument",
                classbody("Example", "static <T> void method(T t) {}"),
                method("new Example.<Integer>method$ "),
                METHOD_OBJECT));
        scenarios.add(scenario("Static Method With Object Bounded Parameter Argument",
                classbody("Example", "static <O extends Object> void method(O o) {}"),
                method("new Example.<Integer>method$ "),
                METHOD_OBJECT));
        scenarios.add(ignoredScenario("Static Method With Specified And Bounded Parameter Argument",
                classbody("Example", "static <N extends Number> void method(N n) {}"),
                method("new Example.<Integer>method$ "),
                METHOD_NUMBER));
        scenarios.add(ignoredScenario("Static Method with Specified Multiple Inheritance Parameter",
                classbody("Example", "static <N extends Number & Comparable> void method(N n) {}"),
                method("new Example.<Integer>method$ "),
                METHOD_NUMBER));

        scenarios.add(scenario("Method With Method Parameter Array Argument",
                classbody("Example", "<T> void method(T[] t) {}"),
                method("new Example().method$ "),
                METHOD_OBJECTS));
        scenarios.add(scenario("Method With Method Bounded Parameter Array Argument",
                classbody("Example", "<O extends Object> void method(O[] o) {}"),
                method("new Example().method$ "),
                METHOD_OBJECTS));

        scenarios.add(scenario("Arguments For Method Call On Object Field",
                classbody("Example", "Boolean b;"),
                method("new Example().b.compareTo$ "),
                COMPARE_TO_BOOLEAN));
        scenarios.add(scenario("Arguments For Method Call On Interface Field",
                classbody("Example", "Delayed d;"),
                method("new Example().d.compareTo$ "),
                COMPARABLE_COMPARE_TO_OBJECT));

        scenarios.add(scenario("Implicit No Args Constructor",
                classbody("Example", ""),
                classbody("SubExample extends Example", "void method() { super($) }"),
                INIT));
        scenarios.add(scenario("Explicit No Args Constructor",
                classbody("Example", "Example() {}"),
                classbody("SubExample extends Example", "void method() { super($) }"),
                INIT));
        scenarios.add(scenario("Constructor With Class Parameter Arguement",
                classbody("Example<T>", "Example(T t) {}"),
                classbody("SubExample extends Example", "void method() { super($) }"),
                INIT_OBJECT));
        scenarios.add(scenario("Constructor With Object Bounnded Class Parameter Argument",
                classbody("Example<T extends Object>", "Example(T t) {}"),
                classbody("SubExample extends Example", "void method() { super($) }"),
                INIT_OBJECT));
        scenarios.add(ignoredScenario("Constructor With Bounded Class Parameter Argument",
                classbody("Example<N extends Number>", "Example(N n) {}"),
                classbody("SubExample extends Example", "void method() { super($) }"),
                INIT_NUMBER));
        scenarios.add(scenario("Constructor With Wildcard Parameterized Argument Extending Class Parameter",
                classbody("Example<N>", "Example(Collection<? extends N> c) {}"),
                classbody("SubExample extends Example", "void method() { super($) }"),
                INIT_COLLECTION));

        // Using nested classes to speed up JDT's constructor completion; this avoids timeouts.
        scenarios.add(scenario("Static Nested Class Constructor",
                classbody("Example", "static class Nested { Nested() { new Example.Nested$ } }"),
                method("new Example.Nested$ "),
                NESTED_INIT));
        scenarios.add(scenario("Static Nested Parameterized Class Constructor",
                classbody("Example", "static class Nested<T> { Nested(T t) {} }"),
                method("new Example.Nested$ "),
                NESTED_INIT_OBJECT));
        scenarios.add(scenario("Static Nested Object Bounded Parameterized Class Constructor",
                classbody("Example", "static class Nested<T extends Object> { Nested(T t) {} }"),
                method("new Example.Nested$ "),
                NESTED_INIT_OBJECT));
        scenarios.add(ignoredScenario("Static Nested Bounded Parameterized Class Constructor",
                classbody("Example", "static class Nested<N extends Number> { Nested(N n) {} }"),
                method("new Example.Nested$ "),
                NESTED_INIT_NUMBER));
        scenarios.add(scenario(
                "Static Nested Class Constructor With Paramterized Wildcard Argument Bounded By Class Parameter",
                classbody("Example", "static class Nested<N> { Nested(Collection<? extends N> c) {} }"),
                method("new Example.Nested$ "),
                NESTED_INIT_COLLECTION));

        scenarios.add(ignoredScenario("Inner Class Constructor",
                classbody("Example", "class Inner { Inner() {} }"),
                method("new Example.Inner$"),
                INNER_INIT_EXAMPLE));
        scenarios.add(ignoredScenario("Parameterized Inner Class Constructor",
                classbody("Example", "class Inner<T> { Inner(T t) {} }"),
                method("new Example.Inner$"),
                INNER_INIT_EXAMPLE_OBJECT));
        scenarios.add(ignoredScenario("Object Bounded Parameterized Inner Class Constructor",
                classbody("Example", "class Inner<T extends Object> { Inner(T t) {} }"),
                method("new Example.Inner$"),
                INNER_INIT_EXAMPLE_OBJECT));
        scenarios.add(ignoredScenario("Bounded Parameterized Inner Class Constructor",
                classbody("Example", "class Inner<N extends Number> { Inner(N n) {} }"),
                method("new Example.Inner$"),
                INNER_INIT_EXAMPLE_NUMBER));
        scenarios.add(ignoredScenario(
                "Parameterized Inner Class Constructor With Paramterized Wildcard Argument Bounded By Class Parameter",
                classbody("Example", "class Inner<N> { Inner(Collection<? extends N> c) {} }"),
                method("new Example.Inner$"),
                INNER_INIT_EXAMPLE_COLLECTION));

        scenarios.add(scenario("Override Superclass Method Inherited From Interface",
                classbody("Example implements Comparable", "public int compareTo(Object o) {return 0;} "),
                classbody("SubExample extends Example", "compareTo$ "),
                COMPARE_TO_OBJECT));
        scenarios.add(scenario("Override Superclass Method Inherited From Interface, Interface Parameterized By Superclass",
                classbody("Example implements Comparable<Example>", "public int compareTo(Example example) {return 0;} "),
                classbody("SubExample extends Example", "compareTo$ "),
                COMPARE_TO_EXAMPLE));
        scenarios.add(scenario("Override Superclass Method Inherited From Interface, Interface And Superlass Share Parameter",
                classbody("Example<T> implements Comparable<T>", "public int compareTo(Object o) {return 0;} "),
                classbody("SubExample extends Example", "compareTo$ "),
                COMPARE_TO_OBJECT));
        scenarios.add(scenario("Override Superclass Method Inherited From Interface, Interface And Class Share Parameter, Class Parameter Bounded",
                classbody("Example<N extends Number> implements Comparable<N>", "public int compareTo(Object o) {return 0;}"),
                classbody("SubExample extends Example", "compareTo$ "),
                COMPARE_TO_OBJECT));

        scenarios.add(scenario("Method Throws Bounded Parameter of Class",
                classbody("Example<T extends Throwable>", "void method() throws T {}"),
                method("new Example().method$ "),
                METHOD_VOID));
        scenarios.add(scenario("Method With Bounded Parameter Throws Parameter",
                classbody("Example", "<T extends Throwable> void method() throws T {}"),
                method("new Example().method$ "),
                METHOD_VOID));

        scenarios.add(scenario("Overridden Method Call",
                classbody("Example", "int hashCode() {}"),
                method(" new Example().hashcode$ "),
                EXAMPLE_HASH_CODE));
        scenarios.add(scenario("Call Non-Overridden Method Of The Object Class",
                classbody("Example", ""),
                classbody("SubExample extends Example", "void method() { this.hashCode$ }"),
                OBJECT_HASH_CODE));
        scenarios.add(scenario("Call This Classes Implementation Of A Non-Overridden Method",
                classbody("Example", "int hashCode() {}"),
                classbody("SubExample extends Example", "void method() { this.hashCode$ }"),
                EXAMPLE_HASH_CODE));
        scenarios.add(scenario("Call This Classes Implementation Of An Overridden Method",
                classbody("Example", "int hashCode() {}"),
                classbody("SubExample extends Example", "int hashCode() { return 0; } void method() { this.hashCode$ }"),
                SUBEXAMPLE_HASH_CODE));
        scenarios.add(scenario("Super Method Call",
                classbody("Example", "int hashCode() {}"),
                classbody("SubExample extends Example", "int hashCode() { super.hashCode$ } "),
                EXAMPLE_HASH_CODE));
        scenarios.add(scenario("Super Method Call In Anonymous Class",
                classbody("Example", "int hashCode() {}"),
                method("new Example() { int hashCode() { return super.hashCode$ } };"),
                EXAMPLE_HASH_CODE));

        scenarios.add(scenario("Invoke Method Of Object Class on Array",
                classbody("Example", "int hashCode() {}"),
                method("new Example[0].hashCode$"),
                OBJECT_HASH_CODE));

        // // See <https://bugs.eclipse.org/bugs/show_bug.cgi?id=442723>.
        scenarios.add(scenario("Clone Method Invoked On Class",
                classbody("Example", ""),
                classbody("Scenario", "void method() { new Scenario().clone$ }"),
                OBJECT_CLONE));
        scenarios.add(scenario("Clone Method Invoked On New Array",
                classbody("Example", ""),
                method("new Example[0].clone$"),
                OBJECT_CLONE));
        scenarios.add(scenario("Clone Method Invoked On New 2D Array",
                classbody("Example", ""),
                method("new Example[0][0].clone$"),
                OBJECT_CLONE));
        scenarios.add(scenario("Clone Method Invoked on New Array Of A Parameterized Type",
                classbody("Example<T>", "void method() {}"),
                classbody("SubExample<T> extends Example<T>", "void method() { new T[0].clone$ }"),
                OBJECT_CLONE));
        // @formatter:on

        return scenarios;
    }

    private static Object[] scenario(String description, CharSequence exampleCU, CharSequence invokingCU,
            IMethodName expectedMethod) {
        return new Object[] { false, description, exampleCU, invokingCU, expectedMethod };
    }

    /**
     * Ignored scenarios are due to <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=467902">Bug 467902</a>. Once
     * JDT makes the necessary changes, the scenarios can be un-ignored.
     */
    private static Object[] ignoredScenario(String description, CharSequence exampleCU, CharSequence invokingCU,
            IMethodName expectedMethod) {
        return new Object[] { true, description, exampleCU, invokingCU, expectedMethod };
    }

    @Test
    public void testSourceBindings() throws Exception {
        assumeThat(ignore, is(equalTo(false)));

        TemporaryProject dependency = WORKSPACE.createProject();
        dependency.createFile(exampleCode);

        TemporaryProject projectWithSources = WORKSPACE.createProject();
        IRecommendersCompletionContext context = projectWithSources.withDependencyOn(dependency)
                .createFile(scenarioCode).triggerContentAssist();

        Collection<CompletionProposal> proposals = context.getProposals().values();
        IMethodName actualMethod = ProposalUtils.toMethodName(getOnlyElement(proposals)).get();

        assertThat(actualMethod, is(equalTo(expectedMethod)));
    }

    @Test
    public void testBinaryBindings() throws Exception {
        assumeThat(ignore, is(equalTo(false)));

        TemporaryProject dependency = WORKSPACE.createProject();
        dependency.createFile(exampleCode);

        TemporaryProject projectWithSources = WORKSPACE.createProject();
        IRecommendersCompletionContext context = projectWithSources.withDependencyOnClassesOf(dependency)
                .createFile(scenarioCode).triggerContentAssist();

        Collection<CompletionProposal> proposals = context.getProposals().values();
        IMethodName actualMethod = ProposalUtils.toMethodName(getOnlyElement(proposals)).get();

        assertThat(actualMethod, is(equalTo(expectedMethod)));
    }

    @Test
    public void testJarBindings() throws Exception {
        assumeThat(ignore, is(equalTo(false)));

        TemporaryProject dependency = WORKSPACE.createProject();
        dependency.createFile(exampleCode);

        TemporaryProject projectWithSources = WORKSPACE.createProject();
        IRecommendersCompletionContext context = projectWithSources.withDependencyOnJarOf(dependency)
                .createFile(scenarioCode).triggerContentAssist();

        Collection<CompletionProposal> proposals = context.getProposals().values();
        IMethodName actualMethod = ProposalUtils.toMethodName(getOnlyElement(proposals)).get();

        assertThat(actualMethod, is(equalTo(expectedMethod)));
    }
}