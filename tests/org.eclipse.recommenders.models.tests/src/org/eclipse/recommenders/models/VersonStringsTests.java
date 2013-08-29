/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Olav Lenz - initial API and implementation
 */
package org.eclipse.recommenders.models;

import static org.junit.Assert.*;

import org.junit.Test;

public class VersonStringsTests {

    @Test
    public void testValidVersion() {
        assertTrue(VersionStrings.isValidVersionString("0.100.1"));
    }

    @Test
    public void testEmptyVersionIsInvalid() {
        assertFalse(VersionStrings.isValidVersionString(""));
    }

    @Test
    public void testVersionWithLeadingZeroInMajorIsInvalid() {
        assertFalse(VersionStrings.isValidVersionString("01.1.1"));
    }

    @Test
    public void testVersionWithLeadingZeroInMinorIsInvalid() {
        assertFalse(VersionStrings.isValidVersionString("1.01.1"));
    }

    @Test
    public void testVersionWithLeadingZeroInMicroIsInvalid() {
        assertFalse(VersionStrings.isValidVersionString("1.1.01"));
    }

    @Test
    public void testVersionWithToManyPartsIsInvalid() {
        assertFalse(VersionStrings.isValidVersionString("0.1.2.3"));
    }

    @Test
    public void testNothingIsAddedIfItIsNotNecessary() {
        assertEquals("2.3.0", VersionStrings.addMicroVersionIfMissing("2.3.0"));
    }

    @Test
    public void testMicroVersionIsAddedCorrect() {
        assertEquals("2.3.0", VersionStrings.addMicroVersionIfMissing("2.3"));
    }

    @Test
    public void testAddMicroVersionFailed() {
        assertNotEquals("2.3.0", VersionStrings.addMicroVersionIfMissing("2.3-SNAPSHOT"));
    }

}
