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
import org.eclipse.recommenders.jayes.io.BinaryWriter;

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
                BinaryWriter wrtr = new BinaryWriter(out);
                wrtr.write(converter.transform(BayesianNetwork.read(inZip.getInputStream(zipEntry))));

            } else {
                IOUtils.copy(inZip.getInputStream(zipEntry), out);
            }
        }

        out.close();
        inZip.close();
    }
}
