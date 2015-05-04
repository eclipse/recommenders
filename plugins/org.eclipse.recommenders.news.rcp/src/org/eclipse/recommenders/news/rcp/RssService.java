/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.news.rcp;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.mylyn.commons.notifications.core.NotificationEnvironment;
import org.eclipse.mylyn.internal.commons.notifications.feed.FeedReader;
import org.eclipse.mylyn.internal.commons.notifications.feed.INotificationsFeed;
import org.eclipse.mylyn.internal.commons.notifications.feed.ServiceMessage;
import org.eclipse.recommenders.internal.news.rcp.FeedDescriptor;

@SuppressWarnings("restriction")
public class RssService {

    private static final String ID_EVENT_SERVICE_MESSAGE = "org.eclipse.recommenders.news.rcp.events.feed"; //$NON-NLS-1$
    private static final long MINUTE = 60 * 1000;
    // 24 hours
    private static final long DEFAULT_DELAY = 1440;
    private static final long START_DELAY = 0;
    private String eventId;
    private final NotificationEnvironment environment;
    private ArrayList<ServiceMessage> messages = new ArrayList<ServiceMessage>();
    private ArrayList<Job> messageCheckJobs = new ArrayList<Job>();
    HashMap<String, ArrayList<String>> groupedMessages = new HashMap<String, ArrayList<String>>();

    public RssService(NotificationEnvironment environment) {
        this.environment = new NotificationEnvironment();
    }

    public void start(List<FeedDescriptor> feedsList) {
        for (final FeedDescriptor feed : feedsList) {
            // Job messageCheckJob;
            // messageCheckJobs.add(new Job(""));
            // if (messageCheckJob == null) {
            final Job messageCheckJob = new Job(feed.getId()) {
                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    try {
                        pollFeed(monitor, feed.getUrl(), feed.getId());
                        return Status.OK_STATUS;
                    } catch (Throwable t) {
                        // fail silently
                        return Status.CANCEL_STATUS;
                    }
                }

                @Override
                public boolean shouldRun() {
                    return true;
                }

            };
            messageCheckJob.setSystem(true);
            messageCheckJob.setPriority(Job.DECORATE);
            messageCheckJob.addJobChangeListener(new JobChangeAdapter() {
                @Override
                public void done(IJobChangeEvent event) {
                    if (feed.getPollingInterval() != null) {
                        messageCheckJob.schedule(Long.parseLong(feed.getPollingInterval()) * MINUTE);
                        return;
                    }
                    messageCheckJob.schedule(DEFAULT_DELAY * MINUTE);
                }
            });
            messageCheckJob.schedule(START_DELAY);
            messageCheckJobs.add(messageCheckJob);
        }
    }

    public int pollFeed(IProgressMonitor monitor, String url, String eventId) {
        int status = -1;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            try {
                connection.connect();
                status = connection.getResponseCode();
                // this is old condition, but hadnt got implemtation of IProgressMonitor, so commented it out
                // if (status == HttpURLConnection.HTTP_OK && !monitor.isCanceled()) {
                if (status == HttpURLConnection.HTTP_OK) {

                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    try {
                        messages.addAll(readMessages(in, monitor, eventId));
                        removeDuplicates(messages);
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

        if (messages != null && messages.size() > 0) {
            // notifyListeners(messages);
        }
        return status;
    }

    private List<? extends ServiceMessage> readMessages(InputStream in, IProgressMonitor monitor, String eventId)
            throws IOException {
        FeedReader reader = new FeedReader(eventId, environment);
        reader.parse(in, monitor);
        return reader.getEntries();
    }

    private void removeDuplicates(List<ServiceMessage> messagesList) {
        Set<ServiceMessage> s = new TreeSet<ServiceMessage>(new Comparator<ServiceMessage>() {

            @Override
            public int compare(ServiceMessage lhs, ServiceMessage rhs) {
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        });
        s.addAll(messagesList);
        messages = new ArrayList<ServiceMessage>();
        messages.addAll(s);
    }

    private void groupMessages(List<ServiceMessage> messagesList) {

    }

    private void logStatus(IStatus status) {
        // if (!statusLogged) {
        // statusLogged = true;
        // StatusHandler.log(status);
        // }
    }

    public List<ServiceMessage> getLastMessages() {
        return messages;
    }
}
