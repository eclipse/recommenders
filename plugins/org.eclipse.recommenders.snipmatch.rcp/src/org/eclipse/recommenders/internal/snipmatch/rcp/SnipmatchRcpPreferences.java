/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 *    Olav Lenz - introduce ISnippetRepositoryConfiguration.
 */
package org.eclipse.recommenders.internal.snipmatch.rcp;

import static org.eclipse.recommenders.internal.snipmatch.rcp.RepositoryConfigurations.convert;

import java.util.Arrays;

import javax.inject.Inject;

import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.recommenders.internal.snipmatch.rcp.Repositories.SnippetRepositoryUrlChangedEvent;
import org.eclipse.recommenders.snipmatch.ISnippetRepositoryConfiguration;
import org.eclipse.recommenders.snipmatch.ISnippetRepositoryProvider;

import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.EventBus;
import com.google.inject.name.Named;

@SuppressWarnings("restriction")
public class SnipmatchRcpPreferences {

    private ISnippetRepositoryConfiguration[] configurations;
    private EventBus bus;
    private ImmutableSet<ISnippetRepositoryProvider> providers;

    public SnipmatchRcpPreferences(EventBus bus,
            @Named(SnipmatchRcpModule.SNIPPET_REPOSITORY_PROVIDERS) ImmutableSet<ISnippetRepositoryProvider> providers) {
        this.bus = bus;
        this.providers = providers;
    }

    @Inject
    public void setLocation(@Preference(Constants.PREF_SNIPPETS_REPO) String newValue) {
        ISnippetRepositoryConfiguration[] old = configurations;
        configurations = convert(newValue, providers);
        if (old != null && !Arrays.deepEquals(old, configurations)) {
            bus.post(new SnippetRepositoryUrlChangedEvent());
        }
    }

    public ISnippetRepositoryConfiguration[] getConfigurations() {
        return configurations;
    }
}
