/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp;

import static java.lang.Long.parseLong;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.mylyn.commons.notifications.core.NotificationEnvironment;
import org.eclipse.recommenders.news.rcp.IFeedMessage;
import org.eclipse.recommenders.news.rcp.IRssService;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;

@SuppressWarnings("restriction")
public class RssService implements IRssService {

    private static final long DEFAULT_DELAY = TimeUnit.DAYS.toMinutes(1);
    private static final long START_DELAY = 0;

    private final NewsRcpPreferences preferences;
    private final EventBus bus;
    private final NotificationEnvironment environment;

    private final HashMap<FeedDescriptor, List<IFeedMessage>> groupedMessages = Maps.newHashMap();

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
        final NewsJob job = new NewsJob(feed, preferences, environment);
        job.setSystem(true);
        job.setPriority(Job.DECORATE);
        job.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                boolean newMessage = false;
                if (!groupedMessages.containsKey(feed)) {
                    groupedMessages.put(feed, Lists.<IFeedMessage>newArrayList());
                }
                List<IFeedMessage> feedMessages = groupedMessages.get(feed);
                for (IFeedMessage message : job.getMessages()) {
                    if (!feedMessages.contains(message)) {
                        feedMessages.add(message);
                        newMessage = true;
                    }
                }

                if (groupedMessages.size() > 0 && newMessage) {
                    bus.post(new NewFeedItemsEvent());
                }

                if (!preferences.isEnabled() || !isFeedEnabled(feed)) {
                    return;
                }
                if (feed.getPollingInterval() != null) {
                    job.schedule(TimeUnit.MINUTES.toMillis(parseLong(feed.getPollingInterval())));
                    return;
                }
                job.schedule(TimeUnit.MINUTES.toMillis(DEFAULT_DELAY));
            }
        });
        if (NewsJob.getJobManager().find(job).length < 1) {
            job.schedule(START_DELAY);
        }
    }

    @Override
    public Map<FeedDescriptor, List<IFeedMessage>> getMessages(final int countPerFeed) {
        return ImmutableMap
                .copyOf(Maps.transformValues(groupedMessages, new Function<List<IFeedMessage>, List<IFeedMessage>>() {

                    @Override
                    public List<IFeedMessage> apply(List<IFeedMessage> input) {
                        return FluentIterable.from(input).limit(countPerFeed).toList();
                    }
                }));
    }

    private boolean isFeedEnabled(FeedDescriptor feed) {
        for (FeedDescriptor fd : preferences.getEnabledFeedDescriptors()) {
            if (feed.getId().equals(fd.getId())) {
                return true;
            }
        }
        return false;
    }

}
