/**
* Copyright (c) 2015 Pawel Nowak.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*/
package org.eclipse.recommenders.news.rcp.parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
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

@SuppressWarnings("restriction")
public class RssService {

    private static final String ID_EVENT_SERVICE_MESSAGE = "org.eclipse.recommenders.news.rcp.events.feed"; //$NON-NLS-1$
    private static final long RECHECK_DELAY = 15 * 1000;
    private static final long START_DELAY = 0;
    private String eventId;
    private final NotificationEnvironment environment;
    private ArrayList<ServiceMessage> messages = new ArrayList<>();
    private Job messageCheckJob;
    private final long checktime = 0;
    private ArrayList<String> urls;

    public RssService(String eventId, NotificationEnvironment environment, long pollingInterval, List<String> urls) {
        this.eventId = ID_EVENT_SERVICE_MESSAGE;
        this.environment = new NotificationEnvironment();
        this.urls = (ArrayList<String>) urls;
    }

    public void start() {
        if (messageCheckJob == null) {
            messageCheckJob = new Job("Checking for new service message") { //$NON-NLS-1$
                @Override
                protected IStatus run(IProgressMonitor monitor) {
                    try {
                        // refresh(monitor);
                        for (String url : urls) {
                            pollFeed(monitor, null, url, null);
                        }
                        return Status.OK_STATUS;
                    } catch (Throwable t) {
                        // fail silently
                        return Status.CANCEL_STATUS;
                    }
                }

            };
            messageCheckJob.setSystem(true);
            messageCheckJob.setPriority(Job.DECORATE);
            messageCheckJob.addJobChangeListener(new JobChangeAdapter() {
                @Override
                public void done(IJobChangeEvent event) {
                    if (messageCheckJob != null) {
                        messageCheckJob.schedule(RECHECK_DELAY);
                    }
                }
            });
        }
        if (checktime == 0) {
            messageCheckJob.schedule(START_DELAY);
        } else {
            long nextCheckTime = checktime + RECHECK_DELAY;
            long now = System.currentTimeMillis();
            if (nextCheckTime < now) {
                messageCheckJob.schedule(START_DELAY);
            } else if (nextCheckTime > now) {
                if (nextCheckTime - now < START_DELAY) {
                    messageCheckJob.schedule(START_DELAY);
                } else {
                    messageCheckJob.schedule(nextCheckTime - now);
                }
            }
        }
    }

    public void stop() {
        if (messageCheckJob != null) {
            messageCheckJob.cancel();
            messageCheckJob = null;
        }
    }

    public int pollFeed(IProgressMonitor monitor, String lastModified, String url, String eTag) {
        // List<? extends ServiceMessage> messages = null;
        int status = -1;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            if (lastModified != null && lastModified.length() > 0) {
                try {
                    connection.setIfModifiedSince(Long.parseLong(lastModified));
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
            if (eTag != null && eTag.length() > 0) {
                connection.setRequestProperty("If-None-Match", eTag); //$NON-NLS-1$
            }

            try {
                connection.connect();

                status = connection.getResponseCode();
                // this is old condition, but hadnt got implemtation of IProgressMonitor, so commented it out
                // if (status == HttpURLConnection.HTTP_OK && !monitor.isCanceled()) {
                if (status == HttpURLConnection.HTTP_OK) {
                    lastModified = connection.getHeaderField("Last-Modified"); //$NON-NLS-1$
                    eTag = connection.getHeaderField("ETag"); //$NON-NLS-1$

                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    try {
                        messages.addAll(readMessages(in, monitor));
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

    private List<? extends ServiceMessage> readMessages(InputStream in, IProgressMonitor monitor) throws IOException {
        FeedReader reader = new FeedReader(eventId, environment);
        reader.parse(in, monitor);
        return reader.getEntries();
    }

    private void removeDuplicates(List<ServiceMessage> messagesList) {
        Set<ServiceMessage> s = new TreeSet<ServiceMessage>(new Comparator<ServiceMessage>() {

            @Override
            public int compare(ServiceMessage lhs, ServiceMessage rhs) {
                // if (lhs.getTitle().equalsIgnoreCase(rhs.getTitle())) {
                // return 0;
                // }
                // return 1;
                return lhs.getTitle().compareTo(rhs.getTitle());
            }
        });
        s.addAll(messagesList);
        messages = new ArrayList<ServiceMessage>();
        messages.addAll(s);
    }

    private void logStatus(IStatus status) {
        // if (!statusLogged) {
        // statusLogged = true;
        // StatusHandler.log(status);
        // }
    }

    public List<ServiceMessage> getLastMessages() {
        // edit so it will return only newest 3 ServiceMessage objects?
        return messages;
    }
}
