package org.eclipse.recommenders.internal.news.rcp;

import static org.eclipse.recommenders.internal.news.rcp.Constants.PREF_FEED_LIST_SORTED;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;

@SuppressWarnings("restriction")
public class NewsRcpPreferences extends AbstractPreferenceInitializer {

    @Inject
    @Preference(Constants.PREF_NEWS_ENABLED)
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void initializeDefaultPreferences() {
        IEclipsePreferences s = DefaultScope.INSTANCE.getNode(Constants.PLUGIN_ID);
        s.putBoolean(Constants.PREF_NEWS_ENABLED, true);
        s.put(PREF_FEED_LIST_SORTED, FeedDescriptors.store(FeedDescriptors.getRegisteredFeeds()));
    }
}
