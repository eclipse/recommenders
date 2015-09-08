/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp.menus;

import org.eclipse.jface.action.Action;
import org.eclipse.recommenders.internal.news.rcp.l10n.Messages;
import org.eclipse.recommenders.news.rcp.IFeed;
import org.eclipse.recommenders.news.rcp.IFeedEvents;

import com.google.common.eventbus.EventBus;

public class MarkAsReadAction extends Action {
    private final EventBus eventBus;
    private final Boolean allFeeds;
    private final IFeed feed;
    private final IFeedEvents feedEvents;

    private MarkAsReadAction(EventBus eventBus, IFeed feed, boolean allFeeds, IFeedEvents feedEvents) {
        super();
        this.eventBus = eventBus;
        this.allFeeds = allFeeds;
        this.feed = feed;
        this.feedEvents = feedEvents;
    }

    public static MarkAsReadAction newMarkFeedAsReadAction(EventBus eventBus, IFeed feed, IFeedEvents feedEvents) {
        return new MarkAsReadAction(eventBus, feed, false, feedEvents);
    }

    public static MarkAsReadAction newMarkAllAsReadAction(EventBus eventBus, IFeedEvents feedEvents) {
        return new MarkAsReadAction(eventBus, null, true, feedEvents);
    }

    @Override
    public void run() {
        if (allFeeds) {
            eventBus.post(feedEvents.createAllReadEvent());
            return;
        }
        eventBus.post(feedEvents.createFeedReadEvent(feed));
    }

    @Override
    public String getText() {
        return Messages.LABEL_MARK_AS_READ;
    }
}
