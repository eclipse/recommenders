/**
 * Copyright (c) 2014 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial implementation
 */
package org.eclipse.recommenders.internal.stacktraces.rcp;

import static org.eclipse.recommenders.internal.stacktraces.rcp.ReportState.*;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

class ThankYouDialog extends org.eclipse.jface.dialogs.TitleAreaDialog {

    public static Image TITLE_IMAGE = ErrorReportWizard.TITLE_IMAGE_DESC.createImage();
    private ReportState state;

    ThankYouDialog(Shell parentShell, ReportState state) {
        super(parentShell);
        this.state = state;
        setHelpAvailable(false);
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    @Override
    protected void configureShell(Shell newShell) {
        newShell.setText(Messages.THANKYOUDIALOG_THANK_YOU);
        super.configureShell(newShell);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        setTitle(Messages.THANKYOUDIALOG_THANK_YOU);
        setMessage(Messages.THANKYOUDIALOG_RECEIVED_AND_TRACKED);
        setTitleImage(TITLE_IMAGE);

        Label linetop = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        linetop.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        Composite border = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        border.setLayout(layout);
        border.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

        Label linebottom = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
        linebottom.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

        Composite container = new Composite(border, SWT.NONE);
        container.setLayout(GridLayoutFactory.swtDefaults().create());
        container.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

        StringBuilder text = new StringBuilder();

        if (state.isCreated()) {
            text.append(Messages.THANKYOUDIALOG_IS_TRACKED_AT)
                    .append(" \n\n    <a>") //$NON-NLS-1$
                    .append(state.getBugUrl().or(Messages.THANKYOUDIALOG_INVALID_SERVER_RESPONSE))
                    .append("</a>.").append("\n\n") //$NON-NLS-1$//$NON-NLS-2$
                    .append(Messages.THANKYOUDIALOG_PLEASE_ADD_YOURSELF_TO_CC_LIST);
        } else {
            boolean needsinfo = ArrayUtils.contains(state.getKeywords().or(EMPTY_STRINGS), KEYWORD_NEEDINFO);
            String status = state.getStatus().or(UNCONFIRMED);
            if (equals(UNCONFIRMED, status) || equals(NEW, status) || equals(ASSIGNED, status)) {
                if (needsinfo) {
                    text.append(Messages.THANKYOUDIALOG_MATCHED_AGAINST_EXISTING_BUG_NEEDS_FURTHER_INFORMATION)
                            .append(Messages.THANKYOUDIALOG_VISIT_BUG_PROVIDE_MORE_DETAILS)
                            .append("\n\n").append("    <a>").append(state.getBugUrl().or(Messages.THANKYOUDIALOG_INVALID_SERVER_RESPONSE)) //$NON-NLS-1$ //$NON-NLS-2$
                            .append("</a>."); //$NON-NLS-1$
                } else {
                    text.append(Messages.THANKYOUDIALOG_MATCHED_AGAINST_EXISTING_BUG)
                            .append(Messages.THANKYOUDIALOG_ADD_TO_CC_LIST)
                    .append("\n\n").append("    <a>").append(state.getBugUrl().or(Messages.THANKYOUDIALOG_INVALID_SERVER_RESPONSE)) //$NON-NLS-1$ //$NON-NLS-2$
                    .append("</a>."); //$NON-NLS-1$
                }
            } else if (equals(RESOLVED, status) || equals(CLOSED, status)) {

                String resolution = state.getResolved().or(UNKNOWN);
                if (equals(FIXED, resolution)) {

                    text.append(Messages.THANKYOUDIALOG_MARKED_FIXED)
                            .append(Messages.THANKYOUDIALOG_VISIT_REPORT_FOR_FURTHER_INFORMATION)
                            .append("\n\n") //$NON-NLS-1$
                            .append("    <a>").append(state.getBugUrl().or(Messages.THANKYOUDIALOG_INVALID_SERVER_RESPONSE)).append("</a>."); //$NON-NLS-1$ //$NON-NLS-2$
                } else if (equals(DUPLICATE, resolution)) {
                    text.append(Messages.THANKYOUDIALOG_MARKED_DUPLICATE)
                            .append(Messages.THANKYOUDIALOG_VISIT_REPORT_FOR_FURTHER_INFORMATION)
                            .append("\n\n") //$NON-NLS-1$
                            .append("    <a>").append(state.getBugUrl().or(Messages.THANKYOUDIALOG_INVALID_SERVER_RESPONSE)).append("</a>."); //$NON-NLS-1$ //$NON-NLS-2$

                } else if (equals(MOVED, resolution)) {
                    text.append(Messages.THANKYOUDIALOG_MARKED_MOVED)
                            .append(Messages.THANKYOUDIALOG_VISIT_REPORT_FURTHER_INFORMATION)
                            .append("\n\n") //$NON-NLS-1$
                            .append("    <a>").append(state.getBugUrl().or(Messages.THANKYOUDIALOG_INVALID_SERVER_RESPONSE)).append("</a>."); //$NON-NLS-1$ //$NON-NLS-2$

                } else if (equals(WORKSFORME, resolution)) {
                    text.append(Messages.THANKYOUDIALOG_NOT_ABLE_TO_REPRODUCE)
                            .append(Messages.THANKYOUDIALOG_PROVIDE_MORE_INFORMATION)
                            .append("\n\n").append("    <a>").append(state.getBugUrl().or(Messages.THANKYOUDIALOG_INVALID_SERVER_RESPONSE)) //$NON-NLS-1$ //$NON-NLS-2$
                            .append("</a>."); //$NON-NLS-1$
                } else if (equals(WONTFIX, resolution) || equals(INVALID, resolution)
                        || equals(NOT_ECLIPSE, resolution)) {
                    text.append(Messages.THANKYOUDIALOG_MARKED_NORMAL)
                            .append(Messages.THANKYOUDIALOG_COMMENT_IF_ERROR)
                            .append("\n\n").append("    <a>").append(state.getBugUrl().or(Messages.THANKYOUDIALOG_INVALID_SERVER_RESPONSE)) //$NON-NLS-1$ //$NON-NLS-2$
                            .append("</a>."); //$NON-NLS-1$
                } else {
                    text.append("The log event you sent has been marked as a '" //$NON-NLS-1$
                            + resolution + "'. ") //$NON-NLS-1$
                            .append(Messages.THANKYOUDIALOG_COMMENT_ON_REPORT_IF_ERROR)
                            .append("\n\n").append("    <a>").append(state.getBugUrl().or(Messages.THANKYOUDIALOG_INVALID_SERVER_RESPONSE)) //$NON-NLS-1$ //$NON-NLS-2$
                            .append("</a>."); //$NON-NLS-1$
                }
            } else {
                text.append(Messages.THANKYOUDIALOG_UNKNOWN_RESPONSE_RAISE_BUG_AGAINST_ERROR_REPORTER);
            }
        }

        text.append("\n\n").append(Messages.THANKYOUDIALOG_NOTE_ADDITIONAL_ACCESS_PERMISSIONS); //$NON-NLS-1$
        text.append("\n").append(Messages.THANKYOUDIALOG_THANK_YOU_FOR_HELP); //$NON-NLS-1$

        Link link = new Link(container, SWT.WRAP);
        link.setText(text.toString());
        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Browsers.openInExternalBrowser(state.getBugUrl().get());
            }
        });
        GridDataFactory.defaultsFor(link).align(GridData.FILL, GridData.BEGINNING).applyTo(link);
        return container;
    }

    private boolean equals(String expected, String actual) {
        return StringUtils.equals(expected, actual);
    }
}
