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
package org.eclipse.recommenders.jayes.io;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;

/**
 * a reader for the binary format written by {@link BinaryWriter}.
 * 
 * @author Michael
 * 
 */
public class BinaryReader {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final int INT_BYTES = 4;
    private static final int DOUBLE_BYTES = 8;

    public BayesNet read(InputStream inStream) throws IOException {
        return read(IOUtils.toByteArray(inStream));

    }

    public BayesNet read(byte[] array) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(array);
        try {
            return readBayesNet(buffer);
        } catch (RuntimeException ex) {
            throw new IOException("Malformed data", ex);
        }

    }

    private BayesNet readBayesNet(ByteBuffer buffer) throws IOException {
        readHeader(buffer);
        BayesNet net = new BayesNet();
        String name = readString(buffer);
        net.setName(name);
        int nrNodes = buffer.getInt();
        for (int i = 0; i < nrNodes; i++) {
            readBayesNodeDeclaration(net, buffer);
        }
        for (int i = 0; i < nrNodes; i++) {
            readBayesNodeDefinition(net, net.getNode(i), buffer);
        }
        return net;
    }

    private void readHeader(ByteBuffer buffer) throws IOException {
        long magic = buffer.getLong();
        if (magic != BinaryWriter.MAGIC) {
            throw new IOException("Cannot parse input: wrong magic number");
        }

        int version = buffer.getInt();
        if (version != BinaryWriter.VERSION) {
            throw new IOException("Cannot parse input: wrong data format version");
        }

    }

    private String readString(ByteBuffer buffer) {
        int nrBytes = buffer.getInt();

        byte[] bytes = new byte[nrBytes];
        buffer.get(bytes);
        return new String(bytes, UTF8);
    }

    /**
     * [Name][Outcomes]
     * 
     * @param node
     * @param buffer
     * @param offset
     */
    private void readBayesNodeDeclaration(BayesNet net, ByteBuffer buffer) {
        BayesNode node = net.createNode(readString(buffer));

        int outcomes = buffer.getInt();
        for (int i = 0; i < outcomes; i++) {
            node.addOutcome(readString(buffer));
        }

    }

    /**
     * [parents][CPT]
     * 
     * @param net
     * @param node
     * @param buffer
     */
    private void readBayesNodeDefinition(BayesNet net, BayesNode node, ByteBuffer buffer) {
        List<BayesNode> parents = readParents(net, buffer);
        node.setParents(parents);

        double[] doubles = readProbabilityTable(buffer);
        node.setProbabilities(doubles);
    }

    private double[] readProbabilityTable(ByteBuffer buffer) {
        int nrDoubles = buffer.getInt();
        double[] doubles = new double[nrDoubles];
        DoubleBuffer asDoubleBuffer = buffer.asDoubleBuffer();
        asDoubleBuffer.get(doubles);
        buffer.position(buffer.position() + asDoubleBuffer.position() * DOUBLE_BYTES);

        return doubles;
    }

    private List<BayesNode> readParents(BayesNet net, ByteBuffer buffer) {
        int[] ids = readParentIds(buffer);

        List<BayesNode> parents = new ArrayList<BayesNode>(ids.length);
        for (int id : ids) {
            parents.add(net.getNode(id));
        }

        return parents;
    }

    private int[] readParentIds(ByteBuffer buffer) {
        int nrParents = buffer.getInt();

        int[] ids = new int[nrParents];
        IntBuffer asIntBuffer = buffer.asIntBuffer();
        asIntBuffer.get(ids);
        buffer.position(buffer.position() + asIntBuffer.position() * INT_BYTES);
        return ids;
    }

}
