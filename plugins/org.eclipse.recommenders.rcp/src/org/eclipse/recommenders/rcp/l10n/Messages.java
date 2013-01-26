package org.eclipse.recommenders.rcp.l10n;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.eclipse.recommenders.rcp.l10n.messages"; //$NON-NLS-1$
    public static String JOB_CLEAR_MODEL_REPOSITORY;
    public static String JOB_LOOKING_FOR_MODEL;
    public static String JOB_NO_CALL_MODEL_FOUND;
    public static String JOB_NO_INFO_AVAILABLE;
    public static String JOB_RESOLVING_MODEL;
    public static String JOB_INITIALIZE_PROJECTS;
    public static String JOB_UPDATE_MODEL_INDEX;
    public static String JOB_DOWNLOADING;
    public static String JOB_UPLOADING;
    public static String JOB_TRANSFER_FINISHED;
    public static String JOB_TRANSFER_FAILED;
    public static String JOB_TRANSFER_CORRUPTED;
    public static String PREFPAGE_OVERVIEW_INTRO;
    public static String PREFPAGE_CLEAR_CACHES;
    public static String PREFPAGE_ENABLE_AUTO_DOWNLOAD;
    public static String PREFPAGE_MODEL_REPOSITORY_HEADLINE;
    public static String PREFPAGE_MODEL_REPOSITORY_INTRO;
    public static String PREFPAGE_URI;
    public static String PREFPAGE_URI_INSERT;
    public static String PREFPAGE_URI_INVALID;
    public static String PREFPAGE_URI_MODEL_REPOSITORY;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
