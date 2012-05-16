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

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.name.Names;

public class ChainCompletionModule extends AbstractModule implements Module {

    @Override
    protected void configure() {
        bind(IPreferenceStore.class).annotatedWith(Names.named("ChainPreferenceStore")).toInstance(
                ChainCompletionPlugin.getDefault().getPreferenceStore());
    }

}
