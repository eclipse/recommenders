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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.eclipse.recommenders.commons.bayesnet.BayesianNetwork;
import org.eclipse.recommenders.jayes.io.JayesBifWriter;

public class ZipConverter {

    public static void main(String[] args) throws Exception {
        String input = args[0];
        String output = args[1];

        ZipFile inZip = new ZipFile(new File(input));
        File outputFile = new File(output);
        convertZip(inZip, outputFile);

    }

    public static void convertZip(ZipFile inZip, File outputFile) throws ZipException, IOException,
            FileNotFoundException, Exception {

        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outputFile));

        Enumeration<? extends ZipEntry> entries = inZip.entries();
        while (entries.hasMoreElements()) {
            ZipEntry zipEntry = entries.nextElement();
            out.putNextEntry(new ZipEntry(zipEntry.getName()));

            if (zipEntry.getName().endsWith(".data")) {
                BayesNetConverter converter = new BayesNetConverter();
                JayesBifWriter wrtr = new JayesBifWriter(out);
                wrtr.write(converter.transform(BayesianNetwork.read(inZip.getInputStream(zipEntry))));

            } else {
                IOUtils.copy(inZip.getInputStream(zipEntry), out);
            }
        }

        out.close();
        inZip.close();
    }
}
