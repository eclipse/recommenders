package org.eclipse.recommenders.internal.news.rcp;

import static org.eclipse.recommenders.internal.news.rcp.MessageUtils.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.recommenders.news.rcp.IFeedMessage;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
public class MessageUtilsSplitMessageByAgeTest {

    private List<IFeedMessage> inputMessages;
    private List<List<IFeedMessage>> expectedMessages;

    public MessageUtilsSplitMessageByAgeTest(List<IFeedMessage> inputMessages,
            List<List<IFeedMessage>> expectedMessages) {
        this.inputMessages = inputMessages;
        this.expectedMessages = expectedMessages;
    }

    @Parameters
    public static Collection<Object[]> scenarios() {
        List<Object[]> scenarios = Lists.newArrayList();

        IFeedMessage todayMessage = mockMessage(getDate(TODAY));
        IFeedMessage yesterdayMessage = mockMessage(getDate(YESTERDAY));
        IFeedMessage thisWeekMessage = mockMessage(getDate(THIS_WEEK));
        IFeedMessage lastWeekMessage = mockMessage(getDate(LAST_WEEK));
        IFeedMessage thisMonthMessage = mockMessage(getDate(THIS_MONTH));
        IFeedMessage lastMonthMessage = mockMessage(getDate(LAST_MONTH));
        IFeedMessage thisYearMessage = mockMessage(getDate(THIS_YEAR));
        IFeedMessage olderMessage = mockMessage(getDate(OLDER));

        scenarios.add(new Object[] { null, Collections.emptyList() });
        scenarios.add(new Object[] { ImmutableList.of(todayMessage),
                ImmutableList.of(ImmutableList.of(todayMessage), ImmutableList.of(), ImmutableList.of(),
                        ImmutableList.of(), ImmutableList.of(), ImmutableList.of(), ImmutableList.of(),
                        ImmutableList.of()) });
        scenarios.add(new Object[] { ImmutableList.of(todayMessage, yesterdayMessage),
                ImmutableList.of(ImmutableList.of(todayMessage), ImmutableList.of(yesterdayMessage), ImmutableList.of(),
                        ImmutableList.of(), ImmutableList.of(), ImmutableList.of(), ImmutableList.of(),
                        ImmutableList.of()) });
        scenarios.add(new Object[] {
                ImmutableList.of(todayMessage, yesterdayMessage, thisWeekMessage, lastWeekMessage, thisMonthMessage,
                        lastMonthMessage, thisYearMessage, olderMessage),
                ImmutableList.of(ImmutableList.of(todayMessage, yesterdayMessage, thisWeekMessage, lastWeekMessage,
                        thisMonthMessage, lastMonthMessage, thisYearMessage, olderMessage)) });
        scenarios.add(new Object[] { ImmutableList.of(todayMessage, yesterdayMessage, lastWeekMessage, thisYearMessage),
                ImmutableList.of(ImmutableList.of(todayMessage), ImmutableList.of(yesterdayMessage), ImmutableList.of(),
                        ImmutableList.of(lastWeekMessage), ImmutableList.of(), ImmutableList.of(),
                        ImmutableList.of(thisYearMessage), ImmutableList.of()) });

        return scenarios;
    }

    private static IFeedMessage mockMessage(Date date) {
        IFeedMessage message = mock(IFeedMessage.class);
        when(message.getDate()).thenReturn(date);
        return message;
    }

    @Test
    public void testSplitMessagesByAge() {
        List<List<IFeedMessage>> splitMessages = MessageUtils.splitMessagesByAge(inputMessages);

        assertThat(splitMessages, is(Matchers.equalTo(expectedMessages)));
        assertThat(splitMessages, hasSize(expectedMessages.size()));
    }

}
