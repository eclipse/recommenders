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
package org.eclipse.recommenders.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Optional;
import com.google.common.base.Throwables;

public final class Uris {

    private Uris() {
    }

    public static String toStringWithMaskedPassword(URI uri, char mask) {
        if (uri.isOpaque()) {
            return uri.toString();
        }

        String userInfo = uri.getUserInfo();
        if (userInfo != null) {
            int indexOfColon = userInfo.indexOf(':');
            if (indexOfColon > 0) {
                userInfo = userInfo.substring(0, indexOfColon + 1)
                        + StringUtils.repeat(mask, userInfo.length() - indexOfColon - 1);
            }
        }
        return toStringWithUserInfo(uri, userInfo);
    }

    public static String toStringWithoutUserinfo(URI uri) {
        if (uri.isOpaque()) {
            return uri.toString();
        }

        return toStringWithUserInfo(uri, null);
    }

    private static String toStringWithUserInfo(URI uri, @Nullable String userInfo) {
        try {
            return new URI(uri.getScheme(), userInfo, uri.getHost(), uri.getPort(), uri.getPath(), uri.getQuery(),
                    uri.getFragment()).toString();
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

    public static URI toUri(String uriString) {
        try {
            return new URI(uriString);
        } catch (URISyntaxException e) {
            throw Throwables.propagate(e);
        }
    }

    public static boolean isUriProtocolSupported(URI uri, List<String> protocols) {
        for (String protocol : protocols) {
            if (StringUtils.equalsIgnoreCase(protocol, uri.getScheme())) {
                return true;
            }
        }

        return false;
    }
}
