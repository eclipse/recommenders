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

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;

import com.google.common.base.Charsets;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

/**
 * a reader for the binary format written by {@link BinaryWriter}.
 * 
 * 
 */
public class BinaryReader implements Closeable {

    private InputStream inStream;

    public BinaryReader(InputStream str) {
        this.inStream = str;
    }

    public BayesNet read() throws IOException {
        return read(IOUtils.toByteArray(inStream));

    }

    private BayesNet read(byte[] array) throws IOException {
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
            readNodeDeclaration(net, buffer);
        }

        for (int i = 0; i < nrNodes; i++) {
            readNodeDefinition(net, net.getNode(i), buffer);
        }
        return net;
    }

    private void readHeader(ByteBuffer buffer) throws IOException {
        int magic = buffer.getInt();
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
        return new String(bytes, Charsets.UTF_8);
    }

    /**
     * [Name][Outcomes]
     * 
     * @param node
     * @param buffer
     * @param offset
     */
    private void readNodeDeclaration(BayesNet net, ByteBuffer buffer) {
        BayesNode node = net.createNode(readString(buffer));

        int nrOutcomes = buffer.getInt();
        String[] outcomes = new String[nrOutcomes];
        for (int i = 0; i < nrOutcomes; i++) {
            outcomes[i] = readString(buffer);
        }
        node.addOutcomes(outcomes);

    }

    /**
     * [parents][CPT]
     * 
     * @param net
     * @param node
     * @param buffer
     * @throws IOException
     */
    private void readNodeDefinition(BayesNet net, BayesNode node, ByteBuffer buffer) throws IOException {
        List<BayesNode> parents = readParents(net, buffer);
        node.setParents(parents);

        double[] doubles = readCpt(buffer);
        node.setProbabilities(doubles);
    }

    private List<BayesNode> readParents(BayesNet net, ByteBuffer buffer) throws IOException {
        int[] ids = readParentIds(buffer);

        List<BayesNode> parents = new ArrayList<BayesNode>(ids.length);
        for (int id : ids) {
            parents.add(net.getNode(id));
        }

        return parents;
    }

    private int[] readParentIds(ByteBuffer buffer) throws IOException {
        int nrParents = buffer.getInt();

        int[] ids = new int[nrParents];
        buffer.asIntBuffer().get(ids);
        buffer.position(buffer.position() + ids.length * Ints.BYTES);
        return ids;
    }

    private double[] readCpt(ByteBuffer buffer) throws IOException {
        int nrDoubles = buffer.getInt();
        double[] doubles = new double[nrDoubles];
        buffer.asDoubleBuffer().get(doubles);
        buffer.position(buffer.position() + doubles.length * Doubles.BYTES);

        return doubles;
    }

    @Override
    public void close() throws IOException {
        inStream.close();

    }

}
