/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

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
