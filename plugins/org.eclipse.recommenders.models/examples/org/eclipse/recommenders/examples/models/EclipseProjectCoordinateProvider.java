package org.eclipse.recommenders.examples.models;

import static org.eclipse.recommenders.utils.Throws.throwNotImplemented;

import org.eclipse.recommenders.examples.models.CompletionEngineExample.IJavaElement;
import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.models.dependencies.IProjectCoordinateProvider;

import com.google.common.base.Optional;

public class EclipseProjectCoordinateProvider implements IProjectCoordinateProvider {

    public Optional<ProjectCoordinate> map(IJavaElement jdtElement) {
        // TODO Auto-generated method stub
        throw throwNotImplemented();
    }

}
