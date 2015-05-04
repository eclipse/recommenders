package org.eclipse.recommenders.news.rcp;

import java.util.List;
import java.util.Map;

import org.eclipse.recommenders.internal.news.rcp.FeedDescriptor;

public interface IRssService {

    Map<FeedDescriptor, List<IFeedMessage>> getMessages(int countPerFeed);

    void start();

    void start(FeedDescriptor feed);

    public class NewFeedItemsEvent {
    }
}
