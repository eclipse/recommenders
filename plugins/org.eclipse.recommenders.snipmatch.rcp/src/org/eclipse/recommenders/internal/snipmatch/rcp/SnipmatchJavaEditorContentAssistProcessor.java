/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch, Madhuranga Lakjeewa - initial API and implementation.
 */
package org.eclipse.recommenders.internal.snipmatch.rcp;

import javax.inject.Inject;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.corext.template.java.JavaContext;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.recommenders.coordinates.IDependencyListener;
import org.eclipse.recommenders.coordinates.rcp.DependencyInfos;
import org.eclipse.recommenders.models.rcp.IProjectCoordinateProvider;
import org.eclipse.recommenders.rcp.SharedImages;
import org.eclipse.recommenders.snipmatch.rcp.model.SnippetRepositoryConfigurations;

@SuppressWarnings("restriction")
public class SnipmatchJavaEditorContentAssistProcessor extends
        AbstractSnipmatchContentAssistProcessor<JavaContentAssistInvocationContext> {

    @Inject
    public SnipmatchJavaEditorContentAssistProcessor(SnippetRepositoryConfigurations configs, Repositories repos,
            IProjectCoordinateProvider pcProvider, IDependencyListener dependencyListener, SharedImages images) {
        super(SnipmatchTemplateContextType.getInstance(), configs, repos, pcProvider, dependencyListener, images);
    }

    @Override
    protected void prepareContext(JavaContentAssistInvocationContext context) {
        IJavaProject project = context.getProject();
        availableDependencies = dependencyListener.getDependenciesForProject(DependencyInfos
                .createDependencyInfoForProject(project));
    }

    @Override
    protected DocumentTemplateContext getDocumentTemplateContext(IDocument document, Position position) {
        ICompilationUnit cu = context.getCompilationUnit();
        JavaContext javaTemplateContext = new JavaContext(templateContextType, document, position, cu);
        javaTemplateContext.setForceEvaluation(true);
        return javaTemplateContext;
    }
}
