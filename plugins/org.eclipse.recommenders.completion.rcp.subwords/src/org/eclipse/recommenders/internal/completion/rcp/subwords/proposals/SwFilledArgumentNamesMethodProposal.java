/**
 * Copyright (c) 2010 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.completion.rcp.subwords.proposals;

import org.eclipse.jdt.core.CompletionProposal;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.javaeditor.EditorHighlightingSynchronizer;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.text.java.JavaMethodCompletionProposal;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedPosition;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.recommenders.internal.completion.rcp.subwords.SubwordsProposalContext;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

/**
 * A method proposal with filled in argument names.
 */
public class SwFilledArgumentNamesMethodProposal extends JavaMethodCompletionProposal {

    private IRegion fSelectedRegion; // initialized by apply()
    private int[] fArgumentOffsets;
    private int[] fArgumentLengths;
    private final SubwordsProposalContext subwordsContext;

    public SwFilledArgumentNamesMethodProposal(final CompletionProposal proposal,
            final JavaContentAssistInvocationContext context, final SubwordsProposalContext subwordsContext) {
        super(proposal, context);
        this.subwordsContext = subwordsContext;
        setRelevance(subwordsContext.calculateRelevance());
    }

    @Override
    protected boolean isPrefix(final String prefix, final String completion) {
        subwordsContext.setPrefix(prefix);
        setRelevance(subwordsContext.calculateRelevance());
        return subwordsContext.isRegexMatch();
    }

    @Override
    public StyledString getStyledDisplayString() {
        final StyledString origin = super.getStyledDisplayString();
        return subwordsContext.getStyledDisplayString(origin);
    }

    // jdt code below ==============================================

    @Override
    public void apply(final IDocument document, final char trigger, final int offset) {
        super.apply(document, trigger, offset);
        final int baseOffset = getReplacementOffset();
        final String replacement = getReplacementString();

        if (fArgumentOffsets != null && getTextViewer() != null) {
            try {
                final LinkedModeModel model = new LinkedModeModel();
                for (int i = 0; i != fArgumentOffsets.length; i++) {
                    final LinkedPositionGroup group = new LinkedPositionGroup();
                    group.addPosition(new LinkedPosition(document, baseOffset + fArgumentOffsets[i],
                            fArgumentLengths[i], LinkedPositionGroup.NO_STOP));
                    model.addGroup(group);
                }

                model.forceInstall();
                final JavaEditor editor = getJavaEditor();
                if (editor != null) {
                    model.addLinkingListener(new EditorHighlightingSynchronizer(editor));
                }

                final LinkedModeUI ui = new EditorLinkedModeUI(model, getTextViewer());
                ui.setExitPosition(getTextViewer(), baseOffset + replacement.length(), 0, Integer.MAX_VALUE);
                ui.setExitPolicy(new ExitPolicy(')', document));
                ui.setDoContextInfo(true);
                ui.setCyclingMode(LinkedModeUI.CYCLE_WHEN_NO_PARENT);
                ui.enter();

                fSelectedRegion = ui.getSelectedRegion();

            } catch (final BadLocationException e) {
                JavaPlugin.log(e);
                openErrorDialog(e);
            }
        } else {
            fSelectedRegion = new Region(baseOffset + replacement.length(), 0);
        }
    }

    /*
     * @see org.eclipse.jdt.internal.ui.text.java.JavaMethodCompletionProposal#needsLinkedMode()
     */
    @Override
    protected boolean needsLinkedMode() {
        return false; // we handle it ourselves
    }

    /*
     * @see org.eclipse.jdt.internal.ui.text.java.LazyJavaCompletionProposal#computeReplacementString()
     */
    @Override
    protected String computeReplacementString() {

        if (!hasParameters() || !hasArgumentList()) {
            return super.computeReplacementString();
        }

        final StringBuffer buffer = new StringBuffer();
        appendMethodNameReplacement(buffer);

        final char[][] parameterNames = fProposal.findParameterNames(null);
        final int count = parameterNames.length;
        fArgumentOffsets = new int[count];
        fArgumentLengths = new int[count];

        final FormatterPrefs prefs = getFormatterPrefs();

        setCursorPosition(buffer.length());

        if (prefs.afterOpeningParen) {
            buffer.append(SPACE);
        }

        for (int i = 0; i != count; i++) {
            if (i != 0) {
                if (prefs.beforeComma) {
                    buffer.append(SPACE);
                }
                buffer.append(COMMA);
                if (prefs.afterComma) {
                    buffer.append(SPACE);
                }
            }

            fArgumentOffsets[i] = buffer.length();
            buffer.append(parameterNames[i]);
            fArgumentLengths[i] = parameterNames[i].length;
        }

        if (prefs.beforeClosingParen) {
            buffer.append(SPACE);
        }

        buffer.append(RPAREN);

        return buffer.toString();
    }

    /**
     * Returns the currently active java editor, or <code>null</code> if it cannot be determined.
     * 
     * @return the currently active java editor, or <code>null</code>
     */
    private JavaEditor getJavaEditor() {
        final IEditorPart part = JavaPlugin.getActivePage().getActiveEditor();
        if (part instanceof JavaEditor) {
            return (JavaEditor) part;
        } else {
            return null;
        }
    }

    /*
     * @see ICompletionProposal#getSelection(IDocument)
     */
    @Override
    public Point getSelection(final IDocument document) {
        if (fSelectedRegion == null) {
            return new Point(getReplacementOffset(), 0);
        }

        return new Point(fSelectedRegion.getOffset(), fSelectedRegion.getLength());
    }

    private void openErrorDialog(final BadLocationException e) {
        final Shell shell = getTextViewer().getTextWidget().getShell();
        MessageDialog.openError(shell, "Failed to guess method parameters", e.getMessage());
    }

}
