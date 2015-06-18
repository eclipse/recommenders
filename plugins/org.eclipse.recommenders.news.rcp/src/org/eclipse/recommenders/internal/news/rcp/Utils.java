package org.eclipse.recommenders.internal.news.rcp;

import java.util.List;
import java.util.Map;

import org.eclipse.recommenders.news.rcp.IFeedMessage;

public class Utils {

    public static boolean containsUnreadMessages(Map<FeedDescriptor, List<IFeedMessage>> map) {
        for (Map.Entry<FeedDescriptor, List<IFeedMessage>> entry : map.entrySet()) {
            for (IFeedMessage message : entry.getValue()) {
                if (!message.isRead()) {
                    return true;
                }
            }
        }
        return false;
    }

}
