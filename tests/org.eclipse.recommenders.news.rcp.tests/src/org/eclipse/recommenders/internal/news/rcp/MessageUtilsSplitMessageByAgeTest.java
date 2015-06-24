package org.eclipse.recommenders.internal.news.rcp;

import static org.eclipse.recommenders.internal.news.rcp.MessageUtils.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.recommenders.news.rcp.IFeedMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
@SuppressWarnings("unchecked")
public class MessageUtilsSplitMessageByAgeTest {

    private static final Date SUNDAY_2015_05_31 = getDate(2015, Calendar.MAY, 31);
    private static final Date MONDAY_2015_06_01 = getDate(2015, Calendar.JUNE, 1);
    private static final Date TUESDAY_2015_06_02 = getDate(2015, Calendar.JUNE, 2);
    private static final Date WEDNESDAY_2015_06_03 = getDate(2015, Calendar.JUNE, 3);

    private Date today;
    private List<Date> inputDates;
    private List<List<Date>> expectedDates;

    public MessageUtilsSplitMessageByAgeTest(Date today, List<Date> inputMessages, List<List<Date>> expectedDates) {
        this.today = today;
        this.inputDates = inputMessages;
        this.expectedDates = expectedDates;
    }

    @Parameters
    public static Collection<Object[]> scenarios() {
        List<Object[]> scenarios = Lists.newArrayList();
        // Message in current week
        scenarios.add(new Object[] { on(MONDAY_2015_06_01), messagesFrom(MONDAY_2015_06_01),
                expectedGroupings(today(MONDAY_2015_06_01)) });
        scenarios.add(new Object[] { on(TUESDAY_2015_06_02), messagesFrom(MONDAY_2015_06_01),
                expectedGroupings(today(), yesterday(MONDAY_2015_06_01)) });
        scenarios.add(new Object[] { on(WEDNESDAY_2015_06_03), messagesFrom(MONDAY_2015_06_01),
                expectedGroupings(today(), yesterday(), thisWeek(MONDAY_2015_06_01)) });

        // Message in previous week
        scenarios.add(new Object[] { on(MONDAY_2015_06_01), messagesFrom(SUNDAY_2015_05_31),
                expectedGroupings(today(), yesterday(MONDAY_2015_06_01)) });
        scenarios.add(new Object[] { on(TUESDAY_2015_06_02), messagesFrom(SUNDAY_2015_05_31),
                expectedGroupings(today(), yesterday(), thisWeek(), lastWeek(MONDAY_2015_06_01)) });

        return scenarios;
    }

    private static Date on(Date date) {
        return date;
    }

    private static List<Date> messagesFrom(Date... inputDates) {
        return Arrays.asList(inputDates);
    }

    private static List<List<Date>> expectedGroupings(List<Date>... expectedDates) {
        List<List<Date>> result = Lists.newArrayList();
        result.addAll(Arrays.asList(expectedDates));
        while (result.size() <= MessageUtils.OLDER) {
            result.add(Collections.<Date>emptyList());
        }
        return result;
    }

    private static List<IFeedMessage> mockMessages(List<Date> dates) {
        List<IFeedMessage> messages = Lists.newArrayList();
        for (Date date : dates) {
            messages.add(new FeedMessage(date.toString(), date, "", "", null));
        }
        return messages;
    }

    @Test
    public void testSplitMessagesByAge() {
        List<IFeedMessage> inputMessages = mockMessages(inputDates);

        List<List<IFeedMessage>> splitMessages = MessageUtils.splitMessagesByAge(inputMessages, today);

        assertThat(splitMessages.get(TODAY), is(equalTo(mockMessages(expectedDates.get(TODAY)))));
        assertThat(splitMessages.get(YESTERDAY), is(equalTo(mockMessages(expectedDates.get(YESTERDAY)))));
        assertThat(splitMessages.get(THIS_WEEK), is(equalTo(mockMessages(expectedDates.get(THIS_WEEK)))));
        assertThat(splitMessages.get(LAST_WEEK), is(equalTo(mockMessages(expectedDates.get(LAST_WEEK)))));
        assertThat(splitMessages.get(THIS_MONTH), is(equalTo(mockMessages(expectedDates.get(THIS_MONTH)))));
        assertThat(splitMessages.get(LAST_MONTH), is(equalTo(mockMessages(expectedDates.get(LAST_MONTH)))));
        assertThat(splitMessages.get(THIS_YEAR), is(equalTo(mockMessages(expectedDates.get(THIS_YEAR)))));
        assertThat(splitMessages.get(OLDER), is(equalTo(mockMessages(expectedDates.get(OLDER)))));
        assertThat(splitMessages, hasSize(OLDER + 1));
    }

    private static Date getDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.getTime();
    }

    private static List<Date> today(Date... dates) {
        return Arrays.asList(dates);
    }

    private static List<Date> yesterday(Date... dates) {
        return Arrays.asList(dates);
    }

    private static List<Date> thisWeek(Date... dates) {
        return Arrays.asList(dates);
    }

    private static List<Date> lastWeek(Date... dates) {
        return Arrays.asList(dates);
    }

    private static List<Date> thisMonth(Date... dates) {
        return Arrays.asList(dates);
    }

    private static List<Date> lastMonth(Date... dates) {
        return Arrays.asList(dates);
    }

    private static List<Date> thisYear(Date... dates) {
        return Arrays.asList(dates);
    }

    private static List<Date> older(Date... dates) {
        return Arrays.asList(dates);
    }

}
