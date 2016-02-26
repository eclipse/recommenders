/**
 * Copyright (c) 2016 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Sewe - initial API and implementation.
 */
package org.eclipse.recommenders.news.api;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.Nullable;

public abstract class PollingPolicy {

    public abstract boolean shouldPoll(@Nullable Date lastPolledDate, Date pollingDate);

    private static class AlwaysPollingPolicy extends PollingPolicy {

        @Override
        public boolean shouldPoll(@Nullable Date lastPolledDate, Date pollingDate) {
            return true;
        }
    }

    private static class NeverPollingPolicy extends PollingPolicy {

        @Override
        public boolean shouldPoll(@Nullable Date lastPolledDate, Date pollingDate) {
            return false;
        }
    }

    private static class IntervalBasedPollingPolicy extends PollingPolicy {

        private final long pollingInterval;
        private final TimeUnit timeUnit;

        private IntervalBasedPollingPolicy(long pollingInterval, TimeUnit timeUnit) {
            this.pollingInterval = pollingInterval;
            this.timeUnit = timeUnit;
        }

        @Override
        public boolean shouldPoll(@Nullable Date lastPolledDate, Date pollingDate) {
            if (lastPolledDate == null) {
                return true;
            }
            long intervalMillis = timeUnit.toMillis(pollingInterval);
            Date nextPollingDate = new Date(lastPolledDate.getTime() + intervalMillis);
            return nextPollingDate.before(pollingDate);
        }
    }

    public static PollingPolicy createAlwaysPollingPolicy() {
        return new AlwaysPollingPolicy();
    }

    public static PollingPolicy createNeverPollingPolicy() {
        return new NeverPollingPolicy();
    }

    public static PollingPolicy createIntervalBasedPollingPolicy(long pollingInterval, TimeUnit timeUnit) {
        return new IntervalBasedPollingPolicy(pollingInterval, timeUnit);
    }
}
