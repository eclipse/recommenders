package org.eclipse.recommenders.internal.news.rcp;

import org.eclipse.mylyn.commons.notifications.core.NotificationEnvironment;

@SuppressWarnings("restriction")
public class JobProvider {

    public boolean jobExists(FeedDescriptor feed, NewsRcpPreferences preferences, NotificationEnvironment environment) {
        PollFeedJob job = new PollFeedJob(feed, preferences, environment);
        if (PollFeedJob.getJobManager().find(job).length > 0) {
            return true;
        }
        return false;
    }

    public PollFeedJob getPollFeedJob(FeedDescriptor feed, NewsRcpPreferences preferences,
            NotificationEnvironment environment) {
        if (!jobExists(feed, preferences, environment)) {
            return new PollFeedJob(feed, preferences, environment);
        }
        return null;
    }

}
