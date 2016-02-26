/**
 * Copyright (c) 2016 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Sewe - initial API and implementation.
 */
package org.eclipse.recommenders.news.core;

import java.io.InputStream;
import java.net.URI;
import java.util.Date;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.annotation.Nullable;

public interface IDownloadService {

    @Nullable
    InputStream download(URI uri, @Nullable IProgressMonitor monitor);

    @Nullable
    InputStream read(URI uri);

    /**
     * @param uri
     *            the download URI
     * @return the time of the last download attempt or <code>null</code> if no attempt has been made yet.
     */
    @Nullable
    Date getLastAttemptDate(URI uri);
}
