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
package org.eclipse.recommenders.internal.completion.rcp;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.recommenders.completion.rcp.processable.SessionProcessorDescriptor;
import org.eclipse.recommenders.completion.rcp.processable.SessionProcessorDescriptors;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

@SuppressWarnings("restriction")
public class CompletionRcpPreferences {

    @Inject
    @Preference(Constants.PREF_SESSIONPROCESSORS)
    private String sessionProcessors;

    public Collection<SessionProcessorDescriptor> getSessionProcessors() {
        List<SessionProcessorDescriptor> registeredProcessors = SessionProcessorDescriptors.getRegisteredProcessors();
        return SessionProcessorDescriptors.fromString(sessionProcessors, registeredProcessors);
    }

    public void storeSessionProcessors() {
        List<SessionProcessorDescriptor> registeredProcessors = SessionProcessorDescriptors.getRegisteredProcessors();
        IPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, Constants.BUNDLE_NAME);
        store.setValue(Constants.PREF_SESSIONPROCESSORS, SessionProcessorDescriptors.toString(registeredProcessors));
    }
}
