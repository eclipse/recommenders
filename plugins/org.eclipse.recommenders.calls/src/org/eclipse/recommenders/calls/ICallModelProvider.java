package org.eclipse.recommenders.calls;

import org.eclipse.recommenders.models.BasedTypeName;
import org.eclipse.recommenders.models.IModelProvider;

import com.google.common.annotations.Beta;

/**
 * The model provider interface for loading {@link ICallModel}s. Note that this interface is a marker interface. Use the
 * "Find references" or "Find implementors" functionality of your IDE to find implementations and example usages of this
 * interface.
 */
@Beta
public interface ICallModelProvider extends IModelProvider<BasedTypeName, ICallModel> {
}
