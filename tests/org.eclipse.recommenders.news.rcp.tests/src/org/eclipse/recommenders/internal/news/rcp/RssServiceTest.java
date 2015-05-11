/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp;

import static org.mockito.Mockito.*;

import org.eclipse.core.runtime.IProgressMonitor;
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
        JobProvider provider = mock(JobProvider.class);
        NewsRcpPreferences preferences = mock(NewsRcpPreferences.class);
        when(feed.getUrl()).thenReturn("http://planeteclipse.org/planet/rss20.xml");
        when(preferences.isEnabled()).thenReturn(true);
        when(preferences.getFeedDescriptors()).thenReturn(ImmutableList.of(feed));
        NotificationEnvironment environment = mock(NotificationEnvironment.class);
        EventBus bus = mock(EventBus.class);
        RssService service = new RssService(preferences, bus, environment, provider);
        when(provider.getPollFeedJob(Mockito.any(FeedDescriptor.class), Mockito.any(NewsRcpPreferences.class),
                Mockito.any(NotificationEnvironment.class), Mockito.any(RssService.class))).thenReturn(job);
        service.start(feed);
        IProgressMonitor monitor = mock(IProgressMonitor.class);
        verify(provider).getPollFeedJob(feed, preferences, environment, service);
        verify(job).schedule();
    }
}
