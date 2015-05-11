/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp;

import static org.eclipse.recommenders.internal.news.rcp.TestUtils.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.mylyn.commons.notifications.core.NotificationEnvironment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("restriction")
public class PollFeedJobTest {

    private static final String FIRST_ELEMENT = "first";
    private static final String SECOND_ELEMENT = "second";
    private NotificationEnvironment environment;
    private NewsRcpPreferences preferences;

    @Before
    public void setUp() {
        environment = mock(NotificationEnvironment.class);
        preferences = mock(NewsRcpPreferences.class);
    }

    @Test
    public void testRunEnabledFeed() {
        FeedDescriptor feed = enabled(FIRST_ELEMENT);
        when(preferences.isEnabled()).thenReturn(true);
        when(preferences.getFeedDescriptors()).thenReturn(ImmutableList.of(feed));
        PollFeedJob job = new PollFeedJob(feed, preferences, environment);
        assertThat(job.shouldRun(), is(true));
    }

    @Test
    public void testRunDisabledFeed() {
        FeedDescriptor feed = disabled(SECOND_ELEMENT);
        when(preferences.isEnabled()).thenReturn(true);
        when(preferences.getFeedDescriptors()).thenReturn(ImmutableList.of(feed));
        PollFeedJob job = new PollFeedJob(feed, preferences, environment);
        assertThat(job.shouldRun(), is(false));
    }

    @Test
    public void testRunPreferencesDisabled() {
        FeedDescriptor feed = enabled(FIRST_ELEMENT);
        when(preferences.isEnabled()).thenReturn(false);
        when(preferences.getFeedDescriptors()).thenReturn(ImmutableList.of(feed));
        PollFeedJob job = new PollFeedJob(feed, preferences, environment);
        assertThat(job.shouldRun(), is(false));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRunUrlMalformed() {
        IConfigurationElement config = Mockito.mock(IConfigurationElement.class);
        when(config.getAttribute("id")).thenReturn(FIRST_ELEMENT);
        when(config.getAttribute("url")).thenReturn("abc");
        FeedDescriptor feed = new FeedDescriptor(config, true);
    }

    @Test
    public void testFeedsWithSameIdBelongsTo() {
        FeedDescriptor first = enabled(FIRST_ELEMENT);
        FeedDescriptor second = enabled(FIRST_ELEMENT);
        when(second.getUrl()).thenReturn("https://eclipse.org/home/eclipsenews.rss");
        PollFeedJob firstJob = new PollFeedJob(first, preferences, environment);
        PollFeedJob secondJob = new PollFeedJob(second, preferences, environment);
        assertThat(firstJob.belongsTo(secondJob), is(true));
        assertThat(secondJob.belongsTo(firstJob), is(true));
    }

    @Test
    public void testFeedsWithDifferentIdDoesntBelongsTo() {
        FeedDescriptor first = enabled(FIRST_ELEMENT);
        FeedDescriptor second = enabled(SECOND_ELEMENT);
        PollFeedJob firstJob = new PollFeedJob(first, preferences, environment);
        PollFeedJob secondJob = new PollFeedJob(second, preferences, environment);
        assertThat(firstJob.belongsTo(secondJob), is(false));
        assertThat(secondJob.belongsTo(firstJob), is(false));
    }
}
