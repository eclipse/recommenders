package org.eclipse.recommenders.internal.subwords.rcp;

import javax.inject.Singleton;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.ui.IWorkbench;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class SubwordsRcpModule extends AbstractModule {

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    public SubwordsRcpPreferences provide(IWorkbench wb) {
        IEclipseContext context = (IEclipseContext) wb.getService(IEclipseContext.class);
        return ContextInjectionFactory.make(SubwordsRcpPreferences.class, context);
    }
}
