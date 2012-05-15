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
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ChainPreferencePage extends org.eclipse.jface.preference.PreferencePage implements
        IWorkbenchPreferencePage {

    public static final String ID_MAX_CHAINS = "recommenders.chain.chains";
    public static final String ID_MAX_DEPTH = "recommenders.chain.depth";
    public static final String ID_TIMEOUT = "recommenders.chain.timeout";

    private Text chains;
    private Text depth;
    private Text timeout;

    public ChainPreferencePage() {
        setDescription("Call chains offer ways to obtain objects of the requested type by calling multiple methods in a row. "
                + "Since those chains can become long and time-consuming to search, the following options allow to limit the proposals.");
    }

    @Override
    protected Control createContents(final Composite parent) {
        final Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(GridLayoutFactory.swtDefaults().numColumns(2).create());

        chains = createField(container, "Maximum number of chains:");
        depth = createField(container, "Maximum chain depth:");
        timeout = createField(container, "Chain search timeout (sec):");

        performDefaults();
        return container;
    }

    private static Text createField(final Composite container, final String text) {
        new Label(container, SWT.NONE).setText(text);
        final Text field = new Text(container, SWT.NONE);
        field.setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, false).create());
        return field;
    }

    @Override
    protected void performDefaults() {
        chains.setText(getPreferenceStore().getString(ID_MAX_CHAINS));
        depth.setText(getPreferenceStore().getString(ID_MAX_DEPTH));
        timeout.setText(getPreferenceStore().getString(ID_TIMEOUT));
    }

    @Override
    public boolean performOk() {
        getPreferenceStore().setValue(ID_MAX_CHAINS, parseInteger(chains, 1));
        getPreferenceStore().setValue(ID_MAX_DEPTH, parseInteger(depth, 2));
        getPreferenceStore().setValue(ID_TIMEOUT, parseInteger(timeout, 1));
        return super.performOk();
    }

    private static int parseInteger(final Text text, final int min) {
        try {
            return Math.max(min, Integer.valueOf(text.getText()));
        } catch (final NumberFormatException e) {
            return min;
        }
    }

    public void init(final IWorkbench workbench) {
        setPreferenceStore(ChainCompletionPlugin.getDefault().getPreferenceStore());
    }

}