package org.eclipse.recommenders.rcp.utils;

import org.eclipse.core.databinding.conversion.Converter;

public class EnumValueToBooleanConverter<T extends Enum<T>> extends Converter {

    private final T[] values;

    @SafeVarargs
    public EnumValueToBooleanConverter(T... values) {
        super(Object.class, Boolean.class);
        this.values = values;
    }

    @Override
    public Object convert(Object fromObject) {
        for (T value : values) {
            if (value == fromObject) {
                return true;
            }
        }
        return false;
    }

}
