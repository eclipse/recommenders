/**
 * Copyright (c) 2012 Massimo Zugno
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.completion.rcp.assignments;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.recommenders.completion.rcp.internal.assignments.visitor.AssignmentExpressionVisitor;
import org.eclipse.recommenders.utils.rcp.internal.RecommendersUtilsPlugin;

/**
 * Analyze each {@link ICompilationUnit} in the current workspace, searching for all the {@link Expression}s which
 * represent an assignment to a particular class.
 */
public class AssignmentExpressionAnalyzer {

    private List<Expression> expressions = new ArrayList<Expression>();
    private String className;
    private IType lhsType;

    public AssignmentExpressionAnalyzer(String className) {
        this.className = className;
    }

    public List<Expression> analyze() {
        return analyze(new NullProgressMonitor());
    }

    /**
     * Performs the actual expression collection stepping through workspace projects, packages and eventually
     * compilation units.
     * 
     * @param monitor
     * @return
     */
    public List<Expression> analyze(IProgressMonitor monitor) {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        IWorkspaceRoot root = workspace.getRoot();
        IProject[] projects = root.getProjects();
        for (IProject project : projects) {
            analyzeProject(project, monitor);
        }
        return expressions;

    }

    /**
     * @param project
     * @param monitor
     */
    private void analyzeProject(IProject project, IProgressMonitor monitor) {
        IJavaProject javaProject = (IJavaProject) project.getAdapter(IJavaElement.class);
        if (javaProject == null || !javaProject.isOpen()) {
            return;
        }
        try {
            lhsType = javaProject.findType(className);
            if (lhsType != null) {
                IPackageFragment[] fragments = javaProject.getPackageFragments();
                for (IPackageFragment fragment : fragments) {
                    analyzePackageFragment(fragment, monitor);
                }
            }
        } catch (JavaModelException e) {
            RecommendersUtilsPlugin.logError(e, e.getMessage());
        }
    }

    /**
     * @param fragment
     * @param monitor
     */
    private void analyzePackageFragment(IPackageFragment fragment, IProgressMonitor monitor) {
        try {
            ICompilationUnit[] units = fragment.getCompilationUnits();
            for (ICompilationUnit unit : units) {
                if (!monitor.isCanceled()) {
                    analyzeCompilationUnit(unit, monitor);
                }
            }
        } catch (JavaModelException e) {
            RecommendersUtilsPlugin.logError(e, e.getMessage());
        }
    }

    /**
     * @param compilationUnit
     * @param monitor
     */
    private void analyzeCompilationUnit(ICompilationUnit compilationUnit, IProgressMonitor monitor) {
        monitor.subTask(compilationUnit.getElementName());
        AssignmentExpressionVisitor visitor = new AssignmentExpressionVisitor(lhsType);
        ASTNode ast = createParser(compilationUnit);
        ast.accept(visitor);
        expressions.addAll(visitor.getExpressions());
    }

    /**
     * @param compilationUnit
     * @return
     */
    private ASTNode createParser(ICompilationUnit compilationUnit) {
        ASTParser parser = ASTParser.newParser(AST.JLS4);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setSource(compilationUnit);
        parser.setResolveBindings(true);
        ASTNode ast = parser.createAST(null);
        return ast;
    }
}
