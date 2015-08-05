/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Based on org.eclipse.recommenders.rcp.utils.BrowserUtils
 */
package org.eclipse.recommenders.internal.news.rcp;

import java.net.URL;
import java.util.Date;

import com.google.common.annotations.VisibleForTesting;

public class StatusFeedMessage extends FeedMessage {

    public enum Status {
        OK("OK"), //$NON-NLS-1$
        FEEDS_NOT_POLLED_YET("feedNotPolledYet"), //$NON-NLS-1$
        FEED_NOT_FOUND_AT_URL("feedNotFoundAtURL"); //$NON-NLS-1$
        private String status;

        Status(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }
    }

    private final Status status;

    public StatusFeedMessage(Status status, String description, String title, URL url) {
        super(status.getStatus(), new Date(), description, title, url);
        this.status = status;
    }

    @VisibleForTesting
    StatusFeedMessage(String id, String description, String title) {
        super(id, new Date(), description, title, null);
        this.status = null;
    }

    public StatusFeedMessage(Status status, String description, String title) {
        super(status.getStatus(), new Date(), description, title, null);
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }
}
