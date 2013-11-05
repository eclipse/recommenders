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

import static org.eclipse.recommenders.tests.jayes.io.utils.Equality.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.eclipse.recommenders.internal.jayes.io.util.CommonsReader;
import org.eclipse.recommenders.internal.jayes.io.util.CommonsWriter;
import org.eclipse.recommenders.internal.jayes.io.util.ZipConverter;
import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.io.IBayesNetReader;
import org.eclipse.recommenders.jayes.io.IBayesNetWriter;
import org.eclipse.recommenders.jayes.io.JayesBifReader;
import org.eclipse.recommenders.jayes.io.JayesBifWriter;
import org.eclipse.recommenders.jayes.io.XDSLReader;
import org.eclipse.recommenders.jayes.io.XDSLWriter;
import org.eclipse.recommenders.jayes.io.XMLBIFReader;
import org.eclipse.recommenders.jayes.io.XMLBIFWriter;
import org.eclipse.recommenders.tests.jayes.util.NetExamples;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
public class ConverterTest {

    private final Class<? extends IBayesNetReader> readerClass1;
    private final Class<? extends IBayesNetWriter> writerClass1;

    private final Class<? extends IBayesNetReader> readerClass2;
    private final Class<? extends IBayesNetWriter> writerClass2;

    public ConverterTest(Class<? extends IBayesNetReader> readerClass1, Class<? extends IBayesNetWriter> writerClass1,
            Class<? extends IBayesNetReader> readerClass2, Class<? extends IBayesNetWriter> writerClass2) {
        this.readerClass1 = readerClass1;
        this.writerClass1 = writerClass1;
        this.readerClass2 = readerClass2;
        this.writerClass2 = writerClass2;
    }

    @Parameters
    public static Collection<Object[]> scenarios() {
        LinkedList<Object[]> scenarios = Lists.newLinkedList();

        scenarios.add(scenario(JayesBifReader.class, JayesBifWriter.class));
        scenarios.add(scenario(XDSLReader.class, XDSLWriter.class));
        scenarios.add(scenario(XMLBIFReader.class, XMLBIFWriter.class));
        scenarios.add(scenario(CommonsReader.class, CommonsWriter.class));

        List<Object[]> combinations = Lists.newArrayList();

        for (Object[] scenario : scenarios) {
            for (Object[] scenario2 : scenarios) {
                Object[] combination = new Object[] { scenario[0], scenario[1], scenario2[0], scenario2[1] };
                combinations.add(combination);
            }
        }

        return combinations;
    }

    private static Object[] scenario(Class<? extends IBayesNetReader> readerClass,
            Class<? extends IBayesNetWriter> writerClass) {
        return new Object[] { readerClass, writerClass };
    }

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void testConversion() throws Exception {
        BayesNet net = NetExamples.testNet1();

        File inputFile = tempFolder.newFile();
        ZipOutputStream out1 = new ZipOutputStream(new FileOutputStream(inputFile));
        out1.putNextEntry(new ZipEntry("foo/1.data"));
        IBayesNetWriter wrtr1 = writerClass1.getConstructor(OutputStream.class).newInstance(out1);
        wrtr1.write(net);
        out1.putNextEntry(new ZipEntry("meta"));
        out1.write(new byte[] { 1, 2, 3, 4, 5, 6, 7 });
        out1.close();

        File outputFile = tempFolder.newFile();

        ZipConverter.main(inputFile.getAbsolutePath(), outputFile.getAbsolutePath(), "-i", "data", "-o", "new", "-r",
                readerClass1.getName(), "-w", writerClass2.getName());

        ZipFile outZip = new ZipFile(outputFile);
        assertThat(outZip.getEntry("meta"), is(notNullValue()));
        assertThat(outZip.getEntry("foo/1.new"), is(notNullValue()));
        assertThat(outZip.getEntry("foo/1.data"), is(nullValue()));

        // test copying of non-model entries
        byte[] bytes = new byte[7];
        outZip.getInputStream(new ZipEntry("meta")).read(bytes);
        assertThat(bytes, is(new byte[] { 1, 2, 3, 4, 5, 6, 7 }));

        // test model entries
        IBayesNetReader rdr2 = readerClass2.getConstructor(InputStream.class).newInstance(
                outZip.getInputStream(new ZipEntry("foo/1.new")));
        BayesNet net2 = rdr2.read();
        rdr2.close();
        outZip.close();

        assertThat(net2, is(equalTo(net)));

    }

}
