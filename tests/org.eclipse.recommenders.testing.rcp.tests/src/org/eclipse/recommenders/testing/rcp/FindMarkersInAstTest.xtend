/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.testing.rcp

import org.junit.Test
import static junit.framework.Assert.*
import static org.eclipse.recommenders.tests.jdt.AstUtils.*

class FindMarkersInAstTest {

    @Test
    def void test001() {
        val code = '''$public class X extends Y {}'''
        val markers = createAstWithMarkers(code.toString)
        assertTrue(markers.second.contains(0))
    }

    @Test
    def void test002() {
        val code = '''class $X$ {}'''
        val markers = createAstWithMarkers(code.toString)

        assertFalse(markers.first.toString.contains(MARKER))

        assertTrue(markers.second.contains(6))
        assertTrue(markers.second.contains(7))
    }
}
