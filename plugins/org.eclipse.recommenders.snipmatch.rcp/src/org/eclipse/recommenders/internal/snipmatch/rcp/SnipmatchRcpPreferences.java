/**
 * Copyright (c) 2014 Olav Lenz.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Olav Lenz - initial API and implementation.
 */
package org.eclipse.recommenders.internal.snipmatch.rcp;

import java.util.Set;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.e4.core.di.extensions.Preference;
import org.osgi.service.prefs.BackingStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

@SuppressWarnings("restriction")
public class SnipmatchRcpPreferences {

    private static Logger LOG = LoggerFactory.getLogger(SnipmatchRcpPreferences.class);

    private Set<String> deletedRepositoryConfigurationIds = Sets.newHashSet();

    static final String SEPERATOR = ";"; //$NON-NLS-1$

    @Inject
    public void setDeletedRepositoryConfigurations(
            @Preference(Constants.PREF_DELETED_REPOSITORY_CONFIGURATION_IDS) String newDeletedRepositoryConfigurationIds)
            throws Exception {
        if (newDeletedRepositoryConfigurationIds != null) {
            deletedRepositoryConfigurationIds = splitString(newDeletedRepositoryConfigurationIds);
        }
    }

    public ImmutableSet<String> getDeletedRepositoryConfigurationIds() {
        return ImmutableSet.copyOf(deletedRepositoryConfigurationIds);
    }

    public static Set<String> splitString(String string) {
        Iterable<String> split = Splitter.on(SEPERATOR).omitEmptyStrings().split(string);
        return Sets.newHashSet(split);
    }

    public static String joinToString(Set<String> strings) {
        return Joiner.on(SnipmatchRcpPreferences.SEPERATOR).join(strings);
    }

    public static void store(Set<String> deletedRepositoryConfigurationIds) {
        store(joinToString(deletedRepositoryConfigurationIds));
    }

    public static void store(String deletedRepositoryConfigurationIds) {
        try {
            IEclipsePreferences s = InstanceScope.INSTANCE.getNode(Constants.BUNDLE_ID);
            s.put(Constants.PREF_DELETED_REPOSITORY_CONFIGURATION_IDS, deletedRepositoryConfigurationIds);
            s.flush();
        } catch (BackingStoreException e) {
            LOG.error("Exception during storing of deleted repository configuration preferences", e); //$NON-NLS-1$
        }
    }

}
