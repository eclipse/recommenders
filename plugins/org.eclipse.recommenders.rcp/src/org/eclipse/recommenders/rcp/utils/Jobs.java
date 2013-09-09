package org.eclipse.recommenders.rcp.utils;

import static com.google.common.collect.Iterables.size;
import static java.util.Arrays.asList;

import java.util.Arrays;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;

public class Jobs {

    private static ISchedulingRule EXCLUSIVE = new ISchedulingRule() {

        @Override
        public boolean isConflicting(ISchedulingRule rule) {
            return rule == this;
        }

        @Override
        public boolean contains(ISchedulingRule rule) {
            return rule == this;
        }
    };

    public static IProgressMonitor getProgressGroup() {
        return Job.getJobManager().createProgressGroup();
    }

    public static void parallel(String task, Job... jobs) {
        parallel(task, asList(jobs));
    }

    public static void parallel(String task, Iterable<Job> jobs) {
        IProgressMonitor group = getProgressGroup();
        group.beginTask(task, size(jobs));
        for (Job job : jobs) {
            job.setProgressGroup(group, 1);
            job.schedule();
        }
    }

    public static void sequential(String task, Job... jobs) {
        sequential(task, Arrays.asList(jobs));
    }

    public static void sequential(String task, Iterable<Job> jobs) {
        IProgressMonitor group = getProgressGroup();
        group.beginTask(task, size(jobs));
        for (Job job : jobs) {
            job.setRule(EXCLUSIVE);
            job.setProgressGroup(group, 1);
            job.schedule();
        }
    }
}
