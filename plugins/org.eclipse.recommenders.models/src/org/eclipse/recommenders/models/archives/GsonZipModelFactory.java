/**
 * Copyright (c) 2010, 2012 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.models.archives;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.recommenders.utils.IOUtils;
import org.eclipse.recommenders.utils.Zips;
import org.eclipse.recommenders.utils.annotations.Nullable;
import org.eclipse.recommenders.utils.annotations.Testing;
import org.eclipse.recommenders.utils.gson.GsonUtil;
import org.eclipse.recommenders.utils.names.IMethodName;
import org.eclipse.recommenders.utils.names.IName;
import org.eclipse.recommenders.utils.names.ITypeName;

import com.google.common.io.ByteStreams;

/**
 * A factory for loading JSON-based models for {@link ITypeName}s or {@link IMethodName}s from a ZIP file. The
 * implementation expects the zip file to contain zip entries following the naming convention described in
 * {@link Zips#path(ITypeName, String)} or {@link Zips#path(IMethodName, String)} with the file extension set to
 * ".json".
 */
public class GsonZipModelFactory<T> extends ModelFactoryAdapter<IName, T> {

    private static final String EXTENSION = ".json";
    private final Type type;
    private ZipFile zip;

    @Testing("available for testing to bypass io")
    GsonZipModelFactory(ZipFile file, Class<T> type) throws IOException {
        this.zip = file;
        this.type = type;
    }

    public GsonZipModelFactory(File file, Class<T> type) throws IOException {
        this(new ZipFile(file), type);
    }

    @Override
    public boolean hasModel(IName key) {
        ZipEntry entry = getEntry(key);
        return entry != null;
    }

    private @Nullable
    ZipEntry getEntry(IName m) {
        String name = null;
        if (m instanceof ITypeName) {
            ITypeName rType = (ITypeName) m;
            name = Zips.path(rType, EXTENSION);
        } else if (m instanceof IMethodName) {
            IMethodName rMethod = (IMethodName) m;
            name = Zips.path(rMethod, EXTENSION);
        }
        return zip.getEntry(name);
    }

    @Override
    public T createModel(IName key) throws Exception {
        InputStream is = null;
        try {
            ZipEntry entry = getEntry(key);
            is = zip.getInputStream(entry);
            String data = new String(ByteStreams.toByteArray(is));
            return GsonUtil.deserialize(data, type);
        } finally {
            IOUtils.closeQuietly(is);
        }
    }

    @Override
    public void close() throws IOException {
        zip.close();
    }
}
