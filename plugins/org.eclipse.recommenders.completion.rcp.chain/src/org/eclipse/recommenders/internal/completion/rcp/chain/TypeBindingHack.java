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

import org.eclipse.jdt.internal.codeassist.InternalCompletionContext;
import org.eclipse.jdt.internal.codeassist.InternalExtendedCompletionContext;
import org.eclipse.jdt.internal.compiler.ASTVisitor;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.Assignment;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.FieldDeclaration;
import org.eclipse.jdt.internal.compiler.ast.LocalDeclaration;
import org.eclipse.jdt.internal.compiler.ast.MethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ReturnStatement;
import org.eclipse.jdt.internal.compiler.lookup.ClassScope;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
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
        final ASTNode parent = context.getCompletionNodeParent();
        if (parent instanceof LocalDeclaration) {
            return ((LocalDeclaration) parent).type.resolvedType;
        } else if (parent instanceof ReturnStatement) {
            // TODO: This should be done properly as soon as possible.
            return searchAST(context);
        } else if (parent instanceof FieldDeclaration) {
            return ((FieldDeclaration) parent).type.resolvedType;
        } else if (parent instanceof Assignment) {
            return ((Assignment) parent).resolvedType;
        } else {
            throw new IllegalStateException("not found: " + parent.getClass());
        }
    }

    private static TypeBinding searchAST(final InternalCompletionContext context) {
        try {
            final InternalExtendedCompletionContext extendedContext = (InternalExtendedCompletionContext) contextField
                    .get(context);
            final Scope scope = (Scope) scopeField.get(extendedContext);
            final CompilationUnitDeclaration unit = (CompilationUnitDeclaration) unitField.get(extendedContext);
            final String expected = String.valueOf(context.getExpectedTypesKeys()[0]);
            final Visitor visitor = new Visitor(expected);
            unit.traverse(visitor, scope.compilationUnitScope());
            return visitor.expectedType;
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private static class Visitor extends ASTVisitor {

        private final String expected;
        private TypeBinding expectedType;

        public Visitor(final String expected) {
            this.expected = expected;
        }

        @Override
        public boolean visit(final MethodDeclaration methodDeclaration, final ClassScope scope) {
            final TypeBinding type = methodDeclaration.returnType.resolvedType;
            final String key = String.valueOf(type.computeUniqueKey());
            if (key.equals(expected)) {
                expectedType = type;
                return false;
            }
            return true;
        }

    }

}
