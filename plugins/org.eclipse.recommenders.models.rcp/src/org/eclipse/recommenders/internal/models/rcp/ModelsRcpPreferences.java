/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marcel Bruch - initial API and implementation
 */
package org.eclipse.recommenders.internal.models.rcp;

import static org.apache.commons.lang3.ArrayUtils.isEquals;
import static org.eclipse.recommenders.internal.models.rcp.Constants.P_REPOSITORY_ENABLE_AUTO_DOWNLOAD;

import javax.inject.Inject;

import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.recommenders.injection.InjectionService;
import org.eclipse.recommenders.models.rcp.ModelEvents.ModelRepositoryUrlChangedEvent;
import org.eclipse.recommenders.models.rcp.ModelEvents.AdvisorConfigurationChangedEvent;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.eventbus.EventBus;

@SuppressWarnings("restriction")
public class ModelsRcpPreferences {

    @Inject
    @Preference(P_REPOSITORY_ENABLE_AUTO_DOWNLOAD)
    public boolean autoDownloadEnabled;

    public String[] remotes;

    public String advisorConfiguration;

    private EventBus bus = InjectionService.getInstance().requestInstance(EventBus.class);

    static final String URL_SEPARATOR = "\t";

    @Inject
    void setRemote(@Preference(Constants.P_REPOSITORY_URL_LIST_ACTIV) String newRemote) throws Exception {
        String[] old = remotes;
        remotes = split(newRemote, URL_SEPARATOR);
        if (!isEquals(remotes, old)) {
            bus.post(new ModelRepositoryUrlChangedEvent());
        }
    }

    @Inject
    void setAdvisorConfiguration(@Preference(Constants.P_ADVISOR_LIST_SORTED) String newAdvisorConfiguration)
            throws Exception {
        String old = advisorConfiguration;
        advisorConfiguration = newAdvisorConfiguration;
        if (!isEquals(advisorConfiguration, old)) {
            bus.post(new AdvisorConfigurationChangedEvent());
        }
    }

    private static String[] split(String stringList, String separator) {
        Iterable<String> split = Splitter.on(separator).omitEmptyStrings().split(stringList);
        return Iterables.toArray(split, String.class);
    }
}
