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

    @Parameters
    public static Collection<Object[]> scenarios() {
        List<Object[]> scenarios = Lists.newArrayList();

        scenarios.add(new Object[] { null, Collections.emptyList() });
        scenarios.add(new Object[] { ImmutableList.of(mockMessage(getDate(TODAY))),
                ImmutableList.of(ImmutableList.of(mockMessage(getDate(TODAY)))) });
        scenarios.add(new Object[] { ImmutableList.of(mockMessage(getDate(TODAY)), mockMessage(getDate(YESTERDAY))),
                ImmutableList.of(ImmutableList.of(mockMessage(getDate(TODAY))),
                        ImmutableList.of(mockMessage(getDate(YESTERDAY)))) });
        scenarios.add(new Object[] { ImmutableList.of(mockMessage(getDate(TODAY)), mockMessage(getDate(YESTERDAY)),
                mockMessage(getDate(THIS_WEEK)), mockMessage(getDate(LAST_WEEK)), mockMessage(getDate(THIS_MONTH)),
                mockMessage(getDate(LAST_MONTH)), mockMessage(getDate(THIS_YEAR)), mockMessage(getDate(OLDER))),
                ImmutableList.of(ImmutableList.of(mockMessage(getDate(TODAY))),
                        ImmutableList.of(mockMessage(getDate(YESTERDAY))),
                        ImmutableList.of(mockMessage(getDate(THIS_WEEK))),
                        ImmutableList.of(mockMessage(getDate(LAST_WEEK))),
                        ImmutableList.of(mockMessage(getDate(THIS_MONTH)),
                                ImmutableList.of(mockMessage(getDate(LAST_MONTH)),
                                        ImmutableList.of(mockMessage(getDate(THIS_YEAR)),
                                                ImmutableList.of(mockMessage(getDate(OLDER))))))) });
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
