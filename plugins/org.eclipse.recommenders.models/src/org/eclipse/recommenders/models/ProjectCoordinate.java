package org.eclipse.recommenders.models;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.recommenders.utils.Checks;
import org.eclipse.recommenders.utils.annotations.Nullable;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Strings;

/**
 * Represents a triple "group-id:artifact-id:version".
 */
public class ProjectCoordinate {

    /**
     * Constant that represents an unknown project coordinate. Use this constant whenever <code>null</code> or
     * {@link Optional#absent()} is not appropriate.
     */
    public static final ProjectCoordinate UNKNOWN = new ProjectCoordinate("UNKNOWN", "UNKNOWN", "-1");

    private final String groupId;
    private final String artifactId;
    private final String version;

    /**
     * Creates a new coordinate. Note that <code>null</code> values are replaced with an empty string.
     */
    public ProjectCoordinate(@Nullable String groupId, @Nullable String artifactId, @Nullable String version) {
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
        return HashCodeBuilder.reflectionHashCode(this, false);
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString() {
        return Joiner.on(':').join(getGroupId(), getArtifactId(), getVersion());
    }

    public static ProjectCoordinate valueOf(String coord) {
        String[] segments = coord.split(":");
        Checks.ensureIsInRange(segments.length,
                3,
                3,
                "Coordiante '%s' has invalid number of segments: %d",
                coord,
                segments.length);
        return new ProjectCoordinate(segments[0], segments[1], segments[2]);
    }
}
