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
package org.eclipse.recommenders.news.api;

import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.annotation.Nullable;

public interface IFeedItemStore {

    /**
     * @param feedUri
     *            The feed URI for which the RSS news feed is to be stored
     * @param in
     *            An RSS news feed
     * @param monitor
     *            A progress monitor, or <code>null</code> if progress reporting and cancellation are not desired
     * @return The list of feed items not stored already
     */
    List<FeedItem> udpate(URI feedUri, InputStream in, @Nullable IProgressMonitor monitor);

    List<FeedItem> getFeedItems(URI feedUri);
}
