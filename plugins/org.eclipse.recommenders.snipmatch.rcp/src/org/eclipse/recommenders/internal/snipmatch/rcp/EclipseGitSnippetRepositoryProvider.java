/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Olav Lenz - initial API and implementation.
 */
package org.eclipse.recommenders.internal.snipmatch.rcp;

import static org.eclipse.recommenders.utils.Checks.cast;
import static com.google.common.base.Optional.of;
import static com.google.common.base.Optional.absent;

import java.io.File;

import org.eclipse.recommenders.snipmatch.ISnippetRepository;
import org.eclipse.recommenders.snipmatch.ISnippetRepositoryConfiguration;
import org.eclipse.recommenders.snipmatch.ISnippetRepositoryProvider;
import org.eclipse.recommenders.utils.gson.GsonUtil;

import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.google.inject.Inject;

public class EclipseGitSnippetRepositoryProvider implements ISnippetRepositoryProvider {

    private final Gson gson = GsonUtil.getInstance();
    private EventBus bus;

    @Inject
    public EclipseGitSnippetRepositoryProvider(EventBus bus) {
        this.bus = bus;
    }

    public EclipseGitSnippetRepositoryProvider() {
    }

    @Override
    public boolean isApplicable(String identifier) {
        return EclipseGitSnippetRepositoryConfiguration.class.getSimpleName().equals(identifier);
    }

    @Override
    public Optional<ISnippetRepository> create(ISnippetRepositoryConfiguration configuration, File basedir) {
        if (configuration instanceof EclipseGitSnippetRepositoryConfiguration) {
            EclipseGitSnippetRepositoryConfiguration config = cast(configuration);
            ISnippetRepository repo = new EclipseGitSnippetRepository(basedir, config.getRepositoryUrl(), bus);
            return of(repo);
        }
        return absent();
    }

    @Override
    public ISnippetRepositoryConfiguration convert(String stringRepresentation) {
        return gson.fromJson(stringRepresentation, EclipseGitSnippetRepositoryConfiguration.class);
    }

    @Override
    public String convert(ISnippetRepositoryConfiguration configurations) {
        StringBuilder sb = new StringBuilder();
        sb.append(EclipseGitSnippetRepositoryConfiguration.class.getSimpleName());
        sb.append(RepositoryConfigurations.INNER_SEPARATOR);
        sb.append(gson.toJson(configurations));
        return sb.toString();
    }

}
