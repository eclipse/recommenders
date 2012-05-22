/**
 * Copyright (c) 2010, 2012 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.utils.rcp.internal;

import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.jdt.ui.PreferenceConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.google.common.collect.Sets;

public abstract class AbstractEnablementPreferencePage extends org.eclipse.jface.preference.PreferencePage implements
        IWorkbenchPreferencePage {

    private Button enablement;

    protected void createEnablementButton(final Composite parent, final String label) {
        enablement = new Button(parent, SWT.CHECK);
        enablement.setText(label);
        enablement.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(final SelectionEvent e) {
                final Set<String> cats = Sets.newHashSet(PreferenceConstants.getExcludedCompletionProposalCategories());
                if (enablement.getSelection()) {
                    cats.remove(getCategoryId());
                } else {
                    cats.add(getCategoryId());
                }
                additionalExcludedCompletionCategoriesUpdates(enablement.getSelection(), cats);
                PreferenceConstants.setExcludedCompletionProposalCategories(cats.toArray(new String[cats.size()]));
            }

        });
    }

    protected abstract void additionalExcludedCompletionCategoriesUpdates(boolean isEnabled, Set<String> cats);

    @Override
    public void setVisible(final boolean visible) {
        // respond to changes in Java > Editor > Content Assist > Advanced:
        // this works only one-way. We respond to changes made in JDT but JDT page may show deprecated values.
        enablement.setSelection(isFeatureEnabled());
        super.setVisible(visible);
    }

    private boolean isFeatureEnabled() {
        final String[] excluded = PreferenceConstants.getExcludedCompletionProposalCategories();
        return !ArrayUtils.contains(excluded, getCategoryId());
    }

    protected abstract String getCategoryId();

}