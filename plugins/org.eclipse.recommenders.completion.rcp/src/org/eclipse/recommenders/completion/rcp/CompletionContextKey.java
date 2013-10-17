/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Olav Lenz - initial API and implementation.
 */
package org.eclipse.recommenders.completion.rcp;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.ILocalVariable;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.codeassist.InternalCompletionContext;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.lookup.Scope;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.recommenders.rcp.IAstProvider;
import org.eclipse.recommenders.utils.names.ITypeName;

public class CompletionContextKey<T> {

    @Deprecated
    public static final CompletionContextKey<ASTNode> CCTX_ASSIST_NODE = new CompletionContextKey<ASTNode>();
    @Deprecated
    public static final CompletionContextKey<ASTNode> CCTX_ASSIST_NODE_PARENT = new CompletionContextKey<ASTNode>();
    @Deprecated
    public static final CompletionContextKey<Scope> CCTX_ASSIST_SCOPE = new CompletionContextKey<Scope>();
    @Deprecated
    public static final CompletionContextKey<CompilationUnitDeclaration> CCTX_COMPILATION_UNIT_DECLARATION = new CompletionContextKey<CompilationUnitDeclaration>();

    public static final CompletionContextKey<Boolean> CCTX_COMPLETION_ON_TYPE = new CompletionContextKey<Boolean>();
    public static final CompletionContextKey<String> CCTX_COMPLETION_PREFIX = new CompletionContextKey<String>();
    public static final CompletionContextKey<IType> CCTX_EXPECTED_TYPE = new CompletionContextKey<IType>();
    public static final CompletionContextKey<Set<ITypeName>> CCTX_EXPECTED_TYPENAMES = new CompletionContextKey<Set<ITypeName>>();
    public static final CompletionContextKey<IJavaElement> CCTX_ENCLOSING_ELEMENT = new CompletionContextKey<IJavaElement>();
    public static final CompletionContextKey<IMethod> CCTX_ENCLOSING_METHOD = new CompletionContextKey<IMethod>();
    public static final CompletionContextKey<IType> CCTX_ENCLOSING_TYPE = new CompletionContextKey<IType>();
    public static final CompletionContextKey<?> CCTX_INTERNAL_COMPLETION_CONTEXT = new CompletionContextKey();
    public static final CompletionContextKey<JavaContentAssistInvocationContext> CCTX_JAVA_CONTENTASSIST_CONTEXT = new CompletionContextKey<JavaContentAssistInvocationContext>();
    public static final CompletionContextKey<Map<IJavaCompletionProposal, CompletionProposal>> CCTX_JAVA_PROPOSALS = new CompletionContextKey<Map<IJavaCompletionProposal, CompletionProposal>>();
    public static final CompletionContextKey<TypeBinding> CCTX_RECEIVER_TYPEBINDING = new CompletionContextKey<TypeBinding>();
    public static final CompletionContextKey<String> CCTX_RECEIVER_NAME = new CompletionContextKey<String>();
    public static final CompletionContextKey<List<IMethod>> CCTX_VISIBLE_METHODS = new CompletionContextKey<List<IMethod>>();
    public static final CompletionContextKey<List<IField>> CCTX_VISIBLE_FIELDS = new CompletionContextKey<List<IField>>();
    public static final CompletionContextKey<List<ILocalVariable>> CCTX_VISIBLE_LOCALS = new CompletionContextKey<List<ILocalVariable>>();
    
    public static final CompletionContextKey<InternalCompletionContext> INTERNAL_CONTEXT = new CompletionContextKey<InternalCompletionContext>();
    public static final CompletionContextKey<JavaContentAssistInvocationContext> JAVA_CONTENT_ASSIST_INVOCATION_CONTECT = new CompletionContextKey<JavaContentAssistInvocationContext>();
    public static final CompletionContextKey<IAstProvider> AST_PROVIDER = new CompletionContextKey<IAstProvider>();

}
