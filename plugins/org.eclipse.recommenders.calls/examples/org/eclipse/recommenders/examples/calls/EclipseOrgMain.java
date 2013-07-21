package org.eclipse.recommenders.examples.calls;

import java.io.File;

import org.eclipse.recommenders.calls.PoolingCallModelProvider;
import org.eclipse.recommenders.examples.calls.EclipseOrgCallsRecommender.ObjectUsage;
import org.eclipse.recommenders.models.AetherModelRepository;
import org.eclipse.recommenders.utils.Recommendation;
import org.eclipse.recommenders.utils.names.IMethodName;
import org.eclipse.recommenders.utils.names.VmMethodName;
import org.eclipse.recommenders.utils.names.VmTypeName;

public class EclipseOrgMain {

    static String remote = "http://download.eclipse.org/recommenders/models/kepler/";
    static File local = com.google.common.io.Files.createTempDir();

    public static void main(String[] args) throws Exception {

        // setup:

        AetherModelRepository repository = new AetherModelRepository(local, remote);
        repository.open();
        PoolingCallModelProvider provider = new PoolingCallModelProvider(repository);
        provider.open();
        EclipseOrgCallsRecommender recommender = new EclipseOrgCallsRecommender(provider);

        // exercise:
        ObjectUsage query = createSampleQuery();
        for (Recommendation<IMethodName> rec : recommender.computeRecommendations(query)) {
            System.out.println(rec);
        }
        //
        for (Recommendation<IMethodName> rec : recommender.computeRecommendations(query)) {
            System.out.println(rec);
        }
    }

    private static ObjectUsage createSampleQuery() {
        ObjectUsage query = ObjectUsage.newObjectUsageWithDefaults();
        query.type = VmTypeName.STRING;
        query.overridesFirst = VmMethodName.get("Ljava/lang/Object.equals(Ljava/lang/Object;)Z");
        return query;
    }
}
