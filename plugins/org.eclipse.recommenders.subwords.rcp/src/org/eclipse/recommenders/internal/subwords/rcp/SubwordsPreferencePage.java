/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Sewe - initial API and implementation.
 */
package org.eclipse.recommenders.internal.subwords.rcp;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class SubwordsPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    public SubwordsPreferencePage() {
        super(GRID);
    }

    @Override
    public void init(IWorkbench workbench) {
        setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, Constants.BUNDLE_ID));
        setMessage(Messages.PREFPAGE_TITLE_SUBWORDS);
        setDescription(Messages.PREFPAGE_DESCRIPTION_SUBWORDS_COMPREHENSIVE_MATCHING);
    }

    @Override
    protected void createFieldEditors() {
        addField(new BooleanFieldEditor(Constants.PREF_COMPREHENSIVE_SUBWORDS_MATCHING_CONSTRUCTORS,
                Messages.FIELD_LABEL_COMPREHENSIVE_SUBWORDS_MATCHING_CONSTRUCTORS, getFieldEditorParent()));
        addField(new BooleanFieldEditor(Constants.PREF_COMPREHENSIVE_SUBWORDS_MATCHING_TYPES,
                Messages.FIELD_LABEL_COMPREHENSIVE_SUBWORDS_MATCHING_TYPES, getFieldEditorParent()));

        addText(Messages.PREFPAGE_DESCRIPTION_SUBWORDS_PREFIX_LENGTH);

        IntegerFieldEditor prefixLengthEditor = new IntegerFieldEditor(
                Constants.PREF_SUBWORDS_MIN_PREFIX_LENGTH_FOR_TYPES, "Min Prefix Length", getFieldEditorParent());
        prefixLengthEditor.setValidRange(1, Integer.MAX_VALUE);
        addField(prefixLengthEditor);
    }

    private void addText(final String text) {
        final Label label = new Label(getFieldEditorParent(), SWT.WRAP);
        label.setText(text);
        GridDataFactory.fillDefaults().span(2, 1).hint(300, SWT.DEFAULT).grab(true, false)
        .align(SWT.FILL, SWT.BEGINNING).applyTo(label);
    }

}
