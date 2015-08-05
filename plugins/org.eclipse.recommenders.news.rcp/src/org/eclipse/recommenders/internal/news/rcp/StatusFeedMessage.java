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

public class StatusFeedMessage extends FeedMessage {

    public StatusFeedMessage(String id, String description, String title, URL url) {
        super(id, new Date(), description, title, url);
    }

    public StatusFeedMessage(String id, String description, String title) {
        super(id, new Date(), description, title, null);
    }
}