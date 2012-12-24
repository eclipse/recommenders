package org.eclipse.recommenders.models.utils;

import org.eclipse.recommenders.models.Gav;

import com.google.common.base.Function;
import com.google.common.base.Optional;

/**
 * Resolves a (potentially IDE specific) resource to a {@link Gav}.
 */
public interface GenericGavMapper<T> extends Function<T, Optional<Gav>> {

    @Override
    public Optional<Gav> apply(T resource);

}
