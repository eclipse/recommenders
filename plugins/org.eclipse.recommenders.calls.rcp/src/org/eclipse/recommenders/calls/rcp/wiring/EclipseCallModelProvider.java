package org.eclipse.recommenders.calls.rcp.wiring;

import java.io.IOException;

import javax.inject.Inject;

import org.eclipse.recommenders.calls.ICallModel;
import org.eclipse.recommenders.calls.ICallModelProvider;
import org.eclipse.recommenders.calls.PoolingCallModelProvider;
import org.eclipse.recommenders.models.BasedTypeName;
import org.eclipse.recommenders.models.IModelRepository;

import com.google.common.base.Optional;

public class EclipseCallModelProvider implements ICallModelProvider {

    ICallModelProvider delegate;

    @Inject
    public EclipseCallModelProvider(IModelRepository repository) {
        delegate = new PoolingCallModelProvider(repository);
    }

    @Override
    public void close() throws IOException {
        delegate.close();
    }

    @Override
    public void open() throws IOException {
        delegate.open();
    }

    @Override
    public Optional<ICallModel> acquireModel(BasedTypeName key) {
        return delegate.acquireModel(key);
    }

    @Override
    public void releaseModel(ICallModel value) {
        delegate.releaseModel(value);
    }
}
