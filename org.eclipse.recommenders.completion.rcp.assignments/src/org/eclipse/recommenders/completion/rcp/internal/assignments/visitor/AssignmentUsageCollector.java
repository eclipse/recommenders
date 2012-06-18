/**
 * Copyright (c) 2012 Massimo Zugno
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.completion.rcp.internal.assignments.visitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;

import com.google.common.base.Joiner;

/**
 * Compute a simple aggregation for assignment expressions of the same kind. For instance, the following assignment
 * expressions:
 * 
 * <pre>
 * Composite parent = new Composite(...);
 * Display d1 = parent.getDisplay();
 * Display d2 = new Composite(parent, SWT.NONE).getDisplay();
 * </pre>
 * 
 * will be collected as 2 occurrences of the same assignment, <code>Widget.getDisplay()</code>
 */
public class AssignmentUsageCollector extends ASTVisitor {

    private class AssignmentFragment {

        private boolean constructor = false;

        private String name;

        private String declaringClass;

        private List<String> parameters = new ArrayList<String>();

    }

    private Map<String, Integer> assignments = new HashMap<String, Integer>();
    private Stack<AssignmentFragment> stack = new Stack<AssignmentFragment>();

    public AssignmentUsageCollector(List<Expression> expressions) {
        for (Expression expression : expressions) {
            expression.accept(this);
            collectExpression();
        }
    }

    @Override
    public boolean visit(ClassInstanceCreation node) {
        IMethodBinding methodBinding = node.resolveConstructorBinding();
        if (methodBinding != null) {
            processMethodBinding(methodBinding);
        }
        return false;
    }

    @Override
    public boolean visit(MethodInvocation node) {
        IMethodBinding methodBinding = node.resolveMethodBinding();
        if (methodBinding != null) {
            processMethodBinding(methodBinding);
        }
        // continue visiting for chained method calls
        return true;
    }

    /**
     * @param methodBinding
     * @param builder
     */
    private void processMethodBinding(IMethodBinding methodBinding) {
        AssignmentFragment fragment = new AssignmentFragment();
        fragment.name = methodBinding.getJavaElement().getElementName();
        fragment.constructor = methodBinding.isConstructor();
        fragment.declaringClass = methodBinding.getDeclaringClass().getName();
        // compute method parameters
        ITypeBinding[] parameterTypes = methodBinding.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            ITypeBinding parameterType = parameterTypes[i];
            fragment.parameters.add(parameterType.getName());
        }
        stack.push(fragment);
    }

    private void collectExpression() {
        if (stack.isEmpty()) {
            // do nothing
            return;
        }
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        while (!stack.isEmpty()) {
            AssignmentFragment fragment = stack.pop();
            boolean isConstructor = fragment.constructor;
            boolean hasNext = !stack.isEmpty();
            if (isConstructor) {
                // ignore a constructor if it's not the only eleement in stack
                // e.g.: "new Composite().getDisplay()" becomes "Widget.getDisplay()"
                if (hasNext) {
                    continue;
                }
                sb.append("new ");
            } else {
                if (isFirst) {
                    // the first element qualifies the assignment,
                    // e.g.: "parent.getDisplay()" becomes "Widget.getDisplay()"
                    sb.append(fragment.declaringClass);
                    isFirst = false;
                }
                sb.append(".");
            }
            sb.append(fragment.name);
            sb.append("(");
            // compute comma-separated parameter list
            String params = Joiner.on(", ").join(fragment.parameters);
            sb.append(params);
            sb.append(")");
        }
        String assignment = sb.toString();
        Integer occurrences = assignments.get(assignment);
        if (occurrences == null) {
            occurrences = 0;
        }
        assignments.put(assignment, ++occurrences);
        stack.clear();
    }

    public Map<String, Integer> getAssignments() {
        return assignments;
    }

}