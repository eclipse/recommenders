/**
 * Copyright (c) 2013 Stefan Prisca.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Prisca - initial API and implementation
 */
package org.eclipse.recommenders.internal.snipmatch.rcp;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class SnippetEditorPreferences {

    private Boolean showEditorNotification;

    public SnippetEditorPreferences() {
        ScopedPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, Constants.BUNDLE_ID);
        this.showEditorNotification = store.getBoolean(Constants.PREF_SNIPPET_EDITOR_DISCOVERY);
    }

    public boolean isEditorExtNotificationEnabled() {
        return showEditorNotification.booleanValue();
    }
}
