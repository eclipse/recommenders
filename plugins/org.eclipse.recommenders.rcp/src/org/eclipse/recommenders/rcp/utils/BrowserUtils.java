/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Dorn - initial API and implementation.
 */
package org.eclipse.recommenders.rcp.utils;

import java.net.URL;

import org.eclipse.swt.program.Program;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

public class BrowserUtils {

    /**
     * Tries to open a website using the default system browser. If that does not work, the browser configured in
     * Eclipse is used (internal browser by default)
     *
     * @param url
     */
    public static void openUrl(String url) {
        try {
            if (Program.launch(url)) {
                return;
            }
            // Fall back to Eclipse configured browser
            IWebBrowser browser = PlatformUI.getWorkbench().getBrowserSupport().createBrowser("recommenders"); //$NON-NLS-1$
            browser.openURL(new URL(url));
        } catch (Exception e) {
        }
    }
}
