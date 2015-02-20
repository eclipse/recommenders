/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Haftstein - manual test code.
 */
package org.eclipse.recommenders.stacktraces.rcp.actions;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.recommenders.internal.stacktraces.rcp.PreferenceInitializer;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorReport;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorReports;
import org.eclipse.recommenders.internal.stacktraces.rcp.notifications.Notifications;
import org.eclipse.recommenders.internal.stacktraces.rcp.notifications.ReportErrorNotification;
import org.eclipse.recommenders.internal.stacktraces.rcp.notifications.StacktracesUiNotification;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.google.common.collect.Maps;

public class NotificationsAction implements IWorkbenchWindowActionDelegate {

    private Map<String, StacktracesUiNotification> notifications = Maps.newHashMap();

    @Override
    public void run(IAction action) {
        System.setProperty("eclipse.buildId", "unit-tests");
        notifications.clear();
        // notifications.put("configuration", new ConfigurationNotification(PreferenceInitializer.getDefault(),
        // PlatformUI
        // .getWorkbench().getActiveWorkbenchWindow().getShell()));
        notifications.put("report", new ReportErrorNotification(newReport()));
        // notifications.put("upload 1", new UploadNotification(new ReportState()));
        // notifications.put("more info 1", new MoreInformationNotification(Messages.UPLOADJOB_NEED_FURTHER_INFORMATION
        // + "" + Messages.THANKYOUDIALOG_INVALID_SERVER_RESPONSE));
        Job job = new Job("test exceptions") {

            @Override
            public IStatus run(IProgressMonitor monitor) {
                for (Entry<String, StacktracesUiNotification> e : notifications.entrySet()) {
                    System.err.println("Testing: " + e.getKey());
                    Notifications.notify(e.getValue());
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        //
                    }
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
    }

    @Override
    public void dispose() {
    }

    @Override
    public void init(IWorkbenchWindow window) {

    }

    private ErrorReport newReport() {
        NullPointerException npe = new NullPointerException("Something seems to be null");
        npe.fillInStackTrace();
        IStatus event = new Status(Status.ERROR, "org.eclipse.recommenders.stacktraces",
                "Error message for testing purpose", npe);
        return ErrorReports.newErrorReport(event, PreferenceInitializer.getDefault());
    }
}
