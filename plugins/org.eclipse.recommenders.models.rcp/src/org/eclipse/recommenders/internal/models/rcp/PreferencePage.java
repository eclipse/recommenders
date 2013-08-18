/**
 * Copyright (c) 2010, 2012 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Patrick Gottschaemmer, Olav Lenz - initial API and implementation.
 *    Olav Lenz - externalize Strings.
 */
package org.eclipse.recommenders.internal.models.rcp;

import static org.eclipse.recommenders.internal.models.rcp.Constants.P_REPOSITORY_ENABLE_AUTO_DOWNLOAD;
import static org.eclipse.recommenders.internal.models.rcp.Constants.P_REPOSITORY_URL;
import static org.eclipse.recommenders.internal.models.rcp.Constants.P_REPOSITORY_URL_LIST;
import static org.eclipse.recommenders.internal.models.rcp.Messages.PREFPAGE_CLEAR_CACHES;
import static org.eclipse.recommenders.internal.models.rcp.Messages.PREFPAGE_ENABLE_AUTO_DOWNLOAD;
import static org.eclipse.recommenders.internal.models.rcp.Messages.PREFPAGE_MODEL_REPOSITORY_HEADLINE;
import static org.eclipse.recommenders.internal.models.rcp.Messages.PREFPAGE_MODEL_REPOSITORY_INTRO;
import static org.eclipse.recommenders.internal.models.rcp.Messages.PREFPAGE_URI;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public PreferencePage() {
        super(GRID);
    }

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, Constants.BUNDLE_ID));
    }

    @Override
    protected void createFieldEditors() {
        createRemoteRepositorySection();

        Button clearCaches = new Button(getFieldEditorParent(), SWT.PUSH);
        clearCaches.setText(PREFPAGE_CLEAR_CACHES);
        GridData data = new GridData(SWT.END, SWT.CENTER, false, false);
        data.horizontalSpan = 3;
        clearCaches.setLayoutData(data);
        clearCaches.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                // new ClearModelRepositoryJob(ModelRepositoryService.getRepository()).schedule();
            }
        });
    }

    private void createRemoteRepositorySection() {
        GridData layoutData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        layoutData.horizontalSpan = 3;

        Label headline = new Label(getFieldEditorParent(), SWT.LEFT);
        headline.setText(PREFPAGE_MODEL_REPOSITORY_HEADLINE);
        headline.setLayoutData(layoutData);
        headline.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));

        Label description = new Label(getFieldEditorParent(), SWT.LEFT);
        description.setText(PREFPAGE_MODEL_REPOSITORY_INTRO);
        description.setLayoutData(layoutData);

        ComboAddValueFieldEditor repositoryUrlEditor = new ComboAddValueFieldEditor(P_REPOSITORY_URL, P_REPOSITORY_URL_LIST,
                PREFPAGE_URI, "<new url>", 5, getFieldEditorParent());
        addField(repositoryUrlEditor);

        addField(new BooleanFieldEditor(P_REPOSITORY_ENABLE_AUTO_DOWNLOAD, PREFPAGE_ENABLE_AUTO_DOWNLOAD,
                getFieldEditorParent()));
    }

}
