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

public class ReportErrorNotification extends ExecutableStacktracesUiNotification {

    private final ErrorReport report;

    public ReportErrorNotification(ErrorReport report) {
        super(Constants.NOTIFY_REPORT);
        this.report = report;
    }

    @Override
    public String getDescription() {
        return longDescription(report);
    }

    @Override
    public String getLabel() {
        return shortDescription(report);
    }

    public ErrorReport getReport() {
        return report;
    }

    private static String shortDescription(ErrorReport report) {
        String pluginId = report.getStatus().getPluginId();
        return "An error was logged in " + pluginId;
    }

    private static String longDescription(ErrorReport report) {
        StringBuilder b = new StringBuilder();
        String message = report.getStatus().getMessage();
        b.append("Summary\n" + message);
        Throwable exception = report.getStatus().getException();
        if (exception != null) {
            b.append("\n" + exception.getClassName() + ": " + Optional.ofNullable(exception.getMessage()).orElse(""));
        }
        b.append("\n\nWe kindly ask you to report them to eclipse.org.\n");
        return b.toString();
    }

    @Override
    public List<Action> getActions() {
        Action a1 = new ExecutableStacktracesUiNotification.Action("Send") {

            @Override
            public void execute() {
                // TODO trigger upload job
            }
        };
        Action a2 = new ExecutableStacktracesUiNotification.Action("Details...") {

            @Override
            public void execute() {
                // TODO pop up reporting dialog for details
            }
        };
        return Lists.newArrayList(a1, a2);
    }
}
