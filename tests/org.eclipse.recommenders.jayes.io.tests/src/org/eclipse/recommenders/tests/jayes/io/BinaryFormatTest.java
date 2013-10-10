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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;
import org.eclipse.recommenders.jayes.inference.junctionTree.JunctionTreeAlgorithm;
import org.eclipse.recommenders.jayes.io.BinaryReader;
import org.eclipse.recommenders.jayes.io.BinaryWriter;
import org.eclipse.recommenders.jayes.io.XDSLReader;
import org.eclipse.recommenders.tests.jayes.io.utils.Equality;
import org.junit.Test;

public class BinaryFormatTest {

    @Test
    public void binaryRoundtripTest() throws IOException {
        XDSLReader xrdr = new XDSLReader();
        BayesNet net = xrdr.read("test/models/rain.xdsl");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryWriter wrtr = new BinaryWriter(out);
        wrtr.write(net);
        wrtr.close();

        byte[] bs = out.toByteArray();

        BinaryReader rdr = new BinaryReader(new ByteArrayInputStream(bs));

        BayesNet net2 = rdr.read();

        rdr.close();

        assertThat(net, Equality.equals(net2));

        JunctionTreeAlgorithm jta = new JunctionTreeAlgorithm();
        jta.setNetwork(net2);
        jta.addEvidence(net2.getNode("grass_wet"), "yes");
        jta.addEvidence(net2.getNode("neighbor_grass_wet"), "yes");

        // compare with computed results from GeNIe
        assertArrayEquals(new double[] { 0.7271, 0.2729 }, jta.getBeliefs(net2.getNode("sprinkler_on")), 0.0001);
        assertArrayEquals(new double[] { 0.4596, 0.5404 }, jta.getBeliefs(net2.getNode("rain")), 0.0001);
    }

    // test a network where the nodes are not topologically sorted. The data format should not depend on the assumption
    // that parents have a lower id that their children
    @Test
    public void nonTopologicallySortedNetworkTest() throws IOException {
        BayesNet net = new BayesNet();
        BayesNode a = net.createNode("A");
        a.addOutcomes("t", "f");

        BayesNode b = net.createNode("B");
        b.addOutcomes("t", "f");

        a.setParents(Arrays.asList(b));

        b.setProbabilities(0.5, 0.5);
        a.setProbabilities(0.5, 0.5, 0.5, 0.5);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        BinaryWriter wrtr = new BinaryWriter(out);
        wrtr.write(net);
        wrtr.close();

        byte[] cs = out.toByteArray();
        BinaryReader rdr = new BinaryReader(new ByteArrayInputStream(cs));
        BayesNet net2 = rdr.read();
        rdr.close();

        assertThat(net2, Equality.equals(net));

    }

}
