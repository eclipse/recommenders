package org.eclipse.recommenders.news.api;

import java.net.URI;
import java.util.Date;
import java.util.Objects;

import org.eclipse.jdt.annotation.Nullable;

public final class FeedItem {

    private final String id;
    private final Date date;
    private final String description;
    private final String title;
    private final URI uri;

    public FeedItem(String id, Date date, String description, String title, URI uri) {
        super();
        this.id = id;
        this.date = date;
        this.description = description;
        this.title = title;
        this.uri = uri;
    }

    public String getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public URI getUrl() {
        return uri;
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
        FeedItem that = (FeedItem) other;
        return Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
