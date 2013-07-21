package org.eclipse.recommenders.overrides;

import org.eclipse.recommenders.models.IBasedName;
import org.eclipse.recommenders.models.IModelProvider;
import org.eclipse.recommenders.utils.names.ITypeName;

public interface IOverrideModelProvider extends IModelProvider<IBasedName<ITypeName>, JayesOverrideModel> {
}
