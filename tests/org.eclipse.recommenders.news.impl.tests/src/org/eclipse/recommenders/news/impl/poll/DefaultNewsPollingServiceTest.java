package org.eclipse.recommenders.news.impl.poll;

import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.HOURS;
import static org.eclipse.recommenders.news.api.poll.PollingResult.Status.*;
import static org.eclipse.recommenders.news.impl.poll.TestUtils.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.recommenders.news.api.NewsItem;
import org.eclipse.recommenders.news.api.poll.INewsPollingService;
import org.eclipse.recommenders.news.api.poll.PollingPolicy;
import org.eclipse.recommenders.news.api.poll.PollingRequest;
import org.eclipse.recommenders.news.api.poll.PollingResult;
import org.junit.Test;

public class DefaultNewsPollingServiceTest {

    private static final URI FEED_URI = URI.create("http://www.example.org/feed.xml");

    private static final NewsItem ITEM = new NewsItem("Item", new Date(0L),
            URI.create("http://www.example.org/items/1"), null);

    private static final List<NewsItem> NO_ITEMS = Collections.emptyList();

    @Test
    public void testPollHourlyMissingFeedNotPolledBefore() throws Exception {
        IDownloadService downloadService = mock(IDownloadService.class);
        when(downloadService.getLastAttemptDate(FEED_URI)).thenReturn(null);
        when(downloadService.read(FEED_URI)).thenReturn(null);
        when(downloadService.download(eq(FEED_URI), any(IProgressMonitor.class))).thenThrow(new IOException());

        IFeedItemStore feedItemStore = mock(IFeedItemStore.class);
        when(feedItemStore.getNewsItems(FEED_URI)).thenReturn(null);

        INewsPollingService sut = new DefaultNewsPollingService(downloadService, feedItemStore);

        PollingRequest request = new PollingRequest(FEED_URI, PollingPolicy.every(1, HOURS));

        Collection<PollingResult> results = sut.poll(singletonList(request), null);

        assertThat(results, contains(new PollingResult(FEED_URI, NO_ITEMS, NO_ITEMS, NOT_DOWNLOADED)));
        assertThat(results.size(), is(equalTo(1)));

        verify(downloadService).download(eq(FEED_URI), any(IProgressMonitor.class));

        verify(feedItemStore, never()).udpate(eq(FEED_URI), any(InputStream.class), any(IProgressMonitor.class));
    }

    @Test
    public void testPollNeverFeedNotPolledBefore() throws Exception {
        IDownloadService downloadService = mock(IDownloadService.class);
        when(downloadService.getLastAttemptDate(FEED_URI)).thenReturn(null);
        when(downloadService.read(FEED_URI)).thenReturn(null);

        IFeedItemStore feedItemStore = mock(IFeedItemStore.class);
        when(feedItemStore.getNewsItems(FEED_URI)).thenReturn(null);

        INewsPollingService sut = new DefaultNewsPollingService(downloadService, feedItemStore);

        PollingRequest request = new PollingRequest(FEED_URI, PollingPolicy.never());

        Collection<PollingResult> results = sut.poll(singletonList(request), null);

        assertThat(results, contains(new PollingResult(FEED_URI, NO_ITEMS, NO_ITEMS, NOT_DOWNLOADED)));
        assertThat(results.size(), is(equalTo(1)));

        verify(downloadService, never()).download(eq(FEED_URI), any(IProgressMonitor.class));

        verify(feedItemStore, never()).udpate(eq(FEED_URI), any(InputStream.class), any(IProgressMonitor.class));
    }

    @Test
    public void testPollNeverFeedPolledBefore() throws Exception {
        IDownloadService downloadService = mock(IDownloadService.class);
        when(downloadService.getLastAttemptDate(FEED_URI)).thenReturn(now(-1, TimeUnit.DAYS));
        when(downloadService.read(FEED_URI)).thenReturn(asInputStream(ITEM));

        IFeedItemStore feedItemStore = mock(IFeedItemStore.class);
        when(feedItemStore.getNewsItems(FEED_URI)).thenReturn(singletonList(ITEM));

        INewsPollingService sut = new DefaultNewsPollingService(downloadService, feedItemStore);

        PollingRequest request = new PollingRequest(FEED_URI, PollingPolicy.never());

        Collection<PollingResult> results = sut.poll(singletonList(request), null);

        assertThat(results, contains(new PollingResult(FEED_URI, NO_ITEMS, singletonList(ITEM), DOWNLOADED)));
        assertThat(results.size(), is(equalTo(1)));

        verify(downloadService, never()).download(eq(FEED_URI), any(IProgressMonitor.class));

        verify(feedItemStore, never()).udpate(eq(FEED_URI), any(InputStream.class), any(IProgressMonitor.class));
    }
}
