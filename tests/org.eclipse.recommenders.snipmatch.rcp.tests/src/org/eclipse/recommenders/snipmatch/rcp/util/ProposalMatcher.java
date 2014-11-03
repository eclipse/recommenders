package org.eclipse.recommenders.snipmatch.rcp.util;

import static java.lang.String.format;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.recommenders.internal.snipmatch.rcp.SnippetProposal;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class ProposalMatcher extends BaseMatcher<ICompletionProposal> {

    private final String displayString;
    private final int relevance;
    private final int repoPriority;
    private final String selection;

    private ProposalMatcher(String displayString, int relevance, int repoPriority, String selection) {
        this.displayString = displayString;
        this.relevance = relevance;
        this.repoPriority = repoPriority;
        this.selection = selection;
    }

    public static ProposalMatcher proposal(String displayString, int relevance, int repoPriority, String selection) {
        return new ProposalMatcher(displayString, relevance, repoPriority, selection);
    }

    @Override
    public boolean matches(Object item) {
        if (!(item instanceof SnippetProposal)) {
            return false;
        }
        SnippetProposal proposal = (SnippetProposal) item;

        if (!displayString.equals(proposal.getDisplayString())) {
            return false;
        }

        if (relevance != proposal.getRelevance()) {
            return false;
        }

        if (repoPriority != proposal.getRepositoryRelevance()) {
            return false;
        }

        if (!StringUtils.equals(selection, proposal.getTemplateContext().getVariable("selection"))) {
            return false;
        }

        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(getDescription(displayString, relevance, repoPriority, selection));
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        if (item instanceof SnippetProposal) {
            description.appendText("was ");
            SnippetProposal proposal = (SnippetProposal) item;
            description.appendText(getDescription(proposal.getDisplayString(), proposal.getRelevance(),
                    proposal.getRepositoryRelevance(), proposal.getTemplateContext().getVariable("selection")));
        } else {
            super.describeMismatch(item, description);
        }
    }

    private String getDescription(String displayString, int relevance, int repoPriority, String selection) {
        return format("a proposal named '%s' with relevance %d, repo priority: %d, selection: %s", displayString,
                relevance, repoPriority, selection);
    }
}