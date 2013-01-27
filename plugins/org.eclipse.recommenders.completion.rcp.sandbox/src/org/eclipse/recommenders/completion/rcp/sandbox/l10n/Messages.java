package org.eclipse.recommenders.completion.rcp.sandbox.l10n;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.eclipse.recommenders.completion.rcp.sandbox.l10n.messages"; //$NON-NLS-1$
    public static String HIPPIE_COMPLETION_PROPOSAL;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
