/**
 * Copyright (c) 2014 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon Laffoy - initial API and implementation.
 */
package org.eclipse.recommenders.internal.snipmatch.rcp;

import java.text.MessageFormat;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.recommenders.snipmatch.model.SnippetRepositoryConfiguration;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

public class RepositoryProposal implements ICompletionProposal {

    private final String displayString;
    private final int repositoryPriority;

    public RepositoryProposal(SnippetRepositoryConfiguration newRepository, int repositoryPriority, int size) {
        this.displayString = MessageFormat.format(Messages.COMPLETION_ENGINE_REPOSITORY_MATCHES,
                newRepository.getName(), size);
        this.repositoryPriority = repositoryPriority;
    }

    @Override
    public Point getSelection(IDocument document) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDisplayString() {
        return displayString;
    }

    public int getRepositoryPriority() {
        return repositoryPriority;
    }

    @Override
    public Image getImage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public IContextInformation getContextInformation() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void apply(IDocument document) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getAdditionalProposalInfo() {
        // TODO Auto-generated method stub
        return null;
    }
}
