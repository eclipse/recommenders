package org.eclipse.recommenders.models.utils;

import java.io.File;

import org.eclipse.recommenders.models.Gav;

import com.google.common.base.Function;
import com.google.common.base.Optional;

public interface FileGavResolver extends Function<File, Optional<Gav>> {

    @Override
    public Optional<Gav> apply(File jarFileOrProjectFolder);

}
