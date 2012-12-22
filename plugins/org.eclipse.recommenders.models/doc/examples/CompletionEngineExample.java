package examples;

import java.io.File;

import org.eclipse.recommenders.models.Gav;
import org.eclipse.recommenders.models.IModelProvider;
import org.eclipse.recommenders.models.IQualifiedName;
import org.eclipse.recommenders.models.utils.FileGavResolver;
import org.eclipse.recommenders.models.utils.GenericGavResolver;
import org.eclipse.recommenders.utils.names.ITypeName;

import com.google.common.base.Optional;

import examples.UsingModelProvider.RecommendationModel;

public class CompletionEngineExample {

    void resolveGavFromJarFile(IPackageFragementRoot jar, FileGavResolver r) {
        if (jar.isjar()) {
            Optional<Gav> gav = r.apply(jar.getFile());
        }
    }

    void resolveGavFromSourceFolder(IPackageFragementRoot srcFolder, FileGavResolver r) {
        if (srcFolder.isSourceFolder()) {
            Optional<Gav> gav = r.apply(srcFolder.getFile());
        }
    }

    private static final class CompletionEngine {
        IModelProvider<IQualifiedName<ITypeName>, RecommendationModel> s;
        GenericGavResolver<IJavaElement> resolver;

        void computeProposals(IJavaElement e) {

            Gav gav = resolver.apply(e).orNull();
            ITypeName type = e.getITypeName(); // convert somehow to ITypeName
            IQualifiedName<ITypeName> name = createQualifiedName(gav, type);
            RecommendationModel net = s.acquireModel(name).orNull();

        }

        private IQualifiedName<ITypeName> createQualifiedName(Gav gav, ITypeName name) {
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
