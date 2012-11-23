package org.eclipse.recommenders.utils.names;

import static org.eclipse.recommenders.utils.Checks.ensureIsNotNull;

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
