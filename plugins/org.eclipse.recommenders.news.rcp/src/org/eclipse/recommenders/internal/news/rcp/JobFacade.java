/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.mylyn.commons.notifications.core.NotificationEnvironment;
import org.eclipse.recommenders.news.rcp.IFeedMessage;
import org.eclipse.recommenders.news.rcp.IJobFacade;
import org.eclipse.recommenders.news.rcp.IPollFeedJob;

@SuppressWarnings("restriction")
public class JobFacade implements IJobFacade {

    private static final long START_DELAY = 0;

    public boolean jobExists(FeedDescriptor feed, NewsRcpPreferences preferences, NotificationEnvironment environment) {
        PollFeedJob job = new PollFeedJob(feed, preferences, environment);
        if (PollFeedJob.getJobManager().find(job).length > 0) {
            return true;
        }
        return false;
    }

    public PollFeedJob getPollFeedJob(final FeedDescriptor feed, final NewsRcpPreferences preferences,
            final NotificationEnvironment environment, final RssService service) {
        if (!jobExists(feed, preferences, environment)) {
            final PollFeedJob job = new PollFeedJob(feed, preferences, environment);
            job.setSystem(true);
            job.setPriority(Job.DECORATE);
            job.addJobChangeListener(new JobChangeAdapter() {
                @Override
                public void done(IJobChangeEvent event) {
                    service.jobDone(feed, job);
                }
            });
            return job;
        }
        return null;
    }

    public void schedule(PollFeedJob job) {
        job.schedule(START_DELAY);
    }

    @Override
    public void schedule() {
        // TODO schedule the job

    }

    @Override
    public IPollFeedJob getJob() {
        // TODO return single job instance
        return null;
    }

    @Override
    public Map<FeedDescriptor, Date> whenWereFeedsLastPolled() {
        // TODO return map of feeds and its last polled date
        return null;
    }

    @Override
    public void jobDone() {
        // TODO rescheduling job, grouping messages

    }

    @Override
    public Map<FeedDescriptor, List<IFeedMessage>> getMessages() {
        // TODO return messages
        return null;
    }
}
