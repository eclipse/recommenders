package org.eclipse.recommenders.calls.rcp.wiring;

import org.eclipse.recommenders.calls.ICallModelProvider;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class CallsRcpModule extends AbstractModule implements Module {

    public CallsRcpModule() {
    }

    @Override
    protected void configure() {
        bind(ICallModelProvider.class).to(EclipseCallModelProvider.class).in(Scopes.SINGLETON);
    }
}
