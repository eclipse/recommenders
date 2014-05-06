/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Sewe - initial API and implementation.
 */
package org.eclipse.recommenders.internal.subwords.rcp;

import static org.eclipse.recommenders.internal.subwords.rcp.Constants.BUNDLE_ID;

import org.eclipse.osgi.util.NLS;

public final class Messages extends NLS {

    public static String PREFPAGE_TITLE_SUBWORDS;

    public static String PREFPAGE_DESCRIPTION_SUBWORDS;

    public static String FIELD_LABEL_ENABLE_COMPLETION_ON_CONSTRUCTORS;
    public static String FIELD_LABEL_ENABLE_COMPLETION_ON_TYPES;

    static {
        NLS.initializeMessages(BUNDLE_ID, Messages.class);
    }

    private Messages() {
    }
}
