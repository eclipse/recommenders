package org.eclipse.recommenders.internal.news.rcp;

import javax.inject.Singleton;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.ui.IWorkbench;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

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

}
