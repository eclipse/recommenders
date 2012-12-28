package examples;

import java.io.File;

import org.eclipse.recommenders.models.IBasedName;
import org.eclipse.recommenders.models.IModelProvider;
import org.eclipse.recommenders.models.IProjectCoordinateProvider;
import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.utils.names.ITypeName;

import com.google.common.base.Optional;

import examples.UsingModelProvider.RecommendationModel;

public class CompletionEngineExample {

    void resolveGavFromPackgeFragmentRoot(IPackageFragementRoot jdtElement, IProjectCoordinateProvider r) {
        if (jdtElement.isjar()) {
            // ignore what type jdtElement is exactly!
        } else if (jdtElement.isSourceFolder()) {
            // src folders are mapped by the mapping service internally.
        }
        Optional<ProjectCoordinate> gav = r.map(jdtElement);
    }

    void resolveGavFromIJavaElement(IJavaElement jdtElement, IProjectCoordinateProvider r) {
        // same for jar, src folder, package etc.:
        Optional<ProjectCoordinate> gav = r.map(jdtElement);
    }

    void resolveGavFromSourceFolder(IPackageFragementRoot srcFolder, IProjectCoordinateProvider r) {
    }

    private static final class CompletionEngine {
        IModelProvider<IBasedName<ITypeName>, RecommendationModel> modelProvider;
        IProjectCoordinateProvider coordService;

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

    interface IPackageFragementRoot {

        // it's slightly more complicated but...
        File getFile();

        boolean isjar();

        boolean isSourceFolder();
    }

    interface IJavaProject {
    }

}
