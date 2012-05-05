/**
 * Copyright (c) 2010 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.utils;

import java.io.Closeable;
import java.io.IOException;

import org.apache.commons.lang3.SystemUtils;

public class IOUtils {

    public static final String LINE_SEPARATOR = SystemUtils.LINE_SEPARATOR;

    /**
     * 
     * @return returns <code>true</code> if the given closeable could be closed or was <code>null</code> -
     *         <code>false</code> in the case of an exception.
     */
    public static boolean closeQuietly(Closeable s) {
        if (s == null) {
            return true;
        }
        try {
            s.close();
            return true;
        } catch (IOException e) {
            System.err.printf("Failed to close resource '%s'. Caught exception printed below.\n", s);
            e.printStackTrace();
            return false;
        }
    }

    private IOUtils() {
        // pure utility class - do not instantiate.
    }
}
