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
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;

/**
 * Writer for a binary representation of a BayesNet. This works quicker than a Java Serialization. <br/>
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
 * @author Michael
 * 
 */
public class BinaryWriter {

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final int INT_BYTES = 4;
    private static final int DOUBLE_BYTES = 8;

    private static final int HEADER_BYTES = 8 + INT_BYTES;

    static final long MAGIC = 0xF00F00F00F00L;
    static final int VERSION = 1;

    /**
     * Writes the BayesNet to an OutputStream. This method does not close the OutputStream.
     * 
     * @param outStream
     * @param net
     * @throws IOException
     */
    public void write(OutputStream outStream, BayesNet net) throws IOException {
        IOUtils.write(write(net), outStream);
    }

    public byte[] write(BayesNet net) {
        int size = estimateBinarySize(net);
        ByteBuffer buffer = ByteBuffer.allocate(size);
        putBayesNet(net, buffer);
        byte[] header = getHeader();
        byte[] out = new byte[buffer.position() + header.length];
        System.arraycopy(header, 0, out, 0, header.length);
        System.arraycopy(buffer.array(), 0, out, header.length, buffer.position());
        return out;
    }

    private byte[] getHeader() {
        ByteBuffer buffer = ByteBuffer.allocate(HEADER_BYTES);
        buffer.putLong(MAGIC);
        buffer.putInt(VERSION);
        return buffer.array();
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
        size += estimateSize(net.getName()); // can only get exact size by converting to UTF-8
        // bayesnodes
        size += INT_BYTES;
        for (BayesNode node : net.getNodes()) {
            size += estimateBinarySize(node);
        }
        return size;
    }

    private int estimateSize(String string) {
        return INT_BYTES + string.length() * 4;
    }

    private int estimateBinarySize(BayesNode node) {
        int size = 0;
        // name
        size += estimateSize(node.getName());
        // outcomes
        size += INT_BYTES;
        for (String outcome : node.getOutcomes()) {
            size += estimateSize(outcome);
        }
        // parents
        size += INT_BYTES * (1 + node.getParents().size());
        // probabilities
        size += INT_BYTES + DOUBLE_BYTES * node.getProbabilities().length;

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
            putBayesNodeDeclaration(node, buffer);
        }
        for (BayesNode node : net.getNodes()) {
            putBayesNodeDefinition(node, buffer);
        }
    }

    private void putString(String string, ByteBuffer buffer) {
        byte[] bytes = string.getBytes(UTF8);
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
    private void putBayesNodeDeclaration(BayesNode node, ByteBuffer buffer) {
        putString(node.getName(), buffer);

        buffer.putInt(node.getOutcomeCount());
        for (String outcome : node.getOutcomes()) {
            putString(outcome, buffer);
        }
    }

    private void putBayesNodeDefinition(BayesNode node, ByteBuffer buffer) {
        putParents(node, buffer);
        putProbabilityTable(node, buffer);
    }

    private void putParents(BayesNode node, ByteBuffer buffer) {
        buffer.putInt(node.getParents().size());

        int[] parentIds = getIds(node.getParents());
        IntBuffer asIntBuffer = buffer.asIntBuffer();
        asIntBuffer.put(parentIds);
        buffer.position(buffer.position() + asIntBuffer.position() * INT_BYTES);
    }

    private void putProbabilityTable(BayesNode node, ByteBuffer buffer) {
        buffer.putInt(node.getProbabilities().length);

        DoubleBuffer asDoubleBuffer = buffer.asDoubleBuffer();
        asDoubleBuffer.put(node.getProbabilities());
        buffer.position(buffer.position() + asDoubleBuffer.position() * DOUBLE_BYTES);
    }

    private int[] getIds(List<BayesNode> parents) {
        IntBuffer buffer = IntBuffer.allocate(parents.size());
        for (BayesNode p : parents) {
            buffer.put(p.getId());
        }
        return buffer.array();
    }

}
