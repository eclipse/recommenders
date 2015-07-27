/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Based on org.eclipse.recommenders.rcp.utils.BrowserUtils
 */
package org.eclipse.recommenders.internal.news.rcp;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.eclipse.core.runtime.URIUtil;
import org.eclipse.recommenders.internal.news.rcp.l10n.LogMessages;
import org.eclipse.recommenders.utils.Logs;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

import com.google.common.annotations.VisibleForTesting;

public class BrowserUtils {

    private BrowserUtils() {
        throw new IllegalStateException("Not meant to be instantiated"); //$NON-NLS-1$
    }

    public static void openInDefaultBrowser(URL url, List<? extends NameValuePair> parameters) {
        String stringUrl = null;
        try {
            stringUrl = parameters == null || parameters.isEmpty() ? url.toExternalForm()
                    : encodeURI(url.toExternalForm(), parameters);
            IWebBrowser defaultBrowser = PlatformUI.getWorkbench().getBrowserSupport().createBrowser(null);
            defaultBrowser.openURL(new URL(stringUrl));
        } catch (Exception e) {
            Logs.log(LogMessages.ERROR_URL_MALFORMED, stringUrl);
        }
    }

    @VisibleForTesting
    static String encodeURI(String url, List<? extends NameValuePair> parameters) throws URISyntaxException {
        if (url == null) {
            return ""; //$NON-NLS-1$
        }
        if (parameters == null) {
            return url;
        }
        StringBuilder requestUrl = new StringBuilder(url);
        String query = URLEncodedUtils.format(parameters, "utf-8"); //$NON-NLS-1$
        if (!requestUrl.toString().contains("?")) { //$NON-NLS-1$
            requestUrl.append("?"); //$NON-NLS-1$
            requestUrl.append(query);
        } else {
            requestUrl.append("&"); //$NON-NLS-1$
            requestUrl.append(query);
        }

        return URIUtil.fromString(requestUrl.toString()).toString();
    }
}
