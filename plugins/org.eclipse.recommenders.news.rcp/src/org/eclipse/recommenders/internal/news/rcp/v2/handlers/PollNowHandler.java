/**
 * Copyright (c) 2016 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Dorn - initial API and implementation.
 */
package org.eclipse.recommenders.internal.news.rcp.v2.handlers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.recommenders.internal.news.rcp.FeedDescriptor;
import org.eclipse.recommenders.internal.news.rcp.NewsRcpPreferences;
import org.eclipse.recommenders.internal.news.rcp.l10n.Messages;
import org.eclipse.recommenders.news.api.poll.INewsPollingService;
import org.eclipse.recommenders.news.api.poll.PollingPolicy;
import org.eclipse.recommenders.news.api.poll.PollingRequest;

public class PollNowHandler {

    @Execute
    public void execute(final NewsRcpPreferences preferences, final INewsPollingService newsService) {
        new Job(Messages.POLL_FEED_JOB_NAME) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                List<FeedDescriptor> feeds = preferences.getFeedDescriptors();
                List<PollingRequest> requests = new ArrayList<>();
                for (FeedDescriptor feed : feeds) {
                    if (!feed.isEnabled()) {
                        continue;
                    }
                    PollingRequest request = new PollingRequest(feed.getUri(), PollingPolicy.always());
                    requests.add(request);
                }
                newsService.poll(requests, null);
                return Status.OK_STATUS; // TODO Real error status
            }
        }.schedule();
    }
}
