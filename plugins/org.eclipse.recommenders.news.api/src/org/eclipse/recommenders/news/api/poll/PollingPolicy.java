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
package org.eclipse.recommenders.news.api.poll;

import static java.util.concurrent.TimeUnit.*;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.eclipse.jdt.annotation.Nullable;

public abstract class PollingPolicy {

    private static final class FixedPollingPolicy extends PollingPolicy {

        private final boolean shouldPoll;

        public FixedPollingPolicy(boolean shouldPoll) {
            this.shouldPoll = shouldPoll;
        }

        @Override
        public boolean shouldPoll(@Nullable Date lastPolledDate, Date pollingDate) {
            return shouldPoll;
        }

        @Override
        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (other == null) {
                return false;
            }
            if (getClass() != other.getClass()) {
                return false;
            }
            FixedPollingPolicy that = (FixedPollingPolicy) other;
            return this.shouldPoll == that.shouldPoll;
        }

        @Override
        public int hashCode() {
            return shouldPoll ? 23 : 223;
        }
    }

    private static final class IntervalBasedPollingPolicy extends PollingPolicy {

        private final long pollingIntervalNanos;

        private IntervalBasedPollingPolicy(long pollingInterval, TimeUnit timeUnit) {
            pollingIntervalNanos = NANOSECONDS.convert(pollingInterval, timeUnit);
        }

        @Override
        public boolean shouldPoll(@Nullable Date lastPolledDate, Date pollingDate) {
            if (lastPolledDate == null) {
                return true;
            }
            long lastPolledNanos = NANOSECONDS.convert(lastPolledDate.getTime(), MILLISECONDS);
            long pollingNanos = NANOSECONDS.convert(pollingDate.getTime(), MILLISECONDS);
            return lastPolledNanos + pollingIntervalNanos < pollingNanos;
        }

        @Override
        public boolean equals(@Nullable Object other) {
            if (this == other) {
                return true;
            }
            if (other == null) {
                return false;
            }
            if (getClass() != other.getClass()) {
                return false;
            }
            IntervalBasedPollingPolicy that = (IntervalBasedPollingPolicy) other;
            return this.pollingIntervalNanos == that.pollingIntervalNanos;
        }

        @Override
        public int hashCode() {
            return (int) (pollingIntervalNanos ^ pollingIntervalNanos >>> 32);
        }
    }

    private static final PollingPolicy ALWAYS_POLLING_POLICY = new FixedPollingPolicy(true);

    private static final PollingPolicy NEVER_POLLING_POLICY = new FixedPollingPolicy(false);

    public abstract boolean shouldPoll(@Nullable Date lastPolledDate, Date pollingDate);

    public static PollingPolicy always() {
        return ALWAYS_POLLING_POLICY;
    }

    public static PollingPolicy never() {
        return NEVER_POLLING_POLICY;
    }

    public static PollingPolicy every(long pollingInterval, TimeUnit timeUnit) {
        return new IntervalBasedPollingPolicy(pollingInterval, timeUnit);
    }
}
