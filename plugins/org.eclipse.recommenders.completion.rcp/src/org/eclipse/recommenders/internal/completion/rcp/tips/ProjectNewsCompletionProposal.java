/**
 * Copyright (c) 2105 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Sewe - initial API and implementation.
 */
package org.eclipse.recommenders.internal.completion.rcp.tips;

import static java.text.MessageFormat.format;
import static java.util.concurrent.TimeUnit.DAYS;
import static org.eclipse.core.runtime.IStatus.WARNING;
import static org.eclipse.jface.viewers.StyledString.DECORATIONS_STYLER;
import static org.eclipse.recommenders.internal.completion.rcp.Constants.*;

import java.io.IOException;
import java.net.URI;
import java.util.Date;

import javax.inject.Inject;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.http.client.utils.DateUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.recommenders.completion.rcp.tips.AbstractCompletionTipProposal;
import org.eclipse.recommenders.internal.completion.rcp.Constants;
import org.eclipse.recommenders.internal.completion.rcp.Messages;
import org.eclipse.recommenders.net.Proxies;
import org.eclipse.recommenders.rcp.SharedImages;
import org.eclipse.recommenders.rcp.SharedImages.Images;
import org.eclipse.recommenders.rcp.utils.BrowserUtils;
import org.eclipse.recommenders.utils.Nullable;
import org.eclipse.recommenders.utils.Urls;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.google.common.base.Optional;

public class ProjectNewsCompletionProposal extends AbstractCompletionTipProposal {

    private static final URI PROJECT_NEWS_URI = Urls.parseURI("http://www.eclipse.org/recommenders/old/project-info/project-page-paragraph.html").get(); //$NON-NLS-1$



    @Inject
    @SuppressWarnings("restriction")
    public ProjectNewsCompletionProposal(SharedImages images) {
        Image image = images.getImage(Images.OBJ_LIGHTBULB);
        setImage(image);
        StyledString text = new StyledString(Messages.PROPOSAL_LABEL_READ_PROJECT_NEWS, DECORATIONS_STYLER);
        setStyledDisplayString(text);
        setSortString(text.getString());
    }

    @Override
    public boolean isApplicable(Date lastSeen) {
        if (isProjectNewsCheckRequired()) {
            CheckProjectNewsJob job = new CheckProjectNewsJob();
            job.setPriority(Job.DECORATE);
            job.schedule();
        }

        return isNewContentAvailable() && isMoreThanOneWeekAfter(lastSeen);
    }

    private boolean isProjectNewsCheckRequired() {
        Date lastChecked = getDatePreference(PREF_PROJECT_NEWS_LAST_CHECKED).orNull();
        return isMoreThanOneWeekAfter(lastChecked);
    }

    private boolean isNewContentAvailable() {
        Date lastRead = getDatePreference(PREF_PROJECT_NEWS_LAST_READ).orNull();
        if (lastRead == null) {
            return true;
        }
        Date lastModified = getDatePreference(PREF_PROJECT_NEWS_LAST_MODIFIED).orNull();
        if (lastModified == null) {
            return false;
        }
        return lastModified.after(lastRead);
    }

    private boolean isMoreThanOneWeekAfter(@Nullable Date then) {
        if (then == null) {
            return true;
        }
        Date now = new Date();
        return now.getTime() - then.getTime() > DAYS.toMillis(7);
    }

    @Override
    public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
        Date now = new Date();
        putDatePreference(PREF_PROJECT_NEWS_LAST_READ, now);

        BrowserUtils.openInDefaultBrowser(PROJECT_NEWS_URI.toString());
    }

    @Override
    protected IInformationControl createInformationControl(Shell parent, String statusLineText) {
        return new CompletionTipProposalInformationControl(parent, statusLineText, Messages.PROPOSAL_TOOLTIP_READ_PROJECT_NEWS);
    }

    private static Optional<Date> getDatePreference(String id) {
        long date = InstanceScope.INSTANCE.getNode(Constants.BUNDLE_NAME).getLong(id, -1);
        return Optional.fromNullable(date >= 0 ? new Date(date) : null);
    }

    private static void putDatePreference(String id, Date date) {
        InstanceScope.INSTANCE.getNode(Constants.BUNDLE_NAME).putLong(id, date.getTime());
    }

    private static class CheckProjectNewsJob extends Job {

        public CheckProjectNewsJob() {
            super("Checking project news page for updates");
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            Date now = new Date();
            putDatePreference(PREF_PROJECT_NEWS_LAST_CHECKED, now);

            return checkProjectNews();
        }

        private IStatus checkProjectNews() {
            try {
                Executor executor = Executor.newInstance();
                Request request = Request.Head(PROJECT_NEWS_URI);
                Response response = Proxies.proxy(executor, PROJECT_NEWS_URI).execute(request);
                HttpResponse httpResponse = response.returnResponse();
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                if (statusCode >= HttpStatus.SC_BAD_REQUEST) {
                    return new Status(WARNING, Constants.BUNDLE_NAME, format("Could not access project news page: {0}",
                            httpResponse.getStatusLine().getReasonPhrase()));
                }
                Header lastModifiedHeader = httpResponse.getFirstHeader(HttpHeaders.LAST_MODIFIED);
                if (lastModifiedHeader == null) {
                    return new Status(WARNING, Constants.BUNDLE_NAME, format("No Last-Modified header found"));
                }
                Date lastModified = DateUtils.parseDate(lastModifiedHeader.getValue());
                if (lastModified == null) {
                    return new Status(WARNING, Constants.BUNDLE_NAME, format(
                            "Could not parse Last-Modified header: {0}", lastModifiedHeader.getValue()));
                }
                putDatePreference(PREF_PROJECT_NEWS_LAST_MODIFIED, lastModified);
                return new Status(IStatus.INFO, Constants.BUNDLE_NAME, format(
                        "Project news last modified on {0,date} at {0,time}", lastModified));
            } catch (IOException e) {
                return new Status(WARNING, Constants.BUNDLE_NAME, format("Could not access project news page"), e);
            }
        }
    }
}
