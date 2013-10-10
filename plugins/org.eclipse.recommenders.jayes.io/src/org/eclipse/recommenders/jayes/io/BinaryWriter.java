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
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;

import com.google.common.base.Charsets;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

/**
 * Writer for a binary representation of a BayesNet. This format uses little-endian byte order. <br/>
 * <br/>
 * grammar (starting symbol S):<br/>
 * <br/>
 * [S] ::= [Header][BayesNet] <br/>
 * [Header] ::= [Magic Number][Format-Version] <br/>
 * [BayesNet] ::= [Name][Nr-Nodes][Node-Declarations][Node-Definitions] <br/>
 * [Name] ::= [Nr-bytes of the String][UTF-8 String] <br/>
 * [Node-Declarations] ::= [Node-Declaration]* <br/>
 * [Node-Declaration] ::= [Name][Nr-Outcomes][Outcomes] <br/>
 * [Outcomes] ::= [Outcome]* <br/>
 * [Outcome] ::= [Name] <br/>
 * [Node-Definitions] ::= [Node-Definition]* <br/>
 * [Node-Definition] ::= [Parents][CPT] <br/>
 * [Parents] ::= [Nr-parents][Parent-Ids] <br/>
 * [CPT] ::= [Nr-Entries][double[]] <br/>
 * 
 * 
 */
public class BinaryWriter implements Closeable {

    private static final int HEADER_BYTES = Ints.BYTES + Ints.BYTES;

    static final int MAGIC = 0xBA7E5B1F;
    static final int VERSION = 1;

    private OutputStream out;

    public BinaryWriter(OutputStream out) {
        this.out = out;
    }

    public void write(BayesNet net) throws IOException {
        IOUtils.write(writeToArray(net), out);
    }

    private byte[] writeToArray(BayesNet net) {
        int size = estimateBinarySize(net);
        ByteBuffer buffer = ByteBuffer.allocate(size + HEADER_BYTES).order(ByteOrder.LITTLE_ENDIAN);
        putHeader(buffer);
        putBayesNet(net, buffer);
        byte[] out = new byte[buffer.position()];
        System.arraycopy(buffer.array(), 0, out, 0, buffer.position());
        return out;
    }

    private void putHeader(ByteBuffer buffer) {
        buffer.putInt(MAGIC);
        buffer.putInt(VERSION);
    }

    /**
     * estimate binary size. Due to different Char encodings, this may be over-estimating, but is guaranteed to never
     * under-estimate.
     * 
     * @param net
     * @return
     */
    private int estimateBinarySize(BayesNet net) {
        int size = 0;
        // name
        size += estimateBinarySize(net.getName()); // can only get exact size by converting to UTF-8, approximate
        // bayesnodes
        size += Ints.BYTES;
        for (BayesNode node : net.getNodes()) {
            size += estimateBinarySize(node);
        }
        return size;
    }

    private int estimateBinarySize(String string) {
        return Ints.BYTES + string.length() * 4;
    }

    private int estimateBinarySize(BayesNode node) {
        int size = 0;
        // name
        size += estimateBinarySize(node.getName());
        // outcomes
        size += Ints.BYTES;
        for (String outcome : node.getOutcomes()) {
            size += estimateBinarySize(outcome);
        }
        // parents
        size += Ints.BYTES * (1 + node.getParents().size());
        // probabilities
        size += Ints.BYTES + Doubles.BYTES * node.getProbabilities().length;

        return size;
    }

    /**
     * [Name][Node-Declaration][Node-Definitions]
     * 
     * @param net
     * @param buffer
     */
    private void putBayesNet(BayesNet net, ByteBuffer buffer) {
        putString(net.getName(), buffer);
        // nodes
        buffer.putInt(net.getNodes().size());
        for (BayesNode node : net.getNodes()) {
            putNodeDeclaration(node, buffer);
        }
        for (BayesNode node : net.getNodes()) {
            putNodeDefinition(node, buffer);
        }
    }

    private void putString(String string, ByteBuffer buffer) {
        byte[] bytes = string.getBytes(Charsets.UTF_8);
        buffer.putInt(bytes.length);
        buffer.put(bytes);
    }

    /**
     * [Name][Outcomes]
     * 
     * @param node
     * @param buffer
     * @param offset
     */
    private void putNodeDeclaration(BayesNode node, ByteBuffer buffer) {
        putString(node.getName(), buffer);

        buffer.putInt(node.getOutcomeCount());
        for (String outcome : node.getOutcomes()) {
            putString(outcome, buffer);
        }
    }

    private void putNodeDefinition(BayesNode node, ByteBuffer buffer) {
        putParents(node, buffer);
        putCpt(node, buffer);
    }

    private void putParents(BayesNode node, ByteBuffer buffer) {
        buffer.putInt(node.getParents().size());

        int[] parentIds = getIds(node.getParents());
        IntBuffer asIntBuffer = buffer.asIntBuffer();
        asIntBuffer.put(parentIds);
        buffer.position(buffer.position() + asIntBuffer.position() * Ints.BYTES);
    }

    /**
     * write the conditional probability table
     */
    private void putCpt(BayesNode node, ByteBuffer buffer) {
        buffer.putInt(node.getProbabilities().length);

        DoubleBuffer asDoubleBuffer = buffer.asDoubleBuffer();
        asDoubleBuffer.put(node.getProbabilities());
        buffer.position(buffer.position() + asDoubleBuffer.position() * Doubles.BYTES);
    }

    private int[] getIds(List<BayesNode> parents) {
        IntBuffer buffer = IntBuffer.allocate(parents.size());
        for (BayesNode p : parents) {
            buffer.put(p.getId());
        }
        return buffer.array();
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

}
