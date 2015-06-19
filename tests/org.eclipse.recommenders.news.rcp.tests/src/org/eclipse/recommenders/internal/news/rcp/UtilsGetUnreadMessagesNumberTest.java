/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;

import org.eclipse.recommenders.news.rcp.IFeedMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
public class UtilsGetUnreadMessagesNumberTest {
    private static final int EXPECT_ONE = 1;
    private static final int EXPECT_TWO = 2;
    private static final int EXPECT_ZERO = 0;

    private List<IFeedMessage> inputMessages;
    private int expectedResult;

    public UtilsGetUnreadMessagesNumberTest(List<IFeedMessage> inputMessages, int expectedResult) {
        this.inputMessages = inputMessages;
        this.expectedResult = expectedResult;
    }

    @Parameters
    public static Collection<Object[]> scenarios() {
        List<Object[]> scenarios = Lists.newArrayList();

        scenarios.add(new Object[] { null, EXPECT_ZERO });
        scenarios.add(new Object[] { Lists.newArrayList(), EXPECT_ZERO });
        scenarios.add(new Object[] { TestUtils.mockMessages(true), EXPECT_ZERO });
        scenarios.add(new Object[] { TestUtils.mockMessages(false), EXPECT_ONE });
        scenarios.add(new Object[] { TestUtils.mockMessages(true, false, false), EXPECT_TWO });

        return scenarios;
    }

    @Test
    public void testGetUnreadMessages() {
        assertEquals(MessageUtils.getUnreadMessagesNumber(inputMessages), expectedResult);
    }
}
