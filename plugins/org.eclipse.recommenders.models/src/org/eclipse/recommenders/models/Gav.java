package org.eclipse.recommenders.models;

import org.eclipse.recommenders.utils.annotations.Nullable;

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

}
