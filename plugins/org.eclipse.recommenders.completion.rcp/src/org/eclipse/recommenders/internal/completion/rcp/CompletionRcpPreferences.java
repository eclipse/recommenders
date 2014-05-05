/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Dorn - initial API and implementation.
 */
package org.eclipse.recommenders.internal.completion.rcp;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.recommenders.completion.rcp.processable.SessionProcessorDescriptor;
import org.eclipse.recommenders.completion.rcp.processable.SessionProcessorDescriptors;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;

@SuppressWarnings("restriction")
public class CompletionRcpPreferences {

    @Inject
    @Preference(Constants.PREF_NODE_ID_SESSIONPROCESSORS)
    private String sessionProcessors;

    public Collection<SessionProcessorDescriptor> getEnabledSessionProcessors() {
        List<SessionProcessorDescriptor> processors = SessionProcessorDescriptors.getRegisteredProcessors();
        return Collections2.filter(processors, new Predicate<SessionProcessorDescriptor>() {

            @Override
            public boolean apply(SessionProcessorDescriptor input) {
                return SessionProcessorDescriptors.isEnabled(input, sessionProcessors);
            }
        });
    }
}
