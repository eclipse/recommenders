package org.eclipse.recommenders.internal.news.rcp;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.mylyn.commons.notifications.core.NotificationEnvironment;

@SuppressWarnings("restriction")
public class JobProvider {

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
            schedule(job);
            return job;
        }
        return null;
    }

    private void schedule(PollFeedJob job) {
        job.schedule(START_DELAY);
    }
}
