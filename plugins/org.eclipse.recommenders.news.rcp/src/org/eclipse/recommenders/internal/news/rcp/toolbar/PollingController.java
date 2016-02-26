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
package org.eclipse.recommenders.internal.news.rcp.toolbar;

import static java.util.concurrent.TimeUnit.MINUTES;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.recommenders.internal.news.rcp.FeedDescriptor;
import org.eclipse.recommenders.internal.news.rcp.NewsRcpPreferences;
import org.eclipse.recommenders.internal.news.rcp.PreferenceConstants;
import org.eclipse.recommenders.internal.news.rcp.l10n.Messages;
import org.eclipse.recommenders.news.api.poll.INewsPollingService;
import org.eclipse.recommenders.news.api.poll.PollingPolicy;
import org.eclipse.recommenders.news.api.poll.PollingRequest;

@Creatable
@Singleton
public class PollingController {

    private final class PeriodicPollingJob extends Job {

        private PeriodicPollingJob() {
            super(Messages.POLL_FEED_JOB_NAME);
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            try {
                return pollFeeds(preferences.getFeedDescriptors(), monitor);
            } finally {
                this.schedule(MINUTES.toMillis(pollingInterval));
            }
        }

        private IStatus pollFeeds(List<FeedDescriptor> feeds, IProgressMonitor monitor) {
            List<PollingRequest> requests = new ArrayList<>();
            for (FeedDescriptor feed : feeds) {
                if (!feed.isEnabled()) {
                    continue;
                }

                PollingRequest request = new PollingRequest(feed.getUri(),
                        PollingPolicy.every(feed.getPollingInterval(), MINUTES));
                requests.add(request);
            }
            pollingService.poll(requests, monitor);
            return Status.OK_STATUS; // TODO Real error status
        }
    }

    private final NewsRcpPreferences preferences;
    private final INewsPollingService pollingService;

    private long pollingInterval;

    /**
     * <code>null</code> iff news is disabled.
     */
    private Job job;

    @Inject
    public PollingController(NewsRcpPreferences preferences, INewsPollingService pollingService) {
        this.preferences = preferences;
        this.pollingService = pollingService;
    }

    @Inject
    public void setPollingInterval(@Preference(PreferenceConstants.POLLING_INTERVAL) long pollingInterval) {
        this.pollingInterval = pollingInterval;
    }

    @Inject
    public synchronized void setEnabled(@Preference(PreferenceConstants.NEWS_ENABLED) boolean enabled,
            @Preference(PreferenceConstants.POLLING_DELAY) int startupDelay) {
        if (enabled) {
            job = new PeriodicPollingJob();
            job.setPriority(Job.DECORATE);
            job.schedule(MINUTES.toMillis(startupDelay));
        } else {
            job.cancel();
            job = null;
        }
    }
}
