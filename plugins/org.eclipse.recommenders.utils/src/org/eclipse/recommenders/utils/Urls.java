/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.utils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;

public final class Urls {

    public static String mangle(URL url) {
        return mangle(url.toExternalForm());
    }

    public static String mangle(String url) {
        return url.replaceAll("\\W", "_");
    }

    public static URL toUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw Throwables.propagate(e);
        }
    }

    public static URI toUri(URL url) {
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw Throwables.propagate(e);
        }
    }

    public static Optional<URI> parseURI(String uriString) {
        try {
            return Optional.of(new URI(uriString));
        } catch (URISyntaxException e) {
            return Optional.absent();
        }
    }

    public static boolean isUriProtocolSupported(URI uri, String... schemes) {
        try {
            for (String protocol : schemes) {
                if (protocol.toLowerCase().equals(uri.getScheme().toLowerCase())) {
                    return true;
                }
            }
        } catch (NullPointerException e) {
        }

        return false;
    }

    private Urls() {
    }
}
