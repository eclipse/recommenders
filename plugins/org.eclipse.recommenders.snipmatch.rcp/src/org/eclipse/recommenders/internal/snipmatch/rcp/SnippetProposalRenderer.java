package org.eclipse.recommenders.internal.snipmatch.rcp;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.text.MessageFormat.format;

import org.eclipse.recommenders.internal.snipmatch.rcp.l10n.Messages;
import org.eclipse.recommenders.snipmatch.ISnippet;

public class SnippetProposalRenderer {

    public static String createDisplayString(ISnippet snippet) {
        if (isNullOrEmpty(snippet.getDescription())) {
            return snippet.getName();
        } else {
            return format(Messages.SEARCH_DISPLAY_STRING, snippet.getName(), snippet.getDescription());
        }
    }
}
