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

import static com.google.common.base.Objects.firstNonNull;
import static org.apache.commons.lang3.StringUtils.substring;
import static org.eclipse.recommenders.utils.Checks.cast;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.mutable.MutableObject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.CompletionContext;
import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.CompletionRequestor;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.codeassist.InternalCompletionContext;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnLocalName;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnMemberAccess;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnQualifiedAllocationExpression;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnQualifiedNameReference;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnSingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.ast.ThisReference;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import org.eclipse.jdt.internal.compiler.lookup.LocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.util.ObjectVector;
import org.eclipse.jdt.internal.core.JavaElement;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.recommenders.completion.rcp.ICompletionContextFunction;
import org.eclipse.recommenders.completion.rcp.IRecommendersCompletionContext;
import org.eclipse.recommenders.completion.rcp.RecommendersCompletionContext;
import org.eclipse.recommenders.internal.rcp.RcpPlugin;
import org.eclipse.recommenders.rcp.utils.JdtUtils;
import org.eclipse.recommenders.utils.names.ITypeName;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

@SuppressWarnings("restriction")
public class CompletionContextFunctions {

    public static final String CCTX_EXPECTED_TYPE = "expected-type";
    public static final String CCTX_EXPECTED_TYPENAMES = "expected-type-names";
    public static final String CCTX_ENCLOSING_ELEMENT = "enclosing-element";
    public static final String CCTX_ENCLOSING_METHOD = "enclosing-method";
    public static final String CCTX_ENCLOSING_TYPE = "enclosing-type";
    public static final String CCTX_INTERNAL_COMPLETION_CONTEXT = InternalCompletionContext.class.getName();
    public static final String CCTX_JAVA_CONTENT_ASSIST_CONTEXT = JavaContentAssistInvocationContext.class.getName();
    public static final String CCTX_RECEIVER_TYPEBINDING = "receiver-typebinding";
    public static final String CCTX_RECEIVER_NAME = "receiver-name";
    public static final String CCTX_VISIBLE_METHODS = "visible-methods";
    public static final String CCTX_VISIBLE_FIELDS = "visible-fields";
    public static final String CCTX_VISIBLE_LOCALS = "visible-locals";

    public static class EnclosingElementContextFunction implements ICompletionContextFunction<IJavaElement> {

        @Override
        public IJavaElement compute(IRecommendersCompletionContext context, String key) {
            IJavaElement res = null;
            try {
                InternalCompletionContext core = context.get(InternalCompletionContext.class, null);
                if (core != null && core.isExtended()) {
                    res = core.getEnclosingElement();
                }
            } catch (Exception e) {
                // IAE thrown by JDT if it fails to parse the signature.
                // we silently ignore that and return nothing instead.
            }
            context.set(key, res);
            return res;
        }
    }

    public static class EnclosingMethodContextFunction implements ICompletionContextFunction<IMethod> {

        @Override
        public IMethod compute(IRecommendersCompletionContext context, String key) {
            IJavaElement enclosing = context.get(CCTX_ENCLOSING_ELEMENT, null);
            IMethod res = (IMethod) (enclosing instanceof IMethod ? enclosing : null);
            context.set(key, res);
            return res;
        }
    }

    public static class EnclosingTypeContextFunction implements ICompletionContextFunction<IType> {

        @Override
        public IType compute(IRecommendersCompletionContext context, String key) {
            IJavaElement enclosing = context.get(CCTX_ENCLOSING_ELEMENT, null);
            IType res = null;
            if (enclosing instanceof IType) {
                res = (IType) enclosing;
            } else if (enclosing instanceof IField) {
                res = ((IField) enclosing).getDeclaringType();
            } else {
                // res = null
            }
            context.set(key, res);
            return res;
        }
    }

    public static class ExpectedTypeContextFunction implements ICompletionContextFunction<IType> {

        @Override
        public IType compute(IRecommendersCompletionContext context, String key) {
            JavaContentAssistInvocationContext ctx = context.get(JavaContentAssistInvocationContext.class, null);
            IType res = ctx == null ? null : ctx.getExpectedType();
            context.set(key, res);
            return res;
        }
    }

    public static class ExpectedTypeNamesContextFunction implements ICompletionContextFunction<Set<ITypeName>> {

        @Override
        public Set<ITypeName> compute(IRecommendersCompletionContext context, String key) {
            ASTNode completion = context.getCompletionNode().orNull();
            InternalCompletionContext core = context.get(InternalCompletionContext.class, null);

            char[][] keys;
            if (isArgumentCompletion(completion) && context.getPrefix().isEmpty()) {
                ICompilationUnit cu = context.getCompilationUnit();
                int offset = context.getInvocationOffset();
                keys = simulateCompletionWithFakePrefix(cu, offset);
            } else {
                keys = core.getExpectedTypesSignatures();
            }
            Set<ITypeName> res = RecommendersCompletionContext.createTypeNamesFromSignatures(keys);
            context.set(key, res);
            return res;
        }

