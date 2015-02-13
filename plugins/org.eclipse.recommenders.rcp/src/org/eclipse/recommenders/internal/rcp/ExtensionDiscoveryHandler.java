package org.eclipse.recommenders.internal.rcp;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.recommenders.rcp.utils.Dialogs;

public class ExtensionDiscoveryHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        Dialogs.newExtensionsDiscoveryDialog().open();

        return null;
    }
}
