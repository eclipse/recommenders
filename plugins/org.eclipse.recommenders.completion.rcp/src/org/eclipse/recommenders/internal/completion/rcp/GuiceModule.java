/**
 * Copyright (c) 2010 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.completion.rcp;

import static com.google.common.collect.ImmutableSet.copyOf;

import javax.inject.Singleton;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.recommenders.completion.rcp.CompletionRcpPreferences;
import org.eclipse.recommenders.completion.rcp.processable.SessionProcessorDescriptor;
import org.eclipse.ui.IWorkbench;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class GuiceModule extends AbstractModule {

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    SessionProcessorDescriptor[] provideSessionProcessorDescriptors(CompletionRcpPreferences prefs) {
        SessionProcessorDescriptor[] processors = SessionProcessorDescriptor.parseExtensions();
        prefs.setProcessors(copyOf(processors));
        return processors;
    }

    @Provides
    @Singleton
    public CompletionRcpPreferences provide(IWorkbench wb) {
        IEclipseContext context = (IEclipseContext) wb.getService(IEclipseContext.class);
        CompletionRcpPreferences prefs = ContextInjectionFactory.make(CompletionRcpPreferences.class, context);
        return prefs;
    }
}
