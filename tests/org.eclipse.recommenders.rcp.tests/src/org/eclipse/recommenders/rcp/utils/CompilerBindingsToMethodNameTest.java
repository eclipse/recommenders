/**
 * Copyright (c) 2014 Codetrails GmbH
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andreas Sewe - Initial API and implementation
 */
package org.eclipse.recommenders.rcp.utils;

import static org.eclipse.recommenders.testing.CodeBuilder.*;
import static org.eclipse.recommenders.utils.names.VmMethodName.get;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.recommenders.testing.jdt.JavaProjectFixture;
import org.eclipse.recommenders.utils.Pair;
import org.eclipse.recommenders.utils.names.IMethodName;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
@SuppressWarnings("restriction")
public class CompilerBindingsToMethodNameTest {

    private static final Method GET_BINDING_RESOLVER = getDeclaredMethod(AST.class, "getBindingResolver");
    private static final Method GET_CORRESPONDING_NODE = getDeclaredMethod(GET_BINDING_RESOLVER.getReturnType(),
            "getCorrespondingNode", ASTNode.class);

    private final CharSequence code;
    private final IMethodName expected;

    public CompilerBindingsToMethodNameTest(String description, CharSequence code, IMethodName expected) {
        this.code = code;
        this.expected = expected;
    }

    @Parameters(name = "{index}: {0}")
    public static Collection<Object[]> scenarios() {
        LinkedList<Object[]> scenarios = Lists.newLinkedList();

        // Real-world test cases
        scenarios.add(scenario("No arguments, void return type", method("this.$wait();"),
                get("Ljava/lang/Object.wait()V")));
        scenarios.add(scenario("Primitive argument, void return type", method("this.$wait(0L);"),
                get("Ljava/lang/Object.wait(J)V")));
        scenarios.add(scenario("Reference argument, void return type", method("System.out.$println(\"\");"),
                get("Ljava/io/PrintStream.println(Ljava/lang/String;)V")));

        scenarios.add(scenario("No arguments, primitive return type", method("this.$hashCode();"),
                get("Ljava/lang/Object.hashCode()I")));
        scenarios.add(scenario("No arguments, reference return type", method("this.$toString();"),
                get("Ljava/lang/Object.toString()Ljava/lang/String;")));

        // Synthetic test cases
        scenarios.add(scenario("Argument is array of primitives",
                classbody("Example", "void method(int[] is) { this.$method(null); }"), get("LExample.method([I)V")));
        scenarios.add(scenario("Argument is array of objects",
                classbody("Example", "void method(Object[] os) { this.$method(null); }"),
                get("LExample.method([Ljava/lang/Object;)V")));
        scenarios.add(scenario("Argument is array of arrays of objects",
                classbody("Example", "void method(Object[][] os) { this.$method(null); }"),
                get("LExample.method([[Ljava/lang/Object;)V")));

        scenarios.add(scenario("Argument is parameterized type (invariant)",
                classbody("Example", "void method(Collection<Number> c) { this.$method(null); }"),
                get("LExample.method(Ljava/util/Collection;)V")));
        scenarios.add(scenario("Argument is parameterized type (wildcard)",
                classbody("Example", "void method(Collection<?> c) { this.$method(null); }"),
                get("LExample.method(Ljava/util/Collection;)V")));
        scenarios.add(scenario("Argument is parameterized type (upper-bounded wildcard)",
                classbody("Example", "void method(Collection<? extends Number> c) { this.$method(null); }"),
                get("LExample.method(Ljava/util/Collection;)V")));
        scenarios.add(scenario("Argument is parameterized type (lower-bounded wildcard)",
                classbody("Example", "void method(Collection<? super Number> c) { this.$method(null); }"),
                get("LExample.method(Ljava/util/Collection;)V")));

        scenarios.add(scenario("Argument is type parameter of declaring class",
                classbody("Example<T>", "void method(T t) { this.$method(null); }"),
                get("LExample.method(Ljava/lang/Object;)V")));
        scenarios.add(scenario("Argument is upper-bounded type parameter of declaring class",
                classbody("Example<N extends Number>", "void method(N n) { this.$method(null); }"),
                get("LExample.method(Ljava/lang/Number;)V")));
        scenarios.add(scenario(
                "Argument is upper-bounded type parameter of declaring class (superfluous bound of Object)",
                classbody("Example<O extends Object>", "void method(O o) { this.$method(null); }"),
                get("LExample.method(Ljava/lang/Object;)V")));
        scenarios.add(scenario("Argument is upper-bounded type parameter of declaring class (multiple bounds)",
                classbody("Example<N extends Number & Comparable>", "void method(N n) { this.$method(null); }"),
                get("LExample.method(Ljava/lang/Number;)V")));
        scenarios.add(scenario(
                "Argument is upper-bounded type parameter of declaring class (bound is parameterized itself)",
                classbody("Example<L extends List<String>>", "void method(L l) { this.$method(null); }"),
                get("LExample.method(Ljava/util/List;)V")));

        scenarios.add(scenario("Argument is type parameter of method",
                classbody("Example", "<T> void method(T t) { this.$method(null); }"),
                get("LExample.method(Ljava/lang/Object;)V")));
        scenarios.add(scenario("Argument is upper-bounded type parameter of method",
                classbody("Example", "<N extends Number> void method(N n) { this.$method(null); }"),
                get("LExample.method(Ljava/lang/Number;)V")));
        scenarios.add(scenario("Argument is upper-bounded type parameter of method (superfluous bound of Object)",
                classbody("Example", "<O extends Object> void method(O o) { this.$method(null); }"),
                get("LExample.method(Ljava/lang/Object;)V")));
        scenarios.add(scenario("Argument is upper-bounded type parameter of method (multiple bounds)",
                classbody("Example", "<N extends Number & Comparable> void method(N n) { this.$method(null); }"),
                get("LExample.method(Ljava/lang/Number;)V")));
        scenarios.add(scenario("Argument is upper-bounded type parameter of method (bound is parameterized itself)",
                classbody("Example", "<L extends List<String>> void method(L l) { this.$method(null); }"),
                get("LExample.method(Ljava/util/List;)V")));

        scenarios
                .add(scenario(
                        "Argument is type parameter of implemented interface",
                        classbody("Example implements Comparable<Number>",
                                "int compareTo(Number n) { this.$compareTo(null); }"),
                        get("Ljava/lang/Comparable.compareTo(Ljava/lang/Number;)I")));

        return scenarios;
    }

