/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp;

import static org.eclipse.recommenders.internal.news.rcp.FeedEvents.createNewFeedItemsEvent;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.eclipse.recommenders.internal.news.rcp.FeedEvents.FeedMessageReadEvent;
import org.eclipse.recommenders.news.rcp.IFeedMessage;
import org.eclipse.recommenders.news.rcp.IJobFacade;
import org.eclipse.recommenders.news.rcp.INewsFeedProperties;
import org.eclipse.recommenders.news.rcp.INewsService;
import org.eclipse.recommenders.news.rcp.IPollFeedJob;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class NewsService implements INewsService {

    private final NewsRcpPreferences preferences;
    private final INewsFeedProperties newsFeedProperties;
    private final Set<String> readIds;
    private final IJobFacade jobFacade;
    private final EventBus bus;

    private boolean override;

    private HashMap<FeedDescriptor, List<IFeedMessage>> groupedMessages = Maps.newHashMap();

    public NewsService(NewsRcpPreferences preferences, EventBus bus, INewsFeedProperties newsFeedProperties,
            IJobFacade jobFacade) {
        this.preferences = preferences;
        this.bus = bus;
        bus.register(this);
        this.newsFeedProperties = newsFeedProperties;
        readIds = newsFeedProperties.getReadIds();
        this.jobFacade = jobFacade;
    }

    @Override
    public void start() {
        if (!preferences.isEnabled()) {
            return;
        }
        jobFacade.schedule(this, TimeUnit.MINUTES.toMillis(preferences.getPollingInterval()));
    }

    @Override
    public void forceStart() {
        if (!preferences.isEnabled()) {
            return;
        }
        override = true;
        jobFacade.forceSchedule(this, TimeUnit.MINUTES.toMillis(preferences.getPollingInterval()));
    }

    @Override
    public Map<FeedDescriptor, List<IFeedMessage>> getMessages(final int countPerFeed) {
        Map<FeedDescriptor, List<IFeedMessage>> transformedMap = Maps.transformValues(groupedMessages,
                new Function<List<IFeedMessage>, List<IFeedMessage>>() {

                    @Override
                    public List<IFeedMessage> apply(List<IFeedMessage> input) {
                        return FluentIterable.from(input).limit(countPerFeed).filter(new Predicate<IFeedMessage>() {

                            @Override
                            public boolean apply(IFeedMessage input) {
                                return !readIds.contains(input.getId());
                            }
                        }).toList();
                    }
                });
        return Maps.filterValues(transformedMap, new Predicate<List<IFeedMessage>>() {

            @Override
            public boolean apply(List<IFeedMessage> input) {
                if (input == null) {
                    return false;
                }
                return !input.isEmpty();
            }

        });
    }

    @Subscribe
    @Override
    public void handleMessageRead(FeedMessageReadEvent event) {
        readIds.add(event.getId());
        newsFeedProperties.writeReadIds(readIds);
    }

    @Override
    public void jobDone(IPollFeedJob job) {
        override = false;
        boolean newMessage = false;
        Map<FeedDescriptor, List<IFeedMessage>> messages = job.getMessages();
        for (Map.Entry<FeedDescriptor, List<IFeedMessage>> entry : messages.entrySet()) {
            if (!groupedMessages.containsKey(entry.getKey())) {
                groupedMessages.put(entry.getKey(), entry.getValue());
                if (!entry.getValue().isEmpty()) {
                    newMessage = true;
                }
            }

            for (IFeedMessage message : entry.getValue()) {
                if (!groupedMessages.get(entry.getKey()).contains(message)) {
                    groupedMessages.get(entry.getKey()).add(message);
                    newMessage = true;
                }
            }
        }
        if (!groupedMessages.isEmpty() && newMessage) {
            bus.post(createNewFeedItemsEvent());
            newsFeedProperties.writePollDates(job.getPollDates());
        }

        if (!preferences.isEnabled()) {
            return;
        }
    }

    @Override
    public boolean shouldPoll(FeedDescriptor feed, boolean override) {
        if (!feed.isEnabled()) {
            return false;
        }
        if (override) {
            return true;
        }
        int pollingInterval = preferences.getPollingInterval().intValue();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, pollingInterval);
        Date lhs = calendar.getTime();
        for (Map.Entry<String, Date> entry : newsFeedProperties.getPollDates().entrySet()) {
            if (entry.getKey().equals(feed.getId())) {
                if (entry.getValue().after(lhs)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Set<FeedDescriptor> getFeedsToPoll(boolean override) {
        Set<FeedDescriptor> feeds = Sets.newHashSet();
        for (final FeedDescriptor feed : preferences.getFeedDescriptors()) {
            if (shouldPoll(feed, override)) {
                feeds.add(feed);
            }
        }
        return feeds;
    }

    @Override
    public void removeFeed(FeedDescriptor feed) {
        if (groupedMessages.containsKey(feed)) {
            groupedMessages.remove(feed);
        }
    }

    @Override
    public boolean isOverride() {
        return override;
    }

}
