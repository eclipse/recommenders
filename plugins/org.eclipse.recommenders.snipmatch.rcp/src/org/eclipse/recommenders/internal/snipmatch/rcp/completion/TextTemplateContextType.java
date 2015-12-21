package org.eclipse.recommenders.internal.snipmatch.rcp.completion;

import static org.eclipse.recommenders.internal.snipmatch.rcp.Constants.SNIPMATCH_CONTEXT_ID;

import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.TemplateContextType;

public class TextTemplateContextType {
    private static TemplateContextType instance;

    public static TemplateContextType getInstance() {
        if (instance == null) {
            instance = createContextType();
        }
        return instance;
    }

    private static TemplateContextType createContextType() {
        TemplateContextType contextType = new TemplateContextType();
        contextType.setId(SNIPMATCH_CONTEXT_ID);

        // global
        contextType.addResolver(new GlobalTemplateVariables.Cursor());
        contextType.addResolver(new GlobalTemplateVariables.WordSelection());
        contextType.addResolver(new GlobalTemplateVariables.LineSelection());
        contextType.addResolver(new GlobalTemplateVariables.Dollar());
        contextType.addResolver(new GlobalTemplateVariables.Date());
        contextType.addResolver(new GlobalTemplateVariables.Year());
        contextType.addResolver(new GlobalTemplateVariables.Time());
        contextType.addResolver(new GlobalTemplateVariables.User());

        return contextType;
    }
}
