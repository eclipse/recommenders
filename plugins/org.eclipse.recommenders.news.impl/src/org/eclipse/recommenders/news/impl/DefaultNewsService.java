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
package org.eclipse.recommenders.news.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.recommenders.internal.news.impl.DefaultDownloadService;
import org.eclipse.recommenders.internal.news.impl.DefaultFeedItemStore;
import org.eclipse.recommenders.news.api.FeedItem;
import org.eclipse.recommenders.news.api.IDownloadService;
import org.eclipse.recommenders.news.api.IFeedItemStore;
import org.eclipse.recommenders.news.api.INewsService;
import org.eclipse.recommenders.news.api.PollingPolicy;
import org.eclipse.recommenders.news.api.PollingRequest;
import org.eclipse.recommenders.news.api.PollingResult;

import com.google.common.annotations.VisibleForTesting;

public class DefaultNewsService implements INewsService {

    private final IDownloadService downloadService;
    private final IFeedItemStore feedItemStore;

    @VisibleForTesting
    DefaultNewsService(IDownloadService downloadService, IFeedItemStore feedItemStore) {
        this.downloadService = downloadService;
        this.feedItemStore = feedItemStore;
    }

    public DefaultNewsService() {
        this.downloadService = new DefaultDownloadService();
        this.feedItemStore = new DefaultFeedItemStore();
    }

    @Override
    public Collection<PollingResult> poll(Collection<PollingRequest> requests, @Nullable IProgressMonitor monitor) {
        SubMonitor progress = SubMonitor.convert(monitor, requests.size());
        try {
            Date now = new Date();

            List<PollingResult> results = new ArrayList<>(requests.size());
            for (PollingRequest request : requests) {
                if (progress.isCanceled()) {
                    break;
                }
                PollingResult result = poll(request, now, progress.newChild(1));
                results.add(result);
            }
            return results;
        } finally {
            if (monitor != null) {
                monitor.done();
            }
        }
    }

    private PollingResult poll(PollingRequest request, Date pollingDate, SubMonitor monitor) {
        SubMonitor progress = SubMonitor.convert(monitor, 2);

        URI feedUri = request.getFeedUri();
        PollingPolicy policy = request.getPollingPolicy();

        List<FeedItem> newItems;
        Date lastPolledDate;
        try {
            lastPolledDate = downloadService.getLastAttemptDate(feedUri);
        } catch (IOException e) {
            lastPolledDate = null;
        }
        if (policy.shouldPoll(lastPolledDate, pollingDate)) {
            try (InputStream stream = downloadService.download(feedUri, progress.newChild(1))) {
                newItems = feedItemStore.udpate(feedUri, stream, progress.newChild(1));
            } catch (IOException e) {
                newItems = Collections.emptyList();
            }
        } else {
            newItems = Collections.emptyList();
        }
        progress.setWorkRemaining(0);

        List<FeedItem> allItems = feedItemStore.getFeedItems(feedUri);
        return new PollingResult(feedUri, newItems, allItems);
    }
}