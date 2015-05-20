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

import java.util.Set;

import org.eclipse.recommenders.news.rcp.IJobFacade;
import org.eclipse.recommenders.news.rcp.INewsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;

@RunWith(MockitoJUnitRunner.class)
public class INewsServiceTest {

    private static final String FIRST_ELEMENT = "first";
    private NewsRcpPreferences preferences;
    private EventBus bus;
    private IJobFacade jobFacade;
    private Set<FeedDescriptor> feeds;

    @Before
    public void setUp() {
        preferences = mock(NewsRcpPreferences.class);
        bus = mock(EventBus.class);
        jobFacade = mock(JobFacade.class);
        feeds = mock(Set.class);
    }

    @Test
    public void testStartEnabledFeed() {
        FeedDescriptor feed = enabled(FIRST_ELEMENT);
        when(preferences.isEnabled()).thenReturn(true);
        when(preferences.getFeedDescriptors()).thenReturn(ImmutableList.of(feed));
        INewsService service = new NewsService(preferences, bus, jobFacade);
        service.start();
        verify(jobFacade, times(1)).schedule(feeds);
    }

    @Test
    public void testStartDisabledFeed() {
        FeedDescriptor feed = disabled(FIRST_ELEMENT);
        when(preferences.isEnabled()).thenReturn(true);
        when(preferences.getFeedDescriptors()).thenReturn(ImmutableList.of(feed));
        INewsService service = new NewsService(preferences, bus, jobFacade);
        service.start();
        verify(jobFacade, times(0)).schedule(feeds);
    }

    @Test
    public void testStartDisabledPreferences() {
        FeedDescriptor feed = enabled(FIRST_ELEMENT);
        when(preferences.isEnabled()).thenReturn(false);
        when(preferences.getFeedDescriptors()).thenReturn(ImmutableList.of(feed));
        INewsService service = new NewsService(preferences, bus, jobFacade);
        service.start();
        verify(jobFacade, times(0)).schedule(feeds);
    }

    @Test
    public void testGetMessages() {
        FeedDescriptor feed = enabled(FIRST_ELEMENT);
        when(preferences.isEnabled()).thenReturn(true);
        when(preferences.getFeedDescriptors()).thenReturn(ImmutableList.of(feed));
        INewsService service = new NewsService(preferences, bus, jobFacade);
        service.start();
        assertThat(service.getMessages(3).size(), is(3));
    }

}
