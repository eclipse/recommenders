package org.eclipse.recommenders.models;

// REVIEW do you have a better name?
public interface IQualifiedName<T> {

    T getName();

    // REVIEW: should we use the gav directly - or the strings?
    Gav getGav();

    String getGroupId();

    String getArtifactId();

    String getVersion();

}
