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
import java.util.List;

import org.eclipse.recommenders.jayes.BayesNode;

import sun.misc.Unsafe;

public class BinaryFormatUtils {

    private static Field buffer = null;
    private static Field nodeParents = null;
    private static Field nodeChildren = null;
    private static Unsafe unsafe = getUnsafe();
    private static final long bbBufferOffset;
    private static final long bnParentsOffset;
    private static final long bnChildrenOffset;
    private static final int byteArrayBaseOffset;

    private static final int intArrayBaseOffset;
    private static final int doubleArrayBaseOffset;

    static {
        try {
            buffer = ByteBuffer.class.getDeclaredField("hb");
            nodeParents = BayesNode.class.getDeclaredField("parents");
            nodeChildren = BayesNode.class.getDeclaredField("children");
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

        bnParentsOffset = unsafe.objectFieldOffset(nodeParents);
        bnChildrenOffset = unsafe.objectFieldOffset(nodeChildren);
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

    public static void fastGet(byte[] dest, ByteBuffer src) {
        Object hb = unsafe.getObject(src, bbBufferOffset);
        unsafe.copyMemory(hb, byteArrayBaseOffset + src.position(), dest, byteArrayBaseOffset, dest.length);
        src.position(src.position() + dest.length);
    }

    public static void fastGet(double[] dest, ByteBuffer src) throws IOException {
        if (dest.length * 8 > src.remaining()) {
            throw new IOException("not enough bytes left over");
        }
        Object hb = unsafe.getObject(src, bbBufferOffset);
        unsafe.copyMemory(hb, byteArrayBaseOffset + src.position(), dest, doubleArrayBaseOffset, dest.length * 8);
        src.position(src.position() + dest.length * 8);
    }

    public static void fastGet(int[] dest, ByteBuffer src) throws IOException {
        if (dest.length * 4 > src.remaining()) {
            throw new IOException("not enough bytes left over");
        }
        Object hb = unsafe.getObject(src, bbBufferOffset);
        unsafe.copyMemory(hb, byteArrayBaseOffset + src.position(), dest, intArrayBaseOffset, dest.length * 4);
        src.position(src.position() + dest.length * 4);
    }

    public static void fastSetParents(BayesNode node, List<BayesNode> parents) {
        unsafe.putObject(node, bnParentsOffset, parents);
        for (BayesNode parent : parents) {
            parent.getChildren().add(node);
        }
    }

}
