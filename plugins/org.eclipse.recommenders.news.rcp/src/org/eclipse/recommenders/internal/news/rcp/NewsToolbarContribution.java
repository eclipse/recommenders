/**
 * Copyright (c) 2015 Codetrails GmbH. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Johannes Dorn - initial API and implementation.
 */
package org.eclipse.recommenders.internal.news.rcp;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.recommenders.internal.news.rcp.FeedEvents.NewFeedItemsEvent;
import org.eclipse.recommenders.internal.news.rcp.l10n.Messages;
import org.eclipse.recommenders.internal.news.rcp.menus.MarkAsReadMenuListener;
import org.eclipse.recommenders.internal.news.rcp.menus.NewsMenuListener;
import org.eclipse.recommenders.internal.news.rcp.menus.NoNewsMenuListener;
import org.eclipse.recommenders.internal.news.rcp.notifications.CommonImages;
import org.eclipse.recommenders.internal.news.rcp.notifications.NewsNotificationPopup;
import org.eclipse.recommenders.news.rcp.IFeedMessage;
import org.eclipse.recommenders.news.rcp.INewsService;
import org.eclipse.recommenders.rcp.SharedImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class NewsToolbarContribution extends WorkbenchWindowControlContribution {

    private final INewsService service;
    private static final int COUNT_PER_FEED = 20;
    private final EventBus eventBus;
    private final NewsRcpPreferences preferences;
    private final NewsFeedProperties properties;

    private UpdatingNewsAction updatingNewsAction;
    private MenuManager menuManager;
    private NoNewsMenuListener noNewsMenuListener;
    private NewsMenuListener newsMenuListener;
    private MarkAsReadMenuListener markAsReadMenuListener;

    @Inject
    public NewsToolbarContribution(INewsService service, SharedImages images, EventBus eventBus,
            NewsRcpPreferences preferences, NewsFeedProperties properties) {
        this.service = service;
        this.eventBus = eventBus;
        this.preferences = preferences;
        this.properties = properties;
        eventBus.register(this);
        noNewsMenuListener = new NoNewsMenuListener();
        newsMenuListener = new NewsMenuListener(eventBus);
        markAsReadMenuListener = new MarkAsReadMenuListener(eventBus);
    }

    @Override
    protected Control createControl(Composite parent) {
        menuManager = new MenuManager();
        updatingNewsAction = new UpdatingNewsAction();
        ToolBarManager manager = new ToolBarManager(SWT.FLAT | SWT.HORIZONTAL);
        manager.add(updatingNewsAction);
        manager.setContextMenuManager(menuManager);
        return manager.createControl(parent);
    }

    @Subscribe
    public void handle(NewFeedItemsEvent event) {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

            @Override
            public void run() {
                updatingNewsAction.setAvailableNews();
                if (preferences.isNotificationEnabled()
                        && !removeEmptyFeeds(getLatestMessages(service.getMessages(COUNT_PER_FEED))).isEmpty()) {
                    new NewsNotificationPopup(removeEmptyFeeds(getLatestMessages(service.getMessages(COUNT_PER_FEED))),
                            eventBus, service).open();
                }
            }
        });
    }

    private Map<FeedDescriptor, List<IFeedMessage>> removeEmptyFeeds(Map<FeedDescriptor, List<IFeedMessage>> map) {
        Map<FeedDescriptor, List<IFeedMessage>> result = Maps.newHashMap();
        for (Map.Entry<FeedDescriptor, List<IFeedMessage>> entry : map.entrySet()) {
            if (!entry.getValue().isEmpty()) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    private Map<FeedDescriptor, List<IFeedMessage>> getLatestMessages(
            Map<FeedDescriptor, List<IFeedMessage>> messages) {
        Map<FeedDescriptor, List<IFeedMessage>> result = Maps.newHashMap();
        for (Entry<FeedDescriptor, List<IFeedMessage>> entry : messages.entrySet()) {
            result.put(entry.getKey(), updateMessages(entry));
        }
        return result;
    }

    private List<IFeedMessage> updateMessages(Entry<FeedDescriptor, List<IFeedMessage>> entry) {
        List<IFeedMessage> iFeedMessages = Lists.newArrayList();
        for (IFeedMessage message : entry.getValue()) {
            if (message.getDate()
                    .after(properties.getDates(Constants.FILENAME_FEED_DATES).get(entry.getKey().getId()))) {
                iFeedMessages.add(message);
            }
        }
        return iFeedMessages;
    }

    private class UpdatingNewsAction extends Action {
        Map<FeedDescriptor, List<IFeedMessage>> messages = Maps.newHashMap();

        private UpdatingNewsAction() {
            setNoAvailableNews();
        }

        @Override
        public void run() {
            setNoAvailableNews();
            messages = service.getMessages(COUNT_PER_FEED);
            menuManager.getMenu().setVisible(true);
            if (!messages.isEmpty()) {
                setAvailableNews();
            }
        }

        private void setNoAvailableNews() {
            setImageDescriptor(CommonImages.RSS_INACTIVE);
            setToolTipText(Messages.TOOLTIP_NO_NEW_MESSAGES);
            clearMenu();
            menuManager.addMenuListener(noNewsMenuListener);
        }

        private void setAvailableNews() {
            messages = service.getMessages(COUNT_PER_FEED);
            if (messages.isEmpty()) {
                return;
            }
            setImageDescriptor(CommonImages.RSS_ACTIVE);
            setToolTipText(Messages.TOOLTIP_NEW_MESSAGES);
            clearMenu();
            setNewsMenu(messages);
        }

        private void clearMenu() {
            menuManager.setRemoveAllWhenShown(true);
            menuManager.removeMenuListener(noNewsMenuListener);
            menuManager.removeMenuListener(newsMenuListener);
            menuManager.removeMenuListener(markAsReadMenuListener);
        }

        private void setNewsMenu(Map<FeedDescriptor, List<IFeedMessage>> messages) {
            newsMenuListener.setMessages(messages);
            menuManager.addMenuListener(newsMenuListener);
            markAsReadMenuListener.setMessages(messages);
            menuManager.addMenuListener(markAsReadMenuListener);
        }
    }

}
