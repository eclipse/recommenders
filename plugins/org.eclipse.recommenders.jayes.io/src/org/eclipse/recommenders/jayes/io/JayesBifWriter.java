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
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;
import org.eclipse.recommenders.jayes.factor.arraywrapper.IntArrayWrapper;
import org.eclipse.recommenders.jayes.util.MathUtils;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Shorts;

/**
 * Writer for the Jayes Binary Interchange Format (JBIF).
 * 
 * JBIF conforms to the following grammar:
 * 
 * <dl>
 * <dt><var>JBIF</var></dt>
 * <dd><var>Header</var> <var>BayesNet</var></dd>
 * <dt><ar>Header<var></dt>
 * <dd>(magicNumber: 0xBA7E5B1F) (formatVersion: 1)</dd>
 * <dt><var>BayesNet</var></dt>
 * <dd><var>Name</var> (nodeCount: <code>int</code>) <var>NodeDeclaration</var>* <var>NodeDefinition</var>*</dd>
 * <dt><var>Name</var></dt>
 * <dd>(byteCount: <code>short</code>) (utf8: <code>byte</code>...)</dd>
 * <dt><var>NodeDeclaration</var></dt>
 * <dd><var>Name</var> (outcomeCount: <code>int</code>) <var>Outcomes</var>*</dd>
 * <dt><var>Outcome</var></dt>
 * <dd><var>Name</var></dd>
 * <dt><var>NodeDefinition</var></dt>
 * <dd><var>Parents</var> <var>CPT</var></dd>
 * <dt><var>Parents</var></dt>
 * <dd>(parentCount: <code>byte</code>) (parentIds: <code>int</code>...)</dd>
 * <dt><var>CPT</var></dt>
 * <dd>0x00 (entryCount: int) (probabilities: double...) | 0x01 (entryCount: int) (frequencies: int...) | 0x02
 * (entryCount: int) (reduced probabilities: double...)</dd>
 * </dl>
 * 
 * The CPT format depends on the set flags, which are automatically determined. The following list describes the current
 * possibilities:
 * 
 * <dl>
 * <dt><var>flags=0x00</var></dt>
 * <dd>plain CPT</dd>
 * <dt><var>flags=0x01</var></dt>
 * <dd>absolute frequencies</dd>
 * <dt><var>flags=0x02</var></dt>
 * <dd>reduced CPT, only saves the degrees of freedom (a row in a CPT sums to 1, therefore the last value of each row
 * can be omitted)</dd>
 * </dl>
 * 
 * Multi-byte primitive types are serialized in network byte-order.
 * 
 * @see JBifNodeHeader
 */
public class JayesBifWriter implements IBayesNetWriter {

    private static final int HEADER_BYTES = 2 * Ints.BYTES;

    public static final int MAGIC_NUMBER = 0xBA7E5B1F;
    public static final int FORMAT_VERSION = 1;

    private OutputStream out;

    public JayesBifWriter(OutputStream out) {
        this.out = out;
    }

    public void write(BayesNet bayesNet) throws IOException {
        IOUtils.write(writeToArray(bayesNet), out);
    }

    private byte[] writeToArray(BayesNet bayesNet) {
        ByteBuffer buffer = ByteBuffer.allocate(estimateBinarySize(bayesNet));
        putHeader(buffer);
        putBayesNet(bayesNet, buffer);
        byte[] out = new byte[buffer.position()];
        System.arraycopy(buffer.array(), 0, out, 0, buffer.position());
        return out;
    }

    /**
     * Estimate binary size. Due to UTF-8 being a variable-length encoding, this may be over-estimating but is
     * guaranteed to never under-estimate.
     */
    private int estimateBinarySize(BayesNet bayesNet) {
        int size = HEADER_BYTES;

        size += estimateBinarySize(bayesNet.getName());

        size += Ints.BYTES;
        for (BayesNode node : bayesNet.getNodes()) {
            size += estimateBinarySize(node);
        }
        return size;
    }

    private int estimateBinarySize(String string) {
        return Shorts.BYTES + string.length() * 4;
    }

