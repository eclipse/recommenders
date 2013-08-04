/**
 * Copyright (c) 2013 Timur Achmetow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Timur Achmetow - Initial API and implementation
 */
package org.eclipse.recommenders.stats.rcp.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.recommenders.stats.rcp.interfaces.ITreeViewerExtension;

/**
 * Wrapper class which executes other plugins, which implements the Extension Point interface.
 */
public abstract class EvaluateExtensionPointContributions {
    public static final String IGREETER_ID = "org.eclipse.recommenders.stats.viewextension";

    public void evaluate() {
        IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(IGREETER_ID);
        try {
            for (IConfigurationElement element : config) {
                final Object object = element.createExecutableExtension("class");
                if (object instanceof ITreeViewerExtension) {
                    executeExtension((ITreeViewerExtension) object);
                }
            }
        } catch (CoreException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void executeExtension(final ITreeViewerExtension o) {
        ISafeRunnable runnable = new ISafeRunnable() {
            @Override
            public void handleException(Throwable e) {
                System.out.println("Exception in client");
            }

            @Override
            public void run() throws Exception {
                executeCode(o);
            }
        };
        SafeRunner.run(runnable);
    }

    public abstract void executeCode(ITreeViewerExtension extension);
}
