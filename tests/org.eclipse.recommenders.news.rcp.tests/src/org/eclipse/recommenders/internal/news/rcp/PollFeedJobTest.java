package org.eclipse.recommenders.internal.news.rcp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.notifications.core.NotificationEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("restriction")
public class PollFeedJobTest {

    private static final String FIRST_ELEMENT = "first";
    private static final String SECOND_ELEMENT = "second";
    private NotificationEnvironment environment;
    private IProgressMonitor monitor;

    @Before
    public void setUp() {
        environment = mock(NotificationEnvironment.class);
        monitor = mock(IProgressMonitor.class);
    }

    @Test
    public void testRunEnabledFeed() {
        FeedDescriptor feed = FeedDescriptorsTest.enabled(FIRST_ELEMENT);
        when(feed.getUrl()).thenReturn("http://planeteclipse.org/planet/rss20.xml");
        NewsRcpPreferences preferences = mock(NewsRcpPreferences.class);
        when(preferences.isEnabled()).thenReturn(true);
        when(preferences.getFeedDescriptors()).thenReturn(ImmutableList.of(feed));
        PollFeedJob job = new PollFeedJob(feed, preferences, environment);
        assertThat(job.shouldRun(), is(true));
        assertThat(job.belongsTo(job), is(true));
        assertThat(job.run(monitor), is(Status.OK_STATUS));
    }

    @Test
    public void testRunDisabledFeed() {
        FeedDescriptor feed = FeedDescriptorsTest.disabled(SECOND_ELEMENT);
        when(feed.getUrl()).thenReturn("http://planeteclipse.org/planet/rss20.xml");
        NewsRcpPreferences preferences = mock(NewsRcpPreferences.class);
        when(preferences.isEnabled()).thenReturn(true);
        when(preferences.getFeedDescriptors()).thenReturn(ImmutableList.of(feed));
        PollFeedJob job = new PollFeedJob(feed, preferences, environment);
        assertThat(job.shouldRun(), is(false));
        assertThat(job.belongsTo(job), is(true));
        assertThat(job.run(monitor), is(Status.OK_STATUS));
    }

    @Test
    public void testRunPreferencesDisabled() {
        FeedDescriptor feed = FeedDescriptorsTest.enabled(FIRST_ELEMENT);
        when(feed.getUrl()).thenReturn("http://planeteclipse.org/planet/rss20.xml");
        NewsRcpPreferences preferences = mock(NewsRcpPreferences.class);
        when(preferences.isEnabled()).thenReturn(false);
        when(preferences.getFeedDescriptors()).thenReturn(ImmutableList.of(feed));
        PollFeedJob job = new PollFeedJob(feed, preferences, environment);
        assertThat(job.shouldRun(), is(false));
        assertThat(job.belongsTo(job), is(true));
        assertThat(job.run(monitor), is(Status.OK_STATUS));
    }

    @Test
    public void testRunURLMalformed() {
        FeedDescriptor feed = FeedDescriptorsTest.enabled(FIRST_ELEMENT);
        NewsRcpPreferences preferences = mock(NewsRcpPreferences.class);
        when(preferences.isEnabled()).thenReturn(true);
        when(preferences.getFeedDescriptors()).thenReturn(ImmutableList.of(feed));
        PollFeedJob job = new PollFeedJob(feed, preferences, environment);
        assertThat(job.shouldRun(), is(true));
        assertThat(job.belongsTo(job), is(true));
        assertThat(job.run(monitor), is(Status.CANCEL_STATUS));
    }
}
