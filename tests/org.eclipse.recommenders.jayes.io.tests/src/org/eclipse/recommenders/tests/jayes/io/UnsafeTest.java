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

import java.io.IOException;
import java.nio.ByteBuffer;

import org.eclipse.recommenders.internal.jayes.io.util.BinaryFormatUtils;
import org.junit.Test;

public class UnsafeTest {

    @Test
    public void testUnsafeCopyInts() throws IOException {
        byte[] theArray = new byte[80];
        int[] src = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        int[] dest = new int[src.length];

        ByteBuffer.wrap(theArray).asIntBuffer().put(src);

        BinaryFormatUtils.fastGet(dest, ByteBuffer.wrap(theArray));

        assertThat(dest, is(src));
    }

    @Test
    public void testUnsafeCopyDouble() throws IOException {
        byte[] theArray = new byte[80];
        double[] src = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        double[] dest = new double[src.length];

        ByteBuffer.wrap(theArray).asDoubleBuffer().put(src);

        BinaryFormatUtils.fastGet(dest, ByteBuffer.wrap(theArray));

        assertThat(dest, is(src));
    }

}
