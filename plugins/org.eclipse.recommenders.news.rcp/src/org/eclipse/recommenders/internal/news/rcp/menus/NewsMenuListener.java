/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp.menus;

import static org.eclipse.recommenders.internal.news.rcp.FeedEvents.createFeedMessageReadEvent;
import static org.eclipse.recommenders.internal.news.rcp.MessageUtils.*;
import static org.eclipse.recommenders.internal.news.rcp.menus.MarkAsReadAction.*;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.recommenders.internal.news.rcp.BrowserUtils;
import org.eclipse.recommenders.internal.news.rcp.FeedDescriptor;
import org.eclipse.recommenders.internal.news.rcp.MessageUtils.MessageAge;
import org.eclipse.recommenders.internal.news.rcp.PollingResult;
import org.eclipse.recommenders.internal.news.rcp.StatusFeedMessage;
import org.eclipse.recommenders.internal.news.rcp.StatusFeedMessage.Status;
import org.eclipse.recommenders.internal.news.rcp.l10n.Messages;
import org.eclipse.recommenders.news.rcp.IFeedMessage;
import org.eclipse.recommenders.news.rcp.INewsService;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;

public class NewsMenuListener implements IMenuListener {
    private final EventBus eventBus;
    private final INewsService service;
    private Map<FeedDescriptor, PollingResult> messages;

    public NewsMenuListener(EventBus eventBus, INewsService service) {
        super();
        this.eventBus = eventBus;
        this.service = service;
    }

    public void setMessages(Map<FeedDescriptor, PollingResult> messages) {
        this.messages = messages;
    }

    @Override
    public void menuAboutToShow(IMenuManager manager) {
        for (Entry<FeedDescriptor, PollingResult> entry : messages.entrySet()) {
            String menuName = getMenuEntryTitle(entry.getKey().getName(), entry.getValue().getMessages());
            MenuManager menu = new MenuManager(menuName, entry.getKey().getId());
            if (entry.getKey().getIcon() != null) {
                // in Kepler: The method setImageDescriptor(ImageDescriptor) is undefined for the type MenuManager
                // menu.setImageDescriptor(ImageDescriptor.createFromImage(entry.getKey().getIcon()));
            }
            if (messages.entrySet().iterator().next().getValue().getStatus() == StatusFeedMessage.Status.OK) {
                groupEntries(menu, entry);
                addMarkAsReadAction(entry.getKey(), menu);
            } else {
                addStatusLabel(menu, entry.getValue().getStatus(), entry.getKey());
            }
            manager.add(menu);
        }
        if (messages.entrySet().iterator().next().getValue().getStatus() == StatusFeedMessage.Status.OK) {
            manager.add(newMarkAllAsReadAction(eventBus));
        } else {
            Action markAsReadAction = newMarkAllAsReadAction(null);
            markAsReadAction.setEnabled(false);
            manager.add(markAsReadAction);
        }
        manager.add(new Separator());
        manager.add(pollFeedsAction());
        manager.add(new Separator());
        manager.add(PreferenceAction.getInstance());
    }

    private void addMarkAsReadAction(FeedDescriptor feed, MenuManager menu) {
        menu.add(new Separator());
        menu.add(newMarkFeedAsReadAction(eventBus, feed));
    }

    private Action pollFeedsAction() {
        return new Action() {
            @Override
            public void run() {
                service.start();
            }

            @Override
            public String getText() {
                return Messages.LABEL_POLL_FEEDS;
            }
        };
    }

    private void groupEntries(MenuManager menu, Entry<FeedDescriptor, PollingResult> entry) {
        List<List<IFeedMessage>> groupedMessages = splitMessagesByAge(entry.getValue().getMessages());
        List<String> labels = ImmutableList.of(Messages.LABEL_TODAY, Messages.LABEL_YESTERDAY, Messages.LABEL_THIS_WEEK,
                Messages.LABEL_LAST_WEEK, Messages.LABEL_THIS_MONTH, Messages.LABEL_LAST_MONTH,
                Messages.LABEL_THIS_YEAR, Messages.LABEL_OLDER_ENTRIES, Messages.LABEL_UNDETERMINED_ENTRIES);
        for (int i = 0; i < MessageAge.values().length; i++) {
            if (!groupedMessages.get(i).isEmpty()) {
                if (!(groupedMessages.get(i).get(0) instanceof StatusFeedMessage)) {
                    addLabel(menu, labels.get(i));
                }
                addMessages(menu, groupedMessages.get(i), entry.getKey());
            }
        }
    }

    private void addMessages(MenuManager menu, List<IFeedMessage> messages, final FeedDescriptor feed) {
        for (final IFeedMessage message : messages) {
            Action action = new Action() {

                @Override
                public void run() {
                    BrowserUtils.openInDefaultBrowser(message.getUrl(), feed.getParameters());
                    eventBus.post(createFeedMessageReadEvent(message.getId()));
                }
            };
            if (!message.isRead()) {
                action.setText(MessageFormat.format(Messages.UNREAD_MESSAGE, message.getTitle()));
            } else {
                action.setText(MessageFormat.format(Messages.READ_MESSAGE_OR_FEED, message.getTitle()));
            }
            if (message instanceof StatusFeedMessage) {
                action.setEnabled(false);
                action.setText(message.getTitle());
            }
            menu.add(action);
        }
    }

    private void addLabel(MenuManager menu, String text) {
        Action action = new Action() {
        };
        action.setText(text);
        action.setEnabled(false);
        menu.add(new Separator());
        menu.add(action);
    }

    private void addStatusLabel(MenuManager menu, Status status, FeedDescriptor feed) {
        Action action = new Action() {
        };
        if (status == Status.FEEDS_NOT_POLLED_YET) {
            action.setText(Messages.FEED_NOT_POLLED_YET);
        } else if (status == Status.FEED_NOT_FOUND_AT_URL) {
            action.setText(MessageFormat.format(Messages.LOG_ERROR_CONNECTING_URL, feed.getUrl()));
        }
        action.setEnabled(false);
        menu.add(action);
    }

    private static String getMenuEntryTitle(String feedName, List<IFeedMessage> messages) {
        int unreadMessages = getUnreadMessagesNumber(messages);
        if (unreadMessages > 0) {
            return MessageFormat.format(Messages.UNREAD_FEED, feedName, unreadMessages);
        } else {
            return MessageFormat.format(Messages.READ_MESSAGE_OR_FEED, feedName);
        }
    }
}
