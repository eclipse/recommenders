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
package org.eclipse.recommenders.internal.news.rcp.v2.toolbar;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.recommenders.internal.news.rcp.CommonImages;
import org.eclipse.recommenders.internal.news.rcp.Constants;
import org.eclipse.recommenders.internal.news.rcp.FeedDescriptor;
import org.eclipse.recommenders.internal.news.rcp.NewsRcpPreferences;
import org.eclipse.recommenders.internal.news.rcp.l10n.Messages;
import org.eclipse.recommenders.news.api.FeedItem;
import org.eclipse.recommenders.news.api.poll.INewsPollingService;
import org.eclipse.recommenders.news.api.poll.PollingPolicy;
import org.eclipse.recommenders.news.api.poll.PollingRequest;
import org.eclipse.recommenders.news.api.poll.PollingResult;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

@SuppressWarnings("restriction")
public class NewsToolControl {

    private static final class PlaceholderAction extends Action {

        private PlaceholderAction(String label) {
            super(label);
            setEnabled(false);
        }
    }

    private final class OpenContextMenuAction extends Action {

        @Override
        public void run() {
            toolBarManager.getContextMenuManager().getMenu().setVisible(true);
        }

        @Override
        public ImageDescriptor getImageDescriptor() {
            return CommonImages.RSS_ACTIVE;
        }
    }

    private final class ExecuteCommandAction extends Action {

        private final String commandId;

        private final Map<String, Object> commandParameters;

        private ExecuteCommandAction(String text, ImageDescriptor image, String commandId,
                Map<String, Object> commandParameters) {
            super(text, image);
            this.commandId = commandId;
            this.commandParameters = commandParameters;
        }

        @Override
        public void run() {
            ParameterizedCommand command = commandService.createCommand(commandId, commandParameters);
            handlerService.executeHandler(command);
        }
    }

    private final MToolControl modelElement;
    private final NewsRcpPreferences preferences;
    private final INewsPollingService newsService;
    private final ECommandService commandService;
    private final EHandlerService handlerService;

    private ToolBarManager toolBarManager;

    @Inject
    public NewsToolControl(MToolControl modelElement, NewsRcpPreferences preferences, INewsPollingService newsService,
            ECommandService commandService, EHandlerService handlerService) {
        this.modelElement = modelElement;
        this.preferences = preferences;
        this.newsService = newsService;
        this.commandService = commandService;
        this.handlerService = handlerService;
    }

    @Inject
    public void setEnabled(@Preference(Constants.PREF_NEWS_ENABLED) boolean enabled) {
        modelElement.setToBeRendered(enabled);
    }

    @PostConstruct
    public void createGui(Composite parent) {
        toolBarManager = new ToolBarManager(SWT.FLAT | SWT.HORIZONTAL);

        MenuManager contextMenu = new MenuManager();
        contextMenu.setRemoveAllWhenShown(true);
        contextMenu.addMenuListener(new IMenuListener() {

            @Override
            public void menuAboutToShow(IMenuManager menu) {
                List<FeedDescriptor> feeds = preferences.getFeedDescriptors();
                boolean feedMenuAdded = false;
                for (FeedDescriptor feed : feeds) {
                    if (!feed.isEnabled()) {
                        continue;
                    }
                    PollingRequest request = new PollingRequest(feed.getUri(), PollingPolicy.never());
                    Collection<PollingResult> results = newsService.poll(Collections.singletonList(request), null);
                    if (results.size() > 0) {
                        PollingResult result = Iterables.getOnlyElement(results);
                        MenuManager feedMenu = createFeedMenu(feed, result);
                        menu.add(feedMenu);
                        feedMenuAdded = true;
                    }
                }

                if (!feedMenuAdded) {
                    PlaceholderAction allFeedDisabledAction = new PlaceholderAction(Messages.LABEL_ALL_FEEDS_DISABLED);
                    menu.add(allFeedDisabledAction);
                }

                menu.add(new Separator());

                ExecuteCommandAction pollNowAction = new ExecuteCommandAction(Messages.LABEL_POLL_FEEDS,
                        CommonImages.REFRESH, "org.eclipse.recommenders.news.rcp.commands.pollNow",
                        Collections.<String, Object>emptyMap());
                menu.add(pollNowAction);

                menu.add(new Separator());

                ExecuteCommandAction preferencesAction = new ExecuteCommandAction(Messages.LABEL_PREFERENCES, null,
                        "org.eclipse.ui.window.preferences",
                        ImmutableMap.<String, Object>of("preferencePageId", Constants.PREF_PAGE_ID));
                menu.add(preferencesAction);
            }
        });

        toolBarManager.setContextMenuManager(contextMenu);
        toolBarManager.add(new OpenContextMenuAction());

        toolBarManager.createControl(parent);
    }

    private MenuManager createFeedMenu(FeedDescriptor feed, PollingResult result) {
        MenuManager feedMenu = new MenuManager();

        feedMenu.setMenuText(feed.getName());

        if (feed.getIcon() != null) {
            feedMenu.setImageDescriptor(ImageDescriptor.createFromImage(feed.getIcon()));
        }

        for (FeedItem item : result.getAllFeedItems()) {
            ExecuteCommandAction openBrowserAction = new ExecuteCommandAction(item.getTitle(), null,
                    "org.eclipse.ui.browser.openBrowser",
                    ImmutableMap.<String, Object>of("url", item.getUri().toString()));
            feedMenu.add(openBrowserAction);
        }

        return feedMenu;
    }

    @PreDestroy
    public void dispose() {
        if (toolBarManager != null) {
            toolBarManager.dispose();
        }
    }
}
