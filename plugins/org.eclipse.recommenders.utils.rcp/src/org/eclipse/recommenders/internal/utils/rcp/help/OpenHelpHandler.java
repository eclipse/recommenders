package org.eclipse.recommenders.internal.utils.rcp.help;

import static org.eclipse.recommenders.utils.rcp.preferences.AbstractLinkContributionPage.COMMAND_HREF_ID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.internal.handlers.DisplayHelpHandler;

@SuppressWarnings({ "unchecked", "restriction" })
public class OpenHelpHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        String parameter = event.getParameter(COMMAND_HREF_ID);
        event.getParameters().put("href", parameter);

        DisplayHelpHandler delegate = new DisplayHelpHandler();
        delegate.execute(event);

        return null;
    }
}
