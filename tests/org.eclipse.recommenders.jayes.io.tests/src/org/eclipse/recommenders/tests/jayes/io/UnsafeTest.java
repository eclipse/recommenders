/*******************************************************************************
 * Copyright (c) 2013 Michael Kutschke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Michael Kutschke - initial API and implementation
 ******************************************************************************/
package org.eclipse.recommenders.tests.jayes.io;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.nio.ByteBuffer;

import org.eclipse.recommenders.internal.jayes.io.util.BinaryFormatUtils;
import org.junit.Test;

public class UnsafeTest {

    @Test
    public void testUnsafeCopy() {
        byte[] theArray = new byte[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        byte[] dest = new byte[theArray.length];

        BinaryFormatUtils.fastGet(dest, ByteBuffer.wrap(theArray));

        assertThat(dest, is(theArray));
    }

    @Test
    public void testUnsafeCopyDouble() {
        byte[] theArray = new byte[80];
        byte[] dest = new byte[theArray.length];

        ByteBuffer.wrap(theArray).asDoubleBuffer().put(new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });

        BinaryFormatUtils.fastGet(dest, ByteBuffer.wrap(theArray));

        assertThat(dest, is(theArray));
    }

}
