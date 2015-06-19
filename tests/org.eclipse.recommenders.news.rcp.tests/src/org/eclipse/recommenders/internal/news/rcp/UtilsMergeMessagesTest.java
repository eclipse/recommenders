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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.recommenders.news.rcp.IFeedMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
public class UtilsMergeMessagesTest {
    private static final int EXPECT_ONE = 1;
    private static final int EXPECT_TWO = 2;
    private static final int EXPECT_ZERO = 0;
    private static final boolean READ = false;
    private static final boolean UNREAD = true;

    private Map<FeedDescriptor, List<IFeedMessage>> inputMap;
    private int expectedResult;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    public UtilsMergeMessagesTest(Map<FeedDescriptor, List<IFeedMessage>> inputMap, int expectedResult) {
        this.inputMap = inputMap;
        this.expectedResult = expectedResult;
    }

    @Parameters
    public static Collection<Object[]> scenarios() {
        List<Object[]> scenarios = Lists.newArrayList();

        scenarios.add(new Object[] { null, EXPECT_ZERO });
        scenarios.add(new Object[] { ImmutableMap.of(mockFeed("emptyFeed"), TestUtils.mockMessages()), EXPECT_ZERO });
        scenarios.add(new Object[] { ImmutableMap.of(mockFeed("oneRead"), TestUtils.mockMessages(READ)), EXPECT_ONE });
        scenarios.add(
                new Object[] { ImmutableMap.of(mockFeed("oneUnread"), TestUtils.mockMessages(UNREAD)), EXPECT_ONE });
        scenarios.add(new Object[] {
                ImmutableMap.of(mockFeed("oneUnreadOneRead"), TestUtils.mockMessages(UNREAD, READ)), EXPECT_TWO });
        scenarios.add(new Object[] { ImmutableMap.of(mockFeed("unreadFeed"), TestUtils.mockMessages(UNREAD),
                mockFeed("readFeed"), TestUtils.mockMessages(READ)), EXPECT_TWO });

        return scenarios;
    }

    private static FeedDescriptor mockFeed(String name) {
        return enabled(name);
    }

    @Test
    public void testMergedMessages() {
        assertEquals(MessageUtils.mergeMessages(inputMap).size(), expectedResult);
    }
}
