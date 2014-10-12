/**
 * Copyright (c) 2014 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 *    Daniel Haftstein - added UI thread safety
 */
package org.eclipse.recommenders.internal.stacktraces.rcp;

import static org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorReports.newErrorReport;
import static org.eclipse.recommenders.utils.Checks.cast;
import static org.eclipse.recommenders.utils.Logs.log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.property.Properties;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorReport;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.SendAction;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.Settings;
import org.eclipse.recommenders.utils.Reflections;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;

public class LogListener implements ILogListener, IStartup {

    private static final String STAND_IN_MESSAGE = "Stand-In Stacktrace supplied by Eclipse Stacktraces & Error Reporting Tool";

    private static Method SET_EXCEPTION = Reflections.getDeclaredMethod(Status.class, "setException", Throwable.class)
            .orNull();
    private static String fileNameAndLocation = "errorRepors.txt";
    private static String tempFileNameAndLocation = "tempErrorRepors.txt";
    private static int maxErrorAgeInDays = 10;

    private IObservableList errorReports;
    private volatile boolean isDialogOpen; 
    private Settings settings;  
    private ArrayList<String> tempErrorReportCache; 

    public LogListener() {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                errorReports = Properties.selfList(ErrorReport.class).observe(Lists.newArrayList());
                tempErrorReportCache = new ArrayList<String>();
                populateTempCacheFromFile(); 
            }
        });
    }

    @Override
    public void earlyStartup() {
        Platform.addLogListener(this); 
    }

    @Override
    public void logging(final IStatus status, String nouse) {
        if (!isErrorSeverity(status)) {
            return;
        }
        settings = readSettings();
        if (!hasPluginIdWhitelistedPrefix(status, settings.getWhitelistedPluginIds())) {
            return;
        }
        SendAction sendAction = settings.getAction();
        if (!isSendingAllowedOnAction(sendAction)) {
            return;
        }
        insertDebugStacktraceIfEmpty(status);
        final ErrorReport report = newErrorReport(status, settings);
        if (settings.isSkipSimilarErrors() && sentSimilarErrorBefore(report)) {
            return;
        }
        // Note that if settings.isSkipSimilarErrors() is false and sentSimilarErrorBefore(report) is true
        // then the error report may exist in the ArrayList tempErrorReportCache
        addForSending(report);
        if (sendAction == SendAction.ASK) {
            checkAndSendWithDialog(report);
        } else if (sendAction == SendAction.SILENT) {
            sendAndClear();
        }
    }

    private boolean isErrorSeverity(final IStatus status) {
        return status.matches(IStatus.ERROR);
    }

    @VisibleForTesting
    protected Settings readSettings() {
        return PreferenceInitializer.readSettings();
    }

    private static boolean hasPluginIdWhitelistedPrefix(IStatus status, List<String> whitelistedIdPrefixes) {
        String pluginId = status.getPlugin();
        for (String id : whitelistedIdPrefixes) {
            if (pluginId.startsWith(id)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSendingAllowedOnAction(SendAction sendAction) {
        return sendAction == SendAction.ASK || sendAction == SendAction.SILENT;
    }

    private static void insertDebugStacktraceIfEmpty(final IStatus status) {
        // TODO this code should probably go elsewhere later.
        if (status.getException() == null && status instanceof Status && SET_EXCEPTION != null) {
            Throwable syntetic = new RuntimeException(STAND_IN_MESSAGE);
            syntetic.fillInStackTrace();
            try {
                SET_EXCEPTION.invoke(status, syntetic);
            } catch (Exception e) {
                log(LogMessages.LOG_WARNING_REFLECTION_FAILED, e, SET_EXCEPTION);
            }
        }
    }

    private boolean sentSimilarErrorBefore(final ErrorReport report) {
        return tempErrorReportCache.contains(computeCacheKey(report));
    }

    private String computeCacheKey(final ErrorReport report) {
        return report.getStatus().getFingerprint();
    }

    private void addForSending(final ErrorReport report) {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                errorReports.add(report);
            }
        });
        // according to the logic of the logging() method, it is possible to
        // reach the addForSending() method if the Error has been previously logged
        if (!sentSimilarErrorBefore(report)) {
            tempErrorReportCache.add(computeCacheKey(report));
            writeErrorReportToFile(computeCacheKey(report));
        }
    }

    private void writeErrorReportToFile(String report) {
        try {
            // Serialize data object to the end of the file
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileNameAndLocation, true));

            // include the date
            out.writeObject(new GregorianCalendar());

            // write the object
            out.writeObject(report);
            out.flush();
            out.close();
        } catch (IOException e) {
        }
    }

    // populate the tempErrorReportCache from the file and create a new file excluding any
    // errors which are sufficiently old
    private void populateTempCacheFromFile() {
        try {
            FileInputStream fin = new FileInputStream(fileNameAndLocation);
            ObjectInputStream in = new ObjectInputStream(fin);
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tempFileNameAndLocation));

            // get the current time
            GregorianCalendar currentTime = new GregorianCalendar();
            GregorianCalendar archivedTime;
            String archivedError;

            while (fin.available() > 0) {
                // get the next date and string
                archivedTime = (GregorianCalendar) in.readObject();
                archivedError = (String) in.readObject();
                // if not too old, add to the cache and write to a new file
                if (errorDateNotTooOld(currentTime, archivedTime)) {
                    tempErrorReportCache.add((String) in.readObject());
                    out.writeObject(archivedTime);
                    out.writeObject(archivedError);
                }
            }
            in.close();
            fin.close();
            out.flush();
            out.close();

            // delete the old file and rename the temp file with the deleted file's name
            File file = new File(fileNameAndLocation);
            file.delete();
            file = new File(tempFileNameAndLocation);
            file.renameTo(new File(fileNameAndLocation));

        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private boolean errorDateNotTooOld(GregorianCalendar c1, GregorianCalendar c2) {
        long span, numberOfAllowedMS;
        numberOfAllowedMS = 1000 * 60 * 60 * 24 * maxErrorAgeInDays;

        if (c1.getTimeInMillis() > c2.getTimeInMillis()) {
            span = c1.getTimeInMillis() - c2.getTimeInMillis();
        } else {
            span = c2.getTimeInMillis() - c1.getTimeInMillis();
        }

        return span < numberOfAllowedMS;
    }

    @VisibleForTesting
    protected void checkAndSendWithDialog(final ErrorReport report) {
        // run on UI-thread to ensure that the observable list is not modified from another thread
        // and that the wizard is created on the UI-thread.
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (isDialogOpen) {
                    return;
                }
                isDialogOpen = true;
                ErrorReportWizard stacktraceWizard = new ErrorReportWizard(settings, errorReports);
                WizardDialog wizardDialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getShell(), stacktraceWizard);
                int open = wizardDialog.open();
                isDialogOpen = false;
                if (open != Dialog.OK) {
                    clear();
                    return;
                } else if (settings.getAction() == SendAction.IGNORE || settings.getAction() == SendAction.PAUSE_DAY
                        || settings.getAction() == SendAction.PAUSE_RESTART) {
                    // the user may have chosen to not to send events in the wizard. Respect this preference:
                    return;
                }
                sendAndClear();
            }
        });
    }

    private void sendAndClear() {
        sendList();
        clear();
    }

    private void sendList() {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                for (Object entry : errorReports) {
                    ErrorReport report = cast(entry);
                    sendStatus(report);
                }
            }
        });
    }

    @VisibleForTesting
    protected void sendStatus(final ErrorReport report) {
        // double safety. This is checked before elsewhere. But just to make sure...
        if (settings.getAction() == SendAction.IGNORE || settings.getAction() == SendAction.PAUSE_DAY
                || settings.getAction() == SendAction.PAUSE_RESTART) {
            return;
        }
        new UploadJob(report, settings, URI.create(settings.getServerUrl())).schedule();
    }

    private void clear() {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                errorReports.clear();
            }
        });
    }
}
