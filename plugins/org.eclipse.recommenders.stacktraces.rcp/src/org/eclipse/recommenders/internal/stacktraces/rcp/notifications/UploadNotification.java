/**
 * Copyright (c) 2014 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Haftstein - initial API and implementation.
 */
package org.eclipse.recommenders.internal.stacktraces.rcp.notifications;

import static java.text.MessageFormat.format;
import static org.eclipse.recommenders.internal.stacktraces.rcp.ReportState.*;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.recommenders.internal.stacktraces.rcp.Constants;
import org.eclipse.recommenders.internal.stacktraces.rcp.Messages;
import org.eclipse.recommenders.internal.stacktraces.rcp.ReportState;

public class UploadNotification extends StacktracesUiNotification {

    private ReportState state;

    public UploadNotification(ReportState reportState) {
        super(Constants.NOTIFY_UPLOAD);
        this.state = reportState;
    }

    private static String buildLabel(ReportState reportState) {
        return Messages.THANKYOUDIALOG_THANK_YOU + " " + Messages.THANKYOUDIALOG_RECEIVED_AND_TRACKED;
    }

    private String buildDescription(ReportState state) {
        StringBuilder text = new StringBuilder();

        if (state.isCreated()) {
            String message = format(Messages.THANKYOUDIALOG_NEW, getBugURL(), getBugId());
            text.append(message);
        } else {
            String status = state.getStatus().or(UNCONFIRMED);
            if (equals(UNCONFIRMED, status) || equals(NEW, status) || equals(ASSIGNED, status)) {
                text.append(format(Messages.THANKYOUDIALOG_MATCHED_EXISTING_BUG, getBugURL(), getBugId()));
            } else if (equals(RESOLVED, status) || equals(CLOSED, status)) {
                String resolution = state.getResolved().or(UNKNOWN);
                if (equals(FIXED, resolution)) {
                    text.append(format(Messages.THANKYOUDIALOG_MARKED_FIXED, getBugURL(), getBugId()));
                } else if (equals(DUPLICATE, resolution)) {
                    text.append(format(Messages.THANKYOUDIALOG_MARKED_DUPLICATE, getBugURL(), getBugId()));
                } else if (equals(MOVED, resolution)) {
                    text.append(format(Messages.THANKYOUDIALOG_MARKED_MOVED, getBugURL(), getBugId()));
                } else if (equals(WORKSFORME, resolution)) {
                    text.append(format(Messages.THANKYOUDIALOG_MARKED_WORKSFORME, getBugURL(), getBugId()));
                } else if (equals(WONTFIX, resolution) || equals(INVALID, resolution)
                        || equals(NOT_ECLIPSE, resolution)) {
                    text.append(format(Messages.THANKYOUDIALOG_MARKED_NORMAL, getBugURL(), getBugId()));
                } else {
                    text.append(format(Messages.THANKYOUDIALOG_MARKED_UNKNOWN, resolution, getBugURL(), getBugId()));
                }
            } else {
                text.append(Messages.THANKYOUDIALOG_RECEIVED_UNKNOWN_SERVER_RESPONSE);
            }
        }

        if (hasInfo()) {
            text.append(format(Messages.THANKYOUDIALOG_COMMITTER_MESSAGE, getInfo()));
        }

        text.append(Messages.THANKYOUDIALOG_THANK_YOU_FOR_HELP);
        return text.toString();
    }

    @Override
    public void open() {
        // TODO Auto-generated method stub
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

    @Override
    public String getDescription() {
        return buildDescription(state);
    }

    @Override
    public String getLabel() {
        return buildLabel(state);
    }
}
