/**
 * Copyright (c) 2015 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Dorn - initial API and implementation.
 */
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
