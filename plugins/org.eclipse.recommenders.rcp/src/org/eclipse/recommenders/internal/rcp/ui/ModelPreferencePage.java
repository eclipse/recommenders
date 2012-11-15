/**
 * Copyright (c) 2010, 2012 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Patrick Gottschaemmer, Olav Lenz - initial API and implementation.
 */
package org.eclipse.recommenders.internal.rcp.ui;

import static org.eclipse.recommenders.rcp.RecommendersPlugin.P_REPOSITORY_ENABLE_AUTO_DOWNLOAD;
import static org.eclipse.recommenders.rcp.RecommendersPlugin.P_REPOSITORY_URL;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.recommenders.internal.rcp.repo.ClearModelRepositoryJob;
import org.eclipse.recommenders.rcp.RecommendersPlugin;
import org.eclipse.recommenders.rcp.repo.ModelRepositoryService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ModelPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public ModelPreferencePage() {
        super(GRID);
    }

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(RecommendersPlugin.getDefault().getPreferenceStore());
    }

    @Override
    protected void createFieldEditors() {
        createRemoteRepositorySection();

        Button clearCaches = new Button(getFieldEditorParent(), SWT.PUSH);
        clearCaches.setText("Clear Caches");
        GridData data = new GridData(SWT.END, SWT.CENTER, false, false);
        data.horizontalSpan = 3;
        clearCaches.setLayoutData(data);
        clearCaches.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                new ClearModelRepositoryJob(ModelRepositoryService.getRepository()).schedule();
            }
        });
    }

    private void createRemoteRepositorySection() {
        GridData layoutData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);
        layoutData.horizontalSpan = 3;

        Label headline = new Label(getFieldEditorParent(), SWT.LEFT);
        headline.setText("Remote Repository");
        headline.setLayoutData(layoutData);
        headline.setFont(formatBold(headline.getFont()));

        Label description = new Label(getFieldEditorParent(), SWT.LEFT);
        description.setText("All models used by Code Recommenders will be pulled from this repository.");
        description.setLayoutData(layoutData);

        StringButtonFieldEditor modelRepoFieldEditor = new StringButtonFieldEditor(P_REPOSITORY_URL,
                "Model Repository: ", getFieldEditorParent()) {
            @Override
            protected String changePressed() {
                InputDialog inputDialog = new InputDialog(getShell(), "Model Repository URL",
                        "Please insert an URL for the Model Repository:", oldValue, new IInputValidator() {
                            @Override
                            public String isValid(String newText) {
                                if (isValidRepoURL(newText)) {
                                    return null;
                                } else {
                                    return "Please insert a valid URL";
                                }
                            }
                        });
                if (inputDialog.open() == Window.OK) {
                    return inputDialog.getValue();
                }
                return oldValue;
            }
        };
        modelRepoFieldEditor.getTextControl(getFieldEditorParent()).setEnabled(false);
        addField(modelRepoFieldEditor);

        addField(new BooleanFieldEditor(P_REPOSITORY_ENABLE_AUTO_DOWNLOAD, "Enable auto-download.",
                getFieldEditorParent()));
    }

    private boolean isValidRepoURL(String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }

    private Font formatBold(Font font) {
        FontData[] fontData = font.getFontData();
        for (FontData fd : fontData) {
            fd.setStyle(SWT.BOLD);
        }
        return new Font(font.getDevice(), fontData);
    }
}