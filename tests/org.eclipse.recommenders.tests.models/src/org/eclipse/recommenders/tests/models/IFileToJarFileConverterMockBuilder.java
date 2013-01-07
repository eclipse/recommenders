/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Olav Lenz - initial API and implementation
 */
package org.eclipse.recommenders.tests.models;

import static com.google.common.base.Optional.fromNullable;
import static org.eclipse.recommenders.utils.Throws.throwUnhandledException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarFile;

import org.eclipse.recommenders.models.mapping.impl.MavenPomPropertiesStrategy.IFileToJarFileConverter;
import org.eclipse.recommenders.tests.JarFileMockBuilder;
import org.eclipse.recommenders.utils.Tuple;

import com.google.common.base.Optional;

public class IFileToJarFileConverterMockBuilder {

    private final List<Tuple<String, Properties>> entries = new LinkedList<Tuple<String, Properties>>();

    public IFileToJarFileConverterMockBuilder put(String pomPropertiesFileName, Properties properties) {
        entries.add(Tuple.newTuple(pomPropertiesFileName, properties));
        return this;
    }

    private ByteArrayInputStream createInputStream(Properties properties) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            properties.store(outputStream, "");
        } catch (IOException e) {
            throwUnhandledException(e);
        }
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public IFileToJarFileConverter build() {
        return new IFileToJarFileConverter() {

            @Override
            public Optional<JarFile> createJarFile(File file) {
                final JarFileMockBuilder builder = new JarFileMockBuilder();

                for (Tuple<String, Properties> entry : entries) {
                    builder.addEntry(entry.getFirst(), createInputStream(entry.getSecond()));
                }
                return fromNullable(builder.build());
            }
        };
    }

    public static IFileToJarFileConverter createEmptyIFileToJarFileConverter() {
        return (new IFileToJarFileConverterMockBuilder()).build();
    }
}
