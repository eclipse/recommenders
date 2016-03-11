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
import java.util.List;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.recommenders.news.api.NewsItem;

public final class PollingResult {

    public enum Status {

        NOT_DOWNLOADED,
        DOWNLOADED;
    }

    private final URI feedUri;
    private final List<NewsItem> newNewsItems;
    private final List<NewsItem> allNewsItems;
    private final Status status;

    public PollingResult(URI feedUri, List<NewsItem> newNewsItems, List<NewsItem> allNewsItems, Status status) {
        this.feedUri = requireNonNull(feedUri);
        this.newNewsItems = requireNonNull(newNewsItems);
        this.allNewsItems = requireNonNull(allNewsItems);
        this.status = requireNonNull(status);
    }

    public URI getFeedUri() {
        return feedUri;
    }

    public List<NewsItem> getNewNewsItems() {
        return newNewsItems;
    }

    public List<NewsItem> getAllNewsItems() {
        return allNewsItems;
    }

    public Status getStatus() {
        return status;
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
        PollingResult that = (PollingResult) other;
        return this.feedUri.equals(that.feedUri) && this.newNewsItems.equals(that.newNewsItems)
                && this.allNewsItems.equals(that.allNewsItems) && this.status.equals(that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(feedUri, newNewsItems, allNewsItems, status);
    }
}
