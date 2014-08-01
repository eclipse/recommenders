/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.stacktraces.rcp;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;

@SuppressWarnings("restriction")
public class StacktracesRcpPreferences {

    @Inject
    @Preference
    public IEclipsePreferences prefs;

    @Inject
    @Preference("email")
    public String email;

    @Inject
    @Preference("server")
    public String server;

    @Inject
    @Preference("name")
    public String name;

    @Inject
    @Preference("mode")
    public String mode;

    public boolean modeSilent() {
        return "silent".equals(mode);
    }

    public boolean modeAsk() {
        return "ask".equals(mode);
    }

    public boolean modeIgnore() {
        return "ignore".equals(mode);
    }

    public void setMode(String mode) {
        prefs.put("mode", mode);
    }
}
