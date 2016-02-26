package org.eclipse.recommenders.news.api;

import java.net.URI;
import java.util.List;

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
