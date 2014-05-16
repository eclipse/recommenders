package org.eclipse.recommenders.snipmatch;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.eclipse.recommenders.snipmatch.messages"; //$NON-NLS-1$
    public static String ERROR_COULD_NOT_CONNECT_TO_REMOTE_REPOSITORY;
    public static String ERROR_INVALID_REMOTE_REPOSITORY;
    public static String ERROR_WHILE_OPENING_REPOSITORY;
    public static String ERROR_WHILE_UPDATE_CLONE_REPOSITORY;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
