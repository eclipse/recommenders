package org.eclipse.recommenders.models;

import org.eclipse.recommenders.utils.names.IMethodName;

public class UniqueMethodName extends AbstractUniqueName<IMethodName> {

    public UniqueMethodName(ProjectCoordinate pc, IMethodName name) {
        super(pc, name);
    }
}
