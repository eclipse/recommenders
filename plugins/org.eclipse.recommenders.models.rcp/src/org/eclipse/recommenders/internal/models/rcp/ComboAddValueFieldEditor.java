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

import static org.eclipse.recommenders.internal.models.rcp.Messages.*;

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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class ComboAddValueFieldEditor extends FieldEditor {
    private int bulkSize;

    private Combo comboBox;
    private String bulkPreferenceName;
    private Button modifyButton;
    private Button addButton;

    public ComboAddValueFieldEditor(String valuePreferenceName, String bulkPreferenceName, String label, int bulkSize,
            Composite parent) {
        init(valuePreferenceName, label);
        this.bulkPreferenceName = bulkPreferenceName;
        this.bulkSize = bulkSize;
        createControl(parent);
    }

    @Override
    protected void adjustForNumColumns(int numColumns) {
        Control control = getLabelControl();
        int colums = numColumns;
        if (control != null) {
            ((GridData) control.getLayoutData()).horizontalSpan = 1;
            colums = colums - 1;
        }
        ((GridData) comboBox.getLayoutData()).horizontalSpan = colums - 2;
        ((GridData) modifyButton.getLayoutData()).horizontalSpan = 1;
        ((GridData) addButton.getLayoutData()).horizontalSpan = 1;
    }

    @Override
    protected void doFillIntoGrid(Composite parent, int numColumns) {
        Control control = getLabelControl(parent);
        GridData gridData = new GridData();
        gridData.horizontalSpan = 1;
        control.setLayoutData(gridData);

        control = getComboBoxControl(parent);
        gridData = new GridData();
        gridData.horizontalSpan = numColumns - 3;
        control.setLayoutData(gridData);
        control.setFont(parent.getFont());

        control = getModifyButtonControl(parent);
        gridData = new GridData();
        gridData.horizontalSpan = 1;
        control.setLayoutData(gridData);
        control.setFont(parent.getFont());

        control = getAddButtonControl(parent);
        gridData = new GridData();
        gridData.horizontalSpan = 1;
        gridData.horizontalAlignment = GridData.FILL;
        control.setLayoutData(gridData);
        control.setFont(parent.getFont());
    }

    private Control getModifyButtonControl(final Composite parent) {
        if (modifyButton == null) {
            modifyButton = new Button(parent, SWT.PUSH);
            modifyButton.setText("Modify");
            modifyButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    String oldValue = comboBox.getText();
                    String newValue = showInputDialog(oldValue, parent);
                    if (newValue == null || newValue.equals(oldValue)) {
                        return;
                    }
                    comboBox.remove(oldValue);
                    if (!"".equals(newValue)) {
                        comboBox.add(newValue, 0);
                    }
                    comboBox.select(0);
                }

            });

        }
        return modifyButton;
    }

    private Control getAddButtonControl(final Composite parent) {
        if (addButton == null) {
            addButton = new Button(parent, SWT.PUSH);
            addButton.setText("Add");
            addButton.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    String newValue = showInputDialog("", parent);
                    if (newValue == null || "".equals(newValue)) {
                        return;
                    }
                    if (containValue(newValue)) {
                        comboBox.remove(newValue);
                    }
                    comboBox.add(newValue, 0);
                    comboBox.setText(newValue);
                }

                private boolean containValue(String value) {
                    for (String item : comboBox.getItems()) {
                        if (item.equals(value)) {
                            return true;
                        }
                    }
                    return false;
                }

            });
        }
        return addButton;
    }

    private Control getComboBoxControl(Composite parent) {
        if (comboBox == null) {
            comboBox = new Combo(parent, SWT.READ_ONLY);

            comboBox.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    String newValue = comboBox.getText();
                    comboBox.remove(newValue);
                    comboBox.add(newValue, 0);
                    comboBox.setText(newValue);
                }

            });

        }
        return comboBox;
    }

    private String showInputDialog(String value, Composite parent) {
        InputDialog inputDialog = new InputDialog(parent.getShell(), PREFPAGE_URI_MODEL_REPOSITORY,
                PREFPAGE_URI_INSERT, value, new IInputValidator() {

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
            return inputDialog.getValue();
        }
        return null;
    }

    private boolean isValidRepoURI(String uri) {
        try {
            new URI(uri);
        } catch (URISyntaxException e) {
            return false;
        }
        return true;
    }

    private void addAndSelectValueToComboBox(String value) {
        List<String> values = Arrays.asList(comboBox.getItems());
        if (!values.contains(value)) {
            comboBox.add(value);
        }
        comboBox.setText(value);
    }

    @Override
    protected void doLoad() {
        String bulk = getPreferenceStore().getString(bulkPreferenceName);
        if (!"".equals(bulk)) {
            for (String item : bulk.split(";")) {
                comboBox.add(item);
            }
        }
        addAndSelectValueToComboBox(getPreferenceStore().getString(getPreferenceName()));
    }

    @Override
    protected void doLoadDefault() {
        addAndSelectValueToComboBox(getPreferenceStore().getDefaultString(getPreferenceName()));
    }

    @Override
    protected void doStore() {
        List<String> items = Lists.newArrayList(Arrays.asList(comboBox.getItems()));
        String bulk = Joiner.on(';').join(items.subList(0, items.size() > bulkSize ? bulkSize : items.size()));
        getPreferenceStore().setValue(getPreferenceName(), comboBox.getText());
        getPreferenceStore().setValue(bulkPreferenceName, bulk);
    }

    @Override
    public int getNumberOfControls() {
        return 4;
    }

}
