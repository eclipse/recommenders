package org.eclipse.recommenders.internal.news.rcp;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.recommenders.news.rcp.IFeedMessage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

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

    public static Map<FeedDescriptor, List<IFeedMessage>> getLatestMessages(
            Map<FeedDescriptor, List<IFeedMessage>> messages) {
        Map<FeedDescriptor, List<IFeedMessage>> result = Maps.newHashMap();
        for (Entry<FeedDescriptor, List<IFeedMessage>> entry : messages.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                result.put(entry.getKey(), updateMessages(entry));
            }
        }
        return result;
    }

    public static List<IFeedMessage> updateMessages(Entry<FeedDescriptor, List<IFeedMessage>> entry) {
        NewsFeedProperties properties = new NewsFeedProperties();
        List<IFeedMessage> feedMessages = Lists.newArrayList();
        for (IFeedMessage message : entry.getValue()) {
            if (properties.getDates(Constants.FILENAME_FEED_DATES).get(entry.getKey().getId()) == null) {
                feedMessages.add(message);
            } else if (message.getDate()
                    .after(properties.getDates(Constants.FILENAME_FEED_DATES).get(entry.getKey().getId()))) {
                feedMessages.add(message);
            }
        }
        return feedMessages;
    }

    public static int getUnreadMessagesNumber(List<IFeedMessage> messages) {
        int counter = 0;
        for (IFeedMessage message : messages) {
            if (!message.isRead()) {
                counter++;
            }
        }
        return counter;
    }

    public static List<IFeedMessage> mergeMessages(Map<FeedDescriptor, List<IFeedMessage>> messages) {
        List<IFeedMessage> result = Lists.newArrayList();
        for (Map.Entry<FeedDescriptor, List<IFeedMessage>> entry : messages.entrySet()) {
            result.addAll(entry.getValue());
        }
        return result;
    }

}
