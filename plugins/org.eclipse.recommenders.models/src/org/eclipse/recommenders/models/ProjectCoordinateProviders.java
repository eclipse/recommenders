package org.eclipse.recommenders.models;

import java.io.File;

import com.google.common.base.Function;

public class ProjectCoordinateProviders {

    public static final Function<File, ProjectCoordinate> POM_PROJECT = new Function<File, ProjectCoordinate>() {
        public ProjectCoordinate apply(File projectRoot) {
            return ProjectCoordinate.UNKNOWN;
        };
    };

    public static final Function<File, ProjectCoordinate> OSGI_PROJECT = new Function<File, ProjectCoordinate>() {
        public ProjectCoordinate apply(File projectRoot) {
            return ProjectCoordinate.UNKNOWN;
        };
    };
    public static final Function<File, ProjectCoordinate> POM_JAR = new Function<File, ProjectCoordinate>() {
        public ProjectCoordinate apply(File jarFile) {
            return ProjectCoordinate.UNKNOWN;
        };
    };

    public static final Function<File, ProjectCoordinate> OSGI_JAR = new Function<File, ProjectCoordinate>() {
        public ProjectCoordinate apply(File jarFile) {
            return ProjectCoordinate.UNKNOWN;
        };
    };

    public static final Function<File, ProjectCoordinate> FINGERPRINT_JAR = new Function<File, ProjectCoordinate>() {
        public ProjectCoordinate apply(File jarFile) {
            return ProjectCoordinate.UNKNOWN;
        };
    };
}
