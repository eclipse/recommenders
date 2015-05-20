package org.eclipse.recommenders.internal.types.rcp;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.recommenders.utils.names.ITypeName;

import com.google.common.collect.ImmutableSet;

public interface ITypesIndexService {

    ImmutableSet<String> subtypes(IType expected, String prefix);

    ImmutableSet<String> subtypes(ITypeName expected, String prefix, IJavaProject project);

    ImmutableSet<String> subtypes(String type, String prefix, IJavaProject project);
}
