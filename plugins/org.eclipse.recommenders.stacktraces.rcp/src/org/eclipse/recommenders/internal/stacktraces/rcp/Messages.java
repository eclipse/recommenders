/**
 * Copyright (c) 2014 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.stacktraces.rcp;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.eclipse.recommenders.internal.stacktraces.rcp.messages"; //$NON-NLS-1$
    public static String PREF_SERVER_LABEL;
    public static String SETTINGS_ACTION;
    public static String SETTINGS_ACTION_REPORT_ALWAYS;
    public static String SETTINGS_ACTION_REPORT_NEVER;
    public static String SETTINGS_ACTION_REPORT_NOW;
    public static String SETTINGS_ANONYMIZE_STACKTRACES;
    public static String SETTINGS_CLEAR_MESSAGES;
    public static String SETTINGS_DESC;
    public static String SETTINGS_EMAIL;
    public static String SETTINGS_EMAIL_DESC;
    public static String SETTINGS_GROUP_PERSONAL;
    public static String SETTINGS_LEARN_MORE;
    public static String SETTINGS_NAME;
    public static String SETTINGS_NAME_DESC;
    public static String SETTINGS_PROVIDE_FEEDBACK;
    public static String SETTINGS_TITEL;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
