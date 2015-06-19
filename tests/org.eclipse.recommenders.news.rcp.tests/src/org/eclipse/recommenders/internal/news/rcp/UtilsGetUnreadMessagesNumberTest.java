/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.recommenders.news.rcp.IFeedMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.MockitoAnnotations;

import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
public class UtilsGetUnreadMessagesNumberTest {
    private static final int ONE_UNREAD_ELEMENT = 1;
    private static final int NULL_LIST = 2;
    private static final int THREE_ELEMENTS_WITH_ONE_READ = 3;
    private static final int EMPTY_LIST = 0;

    private List<IFeedMessage> inputMessages;
    private int expectedResult;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    public UtilsGetUnreadMessagesNumberTest(List<IFeedMessage> inputMessages, int expectedResult) {
        this.inputMessages = inputMessages;
        this.expectedResult = expectedResult;
    }

    @Parameters
    public static Collection<Object[]> primeNumbers() {
        return Arrays.asList(new Object[][] { { prepareMessages(EMPTY_LIST), 0 }, { prepareMessages(NULL_LIST), 0 },
                { prepareMessages(ONE_UNREAD_ELEMENT), 1 }, { prepareMessages(THREE_ELEMENTS_WITH_ONE_READ), 2 } });
    }

    private static List<IFeedMessage> prepareMessages(int instruction) {
        if (instruction == NULL_LIST) {
            return null;
        }
        List<IFeedMessage> result = Lists.newArrayList();
        if (instruction == EMPTY_LIST) {
            return result;
        }
        if (instruction == ONE_UNREAD_ELEMENT) {
            IFeedMessage message = mock(IFeedMessage.class);
            when(message.isRead()).thenReturn(false);
            result.add(message);
        } else if (instruction == THREE_ELEMENTS_WITH_ONE_READ) {
            IFeedMessage messageA = mock(IFeedMessage.class);
            when(messageA.isRead()).thenReturn(false);
            result.add(messageA);
            IFeedMessage messageB = mock(IFeedMessage.class);
            when(messageB.isRead()).thenReturn(false);
            result.add(messageB);
            IFeedMessage messageC = mock(IFeedMessage.class);
            when(messageC.isRead()).thenReturn(true);
            result.add(messageC);
        }
        return result;
    }

    @Test
    public void testGetUnreadMessages() {
        assertEquals(Utils.getUnreadMessagesNumber(inputMessages), expectedResult);
    }
}
