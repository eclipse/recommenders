/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Olav Lenz - initial API and implementation.
 */
package org.eclipse.recommenders.internal.models.rcp;

import static org.eclipse.recommenders.internal.models.rcp.Messages.PREFPAGE_URI_INSERT;
import static org.eclipse.recommenders.internal.models.rcp.Messages.PREFPAGE_URI_INVALID;
import static org.eclipse.recommenders.internal.models.rcp.Messages.PREFPAGE_URI_MODEL_REPOSITORY;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class ComboAddValueFieldEditor extends FieldEditor {
    private int bulkSize;
    private String newValueLabel;
    private Combo comboBox;
    private String bulkPreferenceName;

    public ComboAddValueFieldEditor(String valuePreferenceName, String bulkPreferenceName, String label,
            String newValueLabel, int bulkSize, Composite parent) {
        init(valuePreferenceName, label);
        this.bulkPreferenceName = bulkPreferenceName;
        this.bulkSize = bulkSize;
        this.newValueLabel = newValueLabel;
        createControl(parent);
    }

    @Override
    protected void adjustForNumColumns(int numColumns) {
        Control control = getLabelControl();
        int comboColums = numColumns;
        if (control != null) {
            ((GridData) control.getLayoutData()).horizontalSpan = 1;
            comboColums = comboColums - 1;
        }
        ((GridData) comboBox.getLayoutData()).horizontalSpan = comboColums;
    }

    @Override
    protected void doFillIntoGrid(Composite parent, int numColumns) {
        Control control = getLabelControl(parent);
        GridData gridData = new GridData();
        gridData.horizontalSpan = 1;
        control.setLayoutData(gridData);
        control = getComboBoxControl(parent);
        gridData = new GridData();
        gridData.horizontalSpan = numColumns - 1;
        gridData.horizontalAlignment = GridData.FILL;
        control.setLayoutData(gridData);
        control.setFont(parent.getFont());
    }

    private Control getComboBoxControl(Composite parent) {
        if (comboBox == null) {
            comboBox = new Combo(parent, SWT.READ_ONLY);
            comboBox.setFont(parent.getFont());
            comboBox.add(newValueLabel);

            comboBox.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (newValueLabel.equals(comboBox.getText())) {
                        showInputDialog();
                    } else {
                        String newValue = comboBox.getText();
                        comboBox.remove(comboBox.indexOf(newValue));
                        comboBox.add(newValue, 1);
                    }
                }

                private void showInputDialog() {
                    InputDialog inputDialog = new InputDialog(comboBox.getShell(), PREFPAGE_URI_MODEL_REPOSITORY,
                            PREFPAGE_URI_INSERT, "", new IInputValidator() {

                                @Override
                                public String isValid(String newText) {
                                    if (isValidRepoURI(newText)) {
                                        return null;
                                    } else {
                                        return PREFPAGE_URI_INVALID;
                                    }
                                }
                            });
                    if (inputDialog.open() == Window.OK) {
                        addValueToComboBox(inputDialog.getValue());
                    } else {
                        comboBox.select(1);
                    }
                }

                private boolean isValidRepoURI(String uri) {
                    try {
                        new URI(uri);
                    } catch (URISyntaxException e) {
                        return false;
                    }
                    return true;
                }

            });

        }
        return comboBox;
    }

    private void addValueToComboBox(String value) {
        List<String> values = Arrays.asList(comboBox.getItems());
        if (!values.contains(value)) {
            comboBox.add(value, 1);
        }
        comboBox.setText(value);
    }

    @Override
    protected void doLoad() {
        String bulk = getPreferenceStore().getString(bulkPreferenceName);
        if (bulk.contains(";")) {
            for (String item : bulk.split(";")) {
                comboBox.add(item);
            }
        }
        addValueToComboBox(getPreferenceStore().getString(getPreferenceName()));
    }

    @Override
    protected void doLoadDefault() {
        addValueToComboBox(getPreferenceStore().getDefaultString(getPreferenceName()));
    }

    @Override
    protected void doStore() {
        List<String> items = Lists.newArrayList(Arrays.asList(comboBox.getItems()));
        String bulk = Joiner.on(';')
                .join(items.subList(1, (items.size() > bulkSize + 1) ? bulkSize + 1 : items.size()));
        getPreferenceStore().setValue(getPreferenceName(), comboBox.getText());
        getPreferenceStore().setValue(bulkPreferenceName, bulk);
    }

    @Override
    public int getNumberOfControls() {
        return 2;
    }

}
