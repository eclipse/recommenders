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
package org.eclipse.recommenders.completion.rcp;

import static com.google.common.base.Optional.*;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;
import static org.eclipse.recommenders.internal.completion.rcp.CompletionContextFunctions.*;
import static org.eclipse.recommenders.utils.Checks.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.codeassist.InternalCompletionContext;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnMemberAccess;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.MessageSend;
import org.eclipse.jdt.internal.compiler.lookup.MethodBinding;
import org.eclipse.jdt.internal.compiler.lookup.MissingTypeBinding;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.Region;
import org.eclipse.recommenders.rcp.IAstProvider;
import org.eclipse.recommenders.rcp.utils.CompilerBindings;
import org.eclipse.recommenders.rcp.utils.JdtUtils;
import org.eclipse.recommenders.utils.Nullable;
import org.eclipse.recommenders.utils.names.IMethodName;
import org.eclipse.recommenders.utils.names.ITypeName;
import org.eclipse.recommenders.utils.names.VmTypeName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@SuppressWarnings({ "restriction", "rawtypes", "unchecked" })
public class RecommendersCompletionContext implements IRecommendersCompletionContext {

    private static Logger log = LoggerFactory.getLogger(RecommendersCompletionContext.class);
    private final JavaContentAssistInvocationContext javaContext;
    private final IAstProvider astProvider;

    private Map<String, Object> data = Maps.newHashMap();
    private Map<String, ICompletionContextFunction> functions;

    public RecommendersCompletionContext(final JavaContentAssistInvocationContext jdtContext,
            final IAstProvider astProvider) {
        this(jdtContext, astProvider, Maps.<String, ICompletionContextFunction>newHashMap());
    }

    @Inject
    public RecommendersCompletionContext(final JavaContentAssistInvocationContext jdtContext,
            final IAstProvider astProvider, Map<String, ICompletionContextFunction> functions) {
        javaContext = jdtContext;
        this.astProvider = astProvider;
        this.functions = functions;
    }

    @Override
    public JavaContentAssistInvocationContext getJavaContext() {
        return javaContext;
    }

    @Override
    public IJavaProject getProject() {
        return javaContext.getProject();
    };

    @Override
    public int getInvocationOffset() {
        return javaContext.getInvocationOffset();
    }

    @Override
    public Region getReplacementRange() {
        final int offset = getInvocationOffset();
        final int length = getPrefix().length();
        return new Region(offset, length);
    }

    public Optional<InternalCompletionContext> getCoreContext() {
        return fromNullable(doGetCoreContext());
    }

    private InternalCompletionContext doGetCoreContext() {
        return get(InternalCompletionContext.class, null);
    }

    @Override
    public Optional<IJavaElement> getEnclosingElement() {
        IJavaElement enclosing = get(CCTX_ENCLOSING_ELEMENT, null);
        return fromNullable(enclosing);
    }

    @Override
    public boolean hasEnclosingElement() {
        return getEnclosingElement().isPresent();
    }

    @Override
    public Optional<IMethod> getEnclosingMethod() {
        IMethod enclosing = get(CCTX_ENCLOSING_METHOD, null);
        return fromNullable(enclosing);
    }

    @Override
    public Optional<IType> getEnclosingType() {
        IType enclosing = get(CCTX_ENCLOSING_TYPE, null);
        return fromNullable(enclosing);
    }

    @Override
    public Optional<IType> getClosestEnclosingType() {
        IJavaElement enclosing = get(CCTX_ENCLOSING_ELEMENT, null);
        if (enclosing == null) {
            return absent();
        }
        if (enclosing instanceof IType) {
            return of((IType) enclosing);
        } else {
            final IType type = (IType) enclosing.getAncestor(IJavaElement.TYPE);
            return fromNullable(type);
        }
    }

    @Override
    public boolean isCompletionInMethodBody() {
        return getEnclosingMethod().isPresent();
    }

    @Override
    public boolean isCompletionInTypeBody() {
        return getEnclosingType().isPresent();
    }

    @Override
    public ICompilationUnit getCompilationUnit() {
        return javaContext.getCompilationUnit();
    }

    @Override
    public CompilationUnit getAST() {
        return astProvider.get(getCompilationUnit());
    }

    @Override
    public Map<IJavaCompletionProposal, CompletionProposal> getProposals() {
        return get(CCTX_JAVA_PROPOSALS, Maps.<IJavaCompletionProposal, CompletionProposal>newHashMap());
    }

    @Override
    public Optional<String> getExpectedTypeSignature() {
        InternalCompletionContext coreContext = doGetCoreContext();
        if (coreContext == null) {
            return absent();
        }
        // keys contain '/' instead of dots and may end with ';'
        final char[][] keys = coreContext.getExpectedTypesSignatures();
        if (keys == null) {
            return absent();
        }
        if (keys.length < 1) {
            return absent();
        }
        final String res = new String(keys[0]);
        return of(res);
    }

