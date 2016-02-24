/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp.toolbar;

import static org.eclipse.recommenders.internal.news.rcp.FeedEvents.*;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.jface.action.Action;
import org.eclipse.recommenders.internal.news.rcp.Constants;
import org.eclipse.recommenders.internal.news.rcp.FeedDescriptor;
import org.eclipse.recommenders.internal.news.rcp.l10n.Messages;

public final class MarkAsReadAction extends Action {

    private final IEventBroker eventBroker;
    private final Boolean allFeeds;
    private final FeedDescriptor feed;

    private MarkAsReadAction(IEventBroker eventBroker, FeedDescriptor feed, boolean allFeeds) {
        super();
        this.eventBroker = eventBroker;
        this.allFeeds = allFeeds;
        this.feed = feed;
    }

    public static MarkAsReadAction newMarkFeedAsReadAction(IEventBroker eventBroker, FeedDescriptor feed) {
        return new MarkAsReadAction(eventBroker, feed, false);
    }

    public static MarkAsReadAction newMarkAllAsReadAction(IEventBroker eventBroker) {
        return new MarkAsReadAction(eventBroker, null, true);
    }

    @Override
    public void run() {
        if (allFeeds) {
            eventBroker.post(Constants.TOPIC_ALL_FEEDS_READ, createAllReadEvent());
            return;
        }
        eventBroker.post(Constants.TOPIC_FEED_READ, createFeedReadEvent(feed));
    }

    @Override
    public String getText() {
        return Messages.LABEL_MARK_AS_READ;
    }
}
