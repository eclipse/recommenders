/**
 * Copyright (c) 2011 Sven Amann.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.utils.names;

import static org.eclipse.recommenders.utils.Checks.ensureIsNotNull;

import org.eclipse.recommenders.utils.annotations.Provisional;

@Provisional
public class VmAnnotation implements IAnnotation {

    private ITypeName annotationType;

    public static IAnnotation get(ITypeName annotationType) {
        return new VmAnnotation(annotationType);
    }

    protected VmAnnotation(ITypeName annotationType) {
        ensureIsNotNull(annotationType);
        this.annotationType = annotationType;
    }

    @Override
    public ITypeName getAnnotationType() {
        return annotationType;
    }
}
