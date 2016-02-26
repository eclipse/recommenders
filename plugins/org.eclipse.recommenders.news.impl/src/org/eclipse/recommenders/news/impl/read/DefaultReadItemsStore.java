/**
 * Copyright (c) 2016 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Dorn - initial API and implementation.
 */
package org.eclipse.recommenders.news.impl.read;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import org.eclipse.core.runtime.Platform;
import org.eclipse.recommenders.news.api.FeedItem;
import org.eclipse.recommenders.news.api.read.IReadItemsStore;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.google.common.base.Charsets;

public class DefaultReadItemsStore implements IReadItemsStore {

    private Path fileLocation;

    public DefaultReadItemsStore() {
        Bundle bundle = FrameworkUtil.getBundle(this.getClass());
        Path stateLocation = Platform.getStateLocation(bundle).toFile().toPath();
        fileLocation = stateLocation.resolve("read-items.properties"); //$NON-NLS-1$
    }

    @Override
    public void markAsRead(FeedItem feedItem) {
        try {
            Files.write(fileLocation, feedItem.getId().getBytes(Charsets.UTF_8), StandardOpenOption.APPEND);
        } catch (IOException e) {
            // TODO log
        }
    }

    @Override
    public boolean isRead(FeedItem feedItem) {
        try (BufferedReader reader = Files.newBufferedReader(fileLocation, Charsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.equals(feedItem.getId())) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            // TODO log
            return false;
        }
    }
}
