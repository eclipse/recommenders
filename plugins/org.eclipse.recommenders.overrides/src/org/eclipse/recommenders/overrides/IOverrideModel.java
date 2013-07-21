package org.eclipse.recommenders.overrides;

import java.util.List;

import org.eclipse.recommenders.utils.Recommendation;
import org.eclipse.recommenders.utils.names.IMethodName;
import org.eclipse.recommenders.utils.names.ITypeName;

import com.google.common.collect.ImmutableSet;

public interface IOverrideModel {

    public abstract void reset();

    public abstract void setObservedMethod(IMethodName method);

    public abstract ITypeName getType();

    public abstract ImmutableSet<IMethodName> getKnownMethods();

    public abstract ImmutableSet<String> getKnownPatterns();

    public abstract List<Recommendation<IMethodName>> getRecommendedOverrides();

}
