/**
 * Copyright (c) 2010 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.rcp.ui;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class RecommendersPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    @Override
    public void init(final IWorkbench workbench) {
        // unused
    }

    @Override
    protected Control createContents(final Composite parent) {
        String description = "Expand the tree to edit preferences for a specific feature.";

        final Label label = new Label(parent, 0);
        label.setText(description);

        noDefaultAndApplyButton();
        return label;
    }
}
