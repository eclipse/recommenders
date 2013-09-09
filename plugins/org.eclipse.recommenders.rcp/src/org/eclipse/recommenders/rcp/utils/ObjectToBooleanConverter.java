package org.eclipse.recommenders.rcp.utils;

import org.eclipse.core.databinding.conversion.IConverter;

public final class ObjectToBooleanConverter implements IConverter {
    @Override
    public Object getFromType() {
        return Object.class;
    }

    @Override
    public Object getToType() {
        return Boolean.TYPE;
    }

    @Override
    public Object convert(Object fromObject) {
        return fromObject != null;
    }
}