    private int estimateBinarySize(BayesNode node) {
        int size = 0;

        size += estimateBinarySize(node.getName());

        size += Ints.BYTES;
        for (String outcome : node.getOutcomes()) {
            size += estimateBinarySize(outcome);
        }

        size += 1 + Ints.BYTES * node.getParents().size();

        size += Ints.BYTES + Doubles.BYTES * node.getProbabilities().length;

        return size;
    }

    private void putHeader(ByteBuffer buffer) {
        buffer.putInt(MAGIC_NUMBER);

        buffer.putInt(FORMAT_VERSION);
    }

    private void putBayesNet(BayesNet bayesNet, ByteBuffer buffer) {
        putName(bayesNet.getName(), buffer);

        buffer.putInt(bayesNet.getNodes().size());

        for (BayesNode node : bayesNet.getNodes()) {
            putNodeDeclaration(node, buffer);
        }

        for (BayesNode node : bayesNet.getNodes()) {
            putNodeDefinition(node, buffer);
        }
    }

    private void putName(String string, ByteBuffer buffer) {
        final byte[] utf8 = string.getBytes(Charsets.UTF_8);
        Preconditions.checkArgument(utf8.length < 2 << 16);
        final short byteCount = (short) utf8.length;
        buffer.putShort(byteCount);

        buffer.put(utf8);
    }

    private void putNodeDeclaration(BayesNode node, ByteBuffer buffer) {
        putName(node.getName(), buffer);

        buffer.putInt(node.getOutcomeCount());

        for (String outcome : node.getOutcomes()) {
            putName(outcome, buffer);
        }
    }

    private void putNodeDefinition(BayesNode node, ByteBuffer buffer) {
        putParents(node, buffer);

        JBifNodeHeader nodeHeader = determineHeader(node);

        buffer.put(nodeHeader.getFlag());

        switch (nodeHeader) {
        case DEFAULT:
            putCpt(node, buffer);
            break;
        case FREQUENCIES:
            putFrequencies(node, buffer);
            break;
        case REDUCED:
            putReducedCpt(node, buffer);
        }

    }

    private void putReducedCpt(BayesNode node, ByteBuffer buffer) {
        int[] dimensions = node.getFactor().getDimensions();
        int numCpds = MathUtils.productOfRange(dimensions, 0, dimensions.length - 1);
        int reducedLength = numCpds * (node.getOutcomeCount() - 1);
        buffer.putInt(reducedLength);
        // this assumes the dimensions are properly ordered

        DoubleBuffer asDoubleBuffer = buffer.asDoubleBuffer();
        for (int i = 0; i < numCpds; i++) {
            asDoubleBuffer.put(node.getProbabilities(), i * node.getOutcomeCount(), node.getOutcomeCount() - 1);
        }
        buffer.position(buffer.position() + asDoubleBuffer.position() * Doubles.BYTES);
    }

    private void putFrequencies(BayesNode node, ByteBuffer buffer) {
        buffer.putInt(node.getValues().length());

        Iterator<Number> it = node.getValues().iterator();

        while (it.hasNext()) {
            buffer.putInt(it.next().intValue());
        }

    }

    private JBifNodeHeader determineHeader(BayesNode node) {
        if (node.getValues() instanceof IntArrayWrapper) {
            return JBifNodeHeader.FREQUENCIES;
        }
        if (node.getOutcomeCount() == 2) {// TODO this is just random...
            return JBifNodeHeader.REDUCED;
        }
        return JBifNodeHeader.DEFAULT;
    }

    private void putParents(BayesNode node, ByteBuffer buffer) {
        final int parentCount = node.getParents().size();
        Preconditions.checkArgument(parentCount < 2 << 8);
        buffer.put((byte) parentCount);

        for (BayesNode p : node.getParents()) {
            buffer.putInt(p.getId());
        }
    }

    private void putCpt(BayesNode node, ByteBuffer buffer) {
        buffer.putInt(node.getProbabilities().length);

        DoubleBuffer asDoubleBuffer = buffer.asDoubleBuffer();
        asDoubleBuffer.put(node.getProbabilities());
        buffer.position(buffer.position() + asDoubleBuffer.position() * Doubles.BYTES);
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
