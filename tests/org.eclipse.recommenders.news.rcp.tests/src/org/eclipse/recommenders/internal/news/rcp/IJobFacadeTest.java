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
import static org.mockito.Matchers.notNull;
import static org.mockito.Mockito.*;

import java.util.Set;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.recommenders.news.rcp.IJobFacade;
import org.eclipse.recommenders.news.rcp.IPollFeedJob;
import org.junit.Before;
import org.junit.Test;

import com.google.common.eventbus.EventBus;

public class IJobFacadeTest {

    private EventBus bus;
    private Set<FeedDescriptor> feeds;

    @Before
    public void setup() {
        bus = mock(EventBus.class);
        feeds = mock(Set.class);
    }

    @Test
    public void testJobExists() {
        IJobFacade jobFacade = new JobFacade(bus);
        when(jobFacade.getJob()).thenReturn(mock(IPollFeedJob.class));
        assertThat(jobFacade.jobExists(jobFacade.getJob()), is(true));
    }

    @Test
    public void testSchedule() {
        IJobFacade jobFacade = new JobFacade(bus);
        jobFacade.schedule(feeds);
        when(jobFacade.getJob()).thenReturn(mock(IPollFeedJob.class));
        verify((Job) jobFacade.getJob(), times(1)).schedule();
    }

    @Test
    public void testJobDone() {
        IJobFacade jobFacade = new JobFacade(bus);
        when(jobFacade.getJob()).thenReturn(mock(IPollFeedJob.class));
        jobFacade.jobDone();
        assertThat(jobFacade.getMessages(), is(notNull()));
    }
}