/**
 * Copyright (c) 2011 Stefan Henss.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Henß - initial API and implementation.
 */
package org.eclipse.recommenders.internal.completion.rcp.chain;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class ChainCompletionPlugin extends AbstractUIPlugin {

    private static ChainCompletionPlugin plugin;

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    @Override
    protected void initializeDefaultPreferences(final IPreferenceStore store) {
        store.setDefault(ChainPreferencePage.ID_MAX_CHAINS, 20);
        store.setDefault(ChainPreferencePage.ID_MAX_DEPTH, 4);
        store.setDefault(ChainPreferencePage.ID_TIMEOUT, 3);
    }

    public static ChainCompletionPlugin getDefault() {
        return plugin;
    }

}
