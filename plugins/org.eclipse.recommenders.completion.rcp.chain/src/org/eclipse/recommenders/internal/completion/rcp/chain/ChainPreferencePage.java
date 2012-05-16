/**
 * Copyright (c) 2011 Stefan Henss.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Stefan Henß - initial API and implementation.
 */
package org.eclipse.recommenders.internal.completion.rcp.chain;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.ListEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.google.common.base.Joiner;

public class ChainPreferencePage extends org.eclipse.jface.preference.FieldEditorPreferencePage implements
        IWorkbenchPreferencePage {

    public static final String ID_MAX_CHAINS = "recommenders.chain.max_chains";
    public static final String ID_MAX_DEPTH = "recommenders.chain.max_chain_length";
    public static final String ID_TIMEOUT = "recommenders.chain.timeout";
    public static final String ID_IGNORE_TYPES = "recommenders.chain.ignore_types";

    private StringFieldEditor typeField;

    public ChainPreferencePage() {
        super(GRID);
        setPreferenceStore(ChainCompletionPlugin.getDefault().getPreferenceStore());
        setDescription("Call chains offer ways to obtain objects of the requested type by calling multiple methods in a row. "
                + "Since those chains can become long and time-consuming to search, the following options allow to limit the proposals.");
    }

    @Override
    protected void createFieldEditors() {
        addField(ID_MAX_CHAINS, "Maximum number of chains:", 0, 99);
        addField(ID_MAX_DEPTH, "Maximum chain depth:", 2, 99);
        addField(ID_TIMEOUT, "Chain search timeout (sec):", 1, 99);

        // TODO: Make this a little nicer, e.g., by using a class selection dialog to add new types.
        addField(new ListEditor(ID_IGNORE_TYPES, "Return types to ignore:", getFieldEditorParent()) {

            @Override
            protected String[] parseString(final String stringList) {
                getAddButton().setText("Add Type...");
                getUpButton().dispose();
                getDownButton().dispose();
                getList().setLayoutData(GridDataFactory.fillDefaults().create());

                return stringList.split("\\|");
            }

            @Override
            protected String getNewInputObject() {
                final String value = typeField.getStringValue();
                typeField.setStringValue("");
                return value.length() > 0 ? value : null;
            }

            @Override
            protected String createList(final String[] items) {
                return Joiner.on('|').join(items);
            }
        });
        typeField = new StringFieldEditor("recommenders.chain.ignore_type", "New type to add:", getFieldEditorParent());
        addField(typeField);
    }

    private void addField(final String name, final String labeltext, final int min, final int max) {
        final IntegerFieldEditor field = new IntegerFieldEditor(name, labeltext, getFieldEditorParent());
        field.setValidRange(min, max);
        addField(field);
    }

    public void init(final IWorkbench workbench) {
    }

}