/**
 * Copyright (c) 2015 Codetrails GmbH. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Johannes Dorn - initial API and implementation.
 */
package org.eclipse.recommenders.internal.news.rcp;

import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.recommenders.internal.news.rcp.FeedEvents.AllReadEvent;
import org.eclipse.recommenders.internal.news.rcp.FeedEvents.FeedMessageReadEvent;
import org.eclipse.recommenders.internal.news.rcp.FeedEvents.FeedReadEvent;
import org.eclipse.recommenders.internal.news.rcp.FeedEvents.NewFeedItemsEvent;
import org.eclipse.recommenders.internal.news.rcp.l10n.Messages;
import org.eclipse.recommenders.internal.news.rcp.menus.NewsMenuListener;
import org.eclipse.recommenders.internal.news.rcp.menus.NoNewsMenuListener;
import org.eclipse.recommenders.news.rcp.IFeedMessage;
import org.eclipse.recommenders.news.rcp.INewsService;
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
    private final NewsMenuListener newsMenuListener;
    private final NoNewsMenuListener noNewsMenuListener = new NoNewsMenuListener();
    private final NewsRcpPreferences preferences;
    private UpdatingNewsAction updatingNewsAction;
    private MenuManager menuManager;

    @Inject
    public NewsToolbarContribution(INewsService service, EventBus eventBus, NewsRcpPreferences preferences) {
        this.service = service;
        this.preferences = preferences;
        eventBus.register(this);
        newsMenuListener = new NewsMenuListener(eventBus);
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
            }
        });
    }

    @Subscribe
    public void handleAllRead(AllReadEvent event) {
        updatingNewsAction.checkForNews();
    }

    @Subscribe
    public void handleFeedRead(FeedReadEvent event) {
        updatingNewsAction.checkForNews();
    }

    @Subscribe
    public void handleMessageRead(FeedMessageReadEvent event) {
        updatingNewsAction.checkForNews();
    }

    private class UpdatingNewsAction extends Action {
        Map<FeedDescriptor, List<IFeedMessage>> messages = Maps.newHashMap();

        private UpdatingNewsAction() {
            setNoAvailableNews();
        }

        @Override
        public void run() {
            setNoAvailableNews();
            messages = service.getMessages(Constants.COUNT_PER_FEED);
            menuManager.getMenu().setVisible(true);
            if (!messages.isEmpty() && MessageUtils.containsUnreadMessages(messages)) {
                setAvailableNews();
            }
        }

        @SuppressWarnings("unchecked")
        private void setNoAvailableNews() {
            setImageDescriptor(CommonImages.RSS_INACTIVE);
            setToolTipText(Messages.TOOLTIP_NO_NEW_MESSAGES);
            clearMenu();
            messages = service.getMessages(Constants.COUNT_PER_FEED);
            if (!messages.isEmpty() && !MessageUtils.containsUnreadMessages(messages)) {
                clearMenu();
                setNewsMenu(messages);
                return;
            }
            HashMap<FeedDescriptor, List<IFeedMessage>> groupedMessages = Maps.newHashMap();
            for (FeedDescriptor feed : preferences.getFeedDescriptors()) {
                try {
                    FeedMessage message = new FeedMessage(Constants.FEED_NOT_POLLED_YET, null, "", //$NON-NLS-1$
                            Messages.FEED_NOT_POLLED_YET, new URL("http://eclipse.org")); //$NON-NLS-1$
                    message.setRead(true);
                    groupedMessages.put(feed, (List) Lists.newArrayList(message));
                } catch (Exception e) {
                    // never happen
                }
            }
            noNewsMenuListener.setMessages(groupedMessages);
            menuManager.addMenuListener(noNewsMenuListener);
        }

        private void setAvailableNews() {
            messages = service.getMessages(Constants.COUNT_PER_FEED);
            if (messages.isEmpty() || !MessageUtils.containsUnreadMessages(messages)) {
                return;
            }
            setImageDescriptor(CommonImages.RSS_ACTIVE);
            setToolTipText(MessageFormat.format(Messages.TOOLTIP_NEW_MESSAGES,
                    MessageUtils.getUnreadMessagesNumber(MessageUtils.mergeMessages(messages))));
            clearMenu();
            setNewsMenu(messages);
        }

        private void clearMenu() {
            menuManager.setRemoveAllWhenShown(true);
            menuManager.removeMenuListener(newsMenuListener);
            menuManager.removeMenuListener(noNewsMenuListener);
        }

        private void setNewsMenu(Map<FeedDescriptor, List<IFeedMessage>> messages) {
            newsMenuListener.setMessages(messages);
            menuManager.addMenuListener(newsMenuListener);
        }

        public void checkForNews() {
            messages = service.getMessages(Constants.COUNT_PER_FEED);
            if (messages.isEmpty() || !MessageUtils.containsUnreadMessages(messages)) {
                setNoAvailableNews();
            }
        }
    }
}