        private boolean isArgumentCompletion(ASTNode completion) {
            return completion instanceof MessageSend || completion instanceof CompletionOnQualifiedAllocationExpression;
        }

        private char[][] simulateCompletionWithFakePrefix(ICompilationUnit cu, int offset) {
            final MutableObject<char[][]> res = new MutableObject<char[][]>(null);
            ICompilationUnit wc = null;
            String fakePrefix = "___x";
            try {
                wc = cu.getWorkingCopy(new NullProgressMonitor());
                IBuffer buffer = wc.getBuffer();
                String contents = buffer.getContents();
                String newContents = substring(contents, 0, offset) + fakePrefix
                        + substring(contents, offset, contents.length());
                buffer.setContents(newContents);
                wc.codeComplete(offset + 1, new CompletionRequestor(true) {

                    @Override
                    public boolean isExtendedContextRequired() {
                        return true;
                    }

                    @Override
                    public void acceptContext(CompletionContext context) {
                        res.setValue(context.getExpectedTypesKeys());
                        super.acceptContext(context);
                    }

                    @Override
                    public void accept(CompletionProposal proposal) {
                    }
                });
            } catch (JavaModelException x) {
                RcpPlugin.log(x);
            } finally {
                discardWorkingCopy(wc);
            }
            return res.getValue();
        }

        private void discardWorkingCopy(ICompilationUnit wc) {
            try {
                if (wc != null) {
                    wc.discardWorkingCopy();
                }
            } catch (JavaModelException x) {
                RcpPlugin.log(x);
            }
        }

    }

    public static class ReceiverTypeBindingContextFunction implements ICompletionContextFunction<TypeBinding> {

        @Override
        public TypeBinding compute(IRecommendersCompletionContext context, String key) {
            final ASTNode n = context.getCompletionNode().orNull();
            if (n == null) {
                return null;
            }
            TypeBinding receiver = null;
            if (n instanceof CompletionOnLocalName) {
                // final CompletionOnLocalName c = cast(n);
                // name = c.realName;
            } else if (n instanceof CompletionOnSingleNameReference) {
                final CompletionOnSingleNameReference c = cast(n);
                receiver = c.resolvedType;
            } else if (n instanceof CompletionOnQualifiedNameReference) {
                final CompletionOnQualifiedNameReference c = cast(n);
                switch (c.binding.kind()) {
                case Binding.VARIABLE:
                case Binding.FIELD:
                case Binding.LOCAL:
                    final VariableBinding varBinding = (VariableBinding) c.binding;
                    receiver = varBinding.type;
                    break;
                case Binding.TYPE:
                case Binding.GENERIC_TYPE:
                    // e.g. Class.|<ctrl-space>
                    receiver = (TypeBinding) c.binding;
                    break;
                default:
                }
            } else if (n instanceof CompletionOnMemberAccess) {
                final CompletionOnMemberAccess c = cast(n);
                receiver = c.actualReceiverType;
            }
            context.set(key, receiver);
            return receiver;
        }
    }

    public static class ReceiverNameContextFunction implements ICompletionContextFunction<String> {

        @Override
        public String compute(IRecommendersCompletionContext context, String key) {
            final ASTNode n = context.getCompletionNode().orNull();
            if (n == null) {
                return "";
            }

            char[] name = null;
            if (n instanceof CompletionOnQualifiedNameReference) {
                final CompletionOnQualifiedNameReference c = cast(n);
                switch (c.binding.kind()) {
                case Binding.VARIABLE:
                case Binding.FIELD:
                case Binding.LOCAL:
                    final VariableBinding b = (VariableBinding) c.binding;
                    name = b.name;
                    break;
                }
            } else if (n instanceof CompletionOnLocalName) {
                final CompletionOnLocalName c = cast(n);
                name = c.realName;
            } else if (n instanceof CompletionOnSingleNameReference) {
                final CompletionOnSingleNameReference c = cast(n);
                name = c.token;
            } else if (n instanceof CompletionOnMemberAccess) {
                final CompletionOnMemberAccess c = cast(n);
                if (c.receiver instanceof ThisReference) {
                    name = "this".toCharArray();
                } else if (c.receiver instanceof MessageSend) {
                    // some anonymous type/method return value that has no name...
                    // e.g.:
                    // PlatformUI.getWorkbench()|^Space --> receiver is anonymous
                    // --> name = null
                    name = null;
                } else if (c.fieldBinding() != null) {
                    // does this happen? When?
                    name = c.fieldBinding().name;
                } else if (c.localVariableBinding() != null) {
                    // does this happen? when?
                    name = c.localVariableBinding().name;
                }
            }
            String res = new String(firstNonNull(name, new char[0]));
            res = res.replace(" ", "");
            context.set(key, res);
            return res;
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

    public static class JavaContentAssistInvocationContextFunction implements
            ICompletionContextFunction<JavaContentAssistInvocationContext> {

        @Override
        public JavaContentAssistInvocationContext compute(IRecommendersCompletionContext context, String key) {
            JavaContentAssistInvocationContext res = context.getJavaContext();
            context.set(key, res);
            return res;
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
