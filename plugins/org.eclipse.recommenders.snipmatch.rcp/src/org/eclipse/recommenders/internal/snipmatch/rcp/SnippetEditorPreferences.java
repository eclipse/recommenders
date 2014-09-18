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

import javax.inject.Inject;

import org.eclipse.e4.core.di.extensions.Preference;

@SuppressWarnings("restriction")
public class SnippetEditorPreferences {

    private Boolean showEditorNotification;

    @Inject
    public void setShowEditorNotification(@Preference(Constants.PREF_SNIPPET_EDITOR_DISCOVERY) Boolean notification) {
        this.showEditorNotification = notification;
    }

    public boolean isEditorExtNotificationEnabled() {
        return showEditorNotification.booleanValue();
    }
}
