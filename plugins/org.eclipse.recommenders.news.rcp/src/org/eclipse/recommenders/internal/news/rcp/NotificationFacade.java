package org.eclipse.recommenders.internal.news.rcp;

import java.util.List;
import java.util.Map;

import org.eclipse.recommenders.internal.news.rcp.notifications.NewsNotificationPopup;
import org.eclipse.recommenders.news.rcp.IFeedMessage;

import com.google.common.eventbus.EventBus;

public class NotificationFacade {

    public void displayNotificaiton(Map<FeedDescriptor, List<IFeedMessage>> messages, EventBus eventBus) {
        new NewsNotificationPopup(messages, eventBus).open();
    }

}
