/**
 * Copyright (c) 2014 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Haftstein - initial implementation
 */
package org.eclipse.recommenders.internal.stacktraces.rcp;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.recommenders.internal.stacktraces.rcp.StacktracesRcpPreferences.Mode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class ErrorReportingPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public ErrorReportingPreferencePage() {
        super(GRID);

    }

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, Constants.PLUGIN_ID));
    }

    @Override
    protected void createFieldEditors() {
        addField(new StringFieldEditor(Constants.PROP_SERVER, "Report-Server URL", getFieldEditorParent()));
        addField(new ComboFieldEditor(Constants.PROP_MODE, "Reporting Mode", createModeLabelAndValues(),
                getFieldEditorParent()));
        addField(new BooleanFieldEditor(Constants.PROP_ANONYMIZE_STACKFRAMES, "Anonymize Stackframes",
                getFieldEditorParent()));
        addField(new BooleanFieldEditor(Constants.PROP_CLEAR_MESSAGES, "Clear messages", getFieldEditorParent()));
        addLinks(getFieldEditorParent());
    }

    private void addLinks(Composite parent) {
        Composite feedback = new Composite(parent, SWT.NONE);
        feedback.setLayout(new RowLayout(SWT.VERTICAL));
        Link learnMoreLink = new Link(feedback, SWT.NONE);
        learnMoreLink.setText("<a>Learn more...</a>");
        learnMoreLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Browsers.openInExternalBrowser(StacktraceWizard.HELP_URL);
            }
        });

        Link feedbackLink = new Link(feedback, SWT.NONE);
        feedbackLink.setText("<a>Provide feedback...</a>");
        feedbackLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Browsers.openInExternalBrowser(StacktraceWizard.FEEDBACK_FORM_URL);
            }
        });
    }

    private static String[][] createModeLabelAndValues() {
        Mode[] modes = Mode.values();
        String[][] labelAndValues = new String[modes.length][2];
        for (int i = 0; i < modes.length; i++) {
            Mode mode = modes[i];
            labelAndValues[i][0] = descriptionForMode(mode);
            labelAndValues[i][1] = mode.name();
        }
        return labelAndValues;
    }

    private static String descriptionForMode(Mode mode) {
        switch (mode) {
        case ASK:
            return "Ask each time";
        case IGNORE:
            return "Report nothing";
        case SILENT:
            return "Send every error immediately";
        default:
            return mode.name();
        }
    }

}