    private static Object[] scenario(String description, CharSequence code, IMethodName expected) {
        return new Object[] { description, code, expected };
    }

    @Test
    public void test() throws Exception {
        MethodBinding binding = getBinding(code);

        IMethodName actual = CompilerBindings.toMethodName(binding).get();

        assertThat(actual, is(equalTo(expected)));
    }

    private static MethodBinding getBinding(CharSequence code) throws Exception {
        return getBinding(getCompilerAstNode(code));
    }

    private static MethodBinding getBinding(org.eclipse.jdt.internal.compiler.ast.ASTNode node) {
        if (node instanceof MessageSend) {
            return ((MessageSend) node).binding;
        } else {
            throw new IllegalArgumentException(node.toString());
        }
    }

    private static org.eclipse.jdt.internal.compiler.ast.ASTNode getCompilerAstNode(CharSequence code) throws Exception {
        JavaProjectFixture fixture = new JavaProjectFixture(ResourcesPlugin.getWorkspace(), "test");
        Pair<CompilationUnit, Set<Integer>> parseResult = fixture.parseWithMarkers(code.toString());
        CompilationUnit cu = parseResult.getFirst();
        int start = Iterables.getOnlyElement(parseResult.getSecond());

        AST ast = cu.getAST();
        ASTNode node = NodeFinder.perform(cu, start, 0);
        return getCorrespondingNode(ast, node);
    }

    private static org.eclipse.jdt.internal.compiler.ast.ASTNode getCorrespondingNode(AST ast, ASTNode domNode)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object bindingResolver = GET_BINDING_RESOLVER.invoke(ast);
        return (org.eclipse.jdt.internal.compiler.ast.ASTNode) GET_CORRESPONDING_NODE.invoke(bindingResolver, domNode);
    }

    private static Method getDeclaredMethod(Class<?> declaringClass, String name, Class<?>... parameterTypes) {
        try {
            Method method = declaringClass.getDeclaredMethod(name, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }
}
