/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Olav Lenz - initial API and implementation.
 */
package org.eclipse.recommenders.snipmatch;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.eclipse.recommenders.snipmatch.messages"; //$NON-NLS-1$
    public static String ERROR_COULD_NOT_CONNECT_TO_REMOTE_REPOSITORY;
    public static String ERROR_INVALID_REMOTE_REPOSITORY;
    public static String ERROR_WHILE_OPENING_REPOSITORY;
    public static String ERROR_WHILE_UPDATE_CLONE_REPOSITORY;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
