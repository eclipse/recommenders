package org.eclipse.recommenders.internal.models.rcp;

import org.eclipse.recommenders.models.ModelCoordinate;
import org.eclipse.recommenders.models.ProjectCoordinate;

public class Coordinates {

    public static ModelCoordinate toModelCoordinate(ProjectCoordinate pc, String classifier, String extension) {
        return new ModelCoordinate(pc.getGroupId(), pc.getArtifactId(), classifier, extension, pc.getVersion());
    }

    public static ProjectCoordinate toProjectCoordinate(ModelCoordinate mc) {
        return new ProjectCoordinate(mc.getGroupId(), mc.getArtifactId(), mc.getVersion());
    }

    private Coordinates() {
    }
}
