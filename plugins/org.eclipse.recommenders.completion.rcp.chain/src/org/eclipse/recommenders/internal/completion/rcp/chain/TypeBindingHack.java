/**
 * Copyright (c) 2011 Stefan Henss.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Henß - initial API and implementation.
 */
package org.eclipse.recommenders.internal.completion.rcp.chain;

import java.lang.reflect.Field;

import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.internal.codeassist.InternalCompletionContext;
import org.eclipse.jdt.internal.codeassist.InternalExtendedCompletionContext;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.BlockScope;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.MethodScope;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.corext.dom.GenericVisitor;
import org.eclipse.recommenders.completion.rcp.IRecommendersCompletionContext;

@SuppressWarnings("restriction")
public final class TypeBindingHack {

    private static Field field;
    private static Field contextField;
    private static Field unitField;
    private static Field scopeField;

    static {
        try {
            field = Class.forName("org.eclipse.jdt.core.dom.TypeBinding").getDeclaredField("binding");
            field.setAccessible(true);
            contextField = Class.forName("org.eclipse.jdt.internal.codeassist.InternalCompletionContext")
                    .getDeclaredField("extendedContext");
            contextField.setAccessible(true);
            unitField = Class.forName("org.eclipse.jdt.internal.codeassist.InternalExtendedCompletionContext")
                    .getDeclaredField("compilationUnitDeclaration");
            unitField.setAccessible(true);
            scopeField = Class.forName("org.eclipse.jdt.internal.codeassist.InternalExtendedCompletionContext")
                    .getDeclaredField("assistScope");
            scopeField.setAccessible(true);
        } catch (final Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static TypeBinding resolveBindingForExpectedType(final IRecommendersCompletionContext ctx) {
        final InternalCompletionContext context = (InternalCompletionContext) ctx.getJavaContext().getCoreContext();
        try {
            final InternalExtendedCompletionContext extendedContext = (InternalExtendedCompletionContext) contextField
                    .get(context);
            final Scope scope = (Scope) scopeField.get(extendedContext);
            final CompilationUnitDeclaration unit = (CompilationUnitDeclaration) unitField.get(extendedContext);

            final String expected = String.valueOf(ctx.getJavaContext().getCoreContext().getExpectedTypesKeys()[0]);
            final NewVisitor visitor = new NewVisitor(expected);
            unit.traverse(visitor, scope.compilationUnitScope());
            // TODO: Find a way to make sure to catch every type occurence in the AST.
            if (visitor.expectedType != null) {
                return visitor.expectedType;
            }
            // TODO: This is another alternative for obtaining bindings, but they won't be '=='.
            if (!true) {
                final char[][] bla = CharOperation.splitOn('/', expected.substring(1).replaceFirst("[<;]+.*", "")
                        .toCharArray());
                return scope.getType(bla, bla.length);
            }
            return resolveBindingKey(expected, ctx.getAST());
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    // TODO: Just a backup in case the proper mechanism still fails to resolve a binding.
    private static TypeBinding resolveBindingKey(final String expected, final CompilationUnit compUnit) {
        final Visitor visitor = new Visitor(expected);
        compUnit.accept(visitor);
        try {
            return (TypeBinding) field.get(visitor.expectedType);
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private static class NewVisitor extends ASTVisitor {

        private final String expected;
        private TypeBinding expectedType;

        public NewVisitor(final String expected) {
            this.expected = expected;
        }

        @Override
        public boolean visit(final FieldDeclaration fieldDeclaration, final MethodScope scope) {
            return !accept(fieldDeclaration.type.resolvedType);
        }

        @Override
        public boolean visit(final MethodDeclaration methodDeclaration, final ClassScope scope) {
            return !accept(methodDeclaration.returnType.resolvedType);
        }

        @Override
        public boolean visit(final LocalDeclaration assignment, final BlockScope scope) {
            return !accept(assignment.type.resolvedType);
        }

        private boolean accept(final TypeBinding type) {
            final String key = String.valueOf(type.computeUniqueKey());
            if (key.equals(expected)) {
                expectedType = type;
                return false;
            }
            return true;
        }

    }

    private static class Visitor extends GenericVisitor {

        private final String expected;
        private ITypeBinding expectedType;

        public Visitor(final String expected) {
            super(false);
            this.expected = expected;
        }

        @Override
        protected boolean visitNode(final ASTNode node) {
            if (node instanceof Type) {
                final ITypeBinding candidate = ((Type) node).resolveBinding();
                if (candidate.getKey().equals(expected)) {
                    expectedType = candidate;
                }
            }
            return expectedType == null;
        }

    }

}
