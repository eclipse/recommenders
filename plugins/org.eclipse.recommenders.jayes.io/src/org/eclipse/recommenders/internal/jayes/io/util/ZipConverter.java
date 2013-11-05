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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.recommenders.jayes.io.IBayesNetReader;
import org.eclipse.recommenders.jayes.io.IBayesNetWriter;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class ZipConverter {

    @Argument(index = 0, required = true, usage = "input file", metaVar = "<FILE>")
    File inputFile;

    @Argument(index = 1, required = true, usage = "output file", metaVar = "<FILE>")
    File outputFile;

    @Option(name = "-i", aliases = { "--inputExtension" }, usage = "input extension", metaVar = "<EXT>", required = true)
    String inputExtension;

    @Option(name = "-o", aliases = { "--outputExtension" }, usage = "input extension", metaVar = "<EXT>", required = true)
    String outputExtension;

    @Option(name = "-r", aliases = { "--reader" }, usage = "reader class", handler = ClassOptionHandler.class, required = true)
    Class<? extends IBayesNetReader> readerClass;

    @Option(name = "-w", aliases = { "--writer" }, usage = "writer class", handler = ClassOptionHandler.class, required = true)
    Class<? extends IBayesNetReader> writerClass;

    public static void main(String... args) throws Exception {
        ZipConverter converter = new ZipConverter();

        CmdLineParser parser = new CmdLineParser(converter);
        parser.parseArgument(args);

        converter.convertZip();

    }

    public void convertZip() throws Exception {
        ZipFile inZip = new ZipFile(inputFile);

        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outputFile));

        Enumeration<? extends ZipEntry> entries = inZip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();

            if (zipEntry.getName().endsWith(inputExtension)) {
                out.putNextEntry(new ZipEntry(zipEntry.getName().replaceFirst(inputExtension + "$", outputExtension)));
                IBayesNetWriter wrtr = instantiateWriter(out);
                IBayesNetReader rdr = instantiateReader(inZip.getInputStream(zipEntry));
                wrtr.write(rdr.read());
                rdr.close();

            } else {
                out.putNextEntry(new ZipEntry(zipEntry.getName()));
                IOUtils.copy(inZip.getInputStream(zipEntry), out);
            }
        }

        out.close();
        inZip.close();
    }

    private IBayesNetWriter instantiateWriter(OutputStream out) throws Exception {
        return (IBayesNetWriter) writerClass.getConstructor(OutputStream.class).newInstance(out);
    }

    private IBayesNetReader instantiateReader(InputStream in) throws Exception {
        return (IBayesNetReader) readerClass.getConstructor(InputStream.class).newInstance(in);
    }
}
