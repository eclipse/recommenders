package org.eclipse.recommenders.internal.news.rcp;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.mylyn.commons.notifications.core.NotificationEnvironment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;

@SuppressWarnings("restriction")
@RunWith(MockitoJUnitRunner.class)
public class RssServiceTest {

    private static final String FIRST_ELEMENT = "first";
    private static final String SECOND_ELEMENT = "second";

    @Test
    public void testStartEnabledFeed() {
        FeedDescriptor feed = FeedDescriptorsTest.enabled(FIRST_ELEMENT);
        PollFeedJob job = mock(PollFeedJob.class);
        IJobChangeListener jobChangeListener = mock(IJobChangeListener.class);
        // when(job.addJobChangeListener(jobChangeListener);
        // job.addJobChangeListener(jobChangeListener);
        JobProvider provider = mock(JobProvider.class);
        NewsRcpPreferences preferences = mock(NewsRcpPreferences.class);
        when(preferences.isEnabled()).thenReturn(true);
        when(preferences.getFeedDescriptors()).thenReturn(ImmutableList.of(feed));
        NotificationEnvironment environment = mock(NotificationEnvironment.class);
        EventBus bus = mock(EventBus.class);
        // when(provider.getPollFeedJob(feed, preferences, environment)).thenReturn(job);
        when(provider.getPollFeedJob(Mockito.eq(feed), Mockito.eq(preferences), Mockito.eq(environment)))
                .thenReturn(job);
        RssService service = new RssService(preferences, bus, environment, provider);
        assertThat(preferences, is(notNullValue()));
        assertThat(bus, is(notNullValue()));
        assertThat(environment, is(notNullValue()));
        assertThat(provider, is(notNullValue()));
        assertThat(job, is(notNullValue()));
        // RssService service = mock(RssService.class);
        assertThat(feed, is(notNullValue()));
        assertThat(service, is(notNullValue()));
        // NPE if you uncomment line below
        service.start();
        // service is not a mock, so can't use verify from the line below, OK if you use a mock one
        // verify(service).start();
        // // Wanted, but not invoked: pollFeedJob.shouldSchedule(); - use a mock service to get into here
        // verify(provider.getPollFeedJob(feed, preferences, environment)).shouldSchedule();
        // verify(provider.getPollFeedJob(feed, preferences, environment)).schedule(0);
    }

    @Test
    public void testStartFeedDisabled() {
        RssService service = mock(RssService.class);
        FeedDescriptor feed = FeedDescriptorsTest.disabled(SECOND_ELEMENT);
        service.start(feed);
        NewsRcpPreferences preferences = mock(NewsRcpPreferences.class);
        when(preferences.isEnabled()).thenReturn(true);
        when(preferences.getFeedDescriptors()).thenReturn(ImmutableList.of(feed));
        NotificationEnvironment environment = mock(NotificationEnvironment.class);
        PollFeedJob job = new PollFeedJob(feed, preferences, environment);
        PollFeedJob jobb = mock(PollFeedJob.class);
        verify(jobb).schedule();
        assertThat(job.shouldRun(), is(false));
        verify(service).start(feed);
    }
}
