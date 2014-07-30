/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.models.rcp;

import java.text.MessageFormat;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.recommenders.models.DownloadCallback;

import com.google.common.collect.Maps;

final class ModelArchiveDownloadCallback extends DownloadCallback {
    private static final int MAXIMUM_NUMBER_OF_DOWNLOAD_TASKS_PER_JOB = 2;

    private final int workUnitsPerDownloadTask;
    private final Map<String, IProgressMonitor> downloads = Maps.newHashMap();
    private final IProgressMonitor monitor;
    private boolean archiveDownloaded;
    private long lastTransferred;
    private int finishedWorkUnits;

    ModelArchiveDownloadCallback(IProgressMonitor monitor, int totalWorkUnits) {
        this.monitor = monitor;
        workUnitsPerDownloadTask = totalWorkUnits / MAXIMUM_NUMBER_OF_DOWNLOAD_TASKS_PER_JOB;
    }

    public void updateMonitorWorkForSkippedDownloadTasks() {
        // zero to two tasks might be executed but all work is done.
        int skippedDownloadTasks = MAXIMUM_NUMBER_OF_DOWNLOAD_TASKS_PER_JOB - downloads.size();
        // Skipped tasks count as work to indicate correct progress.
        for (int i = 0; i < skippedDownloadTasks; i++) {
            monitor.worked(workUnitsPerDownloadTask);
        }
    }

    @Override
    public synchronized void downloadInitiated(String path) {
        SubProgressMonitor subProgressMonitor = new SubProgressMonitor(monitor, workUnitsPerDownloadTask);
        subProgressMonitor.beginTask(path, workUnitsPerDownloadTask);
        downloads.put(path, subProgressMonitor);
        lastTransferred = 0;
        finishedWorkUnits = 0;
    }

    @Override
    public synchronized void downloadProgressed(String path, long transferred, long total) {
        IProgressMonitor submonitor = downloads.get(path);
        String message;
        // If no total size is known, total might be -1.
        if (total >= transferred) {
            long newTransferred = transferred - lastTransferred;
            lastTransferred = transferred;
            int workUnits = calculateWorkUnitsForDownloadProgress(newTransferred, total);
            finishedWorkUnits += workUnits;
            submonitor.worked(workUnits);
            message = createProgressMessage(transferred, total);
        } else {
            message = createProgressMessage(transferred);
        }
        submonitor.subTask(message);
    }

    private String createProgressMessage(long transferred) {
        return MessageFormat.format(Messages.JOB_DOWNLOAD_TRANSFERRED_SIZE,
                FileUtils.byteCountToDisplaySize(transferred));
    }

    private String createProgressMessage(long transferred, long total) {
        return MessageFormat.format(Messages.JOB_DOWNLOAD_TRANSFERRED_TOTAL_SIZE,
                FileUtils.byteCountToDisplaySize(transferred), FileUtils.byteCountToDisplaySize(total));
    }

    private int calculateWorkUnitsForDownloadProgress(long newTransferred, long total) {
        double amount = (double) newTransferred / total;
        int workUnits = (int) (workUnitsPerDownloadTask * amount);
        return workUnits;
    }

    @Override
    public synchronized void downloadSucceeded(String path) {
        IProgressMonitor submonitor = downloads.get(path);
        finishMonitorWork(submonitor);
        submonitor.done();
        setArchiveDownloaded(true);
    }

    @Override
    public synchronized void downloadFailed(String path) {
        IProgressMonitor submonitor = downloads.get(path);
        finishMonitorWork(submonitor);
        submonitor.done();
    }

    private void finishMonitorWork(IProgressMonitor submonitor) {
        int unfinishedWorkUnits = workUnitsPerDownloadTask - finishedWorkUnits;
        if (unfinishedWorkUnits > 0) {
            submonitor.worked(unfinishedWorkUnits);
        }
    }

    public boolean isArchiveDownloaded() {
        return archiveDownloaded;
    }

    public void setArchiveDownloaded(boolean archiveDownloaded) {
        this.archiveDownloaded = archiveDownloaded;
    }
}
