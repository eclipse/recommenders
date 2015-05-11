/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
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
    private NewsRcpPreferences preferences;

    @Before
    public void setUp() {
        environment = mock(NotificationEnvironment.class);
        monitor = mock(IProgressMonitor.class);
        preferences = mock(NewsRcpPreferences.class);
    }

    @Test
    public void testRunEnabledFeed() {
        FeedDescriptor feed = FeedDescriptorsTest.enabled(FIRST_ELEMENT);
        when(feed.getUrl()).thenReturn("http://planeteclipse.org/planet/rss20.xml");
        when(preferences.isEnabled()).thenReturn(true);
        when(preferences.getFeedDescriptors()).thenReturn(ImmutableList.of(feed));
        PollFeedJob job = new PollFeedJob(feed, preferences, environment);
        assertThat(job.shouldRun(), is(true));
        assertThat(job.run(monitor), is(Status.OK_STATUS));
    }

    @Test
    public void testRunDisabledFeed() {
        FeedDescriptor feed = FeedDescriptorsTest.disabled(SECOND_ELEMENT);
        when(feed.getUrl()).thenReturn("http://planeteclipse.org/planet/rss20.xml");
        when(preferences.isEnabled()).thenReturn(true);
        when(preferences.getFeedDescriptors()).thenReturn(ImmutableList.of(feed));
        PollFeedJob job = new PollFeedJob(feed, preferences, environment);
        assertThat(job.shouldRun(), is(false));
    }

    @Test
    public void testRunPreferencesDisabled() {
        FeedDescriptor feed = FeedDescriptorsTest.enabled(FIRST_ELEMENT);
        when(feed.getUrl()).thenReturn("http://planeteclipse.org/planet/rss20.xml");
        when(preferences.isEnabled()).thenReturn(false);
        when(preferences.getFeedDescriptors()).thenReturn(ImmutableList.of(feed));
        PollFeedJob job = new PollFeedJob(feed, preferences, environment);
        assertThat(job.shouldRun(), is(false));
    }

    @Test
    public void testRunUrlMalformed() {
        FeedDescriptor feed = FeedDescriptorsTest.enabled(FIRST_ELEMENT);
        when(preferences.isEnabled()).thenReturn(true);
        when(preferences.getFeedDescriptors()).thenReturn(ImmutableList.of(feed));
        PollFeedJob job = new PollFeedJob(feed, preferences, environment);
        assertThat(job.shouldRun(), is(true));
        assertThat(job.run(monitor), is(Status.CANCEL_STATUS));
    }

    @Test
    public void testFeedsWithSameIdBelongsTo() {
        FeedDescriptor first = FeedDescriptorsTest.enabled(FIRST_ELEMENT);
        FeedDescriptor second = FeedDescriptorsTest.enabled(FIRST_ELEMENT);
        when(first.getUrl()).thenReturn("http://planeteclipse.org/planet/rss20.xml");
        when(second.getUrl()).thenReturn("https://eclipse.org/home/eclipsenews.rss");
        PollFeedJob firstJob = new PollFeedJob(first, preferences, environment);
        PollFeedJob secondJob = new PollFeedJob(second, preferences, environment);
        assertThat(firstJob.belongsTo(secondJob), is(true));
        assertThat(secondJob.belongsTo(firstJob), is(true));
    }

    @Test
    public void testFeedsWithDifferentIdDoesntBelongsTo() {
        FeedDescriptor first = FeedDescriptorsTest.enabled(FIRST_ELEMENT);
        FeedDescriptor second = FeedDescriptorsTest.enabled(SECOND_ELEMENT);
        when(first.getUrl()).thenReturn("http://planeteclipse.org/planet/rss20.xml");
        when(second.getUrl()).thenReturn("http://planeteclipse.org/planet/rss20.xml");
        PollFeedJob firstJob = new PollFeedJob(first, preferences, environment);
        PollFeedJob secondJob = new PollFeedJob(second, preferences, environment);
        assertThat(firstJob.belongsTo(secondJob), is(false));
        assertThat(secondJob.belongsTo(firstJob), is(false));
    }
}
