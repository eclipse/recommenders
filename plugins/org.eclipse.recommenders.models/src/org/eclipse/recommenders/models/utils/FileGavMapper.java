package org.eclipse.recommenders.models.utils;

import java.io.File;

import org.eclipse.recommenders.models.Gav;

import com.google.common.base.Optional;

/**
 * Base interface for specialized set of GAV mappers that can resolve GAVs from a give file, e.g., a JAR file or project
 * folder.
 * <p>
 * <code>FileGavResolver</code>s may be used by other (potentially IDE specific) resolvers to find a GAV.
 */
public interface FileGavMapper extends GenericGavMapper<File> {

    @Override
    public Optional<Gav> apply(File jarFileOrProjectFolder);

}
