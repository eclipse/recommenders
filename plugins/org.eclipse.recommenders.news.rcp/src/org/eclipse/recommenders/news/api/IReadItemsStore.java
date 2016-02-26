package org.eclipse.recommenders.news.api;

public interface IReadItemsStore {

    void markAsRead(FeedItem feedItem);

    boolean isRead(FeedItem feedItem);
}
