/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp;

import static java.lang.Long.parseLong;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.mylyn.commons.notifications.core.NotificationEnvironment;
import org.eclipse.mylyn.internal.commons.notifications.feed.FeedReader;
import org.eclipse.mylyn.internal.commons.notifications.feed.INotificationsFeed;
import org.eclipse.recommenders.news.rcp.IFeedMessage;
import org.eclipse.recommenders.news.rcp.IRssService;
import org.eclipse.recommenders.utils.Urls;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;

@SuppressWarnings("restriction")
public class RssService implements IRssService {

    private static final long DEFAULT_DELAY = 1440;
    private static final long START_DELAY = 0;

    private final NewsRcpPreferences preferences;
    private final EventBus bus;
    private final NotificationEnvironment environment;

    private final HashMap<FeedDescriptor, List<IFeedMessage>> groupedMessages = Maps.newHashMap();
    private final List<Job> messageCheckJobs = Lists.newArrayList();

    public RssService(NewsRcpPreferences preferences, EventBus bus, NotificationEnvironment environment) {
        this.preferences = preferences;
        this.bus = bus;
        this.environment = environment;
    }

    @Override
    public void start() {
        for (final FeedDescriptor feed : preferences.getEnabledFeedDescriptors()) {
            start(feed);
        }
    }

    @Override
    public void start(final FeedDescriptor feed) {
        // Job messageCheckJob;
        // messageCheckJobs.add(new Job(""));
        // if (messageCheckJob == null) {
        final Job messageCheckJob = new Job(feed.getId()) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                try {
                    pollFeed(monitor, feed);
                    return Status.OK_STATUS;
                } catch (Throwable t) {
                    // fail silently
                    return Status.CANCEL_STATUS;
                }
            }

            @Override
            public boolean shouldRun() {
                return preferences.isEnabled();
                // TODO check that current feed is enabled
            }

        };
        messageCheckJob.setSystem(true);
        messageCheckJob.setPriority(Job.DECORATE);
        messageCheckJob.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                if (!preferences.isEnabled()) {
                    return;
                }
                // TODO check that current feed is enabled
                if (feed.getPollingInterval() != null) {
                    messageCheckJob.schedule(TimeUnit.MINUTES.toMillis(parseLong(feed.getPollingInterval())));
                    return;
                }
                messageCheckJob.schedule(TimeUnit.MINUTES.toMillis(DEFAULT_DELAY));
            }
        });
        messageCheckJob.schedule(START_DELAY);
        messageCheckJobs.add(messageCheckJob);
    }

    private int pollFeed(IProgressMonitor monitor, FeedDescriptor feed) {
        int status = -1;
        try {
            HttpURLConnection connection = (HttpURLConnection) Urls.toUrl(feed.getUrl()).openConnection();
            try {
                connection.connect();
                status = connection.getResponseCode();
                // this is old condition, but hadnt got implemtation of IProgressMonitor, so commented it out
                // if (status == HttpURLConnection.HTTP_OK && !monitor.isCanceled()) {
                if (status == HttpURLConnection.HTTP_OK) {

                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    try {
                        if (!groupedMessages.containsKey(feed)) {
                            groupedMessages.put(feed, Lists.<IFeedMessage>newArrayList());
                        }
                        List<IFeedMessage> feedMessages = groupedMessages.get(feed);

                        feedMessages.addAll(readMessages(in, monitor, feed.getId()));
                        // TODO Only add to feedMessages if not already present
                    } finally {
                        in.close();
                    }
                } else if (status == HttpURLConnection.HTTP_NOT_FOUND) {
                    // no messages
                } else if (status == HttpURLConnection.HTTP_NOT_MODIFIED) {
                    // no new messages
                } else {
                    logStatus(new Status(IStatus.WARNING, INotificationsFeed.ID_PLUGIN,
                            "Http error retrieving service message: " + connection.getResponseMessage())); //$NON-NLS-1$
                }
            } finally {
                connection.disconnect();
            }
        } catch (Exception e) {
            logStatus(new Status(IStatus.WARNING, INotificationsFeed.ID_PLUGIN,
                    "Http error retrieving service message.", e)); //$NON-NLS-1$
        }

        if (groupedMessages.size() > 0) {
            bus.post(new NewFeedItemsEvent());
            // TODO post event only when there actually are new messages
        }
        return status;
    }

    private List<? extends IFeedMessage> readMessages(InputStream in, IProgressMonitor monitor, String eventId)
            throws IOException {
        FeedReader reader = new FeedReader(eventId, environment);
        reader.parse(in, monitor);
        return Collections.emptyList();
        // reader.getEntries();
        // TODO convert reader.getEntries() to FeedMessage
    }

    private void logStatus(IStatus status) {
        // if (!statusLogged) {
        // statusLogged = true;
        // StatusHandler.log(status);
        // }
    }

    @Override
    public Map<FeedDescriptor, List<IFeedMessage>> getMessages(int countPerFeed) {
        // TODO return grouped messages limited by count
        return ImmutableMap.copyOf(groupedMessages);
    }
}
