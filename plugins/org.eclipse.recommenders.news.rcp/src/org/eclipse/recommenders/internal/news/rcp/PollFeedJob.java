/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.commons.notifications.core.NotificationEnvironment;
import org.eclipse.mylyn.internal.commons.notifications.feed.FeedEntry;
import org.eclipse.mylyn.internal.commons.notifications.feed.FeedReader;
import org.eclipse.recommenders.internal.news.rcp.l10n.Messages;
import org.eclipse.recommenders.news.rcp.IFeedMessage;
import org.eclipse.recommenders.news.rcp.IPollFeedJob;
import org.eclipse.recommenders.utils.Urls;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@SuppressWarnings("restriction")
public class PollFeedJob extends Job implements IPollFeedJob {
    private final NotificationEnvironment environment;
    private final Map<FeedDescriptor, List<IFeedMessage>> groupedMessages = Maps.newHashMap();
    private final Map<FeedDescriptor, Date> pollDates = Maps.newHashMap();

    private Set<FeedDescriptor> feeds = Sets.newHashSet();

    public PollFeedJob(Collection<FeedDescriptor> feeds) {
        super(Messages.POLL_FEED_JOB_NAME);
        Preconditions.checkNotNull(feeds);
        this.environment = new NotificationEnvironment();
        this.feeds.addAll(feeds);
        setSystem(true);
        setPriority(DECORATE);
        setRule(new MutexRule());
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        try {
            for (FeedDescriptor feed : feeds) {
                List<IFeedMessage> messages;
                if (monitor.isCanceled()) {
                    return Status.CANCEL_STATUS;
                }
                HttpURLConnection connection = (HttpURLConnection) feed.getUrl().openConnection();
                try {
                    connection.connect();
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK && !monitor.isCanceled()) {
                        InputStream in = new BufferedInputStream(connection.getInputStream());
                        try {
                            messages = Lists.newArrayList(readMessages(in, monitor, feed.getId()));
                            groupedMessages.put(feed, messages);
                        } finally {
                            in.close();
                        }
                    }
                } finally {
                    connection.disconnect();
                }
                pollDates.put(feed, new Date());
            }
        } catch (Exception e) {
            System.out.println(e.getCause());
            return Status.CANCEL_STATUS;
        }
        return Status.OK_STATUS;
    }

    @Override
    public boolean belongsTo(Object job) {
        if (Objects.equal(Constants.POLL_FEED_JOB_FAMILY, job)) {
            return true;
        }
        return false;
    }

    private List<? extends IFeedMessage> readMessages(InputStream in, IProgressMonitor monitor, String eventId)
            throws IOException {
        FeedReader reader = new FeedReader(eventId, environment);
        reader.parse(in, monitor);
        return FluentIterable.from(reader.getEntries()).transform(new Function<FeedEntry, IFeedMessage>() {

            @Override
            public IFeedMessage apply(FeedEntry entry) {
                return new FeedMessage(entry.getId(), entry.getDate(), entry.getDescription(), entry.getTitle(),
                        Urls.toUrl(entry.getUrl()));
            }
        }).toList();
    }

    @Override
    public Map<FeedDescriptor, List<IFeedMessage>> getMessages() {
        return groupedMessages;
    }

    @Override
    public Map<FeedDescriptor, Date> getPollDates() {
        return pollDates;
    }

    class MutexRule implements ISchedulingRule {

        @Override
        public boolean contains(ISchedulingRule rule) {
            return rule == this;
        }

        @Override
        public boolean isConflicting(ISchedulingRule rule) {
            return rule == this;
        }

    }
}
