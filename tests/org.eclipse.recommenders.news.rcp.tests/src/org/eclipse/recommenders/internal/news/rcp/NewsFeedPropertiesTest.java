/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp;

import static org.eclipse.recommenders.internal.news.rcp.TestUtils.enabled;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.eclipse.recommenders.news.rcp.INewsFeedProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@RunWith(MockitoJUnitRunner.class)
public class NewsFeedPropertiesTest {

    private final String testId = "testID";
    private final String testIdTwo = "testIDtwo";

    @Test
    public void shouldWriteId() {
        INewsFeedProperties sut = new NewsFeedProperties();
        Set<String> writeIds = ImmutableSet.of(testId);
        sut.writeReadIds(writeIds);
        Set<String> readIds = sut.getReadIds();
        assertThat(readIds.contains(testId), is(true));
    }

    @Test
    public void shouldWriteIds() {
        INewsFeedProperties sut = new NewsFeedProperties();
        Set<String> writeIds = ImmutableSet.of(testId, testIdTwo);
        sut.writeReadIds(writeIds);
        Set<String> readIds = sut.getReadIds();
        assertThat(readIds.size(), is(2));
    }

    @Test
    public void shouldWriteEmptySet() {
        INewsFeedProperties sut = new NewsFeedProperties();
        Set<String> writeIds = Sets.newHashSet();
        sut.writeReadIds(writeIds);
        Set<String> readIds = sut.getReadIds();
        assertThat(readIds.size(), is(0));
    }

    @Test(expected = NullPointerException.class)
    public void shouldFailWriteNullset() {
        INewsFeedProperties sut = new NewsFeedProperties();
        Set<String> writeIds = null;
        sut.writeReadIds(writeIds);
        Set<String> readIds = sut.getReadIds();
        assertThat(readIds.size(), is(0));
    }

    @Test
    public void shouldWritePollDate() {
        INewsFeedProperties sut = new NewsFeedProperties();
        Map<FeedDescriptor, Date> writePollDates = Maps.newHashMap();
        Date date = mock(Date.class);
        FeedDescriptor feed = enabled(testId);
        writePollDates.put(feed, date);
        sut.writePollDates(writePollDates);
        Map<String, Date> readPollDates = sut.getPollDates();
        assertThat(readPollDates.keySet().contains(feed.getId()), is(true));
    }

    @Test
    public void shouldWritePollDates() {
        INewsFeedProperties sut = new NewsFeedProperties();
        Map<FeedDescriptor, Date> writePollDates = Maps.newHashMap();
        Date date = mock(Date.class);
        FeedDescriptor feed = enabled(testId);
        FeedDescriptor secondFeed = enabled(testIdTwo);
        writePollDates.put(feed, date);
        writePollDates.put(secondFeed, date);
        sut.writePollDates(writePollDates);
        Map<String, Date> readPollDates = sut.getPollDates();
        assertThat(readPollDates.size(), is(2));
    }

    @Test
    public void shouldWriteEmptyMap() {
        INewsFeedProperties sut = new NewsFeedProperties();
        Map<FeedDescriptor, Date> writePollDates = Maps.newHashMap();
        sut.writePollDates(writePollDates);
        Map<String, Date> readPollDates = sut.getPollDates();
        assertThat(readPollDates.size(), is(2));
    }

    @Test(expected = NullPointerException.class)
    public void shoulFaildWriteNullMap() {
        INewsFeedProperties sut = new NewsFeedProperties();
        Map<FeedDescriptor, Date> writePollDates = null;
        sut.writePollDates(writePollDates);
        Map<String, Date> readPollDates = sut.getPollDates();
        assertThat(readPollDates.size(), is(2));
    }
}
