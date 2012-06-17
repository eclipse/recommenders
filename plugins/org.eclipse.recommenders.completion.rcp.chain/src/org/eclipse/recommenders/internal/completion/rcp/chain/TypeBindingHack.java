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

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.corext.dom.GenericVisitor;
import org.eclipse.recommenders.completion.rcp.IRecommendersCompletionContext;

@SuppressWarnings("restriction")
public final class TypeBindingHack {

    private TypeBindingHack() {
    }

    public static TypeBinding resolveBindingForExpectedType(final IRecommendersCompletionContext ctx) {
        final String expected = String.valueOf(ctx.getJavaContext().getCoreContext().getExpectedTypesKeys()[0]);
        final Visitor visitor = new Visitor(expected);
        ctx.getAST().accept(visitor);
        try {
            final Class clazz = Class.forName("org.eclipse.jdt.core.dom.TypeBinding");
            final Field field = clazz.getDeclaredField("binding");
            field.setAccessible(true);
            return (TypeBinding) field.get(visitor.expectedType);
        } catch (final Exception e) {
            throw new IllegalStateException(e);
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
