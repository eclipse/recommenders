package org.eclipse.recommenders.internal.types.rcp;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class TypesRcpModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(TypesIndexService.class).in(Scopes.SINGLETON);
    }
}
