package org.eclipse.recommenders.internal.models.rcp;

import static org.eclipse.recommenders.internal.models.rcp.Constants.P_REPOSITORY_ENABLE_AUTO_DOWNLOAD;

import java.util.Set;

import javax.inject.Inject;

import org.eclipse.e4.core.di.extensions.Preference;

import com.google.common.collect.Sets;

public class ModelsRcpPreferences {

    @Inject
    @Preference(P_REPOSITORY_ENABLE_AUTO_DOWNLOAD)
    public boolean autoDownloadEnabled;

    @Inject
    @Preference(Constants.P_REPOSITORY_URL)
    public String remote;

    @Inject
    public void setRemote(@Preference(Constants.P_REPOSITORY_URL) String remote) throws Exception {
        for (Runnable r : callbacks) {
            r.run();
        }
    }

    Set<Runnable> callbacks = Sets.newHashSet();

    public void addRemoteUrlChangedCallback(Runnable runnable) {
        callbacks.add(runnable);
    }
}
