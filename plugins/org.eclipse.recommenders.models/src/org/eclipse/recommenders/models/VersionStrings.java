/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Olav Lenz - initial API and implementation.
 */
package org.eclipse.recommenders.models;

public final class VersionStrings {

    /**
     * Checks if the version has the correct format.
     * 
     * The version must have the following structure: <code>major.minor.micro</code> where major, minor and micro are
     * any number (but w\o leading 0).
     */
    public static boolean isValidVersionString(String version) {
        return version.matches("([0-9]|([1-9][0-9]*))\\.([0-9]|([1-9][0-9]*))\\.([0-9]|([1-9][0-9]*))");
    }

    /**
     * Add '.0' as micro version if the micro version is missing. The method counts the '.' contained in the string and
     * add a '.0' if the number of '.' is smaller than 2.
     */
    public static String addMicroVersionIfMissing(String version) {
        String[] parts = version.split("\\.");
        if (parts.length < 3) {
            return version + ".0";
        }
        return version;
    }

}
