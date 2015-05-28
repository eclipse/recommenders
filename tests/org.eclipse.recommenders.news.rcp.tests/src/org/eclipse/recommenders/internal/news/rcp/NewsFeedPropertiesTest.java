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

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

@RunWith(MockitoJUnitRunner.class)
public class NewsFeedPropertiesTest {

    private final String testId = "testID";

    @Test
    public void testWriteIds() {
        INewsFeedProperties sut = new NewsFeedProperties();
        Set<String> firstRead = sut.getReadIds();
        Set<String> writeIds = Sets.newHashSet();
        writeIds.add(testId);
        sut.writeReadIds(writeIds);
        Set<String> secondRead = sut.getReadIds();
        assertThat(firstRead.contains(testId), is(false));
        assertThat(secondRead.contains(testId), is(true));
    }

    @Test
    public void testWritePollDates() {
        INewsFeedProperties sut = new NewsFeedProperties();
        Map<String, Date> firstRead = sut.getPollDates();
        Map<FeedDescriptor, Date> writePollDates = Maps.newHashMap();
        Date date = mock(Date.class);
        FeedDescriptor feed = enabled(testId);
        writePollDates.put(feed, date);
        sut.writePollDates(writePollDates);
        Map<String, Date> secondRead = sut.getPollDates();
        assertThat(firstRead.keySet().contains(feed.getId()), is(false));
        assertThat(secondRead.keySet().contains(feed.getId()), is(true));
    }

}
