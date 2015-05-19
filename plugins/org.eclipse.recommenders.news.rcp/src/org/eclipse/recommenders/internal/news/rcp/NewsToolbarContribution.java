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

import javax.inject.Inject;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.recommenders.internal.news.rcp.FeedEvents.NewFeedItemsEvent;
import org.eclipse.recommenders.internal.news.rcp.menus.MarkAsReadMenuListener;
import org.eclipse.recommenders.internal.news.rcp.menus.NewsMenuListener;
import org.eclipse.recommenders.internal.news.rcp.menus.NoNewsMenuListener;
import org.eclipse.recommenders.internal.news.rcp.notifications.CommonImages;
import org.eclipse.recommenders.internal.news.rcp.notifications.NewsNotificationPopup;
import org.eclipse.recommenders.internal.news.rcp.notifications.NoNewsNotificationPopup;
import org.eclipse.recommenders.news.rcp.IFeedMessage;
import org.eclipse.recommenders.news.rcp.IRssService;
import org.eclipse.recommenders.rcp.SharedImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class NewsToolbarContribution extends WorkbenchWindowControlContribution {

    private final IRssService service;
    private final EventBus eventBus;

    private UpdatingNewsAction updatingNewsAction;
    private MenuManager menuManager;
    private NoNewsMenuListener noNewsMenuListener;
    private NewsMenuListener newsMenuListener;
    private MarkAsReadMenuListener markAsReadMenuListener;

    @Inject
    public NewsToolbarContribution(IRssService service, SharedImages images, EventBus eventBus) {
        this.service = service;
        this.eventBus = eventBus;
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
            }
        });
    }

    private class UpdatingNewsAction extends Action {
        Map<FeedDescriptor, List<IFeedMessage>> messages = Maps.newHashMap();

        private UpdatingNewsAction() {
            setNoAvailableNews();
        }

        @Override
        public void run() {
            setNoAvailableNews();

            messages = service.getMessages(3);
            if (messages.isEmpty()) {
                new NoNewsNotificationPopup().open();
                menuManager.getMenu().setVisible(true);
            } else {
                menuManager.setRemoveAllWhenShown(true);
                menuManager.removeMenuListener(noNewsMenuListener);
                newsMenuListener.setMessages(messages);
                menuManager.addMenuListener(newsMenuListener);
                menuManager.getMenu().setVisible(true);
                markAsReadMenuListener.setMessages(messages);
                menuManager.addMenuListener(markAsReadMenuListener);
                new NewsNotificationPopup(messages, eventBus).open();
            }
        }

        private void setNoAvailableNews() {
            setImageDescriptor(CommonImages.RSS_INACTIVE);
            setToolTipText(Messages.TOOLTIP_NO_NEW_MESSAGES);
            menuManager.setRemoveAllWhenShown(true);
            menuManager.removeMenuListener(newsMenuListener);
            menuManager.removeMenuListener(markAsReadMenuListener);
            menuManager.addMenuListener(noNewsMenuListener);
        }

        private void setAvailableNews() {
            if (messages.size() < 1) {
                return;
            }
            setImageDescriptor(CommonImages.RSS_ACTIVE);
            setToolTipText(Messages.TOOLTIP_NEW_MESSAGES);
            menuManager.setRemoveAllWhenShown(true);
            menuManager.removeMenuListener(noNewsMenuListener);
            newsMenuListener.setMessages(messages);
            menuManager.addMenuListener(newsMenuListener);
            markAsReadMenuListener.setMessages(messages);
            menuManager.addMenuListener(markAsReadMenuListener);
        }
    }

}
