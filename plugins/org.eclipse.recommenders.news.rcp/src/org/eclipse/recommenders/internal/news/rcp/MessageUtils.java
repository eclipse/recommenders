/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp;

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.recommenders.news.rcp.IFeedMessage;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MessageUtils {

    public static final int TODAY = 0;
    public static final int YESTERDAY = 1;
    public static final int THIS_WEEK = 2;
    public static final int THIS_MONTH = 3;
    public static final int OLDER = 4;

    public static boolean containsUnreadMessages(Map<FeedDescriptor, List<IFeedMessage>> map) {
        if (map == null) {
            return false;
        }
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
        Preconditions.checkNotNull(messages);
        Map<FeedDescriptor, List<IFeedMessage>> result = Maps.newHashMap();
        for (Entry<FeedDescriptor, List<IFeedMessage>> entry : messages.entrySet()) {
            List<IFeedMessage> list = updateMessages(entry);
            if (!list.isEmpty()) {
                result.put(entry.getKey(), list);
            }
        }
        return sortByDate(result);
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
        if (messages == null) {
            return 0;
        }
        int counter = 0;
        for (IFeedMessage message : messages) {
            if (!message.isRead()) {
                counter++;
            }
        }
        return counter;
    }

    public static List<IFeedMessage> mergeMessages(Map<FeedDescriptor, List<IFeedMessage>> messages) {
        Preconditions.checkNotNull(messages);
        List<IFeedMessage> result = Lists.newArrayList();
        for (Map.Entry<FeedDescriptor, List<IFeedMessage>> entry : messages.entrySet()) {
            result.addAll(entry.getValue());
        }
        return result;
    }

    public static Map<FeedDescriptor, List<IFeedMessage>> sortByDate(Map<FeedDescriptor, List<IFeedMessage>> map) {
        if (map == null) {
            return Maps.newHashMap();
        }
        for (Map.Entry<FeedDescriptor, List<IFeedMessage>> entry : map.entrySet()) {
            List<IFeedMessage> list = entry.getValue();
            Collections.sort(list, new Comparator<IFeedMessage>() {
                @Override
                public int compare(IFeedMessage lhs, IFeedMessage rhs) {
                    return rhs.getDate().compareTo(lhs.getDate());
                }
            });
            entry.setValue(list);
        }
        return map;
    }

    public static List<List<IFeedMessage>> splitMessagesByAge(List<IFeedMessage> messages) {
        if (messages == null) {
            return Collections.emptyList();
        }
        Calendar c = Calendar.getInstance();
        Date today = new Date();
        Date yesterday = new Date();
        Date thisWeek = new Date();
        Date thisMonth = new Date();
        c.setTime(today);
        c.add(Calendar.DATE, -1);
        today = c.getTime();
        c.setTime(yesterday);
        c.add(Calendar.DATE, -2);
        yesterday = c.getTime();
        c.setTime(thisWeek);
        c.add(Calendar.DATE, -8);
        thisWeek = c.getTime();
        c.setTime(thisMonth);
        c.add(Calendar.DATE, -31);
        thisMonth = c.getTime();
        List<List<IFeedMessage>> result = Lists.newArrayList();
        for (int i = 0; i < OLDER + 1; i++) {
            List<IFeedMessage> list = Lists.newArrayList();
            result.add(list);
        }
        for (IFeedMessage message : messages) {
            if (message.getDate().after(today)) {
                result.get(TODAY).add(message);
            } else if (message.getDate().after(yesterday)) {
                result.get(YESTERDAY).add(message);
            } else if (message.getDate().after(thisWeek)) {
                result.get(THIS_WEEK).add(message);
            } else if (message.getDate().after(thisMonth)) {
                result.get(THIS_MONTH).add(message);
            } else {
                result.get(OLDER).add(message);
            }
        }
        return result;
    }

}
