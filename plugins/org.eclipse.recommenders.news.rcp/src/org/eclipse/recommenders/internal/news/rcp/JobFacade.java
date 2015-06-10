/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.recommenders.news.rcp.IJobFacade;
import org.eclipse.recommenders.news.rcp.INewsService;

public class JobFacade implements IJobFacade {

    @Override
    public void schedule(final INewsService service, final long delay) {
        final Job job = createJob(service, delay);
        job.schedule();
    }

    @Override
    public void forceSchedule(final INewsService service, final long delay) {
        if (Job.getJobManager().find(Constants.JOB_FAMILY).length > 0) {
            Job.getJobManager().find(Constants.JOB_FAMILY)[0].cancel();
        }
        Job job = createJob(service, delay);
        job.schedule();
    }

    private Job createJob(final INewsService service, final long delay) {
        final Job job = new Job(Constants.JOB_FAMILY) {
            @Override
            protected IStatus run(IProgressMonitor monitor) {
                final PollFeedJob pollFeedJob = new PollFeedJob(Constants.JOB_FAMILY,
                        service.getFeedsToPoll(service.isOverride()));
                pollFeedJob.addJobChangeListener(new JobChangeAdapter() {
                    @Override
                    public void done(IJobChangeEvent event) {
                        service.jobDone(pollFeedJob);
                    }
                });
                pollFeedJob.schedule();
                return Status.OK_STATUS;
            }
        };
        job.setSystem(true);
        job.setPriority(Job.DECORATE);
        job.addJobChangeListener(new JobChangeAdapter() {
            @Override
            public void done(IJobChangeEvent event) {
                job.schedule(delay);
            }
        });
        return job;
    }

}
