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

import static java.text.MessageFormat.format;
import static org.eclipse.recommenders.internal.stacktraces.rcp.ReportState.*;

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

import com.google.common.annotations.VisibleForTesting;

class ThankYouDialog extends org.eclipse.jface.dialogs.TitleAreaDialog {

    protected static class MessageBuilder {

        protected ReportState state;

        @VisibleForTesting
        protected String buildText() {
            StringBuilder text = new StringBuilder();

            if (state.isCreated()) {
                text.append(messageNewBugCreated());
            } else {
                String status = state.getStatus().or(UNCONFIRMED);
                if (equals(UNCONFIRMED, status) || equals(NEW, status) || equals(ASSIGNED, status)) {
                    text.append(messageMatchedAgainstExistingBug());
                } else if (equals(RESOLVED, status) || equals(CLOSED, status)) {
                    String resolution = state.getResolved().or(UNKNOWN);
                    if (equals(FIXED, resolution)) {
                        text.append(messageFixed());
                    } else if (equals(DUPLICATE, resolution)) {
                        text.append(messageDuplicate());
                    } else if (equals(MOVED, resolution)) {
                        text.append(messageMoved());
                    } else if (equals(WORKSFORME, resolution)) {
                        text.append(messageWorksforme());
                    } else if (equals(WONTFIX, resolution) || equals(INVALID, resolution)
                            || equals(NOT_ECLIPSE, resolution)) {
                        text.append(messageNormal());
                    } else {
                        text.append(messageUnknown(resolution));
                    }
                } else {
                    text.append(messageUnknownServerResponse());
                }
            }

            if (hasInfo()) {
                text.append(messageCommitterInfo());
            }

            text.append(Messages.THANKYOUDIALOG_THANK_YOU_FOR_HELP);
            return text.toString();
        }

        @VisibleForTesting
        protected String messageNewBugCreated() {
            return format(Messages.THANKYOUDIALOG_NEW, getBugURL(), getBugId());
        }

        @VisibleForTesting
        protected String messageMatchedAgainstExistingBug() {
            return format(Messages.THANKYOUDIALOG_MATCHED_EXISTING_BUG, getBugURL(), getBugId());
        }

        @VisibleForTesting
        protected String messageFixed() {
            return format(Messages.THANKYOUDIALOG_MARKED_FIXED, getBugURL(), getBugId());
        }

        @VisibleForTesting
        protected String messageDuplicate() {
            return format(Messages.THANKYOUDIALOG_MARKED_DUPLICATE, getBugURL(), getBugId());
        }

        @VisibleForTesting
        protected String messageMoved() {
            return format(Messages.THANKYOUDIALOG_MARKED_MOVED, getBugURL(), getBugId());
        }

        @VisibleForTesting
        protected String messageWorksforme() {
            return format(Messages.THANKYOUDIALOG_MARKED_WORKSFORME, getBugURL(), getBugId());
        }

        @VisibleForTesting
        protected String messageNormal() {
            return format(Messages.THANKYOUDIALOG_MARKED_NORMAL, getBugURL(), getBugId());
        }

        @VisibleForTesting
        protected String messageUnknown(String resolution) {
            return format(Messages.THANKYOUDIALOG_MARKED_UNKNOWN, resolution, getBugURL(), getBugId());
        }

        @VisibleForTesting
        protected String messageUnknownServerResponse() {
            return Messages.THANKYOUDIALOG_RECEIVED_UNKNOWN_SERVER_RESPONSE;
        }

        @VisibleForTesting
        protected String messageCommitterInfo() {
            return format(Messages.THANKYOUDIALOG_COMMITTER_MESSAGE, getInfo());
        }

        private boolean hasInfo() {
            return state.getInformation().isPresent();
        }

        private String getInfo() {
            return state.getInformation().or(Messages.THANKYOUDIALOG_COMMITTER_MESSAGE_EMPTY);
        }

        private String getBugId() {
            return state.getBugId().or("---");
        }

        private String getBugURL() {
            return state.getBugUrl().or(Messages.THANKYOUDIALOG_INVALID_SERVER_RESPONSE);
        }

        private static boolean equals(String expected, String actual) {
            return StringUtils.equals(expected, actual);
        }
    }

    public static final Image TITLE_IMAGE = ErrorReportWizard.TITLE_IMAGE_DESC.createImage();

    MessageBuilder messageBuilder = new MessageBuilder();

    private String bugUrl;

    ThankYouDialog(Shell parentShell, ReportState state) {
        super(parentShell);
        messageBuilder.state = state;
        bugUrl = state.getBugUrl().get();
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

        String text = messageBuilder.buildText();

        Link link = new Link(container, SWT.WRAP);
        link.setText(text);
        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Browsers.openInExternalBrowser(bugUrl);
            }
        });
        GridDataFactory.defaultsFor(link).align(GridData.FILL, GridData.BEGINNING).applyTo(link);
        return container;
    }
}
