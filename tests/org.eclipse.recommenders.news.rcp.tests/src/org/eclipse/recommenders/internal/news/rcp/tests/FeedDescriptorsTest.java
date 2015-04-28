/**
* Copyright (c) 2015 Pawel Nowak.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*/
package org.eclipse.recommenders.internal.news.rcp.tests;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.recommenders.internal.news.rcp.FeedDescriptor;
import org.eclipse.recommenders.internal.news.rcp.FeedDescriptors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.ImmutableList;

@RunWith(MockitoJUnitRunner.class)
public class FeedDescriptorsTest {

    @Mock
    public IConfigurationElement first;

    @Mock
    public IConfigurationElement second;

    @Mock
    public IConfigurationElement third;

    @Mock
    public IConfigurationElement fourth;

    @Before
    public void setUp() {
        when(first.getAttribute("id")).thenReturn("first");
        when(second.getAttribute("id")).thenReturn("second");
        when(third.getAttribute("id")).thenReturn("third");
        when(fourth.getAttribute("id")).thenReturn("fourth");
    }

    @Test
    public void testLoadedSettingsIgnoresDefaultEnablement() {
        List<FeedDescriptor> result = FeedDescriptors.load("first;!second;third;!fourth",
                ImmutableList.of(enabled(first), enabled(second), disabled(third), disabled(fourth)));

        assertThat(result.size(), is(4));
        assertThat(result.get(0).getId(), is(equalTo("first")));
        assertThat(result.get(0).isEnabled(), is(true));
        assertThat(result.get(1).getId(), is(equalTo("second")));
        assertThat(result.get(1).isEnabled(), is(false));
        assertThat(result.get(2).getId(), is(equalTo("third")));
        assertThat(result.get(2).isEnabled(), is(true));
        assertThat(result.get(3).getId(), is(equalTo("fourth")));
        assertThat(result.get(3).isEnabled(), is(false));
    }

    @Test
    public void testLoadIgnoresUnknownFeeds() {
        List<FeedDescriptor> result = FeedDescriptors.load("first;unknown;second;!uninstalled",
                ImmutableList.of(enabled(first), enabled(second), disabled(third)));

        assertThat(result.size(), is(3));
        assertThat(result.get(0).getId(), is(equalTo("first")));
        assertThat(result.get(0).isEnabled(), is(true));
        assertThat(result.get(1).getId(), is(equalTo("second")));
        assertThat(result.get(1).isEnabled(), is(true));
    }

    @Test
    public void testLoadAppendsNewFeeds() {
        List<FeedDescriptor> result = FeedDescriptors.load("fourth",
                ImmutableList.of(enabled(first), disabled(second), enabled(third), enabled(fourth)));

        assertThat(result.size(), is(4));
        assertThat(result.get(0).getId(), is(equalTo("fourth")));
        assertThat(result.get(0).isEnabled(), is(true));
        assertThat(result.get(1).getId(), is(equalTo("first")));
        assertThat(result.get(1).isEnabled(), is(true));
        assertThat(result.get(2).getId(), is(equalTo("second")));
        assertThat(result.get(2).isEnabled(), is(false));
        assertThat(result.get(3).getId(), is(equalTo("third")));
        assertThat(result.get(3).isEnabled(), is(true));
    }

    @Test
    public void testStore() {
        String result = FeedDescriptors.store(ImmutableList.of(enabled(first), disabled(second), enabled(third)));

        assertThat(result, is(equalTo("first;!second;third")));
    }

    private FeedDescriptor enabled(IConfigurationElement config) {
        return new FeedDescriptor(config, true);
    }

    private FeedDescriptor disabled(IConfigurationElement config) {
        return new FeedDescriptor(config, false);
    }

}
