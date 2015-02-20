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
import org.eclipse.recommenders.internal.stacktraces.rcp.notifications.ConfigurationNotification;
import org.eclipse.recommenders.internal.stacktraces.rcp.notifications.ErrorReportUiNotification;
import org.eclipse.recommenders.internal.stacktraces.rcp.notifications.Notifications;
import org.eclipse.recommenders.internal.stacktraces.rcp.notifications.ReportErrorNotification;
import org.eclipse.recommenders.internal.stacktraces.rcp.notifications.UploadNotification;
import org.eclipse.recommenders.internal.stacktraces.rcp.notifications.UploadNotification.UploadState;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.Maps;

public class NotificationsAction implements IWorkbenchWindowActionDelegate {

    @Override
    public void run(IAction action) {
        Job job = new Job("test exceptions") {

            Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

            @Override
            public IStatus run(IProgressMonitor monitor) {
                System.setProperty("eclipse.buildId", "unit-tests");
                Map<String, ErrorReportUiNotification> notifications = Maps.newHashMap();
                notifications.clear();
                notifications.put("configuration", new ConfigurationNotification(PreferenceInitializer.getDefault(),
                        shell));
                notifications.put("report", new ReportErrorNotification(newReport()));
                notifications.put("upload need info", new UploadNotification(uploadStateNeedinfo()));
                notifications.put("upload fixed", new UploadNotification(uploadStateFixed()));
                notifications.put("upload invalid", new UploadNotification(uploadStateInvalid()));
                notifications.put("upload new", new UploadNotification(uploadStateNew()));
                notifications.put("upload unconfirmed", new UploadNotification(uploadStateUnconfirmed()));
                boolean testAll = true;
                if (!testAll) {
                    Notifications.notify(notifications.get("report"));
                    return Status.OK_STATUS;
                }
                for (Entry<String, ErrorReportUiNotification> e : notifications.entrySet()) {
                    System.err.println("Testing: " + e.getKey());
                    Notifications.notify(e.getValue());
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        //
                    }
                }
                System.err.println("Done");
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

    private UploadState uploadStateNeedinfo() {
        UploadState state = new UploadState();
        state.setStatus(UploadState.Status.NEEDINFO);
        state.setCommitterMessage("The cake is a lie");
        state.setReportTitle("We had a serious error here");
        state.setBugId("457115");
        state.setBugUrl("https://bugs.eclipse.org/bugs/show_bug.cgi?id=457115");
        state.setIncidentId("54ef2e05e4b0eb19d1a1616c");
        state.setIncidentUrl("https://dev.eclipse.org/recommenders/committers/confess/#/problems/54ef2e05e4b0eb19d1a1616c");
        return state;
    }

    private UploadState uploadStateFixed() {
        UploadState state = new UploadState();
        state.setStatus(UploadState.Status.FIXED);
        state.setReportTitle("We had a serious error here");
        state.setBugId("457115");
        state.setBugUrl("https://bugs.eclipse.org/bugs/show_bug.cgi?id=457115");
        state.setIncidentId("54ef2e05e4b0eb19d1a1616c");
        state.setIncidentUrl("https://dev.eclipse.org/recommenders/committers/confess/#/problems/54ef2e05e4b0eb19d1a1616c");
        return state;
    }

    private UploadState uploadStateInvalid() {
        UploadState state = new UploadState();
        state.setStatus(UploadState.Status.INVALID);
        state.setReportTitle("We had a serious error here");
        state.setBugId("457115");
        state.setBugUrl("https://bugs.eclipse.org/bugs/show_bug.cgi?id=457115");
        state.setIncidentId("54ef2e05e4b0eb19d1a1616c");
        state.setIncidentUrl("https://dev.eclipse.org/recommenders/committers/confess/#/problems/54ef2e05e4b0eb19d1a1616c");
        return state;
    }

    private UploadState uploadStateNew() {
        UploadState state = new UploadState();
        state.setStatus(UploadState.Status.NEW);
        state.setReportTitle("We had a serious error here");
        state.setIncidentId("54ef2e05e4b0eb19d1a1616c");
        state.setIncidentUrl("https://dev.eclipse.org/recommenders/committers/confess/#/problems/54ef2e05e4b0eb19d1a1616c");
        return state;
    }

    private UploadState uploadStateUnconfirmed() {
        UploadState state = new UploadState();
        state.setStatus(UploadState.Status.UNCONFIRMED);
        state.setReportTitle("We had a serious error here");
        state.setIncidentId("54ef2e05e4b0eb19d1a1616c");
        state.setIncidentUrl("https://dev.eclipse.org/recommenders/committers/confess/#/problems/54ef2e05e4b0eb19d1a1616c");
        return state;
    }
}
