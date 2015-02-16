/**
 * Copyright (c) 2015 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Dorn - initial API and implementation.
 */
package org.eclipse.recommenders.internal.rcp.news;

import static java.text.MessageFormat.format;
import static org.eclipse.core.runtime.IStatus.WARNING;

import java.io.IOException;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.recommenders.internal.rcp.Constants;
import org.eclipse.recommenders.net.Proxies;

public class ProjectNewsFetcher extends Job {

    private final URI feedUri;

    public ProjectNewsFetcher(URI feedUri) {
        super("Fetching project news feed");
        this.feedUri = feedUri;
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        try {
            Executor executor = Executor.newInstance();
            Request request = Request.Head(feedUri);
            Response response = Proxies.proxy(executor, feedUri).execute(request);
            HttpResponse httpResponse = response.returnResponse();
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode >= HttpStatus.SC_BAD_REQUEST) {
                return new Status(WARNING, Constants.BUNDLE_ID, format("Could not access project news feed: {0}",
                        httpResponse.getStatusLine().getReasonPhrase()));
            }
            return Status.OK_STATUS;
        } catch (IOException e) {
            return new Status(WARNING, Constants.BUNDLE_ID, format("Could not access project news feed"), e);
        }
    }
}
