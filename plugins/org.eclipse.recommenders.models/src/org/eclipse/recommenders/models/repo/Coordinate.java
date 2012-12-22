package org.eclipse.recommenders.models.repo;

import org.eclipse.recommenders.models.Gav;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

/**
 * Represents a Maven artifact coordinate "group-id:artifact-id:classifier:extension:version". This class is used
 * instead of Aether's Artifact class (i) to hide the usage of the Aether from clients, and (ii) to mkae clear that its
 * only a resource identifier.
 */
public class Coordinate extends Gav {

    public static final Coordinate UNKNOWN = new Coordinate("unknown", "unknown", "unknown", "unknown", "0.0.0");

    private final String classifier;
    private final String extension;

    public Coordinate(String groupId, String artifactId, String classifier, String extension, String version) {
        super(groupId, artifactId, version);
        this.classifier = Strings.nullToEmpty(classifier);
        this.extension = Strings.nullToEmpty(extension);
    }

    public String getClassifier() {
        return classifier;
    }

    public String getExtension() {
        return extension;
    }

    @Override
    public String toString() {
        return Joiner.on(':').join(getGroupId(), getArtifactId(), getClassifier(), getExtension(), getVersion());
    }

}
