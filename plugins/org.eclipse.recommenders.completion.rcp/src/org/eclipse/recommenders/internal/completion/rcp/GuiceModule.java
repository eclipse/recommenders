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

import static org.eclipse.recommenders.internal.completion.rcp.CompletionContextFunctions.*;

import javax.inject.Singleton;

import org.eclipse.recommenders.completion.rcp.ICompletionContextFunction;
import org.eclipse.recommenders.completion.rcp.processable.SessionProcessorDescriptor;
import org.eclipse.recommenders.internal.completion.rcp.CompletionContextFunctions.InternalCompletionContextFunction;
import org.eclipse.recommenders.internal.completion.rcp.CompletionContextFunctions.VisibleFieldsContextFunction;
import org.eclipse.recommenders.internal.completion.rcp.CompletionContextFunctions.VisibleLocalsContextFunction;
import org.eclipse.recommenders.internal.completion.rcp.CompletionContextFunctions.VisibleMethodsContextFunction;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.MapBinder;

public class GuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        MapBinder<String, ICompletionContextFunction> functions = MapBinder.newMapBinder(binder(), String.class,
                ICompletionContextFunction.class);
        functions.addBinding(CCTX_INTERNAL_COMPLETION_CONTEXT).to(InternalCompletionContextFunction.class);
        functions.addBinding(CCTX_VISIBLE_METHODS).to(VisibleMethodsContextFunction.class);
        functions.addBinding(CCTX_VISIBLE_FIELDS).to(VisibleFieldsContextFunction.class);
        functions.addBinding(CCTX_VISIBLE_LOCALS).to(VisibleLocalsContextFunction.class);
    }

    @Provides
    @Singleton
    SessionProcessorDescriptor[] provideSessionProcessorDescriptors() {
        return SessionProcessorDescriptor.parseExtensions();
    }

}
