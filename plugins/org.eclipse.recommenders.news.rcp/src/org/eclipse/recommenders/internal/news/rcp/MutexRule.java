package org.eclipse.recommenders.internal.news.rcp;

import org.eclipse.core.runtime.jobs.ISchedulingRule;

public class MutexRule implements ISchedulingRule {

    @Override
    public boolean contains(ISchedulingRule rule) {
        return rule == this;
    }

    @Override
    public boolean isConflicting(ISchedulingRule rule) {
        return rule == this;
    }

}
