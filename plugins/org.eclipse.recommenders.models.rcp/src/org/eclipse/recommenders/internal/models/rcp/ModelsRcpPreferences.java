/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marcel Bruch - initial API and implementation
 *     Olav Lenz - add advisor service configuration
 */
package org.eclipse.recommenders.internal.models.rcp;

import static org.apache.commons.lang3.ArrayUtils.isEquals;
import static org.eclipse.recommenders.internal.models.rcp.Constants.P_REPOSITORY_ENABLE_AUTO_DOWNLOAD;

import java.util.List;

import javax.inject.Inject;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.recommenders.injection.InjectionService;
import org.eclipse.recommenders.models.IProjectCoordinateAdvisor;
import org.eclipse.recommenders.models.advisors.ProjectCoordinateAdvisorService;
import org.eclipse.recommenders.models.rcp.ModelEvents.AdvisorConfigurationChangedEvent;
import org.eclipse.recommenders.models.rcp.ModelEvents.ModelRepositoryUrlChangedEvent;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.inject.Provider;

@SuppressWarnings("restriction")
public class ModelsRcpPreferences implements Provider<ProjectCoordinateAdvisorService> {

    @Inject
    @Preference(P_REPOSITORY_ENABLE_AUTO_DOWNLOAD)
    public boolean autoDownloadEnabled;

    public String[] remotes;

    public String advisorIds;

    private EventBus bus = InjectionService.getInstance().requestInstance(EventBus.class);

    private ProjectCoordinateAdvisorService advisorService;

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
    void setAdvisorIds(@Preference(Constants.P_ADVISOR_LIST_SORTED) String advisorIds) throws Exception {
        this.advisorIds = advisorIds;
        if (advisorService != null) {
            configurateAdvisorService();
        }
    }

    private static String[] split(String stringList, String separator) {
        Iterable<String> split = Splitter.on(separator).omitEmptyStrings().split(stringList);
        return Iterables.toArray(split, String.class);
    }

    @Override
    public ProjectCoordinateAdvisorService get() {
        if (advisorService == null) {
            advisorService = new ProjectCoordinateAdvisorService();
        }
        configurateAdvisorService();
        return advisorService;
    }

    private void configurateAdvisorService() {
        advisorService.setAdvisors(provideAdvisors(advisorIds));
        bus.post(new AdvisorConfigurationChangedEvent());
    }

    private List<IProjectCoordinateAdvisor> provideAdvisors(String advisorIds) {
        List<AdvisorDescriptor> registeredAdvisors = AdvisorDescriptors.getRegisteredAdvisors();
        List<AdvisorDescriptor> load = AdvisorDescriptors.load(advisorIds, registeredAdvisors);
        List<IProjectCoordinateAdvisor> advisors = Lists.newArrayListWithCapacity(load.size());
        for (AdvisorDescriptor descriptor : load) {
            try {
                advisors.add(descriptor.createAdvisor());
            } catch (CoreException e) {
                continue; // skip
            }
        }
        return advisors;
    }
}
