/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp.l10n;

import org.eclipse.osgi.util.NLS;

public final class Messages extends NLS {

    private static final String BUNDLE_NAME = "org.eclipse.recommenders.internal.news.rcp.l10n.messages"; //$NON-NLS-1$

    public static String FEED_DESCRIPTOR_MALFORMED_URL;
    public static String FEED_DESCRIPTOR_POLLING_INTERVAL_DIGITS_ONLY;

    public static String PREFPAGE_TITLE;
    public static String PREFPAGE_DESCRIPTION;
    public static String PREFPAGE_BUTTON_NEW;
    public static String PREFPAGE_BUTTON_EDIT;
    public static String PREFPAGE_BUTTON_REMOVE;
    public static String FEED_DIALOG_TITLE;
    public static String FEED_DIALOG_ERROR;
    public static String FIELD_LABEL_FEED_NAME;
    public static String FIELD_LABEL_URL;
    public static String FIELD_LABEL_FEEDS;
    public static String FIELD_LABEL_NEWS_ENABLED;
    public static String FIELD_LABEL_NOTIFICATION_ENABLED;
    public static String FIELD_LABEL_POLLING_INTERVAL;
    public static String FEED_DIALOG_ERROR_EMPTY_NAME;
    public static String FEED_DIALOG_ERROR_EMPTY_URL;
    public static String FEED_DIALOG_ERROR_PROTOCOL_UNSUPPORTED;
    public static String FEED_DIALOG_ERROR_INVALID_URL;
    public static String CONFIRM_DIALOG_DELETE_FEED_TITLE;
    public static String CONFIRM_DIALOG_DELETE_FEED_MESSAGE;
    public static String CONFIRM_DIALOG_DELETE_FEED_TOGGLE_MESSAGE;
    public static String CONFIRM_DIALOG_OK;
    public static String CONFIRM_DIALOG_CANCEL;

    public static String POLL_FEED_JOB_SCHEDULER_NAME;
    public static String POLL_FEED_JOB_NAME;
    public static String JOB_NAME_CLOSE;
    public static String JOB_NAME_FADE;
    public static String LABEL_NOTIFICATION_NAME;
    public static String LABEL_NO_NEW_MESSAGES;
    public static String LABEL_MARK_AS_READ;
    public static String TOOLTIP_NO_NEW_MESSAGES;
    public static String TOOLTIP_NEW_MESSAGES;
    public static String HINT_MORE_MESSAGES;

    public static String NOTIFICATION_TITLE;
    public static String UNREAD_MESSAGE_PREFIX;

    public static String LOG_ERROR_READING_PROPERTIES;
    public static String LOG_ERROR_WRITING_PROPERTIES;
    public static String LOG_ERROR_CONNECTING_URL;
    public static String LOG_ERROR_FETCHING_MESSAGES;

    static {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
