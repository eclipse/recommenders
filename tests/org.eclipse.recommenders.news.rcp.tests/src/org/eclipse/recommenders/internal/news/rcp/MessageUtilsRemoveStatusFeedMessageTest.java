/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.recommenders.news.rcp.IFeedMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@RunWith(Parameterized.class)
public class MessageUtilsRemoveStatusFeedMessageTest {
    private static final int EXPECT_ZERO = 0;
    private static final int EXPECT_ONE = 1;
    private static final int EXPECT_TWO = 2;

    private Map<FeedDescriptor, List<IFeedMessage>> inputMessages;
    private int expectedResult;

    public MessageUtilsRemoveStatusFeedMessageTest(Map<FeedDescriptor, List<IFeedMessage>> inputMessages,
            int expectedResult) {
        this.inputMessages = inputMessages;
        this.expectedResult = expectedResult;
    }

    @Parameters
    public static Collection<Object[]> scenarios() {
        List<Object[]> scenarios = Lists.newArrayList();

        scenarios.add(new Object[] { null, EXPECT_ZERO });
        scenarios.add(new Object[] { Collections.emptyMap(), EXPECT_ZERO });
        scenarios.add(new Object[] { mockMessagesMap(0, 0), EXPECT_ZERO });
        scenarios.add(new Object[] { mockMessagesMap(0, 1), EXPECT_ZERO });
        scenarios.add(new Object[] { mockMessagesMap(1, 0), EXPECT_ONE });
        scenarios.add(new Object[] { mockMessagesMap(2, 0), EXPECT_TWO });
        scenarios.add(new Object[] { mockMessagesMap(2, 2), EXPECT_TWO });

        return scenarios;
    }

    @Test
    public void testGetUnreadMessages() {
        assertThat(MessageUtils.removeStatusFeedMessages(inputMessages).size(), is(expectedResult));
    }

    private static Map<FeedDescriptor, List<IFeedMessage>> mockMessagesMap(int numberOfFeedMessagesEntries,
            int numberOfStatusFeedMessagesEntries) {
        Map<FeedDescriptor, List<IFeedMessage>> result = Maps.newHashMap();
        for (int i = 0; i < numberOfFeedMessagesEntries; i++) {
            List<IFeedMessage> messages = TestUtils.mockMessages(false);
            result.put(TestUtils.mockFeed("someFeed" + i), messages);
        }
        for (int i = 0; i < numberOfStatusFeedMessagesEntries; i++) {
            List<IFeedMessage> messages = Lists.newArrayList();
            messages.add(new StatusFeedMessage("id" + i, "", "someTitle"));
            result.put(TestUtils.mockFeed("someOtherFeed" + i), messages);
        }
        return result;
    }
}
