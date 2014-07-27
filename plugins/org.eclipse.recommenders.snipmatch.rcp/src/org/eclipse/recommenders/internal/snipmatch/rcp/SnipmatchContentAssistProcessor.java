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

import static org.eclipse.recommenders.internal.snipmatch.rcp.Constants.SNIPMATCH_CONTEXT_ID;
import static org.eclipse.recommenders.utils.Logs.log;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.core.CompletionContext;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.internal.corext.template.java.JavaContext;
import org.eclipse.jdt.internal.corext.template.java.JavaContextType;
import org.eclipse.jdt.internal.corext.template.java.JavaDocContextType;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.ui.text.IJavaPartitions;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.recommenders.rcp.SharedImages;
import org.eclipse.recommenders.snipmatch.ISnippet;
import org.eclipse.recommenders.snipmatch.ISnippetRepository;
import org.eclipse.recommenders.utils.Recommendation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

@SuppressWarnings("restriction")
public class SnipmatchContentAssistProcessor implements IContentAssistProcessor {

    private static final String F_CONTEXT_TYPE = "context:";

    private final Set<ISnippetRepository> repos;
    private final TemplateContextType snipmatchContextType;
    private final Image image;

    private JavaContentAssistInvocationContext ctx;
    private String contextType;
    private String terms;

    @Inject
    public SnipmatchContentAssistProcessor(Set<ISnippetRepository> repos, SharedImages images) {
        this.repos = repos;
        snipmatchContextType = SnipmatchTemplateContextType.getInstance();
        image = images.getImage(SharedImages.Images.OBJ_BULLET_BLUE);
    }

    /**
     * new
     * 
     * @param ctx
     */
    public void setContext(JavaContentAssistInvocationContext ctx) {
        this.ctx = ctx;
        contextType = getContextType(ctx);
    }

    public void setTerms(String query) {
        terms = query;
    }

    @Override
    public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {

        if (StringUtils.isEmpty(terms)) {
            return new ICompletionProposal[0];
        }

        String contextQuery = " (context:" + contextType + ")";

        LinkedList<ICompletionProposal> proposals = Lists.newLinkedList();
        List<Recommendation<ISnippet>> recommendations = Lists.newArrayList();
        for (ISnippetRepository repo : repos) {
            recommendations.addAll(repo.search(terms + contextQuery));
        }
        ICompilationUnit cu = ctx.getCompilationUnit();
        IEditorPart editor = EditorUtility.isOpenInEditor(cu);

        ISourceViewer sourceViewer = (ISourceViewer) editor.getAdapter(ITextOperationTarget.class);
        Point selection = sourceViewer.getSelectedRange();
        IRegion region = new Region(selection.x, selection.y);
        Position p = new Position(selection.x, selection.y);
        IDocument document = sourceViewer.getDocument();

        String selectedText = null;
        if (selection.y != 0) {
            try {
                selectedText = document.get(selection.x, selection.y);
            } catch (BadLocationException e) {
            }
        }

        JavaContext ctx = new JavaContext(snipmatchContextType, document, p, cu);
        ctx.setVariable("selection", selectedText); //$NON-NLS-1$

        for (Recommendation<ISnippet> recommendation : recommendations) {
            ISnippet snippet = recommendation.getProposal();
            Template template = new Template(snippet.getName(), snippet.getDescription(), SNIPMATCH_CONTEXT_ID,
                    snippet.getCode(), true);

            try {
                proposals.add(SnippetProposal.newSnippetProposal(recommendation, template, ctx, region, image));
            } catch (Exception e) {
                log(LogMessages.ERROR_CREATING_SNIPPET_PROPOSAL_FAILED, e);
            }
        }
        return Iterables.toArray(proposals, ICompletionProposal.class);
    }

    private String getContextType(JavaContentAssistInvocationContext context) {
        try {
            String partition = TextUtilities.getContentType(context.getDocument(), IJavaPartitions.JAVA_PARTITIONING,
                    context.getInvocationOffset(), true);
            if (partition.equals(IJavaPartitions.JAVA_DOC)) {
                return JavaDocContextType.ID;
            } else {
                CompletionContext coreContext = context.getCoreContext();
                if (coreContext != null) {
                    int tokenLocation = coreContext.getTokenLocation();
                    if ((tokenLocation & CompletionContext.TL_MEMBER_START) != 0) {
                        return JavaContextType.ID_MEMBERS;
                    } else if ((tokenLocation & CompletionContext.TL_STATEMENT_START) != 0) {
                        return JavaContextType.ID_STATEMENTS;
                    }
                    return JavaContextType.ID_ALL;
                }
            }
        } catch (BadLocationException e) {
            System.err.println("bad location");
        }
        return "";
    }

    @Override
    public String getErrorMessage() {
        return null;
    }

    @Override
    public IContextInformationValidator getContextInformationValidator() {
        return null;
    }

    @Override
    public char[] getContextInformationAutoActivationCharacters() {
        return null;
    }

    @Override
    public char[] getCompletionProposalAutoActivationCharacters() {
        return null;
    }

    @Override
    public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
        return null;
    }
}