    @Override
    public Set<ITypeName> getExpectedTypeNames() {
        Set<ITypeName> res = get(CCTX_EXPECTED_TYPENAMES, null);
        return res == null ? Sets.<ITypeName>newHashSet() : res;
    }

    @Override
    public Optional<IType> getExpectedType() {
        IType res = get(CCTX_EXPECTED_TYPE, null);
        return fromNullable(res);
    }

    @Override
    public String getPrefix() {
        return get(CCTX_COMPLETION_PREFIX, "");
    }

    @Override
    public String getReceiverName() {
        return get(CCTX_RECEIVER_NAME, "");
    }

    @Override
    public Optional<String> getReceiverTypeSignature() {
        TypeBinding b = get(CCTX_RECEIVER_TYPEBINDING, null);
        return toString(b);
    }

    private Optional<String> toString(final TypeBinding receiver) {
        if (receiver == null) {
            return absent();
        }
        final String res = new String(receiver.signature());
        return of(res);
    }

    @Override
    public Optional<IType> getReceiverType() {
        TypeBinding b = get(CCTX_RECEIVER_TYPEBINDING, null);
        if (b == null || b instanceof MissingTypeBinding) {
            return absent();
        }
        return JdtUtils.createUnresolvedType(b.erasure());
    }

    @Override
    public Optional<IMethodName> getMethodDef() {
        final ASTNode node = getCompletionNode().orNull();
        if (node == null) {
            return absent();
        }

        if (node instanceof CompletionOnMemberAccess) {
            final CompletionOnMemberAccess n = cast(node);
            if (n.receiver instanceof MessageSend) {
                final MessageSend receiver = (MessageSend) n.receiver;
                final MethodBinding binding = receiver.binding;
                return CompilerBindings.toMethodName(binding);
            }
        }
        return absent();
    }

    @Override
    public Optional<ASTNode> getCompletionNode() {
        InternalCompletionContext ctx = doGetCoreContext();
        ASTNode res = ctx != null ? ctx.getCompletionNode() : null;
        return Optional.fromNullable(res);
    }

    @Override
    public Optional<ASTNode> getCompletionNodeParent() {
        InternalCompletionContext ctx = doGetCoreContext();
        ASTNode res = ctx != null ? ctx.getCompletionNodeParent() : null;
        return Optional.fromNullable(res);
    }

    @Override
    public List<IField> getVisibleFields() {
        return get(CCTX_VISIBLE_FIELDS, Collections.<IField>emptyList());
    }

    @Override
    public List<ILocalVariable> getVisibleLocals() {
        return get(CCTX_VISIBLE_LOCALS, Collections.<ILocalVariable>emptyList());
    }

    @Override
    public List<IMethod> getVisibleMethods() {
        return get(CCTX_VISIBLE_METHODS, Collections.<IMethod>emptyList());
    }

    @Override
    public <T> Optional<T> get(String key) {
        Object res = data.get(key);
        if (res == null) {
            ICompletionContextFunction<?> function = functions.get(key);
            if (function != null) {
                res = function.compute(this, key);
            }
        }
        return fromNullable((T) res);
    }

    @Override
    public <T> Optional<T> get(Class<T> key) {
        return get(key.getName());
    }

    @Override
    public <T> T get(String key, @Nullable T defaultValue) {
        T res = (T) get(key).orNull();
        return res != null ? res : defaultValue;
    }

    @Override
    public <T> T get(Class<T> key, T defaultValue) {
        return get(key.getName(), defaultValue);
    }

    @Override
    public void set(String key, Object value) {
        ensureIsNotNull(key);
        if (value instanceof ICompletionContextFunction) {
            functions.put(key, (ICompletionContextFunction<?>) value);
        } else {
            data.put(key, value);
        }
    }

    @Override
    public <T> void set(Class<T> key, Object value) {
        set(key.getName(), value);
    }

    @Override
    public ImmutableMap<String, Object> values() {
        return ImmutableMap.copyOf(data);
    }

    @VisibleForTesting
    public static Set<ITypeName> createTypeNamesFromSignatures(final char[][] sigs) {
        if (sigs == null) {
            return Collections.emptySet();
        }
        if (sigs.length < 1) {
            return Collections.emptySet();
        }
        Set<ITypeName> res = Sets.newHashSet();
        // JDT signatures contain '.' instead of '/' and may end with ';'
        for (char[] sig : sigs) {
            try {
                String descriptor = new String(sig).replace(".", "/");
                descriptor = substringBeforeLast(descriptor, ";");
                res.add(VmTypeName.get(descriptor));
            } catch (Exception e) {
                // this fails sometimes on method argument completion.
                // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=396595
                log.error("Couldn't parse type name: '" + String.valueOf(sig) + "'", e);
            }
        }
        return res;
    }
}
