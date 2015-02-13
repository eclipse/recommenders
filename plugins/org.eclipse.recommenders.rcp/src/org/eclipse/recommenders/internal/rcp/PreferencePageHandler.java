package org.eclipse.recommenders.internal.rcp;

import static org.eclipse.recommenders.internal.rcp.Constants.COMMAND_HREF_ID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class PreferencePageHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(null, event.getParameter(COMMAND_HREF_ID),
                null, null);
        dialog.open();

        return null;
    }

}
