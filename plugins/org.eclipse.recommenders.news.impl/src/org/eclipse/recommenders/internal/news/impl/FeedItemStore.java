/**
 * Copyright (c) 2016 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Dorn - initial API and implementation.
 */
package org.eclipse.recommenders.internal.news.impl;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.mylyn.commons.notifications.core.NotificationEnvironment;
import org.eclipse.mylyn.internal.commons.notifications.feed.FeedEntry;
import org.eclipse.mylyn.internal.commons.notifications.feed.FeedReader;
import org.eclipse.recommenders.news.api.FeedItem;
import org.eclipse.recommenders.news.api.IFeedItemStore;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

@SuppressWarnings("restriction")
public class FeedItemStore implements IFeedItemStore {

    private final ConcurrentMap<URI, List<FeedItem>> cache = new ConcurrentHashMap<>();

    private final NotificationEnvironment environment = new NotificationEnvironment();

    @Override
    public List<FeedItem> udpate(URI feedUri, InputStream in, @Nullable IProgressMonitor monitor) {
        SubMonitor progress = SubMonitor.convert(monitor, 1);
        try {
            List<FeedItem> oldItems = getFeedItems(feedUri);
            List<FeedItem> updatedItems = readItems(in, feedUri.toString(), progress.newChild(1));

            List<FeedItem> result = new ArrayList<>();

            for (FeedItem updatedItem : updatedItems) {
                if (!oldItems.contains(updatedItem)) {
                    result.add(updatedItem);
                }
            }

            return result;
        } finally {
            if (monitor != null) {
                monitor.done();
            }
        }
    }

    @Override
    public List<FeedItem> getFeedItems(URI feedUri) {
        if (cache.containsKey(feedUri)) {
            return cache.get(feedUri);
        }
        return Collections.emptyList();
    }

    private List<FeedItem> readItems(InputStream in, String eventId, @Nullable IProgressMonitor monitor) {
        SubMonitor progress = SubMonitor.convert(monitor, 1);
        FeedReader reader = new FeedReader(eventId, environment);
        reader.parse(in, monitor);
        progress.worked(1);
        return FluentIterable.from(reader.getEntries()).transform(new Function<FeedEntry, FeedItem>() {

            @Override
            public FeedItem apply(@Nullable FeedEntry entry) {
                if (entry != null) {
                    return new FeedItem(entry.getId(), entry.getDate(), entry.getDescription(), entry.getTitle(),
                            URI.create(entry.getUrl()));
                } else {
                    return new FeedItem("", new Date(), "", "", URI.create("")); // TODO ???
                }
            }
        }).toList();
    }
}
