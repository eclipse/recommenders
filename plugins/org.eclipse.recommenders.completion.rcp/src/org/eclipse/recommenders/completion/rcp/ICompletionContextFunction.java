package org.eclipse.recommenders.completion.rcp;

/**
 * Completion context functions provide an extensible API for the recommenders completion context. Extenders may
 * register own implementations by providing them in a separate Guice module or adding them at runtime to the active
 * completion contexts by calling {@link IRecommendersCompletionContext#set(String, Object)} and passing an
 * ICompletionContextFunction as value.
 */
public interface ICompletionContextFunction<T> {

    /**
     * Computes some value for the specified key and from the given context. It's up to the function to either cache the
     * result for repeated accesses by storing it into context under the given key before returning or (ii) to compute
     * the value every time from scratch when a callee requests the key.
     */
    T compute(IRecommendersCompletionContext context, String key);
}
