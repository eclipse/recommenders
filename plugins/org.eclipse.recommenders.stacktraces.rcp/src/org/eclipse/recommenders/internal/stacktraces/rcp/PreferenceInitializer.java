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

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.RememberSendAction;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.SendAction;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IEclipsePreferences s = DefaultScope.INSTANCE.getNode(PLUGIN_ID);
        s.put(PROP_SERVER, SERVER_URL);
        s.put(PROP_NAME, "");
        s.put(PROP_EMAIL, "");
        s.putBoolean(PROP_SKIP_SIMILAR_ERRORS, true);
        s.putBoolean(PROP_CONFIGURED, false);
        s.put(PROP_WHITELISTED_PLUGINS, Constants.WHITELISTED_PLUGINS);
        s.put(PROP_WHITELISTED_PACKAGES, Constants.WHITELISTED_PACKAGES);
        s.put(PROP_SEND_ACTION, SendAction.ASK.name());
        s.put(PROP_REMEMBER_SEND_ACTION, RememberSendAction.NONE.name());
        s.putBoolean(PROP_ANONYMIZE_STACKTRACES, true);
        s.putBoolean(PROP_ANONYMIZE_MESSAGES, false);
    }
}
