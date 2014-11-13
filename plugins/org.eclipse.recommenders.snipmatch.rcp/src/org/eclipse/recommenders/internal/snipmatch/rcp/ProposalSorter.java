/**
 * Copyright (c) 2014 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Dorn - initial API and implementation.
 */
package org.eclipse.recommenders.internal.snipmatch.rcp;

import org.eclipse.jdt.internal.ui.text.java.RelevanceSorter;
import org.eclipse.jdt.ui.text.java.AbstractProposalSorter;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalSorter;

import com.google.common.collect.ComparisonChain;

public class ProposalSorter extends AbstractProposalSorter {
    private final ICompletionProposalSorter RELEVANCE_SORTER = new RelevanceSorter();

    @Override
    public int compare(ICompletionProposal p1, ICompletionProposal p2) {
        if (p1 instanceof SnippetProposal && p2 instanceof SnippetProposal) {
            SnippetProposal s1 = (SnippetProposal) p1;
            SnippetProposal s2 = (SnippetProposal) p2;

            return ComparisonChain.start().compare(s1.getRepositoryRelevance(), s2.getRepositoryRelevance())
                    .compare(s2.getRelevance(), s1.getRelevance())
                    .compare(s1.getSnippet().getName(), s2.getSnippet().getName()).result();
        } else if (p1 instanceof RepositoryProposal && p2 instanceof RepositoryProposal) {
            RepositoryProposal s1 = (RepositoryProposal) p1;
            RepositoryProposal s2 = (RepositoryProposal) p2;

            return ComparisonChain.start().compare(s1.getRepositoryPriority(), s2.getRepositoryPriority()).result();
        } else if (p1 instanceof RepositoryProposal && p2 instanceof SnippetProposal) {
            return compareSnippetWithRepository((SnippetProposal) p2, (RepositoryProposal) p1);
        } else if (p1 instanceof SnippetProposal && p2 instanceof RepositoryProposal) {
            return -compareSnippetWithRepository((SnippetProposal) p1, (RepositoryProposal) p2);
        } else {
            return RELEVANCE_SORTER.compare(p1, p2);
        }
    }

    private int compareSnippetWithRepository(SnippetProposal s, RepositoryProposal r) {
        int comparison = ComparisonChain.start().compare(r.getRepositoryPriority(), s.getRepositoryRelevance())
                .result();
        return comparison != 0 ? comparison : -1;
    }
}
