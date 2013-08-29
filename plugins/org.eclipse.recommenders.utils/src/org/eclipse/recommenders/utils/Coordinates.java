/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Olav Lenz - Initial implementation.
 */
package org.eclipse.recommenders.utils;

import java.util.regex.Pattern;

public class Coordinates {

    private static final Pattern validCoordinatePart = Pattern.compile("[a-zA-Z]+(\\.[a-zA-Z]+)*");

    /**
     * Check if a (not number) part of a coordinate like artifactId or groupId has a valid format. The string is valid
     * if it is not empty and matches to the following regular expression <code>[a-zA-Z]+(\\.[a-zA-Z]+)*</code>.
     */
    public static boolean isCoordinateFieldValid(String string) {
        return validCoordinatePart.matcher(string).matches();
    }

}
