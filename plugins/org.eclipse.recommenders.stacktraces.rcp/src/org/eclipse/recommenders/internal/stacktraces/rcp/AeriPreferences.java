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

import static org.eclipse.recommenders.internal.stacktraces.rcp.Constants.*;

import javax.inject.Inject;

import org.eclipse.e4.core.di.extensions.Preference;

@SuppressWarnings("restriction")
public class AeriPreferences {

    // plugin settings
    @Inject
    @Preference(PROP_SERVER)
    public String server;

    @Inject
    @Preference(PROP_CONFIGURED)
    public boolean configured;

    @Inject
    @Preference(PROP_WHITELISTED_PLUGINS)
    public String whitelistedPlugins;

    @Inject
    @Preference(PROP_WHITELISTED_PACKAGES)
    public String whitelistedPackages;

    // user settings
    @Inject
    @Preference(PROP_NAME)
    public String name;

    @Inject
    @Preference(PROP_EMAIL)
    public String email;

    @Inject
    @Preference(PROP_SKIP_SIMILAR_ERRORS)
    public boolean skipSimilarErrors;

    @Inject
    @Preference(PROP_ANONYMIZE_STACKTRACES)
    public boolean anonymizeStacktraces;

    @Inject
    @Preference(PROP_ANONYMIZE_MESSAGES)
    public boolean anonymizeMessages;

    @Inject
    @Preference(PROP_SEND_ACTION)
    public String sendAction;

    @Inject
    @Preference(PROP_REMEMBER_SEND_ACTION)
    public String remembereSendAction;

}
