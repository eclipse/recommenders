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
package org.eclipse.recommenders.news.impl.poll;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.recommenders.news.api.NewsItem;
import org.eclipse.recommenders.news.api.Topics;
import org.eclipse.recommenders.news.api.poll.INewsPollingService;
import org.eclipse.recommenders.news.api.poll.PollingPolicy;
import org.eclipse.recommenders.news.api.poll.PollingRequest;
import org.eclipse.recommenders.news.api.poll.PollingResult;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;

public class DefaultNewsPollingService implements INewsPollingService {

    private final IDownloadService downloadService;
    private final IFeedItemStore feedItemStore;

    private EventAdmin eventAdmin;

    @VisibleForTesting
    DefaultNewsPollingService(IDownloadService downloadService, IFeedItemStore feedItemStore) {
        this.downloadService = downloadService;
        this.feedItemStore = feedItemStore;
    }

    public DefaultNewsPollingService() {
        this.downloadService = new DefaultDownloadService();
        this.feedItemStore = new DefaultFeedItemStore();
    }

    public void bindEventAdmin(EventAdmin eventAdmin) {
        this.eventAdmin = eventAdmin;
    }

    public void unbindEventAdmin(EventAdmin eventAdmin) {
        if (this.eventAdmin == eventAdmin) {
            eventAdmin = null;
        }
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

            postResults(results);

            return results;
        } finally {
            if (monitor != null) {
                monitor.done();
            }
        }
    }

    private void postResults(List<PollingResult> results) {
        if (eventAdmin != null) {
            Map<String, ?> parameters = ImmutableMap.of(
                    EventConstants.EVENT_TOPIC, Topics.POLLING_RESULT,
                    "org.eclipse.e4.data", results); //$NON-NLS-1$
            Event event = new Event(Topics.POLLING_RESULT, parameters);
            eventAdmin.postEvent(event);
        }
    }

    private PollingResult poll(PollingRequest request, Date pollingDate, SubMonitor monitor) {
        URI feedUri = request.getFeedUri();

        SubMonitor progress = SubMonitor.convert(monitor, feedUri.toString(), 2);

        Date lastPolledDate;
        try {
            lastPolledDate = downloadService.getLastAttemptDate(feedUri);
        } catch (IOException e) {
            lastPolledDate = null;
        }

        PollingPolicy policy = request.getPollingPolicy();
        List<NewsItem> newItems;
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

        List<NewsItem> allItems = feedItemStore.getNewsItems(feedUri);
        return new PollingResult(feedUri, newItems, allItems);
    }
}
