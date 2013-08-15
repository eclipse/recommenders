/**
 * Copyright (c) 2010 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Dorn - initial API and implementation.
 */
package org.eclipse.recommenders.internal.rcp;

import static org.eclipse.recommenders.internal.rcp.Constants.BUNDLE_NAME;
import static org.eclipse.recommenders.internal.rcp.Constants.SURVEY_DESCRIPTION;
import static org.eclipse.recommenders.internal.rcp.Constants.SURVEY_URL;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class SurveyPreferencePage extends org.eclipse.jface.preference.PreferencePage implements
        IWorkbenchPreferencePage {

    private static final String SURVEY_LINK_TEXT = "<a>Take the survey</a> (will open in a browser window).";

    @Override
    public void init(IWorkbench workbench) {
        setDescription(SURVEY_DESCRIPTION);
    }

    @Override
    protected Control createContents(final Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);

        container.setLayout(GridLayoutFactory.swtDefaults().margins(0, 0).create());
        Link surveyLink = new Link(container, SWT.NONE | SWT.WRAP);
        surveyLink
                .setLayoutData(GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).grab(true, false)
                        .hint(convertHorizontalDLUsToPixels(IDialogConstants.MINIMUM_MESSAGE_AREA_WIDTH), SWT.DEFAULT)
                        .create());
        surveyLink.setText(SURVEY_LINK_TEXT);
        surveyLink.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                try {
                    IPreferenceStore preferences = new ScopedPreferenceStore(InstanceScope.INSTANCE, BUNDLE_NAME);
                    IWebBrowser browser = PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser();
                    browser.openURL(SURVEY_URL);
                    preferences.setValue(Constants.SURVEY_TAKEN, true);
                } catch (PartInitException e) {
                    e.printStackTrace();
                }
            }
        });

        return container;
    }

}
