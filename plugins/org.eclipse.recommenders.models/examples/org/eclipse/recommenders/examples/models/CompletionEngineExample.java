package org.eclipse.recommenders.examples.models;

import java.io.File;

import org.eclipse.recommenders.examples.models.UsingModelProvider.RecommendationModel;
import org.eclipse.recommenders.models.IBasedName;
import org.eclipse.recommenders.models.IModelProvider;
import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.utils.names.ITypeName;

import com.google.common.base.Optional;

public class CompletionEngineExample {

    void resolveGavFromPackgeFragmentRoot(IPackageFragementRoot jdtElement, EclipseProjectCoordinateProvider r) {
        if (jdtElement.isjar()) {
            // ignore what type jdtElement is exactly!
        } else if (jdtElement.isSourceFolder()) {
            // src folders are mapped by the mapping service internally.
        }
        Optional<ProjectCoordinate> gav = r.map(jdtElement);
    }

    void resolveGavFromIJavaElement(IJavaElement jdtElement, EclipseProjectCoordinateProvider r) {
        // same for jar, src folder, package etc.:
        Optional<ProjectCoordinate> gav = r.map(jdtElement);
    }

    void resolveGavFromSourceFolder(IPackageFragementRoot srcFolder, EclipseProjectCoordinateProvider r) {
    }

    private static final class CompletionEngine {
        IModelProvider<IBasedName<ITypeName>, RecommendationModel> modelProvider;
        EclipseProjectCoordinateProvider coordService;

        void computeProposals(IJavaElement e) {
            ProjectCoordinate gav = coordService.map(e).orNull();
            ITypeName type = e.getITypeName(); // convert somehow to ITypeName
            IBasedName<ITypeName> name = createQualifiedName(gav, type);
            RecommendationModel net = modelProvider.acquireModel(name).orNull();
            // ... do work
            modelProvider.releaseModel(net);

        }

        private IBasedName<ITypeName> createQualifiedName(ProjectCoordinate gav, ITypeName name) {
            return null;
        }
    }

    interface IJavaElement {

        ITypeName getITypeName();
    }

    interface IPackageFragementRoot extends IJavaElement {

        // it's slightly more complicated but...
        File getFile();

        boolean isjar();

        boolean isSourceFolder();
    }

    interface IJavaProject {
    }

}
