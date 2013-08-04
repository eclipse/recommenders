/**
 * Copyright (c) 2013 Timur Achmetow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Timur Achmetow - Initial API and implementation
 */
package org.eclipse.recommenders.stats.rcp;

import static java.lang.String.format;
import static org.eclipse.recommenders.stats.rcp.TableViewerFactory.createWrapperComposite;

import java.util.Date;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.google.common.collect.Iterables;

public class DeveloperActivityDialog extends TitleAreaDialog {

    private StatisticsView statsView;

    public DeveloperActivityDialog(Shell parentShell) {
        super(parentShell);
        setHelpAvailable(false);
        statsView = new StatisticsView();
    }

    @Override
    protected Control createContents(Composite parent) {
        super.createContents(parent);
        getShell().setText("Developer Activity Report");
        getShell().setSize(700, 800);
        setMessage(getDescriptionText(), IMessageProvider.INFORMATION);
        return parent;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        parent.setLayout(new GridLayout());
        SashForm masterSash = new SashForm(parent, SWT.HORIZONTAL);
        masterSash.setLayoutData(new GridData(GridData.FILL_BOTH));

        final Composite masterCmp = createWrapperComposite(masterSash);
        final Composite detailCmp = createWrapperComposite(masterSash);
        final StackLayout stack = new StackLayout();
        detailCmp.setLayout(stack);

        final Composite statsCmp = createWrapperComposite(detailCmp);
        final Composite commandsCmp = createWrapperComposite(detailCmp);
        NavigationViewer navigationViewer = new NavigationViewer();
        navigationViewer.createViewer(masterCmp);
        navigationViewer.addSelectionChangedListener(stack, detailCmp, statsCmp, commandsCmp);
        statsView.createContent(statsCmp);
        new TriggeredCommandsView().createContent(commandsCmp);

        masterSash.setWeights(new int[] { 30, 70 });
        return parent;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    private String getDescriptionText() {
        String date = "the beginning of recording";
        if (statsView.getOkayEvents().size() > 0) {
            Date start = new Date(Iterables.getFirst(statsView.getOkayEvents(), null).sessionStarted);
            date = format("%tF", start);
        }
        return "Here is a summary of your activities since " + date;
    }
}
