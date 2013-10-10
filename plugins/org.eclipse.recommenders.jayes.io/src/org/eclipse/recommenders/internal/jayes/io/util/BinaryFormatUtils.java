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
package org.eclipse.recommenders.internal.jayes.io.util;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import sun.misc.Unsafe;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

@SuppressWarnings("restriction")
public class BinaryFormatUtils {

    private static Field buffer = null;
    private static Unsafe unsafe = getUnsafe();
    private static final long bbBufferOffset;
    private static final int byteArrayBaseOffset;

    private static final int intArrayBaseOffset;
    private static final int doubleArrayBaseOffset;

    static {
        try {
            buffer = ByteBuffer.class.getDeclaredField("hb");
        } catch (NoSuchFieldException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        bbBufferOffset = unsafe.objectFieldOffset(buffer);

        byteArrayBaseOffset = unsafe.arrayBaseOffset(byte[].class);
        doubleArrayBaseOffset = unsafe.arrayBaseOffset(double[].class);
        intArrayBaseOffset = unsafe.arrayBaseOffset(int[].class);

    }

    public static Unsafe getUnsafe() {
        Unsafe unsafe = null;

        try {
            Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (sun.misc.Unsafe) field.get(null);
        } catch (Exception e) {
            throw new AssertionError(e);
        }
        return unsafe;
    }

    public static void fastGet(double[] dest, ByteBuffer src) throws IOException {
        if (dest.length * Doubles.BYTES > src.remaining()) {
            throw new IOException("not enough bytes left over");
        }

        if (ByteOrder.nativeOrder().equals(src.order())) {
            Object hb = unsafe.getObject(src, bbBufferOffset);
            unsafe.copyMemory(hb, byteArrayBaseOffset + src.position(), dest, doubleArrayBaseOffset, dest.length * 8);
        } else {
            src.asDoubleBuffer().get(dest);
        }
        src.position(src.position() + dest.length * Doubles.BYTES);
    }

    public static void fastGet(int[] dest, ByteBuffer src) throws IOException {
        if (dest.length * Ints.BYTES > src.remaining()) {
            throw new IOException("not enough bytes left over");
        }

        if (ByteOrder.nativeOrder().equals(src.order())) {
            Object hb = unsafe.getObject(src, bbBufferOffset);
            unsafe.copyMemory(hb, byteArrayBaseOffset + src.position(), dest, intArrayBaseOffset, dest.length * 4);
        } else {
            src.asIntBuffer().get(dest);
        }
        src.position(src.position() + dest.length * Ints.BYTES);
    }

}
