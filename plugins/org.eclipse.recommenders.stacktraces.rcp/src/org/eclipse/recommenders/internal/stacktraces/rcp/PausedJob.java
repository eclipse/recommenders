/**
 * Copyright (c) 2014 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.stacktraces.rcp;

import static org.eclipse.recommenders.internal.stacktraces.rcp.LogMessages.REPORTING_ERROR;
import static org.eclipse.recommenders.internal.stacktraces.rcp.model.RememberSendAction.RESTART;
import static org.eclipse.recommenders.net.Proxies.proxy;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.Settings;
import org.eclipse.recommenders.utils.Logs;

/**
 * Responsible to anonymize (if requested) and send an error report.
 */
public class PausedJob extends Job {

    private Executor executor;
    private Settings settings;

    public PausedJob(Settings settings) {
        super("Checking Error Reports Server Status");
        this.settings = settings;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        monitor.beginTask(Messages.UPLOADJOB_TASKNAME, 1);
        try {
            executor = Executor.newInstance();
            String url = settings.getServerUrl();
            URI uri = URI.create(url);
            Request request = Request.Head(url);
            Response response = proxy(executor, uri).execute(request);
            HttpResponse httpResponse = response.returnResponse();
            int code = httpResponse.getStatusLine().getStatusCode();
            if (code != 200) {
                settings.setRememberSendAction(RESTART);
                PreferenceInitializer.saveSettings(settings);
            }
        } catch (Exception e) {
            Logs.log(REPORTING_ERROR, e);
        }
        return Status.OK_STATUS;
    }
}
