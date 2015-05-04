package org.eclipse.recommenders.internal.news.rcp;

import javax.inject.Inject;

import org.eclipse.recommenders.news.rcp.IRssService;
import org.eclipse.ui.IStartup;

public class Startup implements IStartup {

    private final IRssService service;
    private final NewsRcpPreferences preferences;

    @Inject
    public Startup(IRssService service, NewsRcpPreferences preferences) {
        this.service = service;
        this.preferences = preferences;

    }

    @Override
    public void earlyStartup() {
        if (preferences.isEnabled()) {
            service.start();
        }
    }
}
