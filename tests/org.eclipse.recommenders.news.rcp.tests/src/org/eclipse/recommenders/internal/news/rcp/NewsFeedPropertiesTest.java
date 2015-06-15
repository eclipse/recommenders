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
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.recommenders.internal.news.rcp.l10n.LogMessages;
import org.eclipse.recommenders.utils.Logs;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;

@RunWith(MockitoJUnitRunner.class)
public class NewsFeedPropertiesTest {

    private static final String FILENAME_POLL_DATES = "poll-dates.properties";
    private final String testId = "testID";
    private final String testIdTwo = "testIDtwo";
    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private NewsFeedProperties sut;

    @Before
    public void setUp() {
        sut = new NewsFeedProperties();
    }

    @After
    public void tearDown() {
        File statusFile = sut.getFile(FILENAME_POLL_DATES);
        if (!statusFile.exists()) {
            return;
        }
        Properties properties = new Properties();
        try (InputStream stream = Files.asByteSource(statusFile).openStream()) {
            properties.load(stream);
            properties.clear();
        } catch (IOException e) {
            Logs.log(LogMessages.ERROR_READING_PROPERTIES, e, FILENAME_POLL_DATES);
        }
        try (FileOutputStream stream = new FileOutputStream(sut.getFile(FILENAME_POLL_DATES))) {
            properties.store(stream, "");
        } catch (IOException e) {
            Logs.log(LogMessages.ERROR_WRITING_PROPERTIES, FILENAME_POLL_DATES, e);
        }
    }

    @Test
    public void testWriteSingleId() {
        Set<String> writeIds = ImmutableSet.of(testId);

        sut.writeReadIds(writeIds);
        Set<String> readIds = sut.getReadIds();

        assertThat(readIds, contains(testId));
        assertThat(readIds.size(), is(1));
    }

    @Test
    public void testWriteMultipleIds() {
        Set<String> writeIds = ImmutableSet.of(testId, testIdTwo);

        sut.writeReadIds(writeIds);
        Set<String> readIds = sut.getReadIds();

        assertThat(readIds, containsInAnyOrder(testId, testIdTwo));
        assertThat(readIds.size(), is(2));
    }

    @Test
    public void testWriteEmptySet() {
        Set<String> writeIds = Sets.newHashSet();

        sut.writeReadIds(writeIds);
        Set<String> readIds = sut.getReadIds();

        assertThat(readIds.isEmpty(), is(true));
    }

    @Test
    public void testWriteNullReadIdSet() {
        Set<String> writeIds = null;

        sut.writeReadIds(writeIds);
        Set<String> readIds = sut.getReadIds();

        assertThat(readIds.isEmpty(), is(true));
    }

    @Test
    public void testWritePollDate() throws ParseException {
        Map<FeedDescriptor, Date> writePollDates = Maps.newHashMap();
        Date date = dateFormat.parse(dateFormat.format(new Date()));
        FeedDescriptor feed = enabled(testId);
        writePollDates.put(feed, date);

        sut.writePollDates(writePollDates);
        Map<String, Date> readPollDates = sut.getPollDates();

        assertThat(readPollDates.keySet().contains(feed.getId()), is(true));
        assertThat(readPollDates.values().contains(date), is(true));
    }

    @Test
    public void testdWriteMultiplePollDates() throws ParseException {
        Map<FeedDescriptor, Date> writePollDates = Maps.newHashMap();
        Date date = dateFormat.parse(dateFormat.format(new Date()));
        FeedDescriptor feed = enabled(testId);
        FeedDescriptor secondFeed = enabled(testIdTwo);
        writePollDates.put(feed, date);
        writePollDates.put(secondFeed, date);

        sut.writePollDates(writePollDates);
        Map<String, Date> readPollDates = sut.getPollDates();

        assertThat(readPollDates.keySet(), containsInAnyOrder(testId, testIdTwo));
        assertThat(readPollDates.values(), containsInAnyOrder(date, date));
        assertThat(readPollDates.size(), is(2));
    }

    @Test
    public void testWriteEmptyMap() {
        Map<FeedDescriptor, Date> writePollDates = Maps.newHashMap();

        sut.writePollDates(writePollDates);
        Map<String, Date> readPollDates = sut.getPollDates();

        assertThat(readPollDates.size(), is(0));
    }

    // flickering test
    @Test
    public void testWriteNullMap() {
        Map<FeedDescriptor, Date> writePollDates = null;

        sut.writePollDates(writePollDates);
        Map<String, Date> readPollDates = sut.getPollDates();

        assertThat(readPollDates.size(), is(0));
    }
}
