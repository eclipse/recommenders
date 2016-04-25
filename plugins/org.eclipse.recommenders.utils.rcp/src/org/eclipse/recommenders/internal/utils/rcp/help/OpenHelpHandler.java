package org.eclipse.recommenders.internal.utils.rcp.help;

import static org.eclipse.recommenders.utils.rcp.preferences.AbstractLinkContributionPage.COMMAND_HREF_ID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;

public class OpenHelpHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        final IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
        final String href = event.getParameter(COMMAND_HREF_ID);

        if (href == null) {
            helpSystem.displayHelp();
        } else {
            helpSystem.displayHelpResource(href);
        }

        return null;
    }
}
