/**
 * Copyright (c) 2012 Massimo Zugno
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.completion.rcp.internal.assignments.visitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.recommenders.utils.rcp.JdtUtils;

/**
 * Visit the {@link AST} tree collecting all the expressions which represent an assignment for the specified
 * {@link IType}. An expression is considered an assignment if it is:
 * <ul>
 * <li>a {@link VariableDeclarationStatement} e.g.:
 * <p>
 * <code>Display d = new Display();</code></li>
 * <li>an {@link Assignment} e.g.:
 * <p>
 * <code>d = Display.getDefault();</code></li>
 * <li>either the Else and Then expressions of a {@link ConditionalExpression} e.g.:
 * <p>
 * <code>Display d = Display.getCurrent() != null ? Display.getCurrent() : Display.getDefault();</code></li>
 * </ul>
 */
public class AssignmentExpressionVisitor extends ASTVisitor {

    private List<Expression> expressions = new ArrayList<Expression>();

    private IType lhsType;

    public AssignmentExpressionVisitor(IType lhsType) {
        this.lhsType = lhsType;
    }

    @Override
    public boolean visit(VariableDeclarationFragment node) {
        Expression rhs = node.getInitializer();
        return processExpression(rhs);
    }

    @Override
    public boolean visit(Assignment node) {
        Expression rhs = node.getRightHandSide();
        return processExpression(rhs);
    }

    @Override
    public boolean visit(ConditionalExpression node) {
        processExpression(node.getThenExpression());
        processExpression(node.getElseExpression());
        return false;
    }

    private boolean processExpression(Expression expression) {
        if (expression != null) {
            switch (expression.getNodeType()) {
            case ASTNode.CONDITIONAL_EXPRESSION:
                return true;
            default:
                ITypeBinding rhsTypeBinding = expression.resolveTypeBinding();
                if (rhsTypeBinding != null && rhsTypeBinding.getJavaElement() != null) {
                    IJavaElement rhsJavaElement = rhsTypeBinding.getJavaElement();
                    if (rhsJavaElement instanceof IType) {
                        IType rhsType = (IType) rhsJavaElement;
                        if (JdtUtils.isAssignable(lhsType, rhsType)) {
                            expressions.add(expression);
                        }
                    }
                }
                break;
            }
        }
        return false;
    }

    /**
     * @return the expressions
     */
    public List<Expression> getExpressions() {
        return expressions;
    }

}
