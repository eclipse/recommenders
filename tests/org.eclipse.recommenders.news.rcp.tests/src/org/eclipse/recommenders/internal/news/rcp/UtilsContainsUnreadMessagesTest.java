package org.eclipse.recommenders.internal.news.rcp;

import static org.eclipse.recommenders.internal.news.rcp.TestUtils.enabled;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

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
public class UtilsContainsUnreadMessagesTest {
    private static final int ONE_ENABLED_ELEMENT = 1;
    private static final int NULL_MAP = 3;
    private static final int ONE_DISABLED_ELEMENT = 2;
    private static final int EMPTY_MAP = 0;

    private Map<FeedDescriptor, List<IFeedMessage>> inputMap;
    private boolean expectedResult;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    public UtilsContainsUnreadMessagesTest(Map<FeedDescriptor, List<IFeedMessage>> inputMap, boolean expectedResult) {
        this.inputMap = inputMap;
        this.expectedResult = expectedResult;
    }

    @Parameters
    public static Collection<Object[]> primeNumbers() {
        return Arrays.asList(
                new Object[][] { { prepareMap(ONE_ENABLED_ELEMENT), false }, { prepareMap(ONE_DISABLED_ELEMENT), true },
                        { prepareMap(NULL_MAP), false }, { prepareMap(EMPTY_MAP), false } });
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
        FeedMessage message = mock(FeedMessage.class);
        if (instruction == ONE_DISABLED_ELEMENT) {
            when(message.isRead()).thenReturn(false);
        } else if (instruction == ONE_ENABLED_ELEMENT) {
            when(message.isRead()).thenReturn(true);
        }
        iFeedMessages.add(message);
        messages.put(feed, iFeedMessages);
        return messages;
    }

    @Test
    public void testContainsUnreadMessages() {
        assertEquals(Utils.containsUnreadMessages(inputMap), expectedResult);
    }
}
