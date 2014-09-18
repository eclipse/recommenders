/**
 * Copyright (c) 2013 Stefan Prisca.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan Prisca - initial API and implementation
 */
package org.eclipse.recommenders.internal.snipmatch.rcp;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class SnippetEditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public SnippetEditorPreferencePage() {
        super(GRID);
    }

    @Override
    public void init(IWorkbench workbench) {
        ScopedPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, Constants.BUNDLE_ID);
        setPreferenceStore(store);
    }

    @Override
    protected void createFieldEditors() {
        Composite contents = getFieldEditorParent();
        contents.setLayout(new GridLayout(1, false));

        Link discoverLink = new Link(contents, SWT.None);
        discoverLink.setText(Messages.PREFPAGE_EDITOR_EXTENSIONS_LINK);
        discoverLink.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 1, 1));

        discoverLink.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                SnippetEditorDiscoveryUtils.openDiscoveryDialog();
            }
        });

        BooleanFieldEditor editorDisp = new BooleanFieldEditor(Constants.PREF_SNIPPET_EDITOR_DISCOVERY,
                Messages.PREFPAGE_EDITOR_EXTENSIONS_ENABLE_NOTIFICATION, contents);
        editorDisp.getDescriptionControl(contents).setLayoutData(
                new GridData(SWT.CENTER, SWT.BEGINNING, false, false, 1, 1));
        addField(editorDisp);

    }

}
