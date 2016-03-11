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

import static java.util.Objects.requireNonNull;

import java.net.URI;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

public final class PollingRequest {

    private final URI feedUri;
    private final PollingPolicy pollingPolicy;

    public PollingRequest(URI feedUri, PollingPolicy pollingPolicy) {
        this.feedUri = requireNonNull(feedUri);
        this.pollingPolicy = requireNonNull(pollingPolicy);
    }

    public URI getFeedUri() {
        return feedUri;
    }

    public PollingPolicy getPollingPolicy() {
        return pollingPolicy;
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedUri, pollingPolicy);
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
        PollingRequest that = (PollingRequest) other;
        return this.feedUri.equals(that.feedUri) && this.pollingPolicy.equals(that.pollingPolicy);
    }
}
