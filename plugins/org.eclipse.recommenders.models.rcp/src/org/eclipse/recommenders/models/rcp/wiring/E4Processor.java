package org.eclipse.recommenders.models.rcp.wiring;

import javax.annotation.PostConstruct;

import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.recommenders.models.rcp.ModelArchiveProvider;
import org.eclipse.recommenders.models.rcp.ProjectCoordinateProvider;

public class E4Processor {

    @PostConstruct
    public void postConstruct(IEclipseContext context) {
        context.set(ProjectCoordinateProvider.class, new ProjectCoordinateProvider());
        ModelArchiveProvider provider = ContextInjectionFactory.make(ModelArchiveProvider.class, context);
        context.set(ModelArchiveProvider.class, provider);
    }
}
