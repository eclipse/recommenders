package org.eclipse.recommenders.internal.snipmatch.rcp;

import org.eclipse.jface.dialogs.IInputValidator;

public class BranchInputValidator implements IInputValidator {

    @Override
    public String isValid(String newText) {
        if (newText.matches("[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*(/[a-zA-Z0-9]+(\\-[a-zA-Z0-9]+)*)*")) {
            return null;
        }
        return Messages.PREFPAGE_ERROR_INVALID_BRANCH_PREFIX_FORMAT;
    }
}