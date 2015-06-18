package org.eclipse.recommenders.internal.news.rcp.notifications;

import static org.eclipse.recommenders.internal.news.rcp.TestUtils.enabled;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import org.eclipse.recommenders.internal.news.rcp.FeedDescriptor;
import org.eclipse.recommenders.news.rcp.IFeedMessage;
import org.eclipse.recommenders.news.rcp.INewsService;
import org.eclipse.swt.widgets.Display;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;

@RunWith(MockitoJUnitRunner.class)
public class NewsNotificationPopupTest {
    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); //$NON-NLS-1$

    private EventBus eventBus;
    private INewsService service;
    private Display display;

    @Before
    public void setUp() {
        eventBus = mock(EventBus.class);
        service = mock(INewsService.class);
        display = mock(Display.class);
    }

    @SuppressWarnings("static-access")
    @Test
    public void testSortByDate() throws ParseException {
        HashMap<FeedDescriptor, List<IFeedMessage>> messages = Maps.newHashMap();
        FeedDescriptor feed = enabled("rndm");
        List<IFeedMessage> iFeedMessages = Lists.newArrayList();
        IFeedMessage messageA = mock(IFeedMessage.class);
        IFeedMessage messageB = mock(IFeedMessage.class);
        when(messageA.getDate()).thenReturn(dateFormat.parse("10/06/1991 20:30:30"));
        when(messageB.getDate()).thenReturn(dateFormat.parse("10/06/1991 20:30:31"));
        iFeedMessages.add(messageA);
        iFeedMessages.add(messageB);
        messages.put(feed, iFeedMessages);

        NewsNotificationPopup sut = new NewsNotificationPopup(messages, eventBus, service, display);
        sut.sortByDate(messages);

        assertEquals(messageB.getDate(), sut.sortByDate(messages).get(feed).get(0).getDate());
    }

    @SuppressWarnings("static-access")
    @Test
    public void testSortByDateEqualDates() throws ParseException {
        HashMap<FeedDescriptor, List<IFeedMessage>> messages = Maps.newHashMap();
        FeedDescriptor feed = enabled("rndm");
        List<IFeedMessage> iFeedMessages = Lists.newArrayList();
        IFeedMessage messageA = mock(IFeedMessage.class);
        IFeedMessage messageB = mock(IFeedMessage.class);
        IFeedMessage messageC = mock(IFeedMessage.class);
        when(messageA.getDate()).thenReturn(dateFormat.parse("10/06/1991 20:30:30"));
        when(messageB.getDate()).thenReturn(dateFormat.parse("10/06/1991 20:30:30"));
        when(messageC.getDate()).thenReturn(dateFormat.parse("10/06/1991 20:30:29"));
        iFeedMessages.add(messageC);
        iFeedMessages.add(messageA);
        iFeedMessages.add(messageB);
        messages.put(feed, iFeedMessages);

        NewsNotificationPopup sut = new NewsNotificationPopup(messages, eventBus, service, display);
        sut.sortByDate(messages);

        assertEquals(messageC.getDate(), sut.sortByDate(messages).get(feed).get(2).getDate());
        assertEquals(messageA.getDate(), sut.sortByDate(messages).get(feed).get(0).getDate());
    }

}
