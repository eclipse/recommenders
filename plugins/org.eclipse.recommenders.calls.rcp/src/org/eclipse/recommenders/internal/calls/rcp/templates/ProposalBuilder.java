/**
 * Copyright (c) 2010 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Lerch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.calls.rcp.templates;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.codeassist.complete.CompletionOnSingleNameReference;
import org.eclipse.jdt.internal.compiler.ast.ASTNode;
import org.eclipse.jdt.internal.corext.template.java.AbstractJavaContextType;
import org.eclipse.jdt.internal.corext.template.java.JavaContext;
import org.eclipse.jdt.internal.corext.template.java.JavaContextType;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.recommenders.completion.rcp.IRecommendersCompletionContext;
import org.eclipse.recommenders.rcp.JavaElementResolver;
import org.eclipse.recommenders.utils.names.IMethodName;
import org.eclipse.recommenders.utils.names.ITypeName;
import org.eclipse.swt.graphics.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@SuppressWarnings("restriction")
public class ProposalBuilder {

    private Logger log = LoggerFactory.getLogger(getClass());
    private final List<PatternRecommendation> patterns = Lists.newLinkedList();
    private final Image icon;
    final IRecommendersCompletionContext rCtx;
    private JavaContext documentContext;
    final JavaElementResolver resolver;
    String variableName;

    public ProposalBuilder(final Image icon, final IRecommendersCompletionContext rCtx,
            final JavaElementResolver resolver, final String variableName) {
        this.icon = icon;
        this.rCtx = rCtx;
        this.resolver = resolver;
        this.variableName = variableName;
        createDocumentContext();
    }

    private void createDocumentContext() {
        final JavaPlugin plugin = JavaPlugin.getDefault();
        if (plugin != null) {
            final AbstractJavaContextType type = (AbstractJavaContextType) plugin.getTemplateContextRegistry()
                    .getContextType(JavaContextType.ID_ALL);

            final JavaContentAssistInvocationContext javaContext = rCtx.getJavaContext();
            final Region region = rCtx.getReplacementRange();

            int offset = 0;
            ASTNode node = rCtx.getCompletionNode().orNull();
            if (node instanceof CompletionOnSingleNameReference) {
                offset = region.getOffset() - rCtx.getPrefix().length();
            } else {
                offset = region.getOffset() - variableName.length();
            }

            final int length = Math.max(0, region.getLength() - 1);
            documentContext = new JavaContext(type, javaContext.getDocument(), offset, length,
                    rCtx.getCompilationUnit());
            documentContext.setForceEvaluation(true);
        } else {
            throw new IllegalStateException("No default JavaPlugin found.");
        }
    }

    public void addPattern(final PatternRecommendation pattern) {
        patterns.add(pattern);
    }

    public List<JavaTemplateProposal> createProposals() {

        // sort the most likely patterns on top:
        Collections.sort(patterns, new Comparator<PatternRecommendation>() {

            @Override
            public int compare(PatternRecommendation o1, PatternRecommendation o2) {
                return Double.compare(o2.getProbability(), o1.getProbability());
            }
        });

        final List<JavaTemplateProposal> result = Lists.newLinkedList();
        // get rid of duplicates: yes, this happens!
        HashSet<PatternRecommendation> noDuplicates = Sets.newHashSet(patterns);
        for (final PatternRecommendation pattern : noDuplicates) {
            try {
                result.add(new JavaTemplateProposal(createTemplate(pattern), documentContext, icon, pattern));
            } catch (final Exception e) {
                log.warn("Failed to create proposals", e);
            }
        }
        return result;
    }

    private Template createTemplate(final PatternRecommendation pattern) {
        final String code = createTemplateCode(pattern);
        return new Template(pattern.getName(), pattern.getType().getClassName(), "java", code, false);
    }

    private String createTemplateCode(final PatternRecommendation pattern) {
        TemplateBuilder tb = new TemplateBuilder();

        String receiverName = variableName;
        for (final IMethodName method : pattern.getMethods()) {
            if (method.isInit()) {
                receiverName = tb.appendCtor(method, lookupArgumentNames(method));
                tb.nl();
            } else {
                tb.appendCall(method, receiverName, lookupArgumentNames(method));
                tb.nl();
            }
        }
        tb.cursor();

        // final TemplateCodeBuilder builder = new TemplateCodeBuilder(variableName, resolver);
        // for (final IMethodName method : pattern.getMethods()) {
        // builder.addMethodCall(method);
        // }
        // String oTemplate = builder.build();
        return tb.toString();
    }

    private String[] lookupArgumentNames(IMethodName method) {
        IMethod jdtMethod = resolver.toJdtMethod(method).orNull();
        try {
            if (jdtMethod != null) {
                return jdtMethod.getParameterNames();
            }
        } catch (JavaModelException e) {
            log.warn("Failed to lookup method arguments names for " + jdtMethod, e);
        }

        ITypeName[] parameterTypes = method.getParameterTypes();
        String[] parameterNames = new String[parameterTypes.length];
        for (int i = 0; i < parameterNames.length; i++) {
            parameterNames[i] = "arg" + i;
        }
        return parameterNames;
    }
}
