/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.news.rcp;

public interface IFeedEvents {

    NewFeedItemsEvent createNewFeedItemsEvent();

    FeedMessageReadEvent createFeedMessageReadEvent(String id);

    FeedReadEvent createFeedReadEvent(IFeed feed);

    AllReadEvent createAllReadEvent();

    public static class NewFeedItemsEvent {
    }

    public static class FeedMessageReadEvent {
        private final String id;

        public FeedMessageReadEvent(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }

    public static class FeedReadEvent {
        private final IFeed feed;

        public FeedReadEvent(IFeed feed) {
            this.feed = feed;
        }

        public IFeed getFeed() {
            return feed;
        }
    }

    public static class AllReadEvent {
    }
}
