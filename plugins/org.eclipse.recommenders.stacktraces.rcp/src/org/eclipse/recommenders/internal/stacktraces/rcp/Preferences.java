package org.eclipse.recommenders.internal.stacktraces.rcp;

import static org.eclipse.recommenders.internal.stacktraces.rcp.Constants.*;

import javax.inject.Inject;

import org.eclipse.e4.core.di.extensions.Preference;

@SuppressWarnings("restriction")
public class Preferences {

    @Inject
    @Preference(PROP_SERVER)
    public String server;
    @Inject
    @Preference(PROP_NAME)
    public String name;
}
