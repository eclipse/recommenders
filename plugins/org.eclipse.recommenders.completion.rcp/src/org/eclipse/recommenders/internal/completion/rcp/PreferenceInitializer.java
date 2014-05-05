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
package org.eclipse.recommenders.internal.completion.rcp;

import static org.eclipse.recommenders.internal.completion.rcp.Constants.BUNDLE_NAME;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.recommenders.completion.rcp.processable.SessionProcessorDescriptors;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

    @Override
    public void initializeDefaultPreferences() {
        IEclipsePreferences s = DefaultScope.INSTANCE.getNode(BUNDLE_NAME);
        s.put(SessionProcessorDescriptors.PREF_NODE_ID_SESSIONPROCESSORS,
                SessionProcessorDescriptors.store(SessionProcessorDescriptors.getRegisteredProcessors()));
        // s.putBoolean(PREF_REPOSITORY_ENABLE_AUTO_DOWNLOAD, true);

    }
}
