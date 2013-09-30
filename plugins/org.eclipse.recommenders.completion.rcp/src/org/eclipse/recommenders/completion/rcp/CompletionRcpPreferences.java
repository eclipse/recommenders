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
package org.eclipse.recommenders.completion.rcp;

import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Iterables.addAll;
import static com.google.common.collect.Sets.newHashSet;
import static org.eclipse.recommenders.injection.InjectionService.getInstance;

import java.util.Set;

import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.recommenders.completion.rcp.processable.SessionProcessorDescriptor;
import org.eclipse.recommenders.utils.Nullable;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

@SuppressWarnings("restriction")
public class CompletionRcpPreferences {

    private static final String PREF_PROCESSORS_DISABLED = "processors.disabled";
    private static final char SEPARATOR = ';';
    private static final EnabledPredicate ENABLED = new EnabledPredicate();

    private ImmutableSet<SessionProcessorDescriptor> processors;

    @Inject
    @Preference
    IEclipsePreferences prefs;

    private Set<String> disabledProcessors = Sets.newHashSet();

    @Inject
    private void loadDisabledProcessors(@Preference(PREF_PROCESSORS_DISABLED) @Nullable String ids) {
        if (ids == null) {
            return;
        }
        Iterable<String> split = Splitter.on(';').omitEmptyStrings().split(ids);
        addAll(disabledProcessors, split);
    }

    private void updateProcessorState() {
        for (SessionProcessorDescriptor d : processors) {
            d.setEnabled(isEnabled(d));
        }
    }

    private void internal_storeDisabledProcessors() {
        String join = Joiner.on(SEPARATOR).skipNulls().join(disabledProcessors);
        prefs.put(PREF_PROCESSORS_DISABLED, join);
    }

    public void clear() {
        disabledProcessors.clear();
        internal_storeDisabledProcessors();
    }

    public void setEnabled(Set<SessionProcessorDescriptor> enabled) {
        SetView<SessionProcessorDescriptor> disabled = Sets.difference(processors, enabled);
        for (SessionProcessorDescriptor d : enabled) {
            d.setEnabled(true);
            disabledProcessors.remove(d.getId());
        }
        for (SessionProcessorDescriptor d : disabled) {
            d.setEnabled(false);
            disabledProcessors.add(d.getId());
        }
        internal_storeDisabledProcessors();
    }

    public Set<SessionProcessorDescriptor> getEnabled() {
        return newHashSet(filter(getProcessors(), ENABLED));
    }

    public boolean isEnabled(SessionProcessorDescriptor processor) {
        return !disabledProcessors.contains(processor.getId());
    }

    public ImmutableSet<SessionProcessorDescriptor> getProcessors() {
        if (processors == null) {
            // TODO don't change this thoughtless
            // in the case a user did not trigger a completion, this ensures that the system is initialized properly
            // GuiceModule for counterpart logic.
            // TODO this should be solved differently...
            SessionProcessorDescriptor[] p = getInstance().requestInstance(SessionProcessorDescriptor[].class);
            setProcessors(ImmutableSet.copyOf(p));
        }
        return processors;
    }

    public void setProcessors(ImmutableSet<SessionProcessorDescriptor> processors) {
        this.processors = processors;
    }

    private static final class EnabledPredicate implements Predicate<SessionProcessorDescriptor> {
        @Override
        public boolean apply(SessionProcessorDescriptor p) {
            return p.isEnabled();
        }
    }
}
