package org.eclipse.recommenders.internal.rcp;

import static org.eclipse.recommenders.internal.rcp.Constants.COMMAND_HREF_ID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.recommenders.rcp.utils.BrowserUtils;

public class OpenBrowserHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        BrowserUtils.openInExternalBrowser(event.getParameter(COMMAND_HREF_ID));

        return null;
    }

}
