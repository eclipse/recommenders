package examples;

import java.io.File;

import org.eclipse.recommenders.models.Gav;
import org.eclipse.recommenders.models.IBasedName;
import org.eclipse.recommenders.models.IModelProvider;
import org.eclipse.recommenders.models.utils.FileGavMapper;
import org.eclipse.recommenders.models.utils.GenericGavMapper;
import org.eclipse.recommenders.utils.names.ITypeName;

import com.google.common.base.Optional;

import examples.UsingModelProvider.RecommendationModel;

public class CompletionEngineExample {

    void resolveGavFromJarFile(IPackageFragementRoot jar, FileGavMapper r) {
        if (jar.isjar()) {
            Optional<Gav> gav = r.apply(jar.getFile());
        }
    }

    void resolveGavFromSourceFolder(IPackageFragementRoot srcFolder, GenericGavMapper r) {

        // REVIEW: no decision how the generic gav mapper was obtained. This needs to be designed later.
        if (srcFolder.isSourceFolder()) {
            Optional<Gav> gav = r.apply(srcFolder);
        }
    }

    private static final class CompletionEngine {
        IModelProvider<IBasedName<ITypeName>, RecommendationModel> s;
        GenericGavMapper<IJavaElement> resolver;

        void computeProposals(IJavaElement e) {
            Gav gav = resolver.apply(e).orNull();
            ITypeName type = e.getITypeName(); // convert somehow to ITypeName
            IBasedName<ITypeName> name = createQualifiedName(gav, type);
            RecommendationModel net = s.acquireModel(name).orNull();
            // ... do work
            s.releaseModel(net);

        }

        private IBasedName<ITypeName> createQualifiedName(Gav gav, ITypeName name) {
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
