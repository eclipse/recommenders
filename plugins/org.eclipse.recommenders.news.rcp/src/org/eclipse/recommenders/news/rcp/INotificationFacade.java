package org.eclipse.recommenders.news.rcp;

import java.util.List;
import java.util.Map;

import org.eclipse.recommenders.internal.news.rcp.FeedDescriptor;

import com.google.common.eventbus.EventBus;

public interface INotificationFacade {

    void displayNotification(Map<FeedDescriptor, List<IFeedMessage>> messages, EventBus bus);

}
