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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
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
    public static final int LAST_WEEK = 3;
    public static final int THIS_MONTH = 4;
    public static final int LAST_MONTH = 5;
    public static final int THIS_YEAR = 6;
    public static final int OLDER = 7;

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

        List<List<IFeedMessage>> result = Lists.newArrayList();
        for (int i = 0; i <= OLDER; i++) {
            List<IFeedMessage> list = Lists.newArrayList();
            result.add(list);
        }
        System.out.println(result.size());
        for (IFeedMessage message : messages) {
            System.out.println(message.getTitle() + " " + message.getDate());
            if (message.getDate().after(getDate(TODAY)) || message.getDate().equals(getDate(TODAY))) {
                result.get(TODAY).add(message);
            } else if (message.getDate().after(getDate(YESTERDAY)) || message.getDate().equals(getDate(YESTERDAY))) {
                result.get(YESTERDAY).add(message);
            } else if (message.getDate().after(getDate(THIS_WEEK)) || message.getDate().equals(getDate(THIS_WEEK))) {
                result.get(THIS_WEEK).add(message);
            } else if (message.getDate().after(getDate(LAST_WEEK)) || message.getDate().equals(getDate(LAST_WEEK))) {
                result.get(LAST_WEEK).add(message);
            } else if (message.getDate().after(getDate(THIS_MONTH)) || message.getDate().equals(getDate(THIS_MONTH))) {
                result.get(THIS_MONTH).add(message);
            } else if (message.getDate().after(getDate(LAST_MONTH)) || message.getDate().equals(getDate(LAST_MONTH))) {
                result.get(LAST_MONTH).add(message);
            } else if (message.getDate().after(getDate(THIS_YEAR)) || message.getDate().equals(getDate(THIS_YEAR))) {
                result.get(THIS_YEAR).add(message);
            } else if (message.getDate().before(getDate(OLDER)) || message.getDate().equals(getDate(OLDER))) {
                result.get(OLDER).add(message);
            }
        }
        for (int i = 0; i < OLDER + 1; i++) {
            for (IFeedMessage message : result.get(i)) {
                System.out.println(message.getDate());
            }
        }
        return result;
    }

    public static Date getDate(int period) {
        Date date = new Date();
        Calendar calendar = GregorianCalendar.getInstance(Locale.getDefault());
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        if (period == TODAY) {
            return calendar.getTime();
        } else if (period == YESTERDAY) {
            calendar.add(Calendar.DATE, -1);
        } else if (period == THIS_WEEK) {
            int firstDayOfWeek = calendar.getFirstDayOfWeek();
            calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
        } else if (period == LAST_WEEK) {
            int firstDayOfWeek = calendar.getFirstDayOfWeek();
            calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
            calendar.add(Calendar.DATE, -1);
            calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
        } else if (period == THIS_MONTH) {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        } else if (period == LAST_MONTH) {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DATE, -1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        } else if (period == THIS_YEAR) {
            calendar.set(Calendar.MONTH, 0);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        } else {
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) - 1);
            calendar.set(Calendar.MONTH, 11);
            calendar.set(Calendar.DAY_OF_MONTH, 31);
        }
        return calendar.getTime();
    }

}
