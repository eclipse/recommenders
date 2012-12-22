package examples;

import org.eclipse.recommenders.models.IModelProvider;
import org.eclipse.recommenders.models.IQualifiedName;
import org.eclipse.recommenders.utils.names.ITypeName;

public class UsingModelProvider {

    RecommendationModel DUMMY = new RecommendationModel();
    IModelProvider<IQualifiedName<ITypeName>, RecommendationModel> service;

    void getModelForIDEType(Object ideIType) {
        IQualifiedName<ITypeName> name = convertToQualifiedTypeName(ideIType);
        RecommendationModel model = service.acquireModel(name).or(DUMMY);
        model.compute();
        // ...
    }

    private IQualifiedName<ITypeName> convertToQualifiedTypeName(Object ideIType) {
        // TODO Auto-generated method stub
        return null;
    }

    static class RecommendationModel {

        public void compute() {
            // TODO Auto-generated method stub

        }
    }
}
