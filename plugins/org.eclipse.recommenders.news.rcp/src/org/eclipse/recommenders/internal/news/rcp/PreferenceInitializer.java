/**
* Copyright (c) 2015 Pawel Nowak.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*/
package org.eclipse.recommenders.internal.news.rcp;

import static org.eclipse.recommenders.internal.news.rcp.Constants.PREF_FEED_LIST_SORTED;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class PreferenceInitializer extends AbstractPreferenceInitializer {

    // public static final String EF_RSS_URL = "http://feeds.feedburner.com/eclipse/fnews"; //$NON-NLS-1$

    @Override
    public void initializeDefaultPreferences() {
        IEclipsePreferences s = DefaultScope.INSTANCE.getNode(Constants.PLUGIN_ID);
        // s.put(PREF_FEED_LIST_SORTED, EF_RSS_URL);
        s.put(PREF_FEED_LIST_SORTED, FeedDescriptors.store(FeedDescriptors.getRegisteredFeeds()));
    }

}
