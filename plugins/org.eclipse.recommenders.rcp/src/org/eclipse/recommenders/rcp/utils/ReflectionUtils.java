/**
 * Copyright (c) 2014 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Sewe - initial API and implementation.
 */
package org.eclipse.recommenders.rcp.utils;

import static org.eclipse.recommenders.utils.Logs.log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.recommenders.internal.rcp.LogMessages;

public final class ReflectionUtils {

    public static Field getDeclaredField(Class<?> declaringClass, String name) {
        try {
            Field field = declaringClass.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (Exception e) {
            log(LogMessages.LOG_WARNING_REFLECTION_FAILED, e, name);
            return null;
        }
    }

    public static Method getDeclaredMethod(Class<?> declaringClass, String name, Class<?>... parameterTypes) {
        try {
            Method method = declaringClass.getDeclaredMethod(name, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (Exception e) {
            log(LogMessages.LOG_WARNING_REFLECTION_FAILED, e, name);
            return null;
        }
    }
}
