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

import java.util.List;
import java.util.Optional;

import org.eclipse.recommenders.internal.stacktraces.rcp.Constants;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorReport;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.Throwable;

import com.google.common.collect.Lists;

public class ReportErrorNotification extends ExecutableErrorReportUiNotification {

    private final ErrorReport report;

    public ReportErrorNotification(ErrorReport report) {
        super(Constants.NOTIFY_REPORT);
        this.report = report;
    }

    @Override
    public String getDescription() {
        StringBuilder b = new StringBuilder();
        Throwable exception = report.getStatus().getException();
        if (exception != null) {
            b.append(exception.getClassName() + ": " + Optional.ofNullable(exception.getMessage()).orElse(""));
        }
        b.append("\n\nDo you want to report it to eclipse.org?\n");
        return b.toString();
    }

    @Override
    public String getLabel() {
        return report.getStatus().getMessage();
    }

    public ErrorReport getReport() {
        return report;
    }

    private static String shortDescription(ErrorReport report) {
        return report.getStatus().getMessage();
    }

    private static String longDescription(ErrorReport report) {
        StringBuilder b = new StringBuilder();
        Throwable exception = report.getStatus().getException();
        if (exception != null) {
            b.append("\n" + exception.getClassName() + ": " + Optional.ofNullable(exception.getMessage()).orElse(""));
        }
        b.append("\n\nDo you want to report it to eclipse.org?\n");
        return b.toString();
    }

    @Override
    public List<Action> getActions() {
        Action a1 = new ExecutableErrorReportUiNotification.Action("Details") {

            @Override
            public void execute() {
                // TODO pop up reporting dialog for details
            }
        };
        Action a2 = new ExecutableErrorReportUiNotification.Action("Send") {

            @Override
            public void execute() {
                // TODO trigger upload job
            }
        };
        return Lists.newArrayList(a1, a2);
    }

    @Override
    protected String getTitle() {
        return "Error in " + getReport().getStatus().getPluginId();
    }
}
