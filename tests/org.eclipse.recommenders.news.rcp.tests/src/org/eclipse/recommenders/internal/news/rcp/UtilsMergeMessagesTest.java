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
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.recommenders.news.rcp.IFeedMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RunWith(Parameterized.class)
public class UtilsMergeMessagesTest {
    private static final int TWO_FEEDS_TWO_MESSAGES = 1;
    private static final int NULL_MAP = 3;
    private static final int ONE_FEED_ONE_MESSAGE = 2;
    private static final int EMPTY_MAP = 0;

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
    public static Collection<Object[]> primeNumbers() {
        return Arrays.asList(new Object[][] { { prepareMap(TWO_FEEDS_TWO_MESSAGES), 4 },
                { prepareMap(ONE_FEED_ONE_MESSAGE), 1 }, { prepareMap(NULL_MAP), 0 }, { prepareMap(EMPTY_MAP), 0 } });
    }

    private static Map<FeedDescriptor, List<IFeedMessage>> prepareMap(int instruction) {
        if (instruction == NULL_MAP) {
            return null;
        }
        HashMap<FeedDescriptor, List<IFeedMessage>> messages = Maps.newHashMap();
        if (instruction == EMPTY_MAP) {
            return messages;
        }
        FeedDescriptor feed = enabled("rndm");
        List<IFeedMessage> iFeedMessages = Lists.newArrayList();
        if (instruction == ONE_FEED_ONE_MESSAGE) {
            FeedMessage message = mock(FeedMessage.class);
            iFeedMessages.add(message);
            messages.put(feed, iFeedMessages);
        } else if (instruction == TWO_FEEDS_TWO_MESSAGES) {
            FeedMessage message = mock(FeedMessage.class);
            iFeedMessages.add(message);
            iFeedMessages.add(message);
            FeedDescriptor secondFeed = enabled("test");
            messages.put(secondFeed, iFeedMessages);
            messages.put(feed, iFeedMessages);
        }
        return messages;
    }

    @Test
    public void testMergedMessages() {
        assertEquals(Utils.mergeMessages(inputMap).size(), expectedResult);
    }
}
