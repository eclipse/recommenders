package org.eclipse.recommenders.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.recommenders.utils.annotations.Nullable;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

/**
 * Represents a triple "group-id:artifact-id:version".
 */
public class Gav {

    private final String groupId;
    private final String artifactId;
    private final String version;

    /**
     * Creates a new GAV. Note that <code>null</code> values are replaced with an empty string.
     */
    public Gav(@Nullable String groupId, @Nullable String artifactId, @Nullable String version) {
        this.groupId = Strings.nullToEmpty(groupId);
        this.artifactId = Strings.nullToEmpty(artifactId);
        this.version = Strings.nullToEmpty(version);
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public int hashCode() {
        // REVIEW: this may be replaced by a faster implementation later.
        // it's only here to make Andreas happy ;)
        return HashCodeBuilder.reflectionHashCode(this, false);
    }

    @Override
    public boolean equals(Object obj) {
        // REVIEW: this may be replaced by a faster implementation later.
        // it's only here to make Andreas happy ;)
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString() {
        return Joiner.on(':').join(getGroupId(), getArtifactId(), getVersion());
    }
}
