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

import org.eclipse.core.runtime.URIUtil;
import org.eclipse.recommenders.internal.news.rcp.l10n.LogMessages;
import org.eclipse.recommenders.utils.Logs;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;

public class BrowserUtils {

    private BrowserUtils() {
        throw new IllegalStateException("Not meant to be instantiated"); //$NON-NLS-1$
    }

    public static void openInDefaultBrowser(URL url, List<String> parameters) {
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
    static String encodeURI(String url, List<String> parameters) throws URISyntaxException {
        String stringUrl;
        if (url == null) {
            return "";
        }
        if (parameters == null) {
            return url;
        }
        if (url.contains("?")) { //$NON-NLS-1$
            stringUrl = url.concat("&").concat(Joiner.on("&").join(parameters)); //$NON-NLS-1$ , //$NON-NLS-2$
        } else {
            stringUrl = url.concat("?").concat(Joiner.on("&").join(parameters)); //$NON-NLS-1$ , //$NON-NLS-2$
        }

        return URIUtil.fromString(stringUrl).toString();
    }
}
