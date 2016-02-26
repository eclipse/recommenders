/**
 * Copyright (c) 2016 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Sewe - initial API and implementation.
 */
package org.eclipse.recommenders.news.api.poll;

import java.net.URI;
import java.util.List;

import org.eclipse.recommenders.news.api.FeedItem;

public class PollingResult {

    private final URI feedUri;
    private final List<FeedItem> newFeedItems;
    private final List<FeedItem> allFeedItems;

    public PollingResult(URI feedUri, List<FeedItem> newFeedItems, List<FeedItem> allFeedItems) {
        this.feedUri = feedUri;
        this.newFeedItems = newFeedItems;
        this.allFeedItems = allFeedItems;
    }

    public URI getFeedUri() {
        return feedUri;
    }

    public List<FeedItem> getNewFeedItems() {
        return newFeedItems;
    }

    public List<FeedItem> getAllFeedItems() {
        return allFeedItems;
    }
}
