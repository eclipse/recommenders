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

@SuppressWarnings("restriction")
public class CompletionContextKey<T> {

    @Deprecated
    public static final CompletionContextKey<ASTNode> ASSIST_NODE = new CompletionContextKey<ASTNode>();
    @Deprecated
    public static final CompletionContextKey<ASTNode> ASSIST_NODE_PARENT = new CompletionContextKey<ASTNode>();
    @Deprecated
    public static final CompletionContextKey<Scope> ASSIST_SCOPE = new CompletionContextKey<Scope>();
    public static final CompletionContextKey<IAstProvider> AST_PROVIDER = new CompletionContextKey<IAstProvider>();

    @Deprecated
    public static final CompletionContextKey<CompilationUnitDeclaration> CCTX_COMPILATION_UNIT_DECLARATION = new CompletionContextKey<CompilationUnitDeclaration>();
    public static final CompletionContextKey<String> COMPLETION_PREFIX = new CompletionContextKey<String>();
    public static final CompletionContextKey<IJavaElement> ENCLOSING_ELEMENT = new CompletionContextKey<IJavaElement>();
    public static final CompletionContextKey<IMethod> ENCLOSING_METHOD = new CompletionContextKey<IMethod>();
    public static final CompletionContextKey<IType> ENCLOSING_TYPE = new CompletionContextKey<IType>();
    public static final CompletionContextKey<IType> EXPECTED_TYPE = new CompletionContextKey<IType>();
    public static final CompletionContextKey<Set<ITypeName>> EXPECTED_TYPENAMES = new CompletionContextKey<Set<ITypeName>>();
    public static final CompletionContextKey<InternalCompletionContext> INTERNAL_COMPLETIONCONTEXT = new CompletionContextKey<InternalCompletionContext>();
    public static final CompletionContextKey<Boolean> IS_COMPLETION_ON_TYPE = new CompletionContextKey<Boolean>();
    public static final CompletionContextKey<JavaContentAssistInvocationContext> JAVA_CONTENT_ASSIST_INVOCATION_CONTECT = new CompletionContextKey<JavaContentAssistInvocationContext>();
    public static final CompletionContextKey<JavaContentAssistInvocationContext> JAVA_CONTENTASSIST_CONTEXT = new CompletionContextKey<JavaContentAssistInvocationContext>();
    public static final CompletionContextKey<Map<IJavaCompletionProposal, CompletionProposal>> JAVA_PROPOSALS = new CompletionContextKey<Map<IJavaCompletionProposal, CompletionProposal>>();
    public static final CompletionContextKey<String> RECEIVER_NAME = new CompletionContextKey<String>();
    public static final CompletionContextKey<TypeBinding> RECEIVER_TYPEBINDING = new CompletionContextKey<TypeBinding>();
    public static final CompletionContextKey<List<IField>> VISIBLE_FIELDS = new CompletionContextKey<List<IField>>();
    public static final CompletionContextKey<List<ILocalVariable>> VISIBLE_LOCALS = new CompletionContextKey<List<ILocalVariable>>();
    public static final CompletionContextKey<List<IMethod>> VISIBLE_METHODS = new CompletionContextKey<List<IMethod>>();

}
