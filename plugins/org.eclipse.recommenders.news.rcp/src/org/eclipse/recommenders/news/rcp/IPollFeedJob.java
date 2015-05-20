package org.eclipse.recommenders.news.rcp;

import java.util.Collection;

public interface IPollFeedJob {

    Collection<? extends IFeedMessage> getMessages();
}
