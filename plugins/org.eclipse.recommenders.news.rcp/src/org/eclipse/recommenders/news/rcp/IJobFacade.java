package org.eclipse.recommenders.news.rcp;

import java.util.Date;
import java.util.Map;

import org.eclipse.recommenders.internal.news.rcp.FeedDescriptor;

public interface IJobFacade {

    void schedule();

    IPollFeedJob getJob();

    Map<FeedDescriptor, Date> whenWereFeedsLastPolled();

}
