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
import static org.apache.commons.lang3.StringUtils.abbreviate;

import java.util.List;

import org.eclipse.recommenders.internal.stacktraces.rcp.Constants;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

public class UploadNotification extends ExecutableErrorReportUiNotification {

    public static class UploadState {

        public static enum Status {
            NEW("new"),
            UNCONFIRMED("unconfirmed"),
            FIXED("fixed"),
            NEEDINFO("need info"),
            INVALID("invalid"),
            WONTFIX("won't fix");

            private String text;

            Status(String text) {
                this.text = text;
            }

            @Override
            public String toString() {
                return text;

            }
        }

        private String incidentUrl;
        private String incidentId;
        private Optional<String> bugUrl = Optional.absent();
        private Optional<String> bugId = Optional.absent();
        private Status status;
        private Optional<String> committerMessage = Optional.absent();
        private String reportTitle;

        public Status getStatus() {
            return status;
        }

        public void setStatus(Status status) {
            this.status = status;
        }

        public Optional<String> getCommitterMessage() {
            return committerMessage;
        }

        public void setCommitterMessage(String committerMessage) {
            this.committerMessage = Optional.fromNullable(committerMessage);
        }

        public void setIncidentUrl(String incidentUrl) {
            this.incidentUrl = incidentUrl;
        }

        public void setIncidentId(String incidentId) {
            this.incidentId = incidentId;
        }

        public void setBugUrl(String bugUrl) {
            this.bugUrl = Optional.fromNullable(bugUrl);
        }

        public void setBugId(String bugId) {
            this.bugId = Optional.fromNullable(bugId);
        }

        public String getIncidentUrl() {
            return incidentUrl;
        }

        public String getIncidentId() {
            return incidentId;
        }

        public Optional<String> getBugUrl() {
            return bugUrl;
        }

        public Optional<String> getBugId() {
            return bugId;
        }

        public String getReportTitle() {
            return reportTitle;
        }

        public void setReportTitle(String reportTitle) {
            this.reportTitle = reportTitle;
        }

    }

    private static final int MAX_REPORT_TITLE_CHARACTER_LENGTH = 40;

    private UploadState state;

    public UploadNotification(UploadState state) {
        this(Constants.NOTIFY_UPLOAD, state);
    }

    public UploadNotification(String eventId, UploadState state) {
        super(eventId);
        this.state = state;
    }

    @Override
    protected String getTitle() {
        String reportTitle = abbreviate(state.getReportTitle(), MAX_REPORT_TITLE_CHARACTER_LENGTH);
        switch (state.getStatus()) {
        case FIXED:
            return format("Fixed: {0}", reportTitle);
        case NEEDINFO:
            return format("Your assistance is requested: {0}", reportTitle);
        case NEW:
        case UNCONFIRMED:
            return format("To be confirmed: {0}", reportTitle);
        case WONTFIX:
            return format("Minor issue: {0}", reportTitle);
        case INVALID:
            return format("Log message: {0}", reportTitle);
        default:
            return state.getStatus().name();
        }
    }

    @Override
    public String getLabel() {
        switch (state.getStatus()) {
        case FIXED:
            return format("Your issue is already fixed.");
        case INVALID:
            return format("Your report has been marked as a log message");
        case NEEDINFO:
            return format("Your issue is known but further information is required.");
        case UNCONFIRMED:
        case NEW:
            return format("Your report is now recorded.");
        case WONTFIX:
            return format("Your report has been marked as a minor issue.");
        default:
            return "";

        }
    }

    @Override
    public String getDescription() {
        StringBuilder text = new StringBuilder();
        switch (state.getStatus()) {
        case FIXED: {
            text.append("Please visit the bug report for further details.");
            break;
        }
        case NEEDINFO: {
            text.append("Please visit the incident and see if you can provide additional information.");
            break;
        }
        case UNCONFIRMED:
        case NEW: {
            text.append(format("It's not yet confirmed as a bug. "
                    + "Please visit your report and see if you can provide more information.", state.getReportTitle()));
            break;
        }
        case INVALID:
        case WONTFIX: {
            text.append("If you think your report is actually an error, please visit your report and leave a comment.");
            break;
        }
        default: {
            break;
        }
        }

        if (state.getCommitterMessage().isPresent()) {
            text.append(format("\nCommitter message: {0}", state.getCommitterMessage().get()));
        }

        text.append("\n\nThank you for your help!");
        return text.toString();
    }

    @Override
    public List<Action> getActions() {
        List<Action> actions = Lists.newArrayList();
        if (state.getBugId().isPresent()) {
            Action a = new ExecutableErrorReportUiNotification.Action("Tracked at #" + state.getBugId().get()) {

                @Override
                public void execute() {
                    // TODO open bugzilla bug
                }
            };
            actions.add(a);
        }
        Action a2 = new ExecutableErrorReportUiNotification.Action("Your Report") {

            @Override
            public void execute() {
                // TODO open incident url
            }
        };
        actions.add(a2);
        return actions;
    }
}
