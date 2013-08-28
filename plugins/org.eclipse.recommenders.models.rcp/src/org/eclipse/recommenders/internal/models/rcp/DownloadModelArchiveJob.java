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

import static org.eclipse.core.runtime.Status.OK_STATUS;
import static org.eclipse.recommenders.internal.models.rcp.Constants.BUNDLE_ID;
import static org.eclipse.recommenders.internal.models.rcp.Messages.*;
import static org.eclipse.ui.internal.misc.StatusUtil.newStatus;

import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.recommenders.models.DownloadCallback;
import org.eclipse.recommenders.models.IModelRepository;
import org.eclipse.recommenders.models.ModelCoordinate;

import com.google.common.collect.Maps;

@SuppressWarnings("restriction")
public class DownloadModelArchiveJob extends Job {

    private IModelRepository repository;
    private ModelCoordinate mc;

    private Map<String, IProgressMonitor> downloads = Maps.newHashMap();

    public DownloadModelArchiveJob(IModelRepository repository, ModelCoordinate mc) {
        super(String.format(JOB_RESOLVING_MODEL, mc));
        this.repository = repository;
        this.mc = mc;
    }

    @Override
    protected IStatus run(final IProgressMonitor monitor) {
        try {
            monitor.beginTask(TASK_RESOLVING, IProgressMonitor.UNKNOWN);
            repository.resolve(mc, new DownloadCallback() {

                @Override
                public void downloadInitiated(String path) {
                    downloads.put(path, new SubProgressMonitor(monitor, 1));
                }

                @Override
                public void downloadProgressed(String path, long transferred, long total) {
                    IProgressMonitor submonitor = downloads.get(path);
                    String message = bytesToString(transferred) + "/" + bytesToString(total);
                    submonitor.subTask(message);
                    submonitor.worked(1);
                }

                @Override
                public void downloadSucceeded(String path) {
                    downloads.get(path).done();
                }

                @Override
                public void downloadFailed(String path) {
                    downloads.get(path).done();
                }
            }).get();
        } catch (Exception e) {
            return newStatus(BUNDLE_ID, "Failed to download " + mc, e);
        } finally {
            monitor.done();
        }
        return OK_STATUS;
    }

    private static String bytesToString(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "i";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}
