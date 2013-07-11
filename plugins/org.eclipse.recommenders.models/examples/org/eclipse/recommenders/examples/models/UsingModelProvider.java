package org.eclipse.recommenders.examples.models;

import org.eclipse.recommenders.models.IBasedName;
import org.eclipse.recommenders.models.IModelProvider;
import org.eclipse.recommenders.utils.names.ITypeName;

public class UsingModelProvider {

    RecommendationModel DUMMY = new RecommendationModel();
    IModelProvider<IBasedName<ITypeName>, RecommendationModel> service;

    void getModelForIDEType(Object ideIType) {
        IBasedName<ITypeName> name = convertToQualifiedTypeName(ideIType);
        RecommendationModel model = service.acquireModel(name).or(DUMMY);
        model.compute();
        // ...
        service.releaseModel(model);
    }

    private IBasedName<ITypeName> convertToQualifiedTypeName(Object ideIType) {
        // TODO Auto-generated method stub
        return null;
    }

    static class RecommendationModel {

        public void compute() {
            // TODO Auto-generated method stub

        }
    }
}
