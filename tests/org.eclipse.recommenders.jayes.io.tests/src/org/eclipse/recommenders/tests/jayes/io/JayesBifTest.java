package org.eclipse.recommenders.tests.jayes.io;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.io.JayesBifReader;
import org.eclipse.recommenders.jayes.io.JayesBifWriter;
import org.junit.Test;

public class JayesBifTest {

    @Test
    public void testReadDefaultNode() throws IOException {
        // create simple network
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // header
        buffer.putInt(JayesBifWriter.MAGIC_NUMBER);
        buffer.putInt(JayesBifWriter.FORMAT_VERSION);
        // network
        buffer.putShort((short) 0);
        buffer.putInt(1);
        // node declaration
        buffer.putShort((short) 0);
        buffer.putInt(2);
        buffer.putShort((short) 1);
        buffer.put((byte) 'a');
        buffer.putShort((short) 1);
        buffer.put((byte) 'b');
        // node definition
        buffer.put((byte) 0);
        buffer.put((byte) 0x00);
        buffer.putInt(2);
        buffer.putDouble(0.4);
        buffer.putDouble(0.6);

        byte[] array = buffer.array();

        ByteArrayInputStream in = new ByteArrayInputStream(array);

        JayesBifReader reader = new JayesBifReader(in);
        BayesNet net = reader.read();
        reader.close();

        assertThat(net.getNodes().size(), is(1));
        assertThat(net.getNode("").getOutcomeCount(), is(2));
        assertThat(net.getNode("").getOutcomeName(0), is("a"));
        assertThat(net.getNode("").getOutcomeName(1), is("b"));
        assertThat(net.getNode("").getProbabilities(), is(new double[] { 0.4, 0.6 }));

    }

    @Test
    public void testReadReducedNode() throws IOException {
        // create simple network
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // header
        buffer.putInt(JayesBifWriter.MAGIC_NUMBER);
        buffer.putInt(JayesBifWriter.FORMAT_VERSION);
        // network
        buffer.putShort((short) 0);
        buffer.putInt(1);
        // node declaration
        buffer.putShort((short) 0);
        buffer.putInt(2);
        buffer.putShort((short) 1);
        buffer.put((byte) 'a');
        buffer.putShort((short) 1);
        buffer.put((byte) 'b');
        // node definition
        buffer.put((byte) 0);
        buffer.put((byte) 0x02);
        buffer.putInt(1);
        buffer.putDouble(0.4);

        byte[] array = buffer.array();

        ByteArrayInputStream in = new ByteArrayInputStream(array);

        JayesBifReader reader = new JayesBifReader(in);
        BayesNet net = reader.read();
        reader.close();

        assertThat(net.getNodes().size(), is(1));
        assertThat(net.getNode("").getOutcomeCount(), is(2));
        assertThat(net.getNode("").getOutcomeName(0), is("a"));
        assertThat(net.getNode("").getOutcomeName(1), is("b"));
        assertThat(net.getNode("").getProbabilities(), is(new double[] { 0.4, 0.6 }));

    }

    @Test
    public void testReadFrequencyNode() throws IOException {
        // create simple network
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        // header
        buffer.putInt(JayesBifWriter.MAGIC_NUMBER);
        buffer.putInt(JayesBifWriter.FORMAT_VERSION);
        // network
        buffer.putShort((short) 0);
        buffer.putInt(1);
        // node declaration
        buffer.putShort((short) 0);
        buffer.putInt(2);
        buffer.putShort((short) 1);
        buffer.put((byte) 'a');
        buffer.putShort((short) 1);
        buffer.put((byte) 'b');
        // node definition
        buffer.put((byte) 0);
        buffer.put((byte) 0x01);
        buffer.putInt(2);
        buffer.putInt(4);
        buffer.putInt(6);

        byte[] array = buffer.array();

        ByteArrayInputStream in = new ByteArrayInputStream(array);

        JayesBifReader reader = new JayesBifReader(in);
        BayesNet net = reader.read();
        reader.close();

        assertThat(net.getNodes().size(), is(1));
        assertThat(net.getNode("").getOutcomeCount(), is(2));
        assertThat(net.getNode("").getOutcomeName(0), is("a"));
        assertThat(net.getNode("").getOutcomeName(1), is("b"));
        assertThat(net.getNode("").getProbabilities(), is(new double[] { 0.4, 0.6 }));

    }

}
