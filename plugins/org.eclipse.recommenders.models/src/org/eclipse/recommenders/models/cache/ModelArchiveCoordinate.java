package org.eclipse.recommenders.models.cache;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.recommenders.models.ProjectCoordinate;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

/**
 * Represents a Maven artifact coordinate "group-id:artifact-id:classifier:extension:version". This class is used
 * instead of Aether's Artifact class (i) to hide the usage of the Aether from clients, and (ii) to make clear that its
 * only a resource identifier; it does not locate a (resolved) resource.
 */
public final class ModelArchiveCoordinate extends ProjectCoordinate {

    public static final ModelArchiveCoordinate UNKNOWN = new ModelArchiveCoordinate("unknown", "unknown", "unknown", "unknown",
            "0.0.0");

    private final String classifier;
    private final String extension;

    public ModelArchiveCoordinate(String groupId, String artifactId, String classifier, String extension, String version) {
        super(groupId, artifactId, version);
        // REVIEW: empty classifiers stay empty. A specific implementation may interpret "empty" as "jar" but setting
        // the default here seems odd, is unexpected for developers not coming from maven land. Also Aether works with
        // "" and interprets it as "jar" when resolving such a coordinate.
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
        // REVIEW: this is not exactly the maven behavior as empty fields will be part of the string.
        // needs to be discussed later.
        return Joiner.on(':').join(getGroupId(), getArtifactId(), getClassifier(), getExtension(), getVersion());
    }

}
