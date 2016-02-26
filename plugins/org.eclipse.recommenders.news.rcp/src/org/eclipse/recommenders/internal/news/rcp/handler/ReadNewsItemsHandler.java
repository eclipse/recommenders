/**
 * Copyright (c) 2016 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Sewe - initial API and implementation.
 */
package org.eclipse.recommenders.internal.news.rcp.handler;

import static java.lang.Boolean.TRUE;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.recommenders.internal.news.rcp.command.Commands;
import org.eclipse.recommenders.internal.news.rcp.l10n.Messages;
import org.eclipse.recommenders.news.api.NewsItem;
import org.eclipse.recommenders.news.api.read.IReadItemsStore;

import com.google.common.collect.ImmutableMap;

@SuppressWarnings("restriction")
public class ReadNewsItemsHandler {

    private final ECommandService commandService;
    private final EHandlerService handlerService;
    private final UISynchronize uiSynchronize;

    @Inject
    public ReadNewsItemsHandler(ECommandService commandService, EHandlerService handlerService, UISynchronize uiSynchronize) {
        this.commandService = commandService;
        this.handlerService = handlerService;
        this.uiSynchronize = uiSynchronize;
    }

    @Execute
    public void execute(final IReadItemsStore readItemsStore,
            @Named(Commands.COMMAND_PARAM_READ_NEWS_ITEMS_NEWS_ITEMS) final List<NewsItem> newsItems,
            @Optional @Nullable @Named(Commands.COMMAND_PARAM_READ_NEWS_ITEMS_OPEN_BROWSER) final Boolean openBrowser) {
        if (newsItems.isEmpty()) {
            return;
        }

        new Job(Messages.JOB_NAME_READ_NEWS_ITEMS) {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                SubMonitor progress = SubMonitor.convert(monitor, newsItems.size());
                for (NewsItem newsItem : newsItems) {
                    readNewsItem(newsItem, progress.newChild(1));
                }
                return Status.OK_STATUS;
            }

            private void readNewsItem(NewsItem newsItem, SubMonitor monitor) {
                SubMonitor progress = SubMonitor.convert(monitor, 2);
                if (TRUE.equals(openBrowser)) {
                    openBrowser(newsItem);
                }
                progress.setWorkRemaining(1);

                readItemsStore.markAsRead(newsItem);
                progress.worked(1);
            }

            private void openBrowser(final NewsItem newsItem) {
                uiSynchronize.asyncExec(new Runnable() {

                    @Override
                    public void run() {
                        ParameterizedCommand command = commandService.createCommand(Commands.COMMAND_ID_OPEN_BROWSER,
                                ImmutableMap.<String, Object>of(Commands.COMMAND_PARAM_OPEN_BROWSER_URL,
                                        newsItem.getUri().toString()));
                        handlerService.executeHandler(command);
                    }
                });
            }
        }.schedule();
    }
}
