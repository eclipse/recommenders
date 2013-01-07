package examples;

import static org.eclipse.recommenders.utils.Throws.throwNotImplemented;

import org.eclipse.recommenders.models.IProjectCoordinateProvider;
import org.eclipse.recommenders.models.ProjectCoordinate;

import com.google.common.base.Optional;

import examples.CompletionEngineExample.IJavaElement;

public class EclipseProjectCoordinateProvider implements IProjectCoordinateProvider {

    public Optional<ProjectCoordinate> map(IJavaElement jdtElement) {
        // TODO Auto-generated method stub
        throw throwNotImplemented();
    }

}
