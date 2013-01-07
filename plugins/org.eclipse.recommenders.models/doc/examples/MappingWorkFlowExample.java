package examples;

import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.models.mapping.IElementInfo;
import org.eclipse.recommenders.models.mapping.IMappingProvider;
import org.eclipse.recommenders.models.mapping.impl.MavenPomStrategy;

import com.google.common.base.Optional;

public class MappingWorkFlowExample {

	public static void useOfMapping(IMappingProvider mapping){
		IElementInfo ed = null;
		
		mapping.addStrategy(new MavenPomStrategy());
		
		Optional<ProjectCoordinate> optionalProjectCoordinate = mapping.getProjectCoordinate(ed);
		
		ProjectCoordinate projectCoordinate = null;
		if (optionalProjectCoordinate.isPresent()){
			projectCoordinate = optionalProjectCoordinate.get();
		}
	}
	
}
