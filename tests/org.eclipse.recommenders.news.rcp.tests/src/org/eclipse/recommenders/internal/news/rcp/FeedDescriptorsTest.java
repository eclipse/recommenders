/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp;

import static org.eclipse.recommenders.internal.news.rcp.FeedDescriptors.*;
import static org.eclipse.recommenders.internal.news.rcp.TestUtils.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;

@RunWith(MockitoJUnitRunner.class)
public class FeedDescriptorsTest {

    private static final String FIRST_ELEMENT = "first";
    private static final String SECOND_ELEMENT = "second";
    private static final String THIRD_ELEMENT = "third";
    private static final String UNINSTALLED_ELEMENT = "uninstalled";
    private static final String EMPTY_STRING = "";
    private static final String VALID_JSON_FOR_ENABLED_FIRST_ELEMENT_AND_DISABLED_SECOND_ELEMENT = "[{\"defaultRepository\":true,\"id\":\"first\",\"url\":\"http://planeteclipse.org/planet/rss20.xml\",\"name\":\"first\",\"enabled\":true},{\"defaultRepository\":true,\"id\":\"second\",\"url\":\"http://planeteclipse.org/planet/rss20.xml\",\"name\":\"second\",\"enabled\":false}]";

    @Test
    public void testLoadedSettingsIgnoresDefaultEnablement() {
        List<FeedDescriptor> result = FeedDescriptors.load(
                FIRST_ELEMENT + SEPARATOR + DISABLED_FLAG + SECOND_ELEMENT + SEPARATOR + THIRD_ELEMENT,
                ImmutableList.of(enabled(FIRST_ELEMENT), enabled(SECOND_ELEMENT), disabled(THIRD_ELEMENT)));

        assertThat(result.get(0).getId(), is(equalTo(FIRST_ELEMENT)));
        assertThat(result.get(0).isEnabled(), is(true));
        assertThat(result.get(1).getId(), is(equalTo(SECOND_ELEMENT)));
        assertThat(result.get(1).isEnabled(), is(false));
        assertThat(result.get(2).getId(), is(equalTo(THIRD_ELEMENT)));
        assertThat(result.get(2).isEnabled(), is(true));
        assertThat(result.size(), is(3));
    }

    @Test
    public void testLoadIgnoresUnknownFeeds() {
        List<FeedDescriptor> result = FeedDescriptors.load(
                FIRST_ELEMENT + SEPARATOR + DISABLED_FLAG + UNINSTALLED_ELEMENT + SEPARATOR + SECOND_ELEMENT,
                ImmutableList.of(enabled(FIRST_ELEMENT), enabled(SECOND_ELEMENT)));

        assertThat(result.get(0).getId(), is(equalTo(FIRST_ELEMENT)));
        assertThat(result.get(0).isEnabled(), is(true));
        assertThat(result.get(1).getId(), is(equalTo(SECOND_ELEMENT)));
        assertThat(result.get(1).isEnabled(), is(true));
        assertThat(result.size(), is(2));
    }

    @Test
    public void testLoadEmptyString() {
        List<FeedDescriptor> result = FeedDescriptors.load(EMPTY_STRING,
                ImmutableList.of(enabled(FIRST_ELEMENT), disabled(SECOND_ELEMENT)));
        assertThat(result.get(0).getId(), is(equalTo(FIRST_ELEMENT)));
        assertThat(result.get(0).isEnabled(), is(true));
        assertThat(result.get(1).getId(), is(equalTo(SECOND_ELEMENT)));
        assertThat(result.get(1).isEnabled(), is(false));
        assertThat(result.size(), is(2));
    }

    @Test
    public void testLoadPrependsNewFeeds() {
        List<FeedDescriptor> result = FeedDescriptors.load(THIRD_ELEMENT,
                ImmutableList.of(enabled(FIRST_ELEMENT), disabled(SECOND_ELEMENT), enabled(THIRD_ELEMENT)));

        assertThat(result.get(0).getId(), is(equalTo(THIRD_ELEMENT)));
        assertThat(result.get(0).isEnabled(), is(true));
        assertThat(result.get(1).getId(), is(equalTo(FIRST_ELEMENT)));
        assertThat(result.get(1).isEnabled(), is(true));
        assertThat(result.get(2).getId(), is(equalTo(SECOND_ELEMENT)));
        assertThat(result.get(2).isEnabled(), is(false));
        assertThat(result.size(), is(3));
    }

    @Test
    public void testLoadDuplicateFeeds() {
        List<FeedDescriptor> result = FeedDescriptors.load(
                FIRST_ELEMENT + SEPARATOR + SECOND_ELEMENT + SEPARATOR + FIRST_ELEMENT,
                ImmutableList.of(enabled(FIRST_ELEMENT), enabled(SECOND_ELEMENT)));

        assertThat(result.get(0).getId(), is(equalTo(FIRST_ELEMENT)));
        assertThat(result.get(0).isEnabled(), is(true));
        assertThat(result.get(1).getId(), is(equalTo(SECOND_ELEMENT)));
        assertThat(result.get(1).isEnabled(), is(true));
        assertThat(result.size(), is(2));
    }

    @Test
    public void testLoadCustomFeeds() {
        List<FeedDescriptor> result = FeedDescriptors
                .getFeeds(VALID_JSON_FOR_ENABLED_FIRST_ELEMENT_AND_DISABLED_SECOND_ELEMENT);

        assertThat(result.get(0).getId(), is(equalTo(FIRST_ELEMENT)));
        assertThat(result.get(0).isEnabled(), is(true));
        assertThat(result.get(1).getId(), is(equalTo(SECOND_ELEMENT)));
        assertThat(result.get(1).isEnabled(), is(false));
        assertThat(result.size(), is(2));
    }

    @Test
    public void testStoreFeedsList() {
        String result = FeedDescriptors.feedsToString(
                ImmutableList.of(enabled(FIRST_ELEMENT), disabled(SECOND_ELEMENT), enabled(THIRD_ELEMENT)));
        assertThat(result,
                is(equalTo(FIRST_ELEMENT + SEPARATOR + DISABLED_FLAG + SECOND_ELEMENT + SEPARATOR + THIRD_ELEMENT)));
    }

    @Test
    public void testStoreCustomFeedsList() {
        String result = FeedDescriptors
                .customFeedsToString(ImmutableList.of(enabled(FIRST_ELEMENT), disabled(SECOND_ELEMENT)));
        assertThat(result, is(equalTo(VALID_JSON_FOR_ENABLED_FIRST_ELEMENT_AND_DISABLED_SECOND_ELEMENT)));
    }

    @Test
    public void testStoreEmptyList() {
        ImmutableList<FeedDescriptor> emptyList = ImmutableList.of();
        String result = FeedDescriptors.feedsToString(emptyList);
        assertThat(result, is(equalTo(EMPTY_STRING)));
    }

    @Test
    public void testStoreDescriptorMultipleTimes() {
        String result = FeedDescriptors.feedsToString(ImmutableList.of(enabled(FIRST_ELEMENT), disabled(SECOND_ELEMENT),
                enabled(THIRD_ELEMENT), disabled(FIRST_ELEMENT)));
        assertThat(result,
                is(equalTo(FIRST_ELEMENT + SEPARATOR + DISABLED_FLAG + SECOND_ELEMENT + SEPARATOR + THIRD_ELEMENT)));
    }

}
