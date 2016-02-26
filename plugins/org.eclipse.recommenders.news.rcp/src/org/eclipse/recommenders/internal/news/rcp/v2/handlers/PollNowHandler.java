
package org.eclipse.recommenders.internal.news.rcp.v2.handlers;

import java.net.URI;
import java.util.Collections;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.recommenders.internal.news.rcp.l10n.Messages;
import org.eclipse.recommenders.news.api.poll.INewsPollingService;
import org.eclipse.recommenders.news.api.poll.PollingPolicy;
import org.eclipse.recommenders.news.api.poll.PollingRequest;

public class PollNowHandler {

    @Execute
    public void execute(final INewsPollingService newsService) {
        new Job(Messages.POLL_FEED_JOB_NAME) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                PollingRequest request = new PollingRequest(URI.create("http://planeteclipse.org/planet/rss20.xml"),
                        PollingPolicy.always());
                newsService.poll(Collections.singletonList(request), null);
                return Status.OK_STATUS; // TODO Real error status
            }
        }.schedule();
    }
}
