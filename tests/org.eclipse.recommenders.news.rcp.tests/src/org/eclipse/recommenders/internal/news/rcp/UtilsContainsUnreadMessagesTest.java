/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp;

import static org.eclipse.recommenders.internal.news.rcp.TestUtils.enabled;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.recommenders.news.rcp.IFeedMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
public class UtilsContainsUnreadMessagesTest {

    private static final boolean READ = true;
    private static final boolean UNREAD = false;
    private static final boolean EXPECT_TRUE = true;
    private static final boolean EXPECT_FALSE = false;

    private Map<FeedDescriptor, List<IFeedMessage>> inputMap;
    private boolean expectedResult;

    public UtilsContainsUnreadMessagesTest(Map<FeedDescriptor, List<IFeedMessage>> inputMap, boolean expectedResult) {
        this.inputMap = inputMap;
        this.expectedResult = expectedResult;
    }

    @Parameters
    public static Collection<Object[]> scenarios() {
        List<Object[]> scenarios = Lists.newArrayList();

        scenarios.add(new Object[] { null, EXPECT_FALSE });
        scenarios.add(new Object[] { ImmutableMap.of(mockFeed("emptyFeed"), mockMessages()), EXPECT_FALSE });
        scenarios.add(new Object[] { ImmutableMap.of(mockFeed("oneRead"), mockMessages(READ)), EXPECT_FALSE });
        scenarios.add(new Object[] { ImmutableMap.of(mockFeed("oneUnread"), mockMessages(UNREAD)), EXPECT_TRUE });
        scenarios.add(new Object[] { ImmutableMap.of(mockFeed("oneUnreadOneRead"), mockMessages(UNREAD, READ)),
                EXPECT_TRUE });
        scenarios.add(new Object[] {
                ImmutableMap.of(mockFeed("unreadFeed"), mockMessages(UNREAD), mockFeed("readFeed"), mockMessages(READ)),
                EXPECT_TRUE });

        return scenarios;
    }

    private static FeedDescriptor mockFeed(String name) {
        return enabled(name);
    }

    private static List<IFeedMessage> mockMessages(boolean... readMessages) {
        List<IFeedMessage> feedMessages = Lists.newArrayList();
        for (boolean isRead : readMessages) {
            IFeedMessage message = mock(IFeedMessage.class);
            when(message.isRead()).thenReturn(isRead);
            feedMessages.add(message);
        }
        return feedMessages;
    }

    @Test
    public void testContainsUnreadMessages() {
        assertEquals(MessageUtils.containsUnreadMessages(inputMap), expectedResult);
    }
}
