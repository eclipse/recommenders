/**
 * Copyright (c) 2015 Codetrails GmbH. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Johannes Dorn - initial API and implementation.
 */
package org.eclipse.recommenders.internal.news.rcp;

import javax.inject.Singleton;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.mylyn.commons.notifications.core.NotificationEnvironment;
import org.eclipse.recommenders.news.rcp.IRssService;
import org.eclipse.ui.IWorkbench;

import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

@SuppressWarnings("restriction")
public class NewsRcpModule extends AbstractModule {

    @Override
    protected void configure() {
        // no-op
    }

    @Provides
    @Singleton
    NewsRcpPreferences providePreferences(IWorkbench wb) {
        IEclipseContext context = (IEclipseContext) wb.getService(IEclipseContext.class);
        return ContextInjectionFactory.make(NewsRcpPreferences.class, context);
    }

    @Provides
    @Singleton
    IRssService provideRssService(NewsRcpPreferences preferences, EventBus eventBus, NotificationEnvironment environment) {
        return new RssService(preferences, eventBus, environment);
    }

}