/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.completion.rcp;

import static org.eclipse.recommenders.utils.Checks.cast;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.codeassist.InternalCompletionContext;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.util.ObjectVector;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.recommenders.completion.rcp.ICompletionContextFunction;
import org.eclipse.recommenders.completion.rcp.IRecommendersCompletionContext;
import org.eclipse.recommenders.completion.rcp.RecommendersCompletionContext;
import org.eclipse.recommenders.rcp.utils.JdtUtils;
import org.eclipse.recommenders.utils.names.ITypeName;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

@SuppressWarnings("restriction")
public class CompletionContextFunctions {

    public static final String CCTX_INTERNAL_COMPLETION_CONTEXT = InternalCompletionContext.class.getName();
    public static final String CCTX_VISIBLE_METHODS = "visible-methods";
    public static final String CCTX_VISIBLE_FIELDS = "visible-fields";
    public static final String CCTX_VISIBLE_LOCALS = "visible-locals";

    public static class ExpectedTypeContextFunction implements ICompletionContextFunction<IType> {

        @Override
        public IType compute(IRecommendersCompletionContext context, String key) {
            IType value = context.getExpectedType().orNull();
            context.set(key, value);
            return value;
        }
    }

    public static class ExpectedTypeNamesContextFunction implements ICompletionContextFunction<Set<ITypeName>> {

        @Override
        public Set<ITypeName> compute(IRecommendersCompletionContext context, String key) {
            Set<ITypeName> value = context.getExpectedTypeNames();
            context.set(key, value);
            return value;
        }
    }

    public static class InternalCompletionContextFunction implements
            ICompletionContextFunction<InternalCompletionContext> {

        @Override
        public InternalCompletionContext compute(IRecommendersCompletionContext context, String key) {
            InternalCompletionContext ctx = ((RecommendersCompletionContext) context).getCoreContext().orNull();
            context.set(key, ctx);
            return ctx;
        }
    }

    public static class VisibleMethodsContextFunction implements ICompletionContextFunction<List<IMethod>> {

        @Override
        public List<IMethod> compute(IRecommendersCompletionContext context, String key) {
            InternalCompletionContext ctx = context.get(InternalCompletionContext.class, null);
            if (ctx == null || !ctx.isExtended()) {
                return Collections.emptyList();
            }
            final ObjectVector v = ctx.getVisibleMethods();
            final List<IMethod> res = Lists.newArrayListWithCapacity(v.size);
            for (int i = v.size(); i-- > 0;) {
                final MethodBinding b = cast(v.elementAt(i));
                final Optional<IMethod> f = JdtUtils.createUnresolvedMethod(b);
                if (f.isPresent()) {
                    res.add(f.get());
                }
            }
            context.set(key, res);
            return res;
        }
    }

    public static class VisibleFieldsContextFunction implements ICompletionContextFunction<List<IField>> {

        @Override
        public List<IField> compute(IRecommendersCompletionContext context, String key) {
            InternalCompletionContext ctx = context.get(InternalCompletionContext.class, null);
            if (ctx == null || !ctx.isExtended()) {
                return Collections.emptyList();
            }
            final ObjectVector v = ctx.getVisibleFields();
            final List<IField> res = Lists.newArrayListWithCapacity(v.size);
            for (int i = v.size(); i-- > 0;) {
                final FieldBinding b = cast(v.elementAt(i));
                final Optional<IField> f = JdtUtils.createUnresolvedField(b);
                if (f.isPresent()) {
                    res.add(f.get());
                }
            }
            context.set(key, res);
            return res;
        }
    }

    public static class VisibleLocalsContextFunction implements ICompletionContextFunction<List<ILocalVariable>> {

        @Override
        public List<ILocalVariable> compute(IRecommendersCompletionContext context, String key) {
            InternalCompletionContext ctx = context.get(InternalCompletionContext.class, null);
            if (ctx == null || !ctx.isExtended()) {
                return Collections.emptyList();
            }
            final ObjectVector v = ctx.getVisibleLocalVariables();
            final List<ILocalVariable> res = Lists.newArrayListWithCapacity(v.size);
            for (int i = v.size(); i-- > 0;) {
                final LocalVariableBinding b = cast(v.elementAt(i));
                final JavaElement parent = (JavaElement) context.getEnclosingElement().get();
                final ILocalVariable f = JdtUtils.createUnresolvedLocaVariable(b, parent);
                res.add(f);
            }
            context.set(key, res);
            return res;
        }
    }
}
