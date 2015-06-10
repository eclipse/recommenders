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

import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PollFeedJobTest {

    private Set<FeedDescriptor> feeds;

    @Before
    public void setup() {
        feeds = new HashSet<>();
    }

    @Test
    public void testFeedsWithSameIdBelongsTo() throws MalformedURLException {
        assertThat(new PollFeedJob(feeds).belongsTo(Constants.POLL_FEED_JOB_FAMILY), is(true));
    }
}
