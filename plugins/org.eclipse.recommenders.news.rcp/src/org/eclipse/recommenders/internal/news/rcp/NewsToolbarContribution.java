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

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class NewsToolbarContribution extends WorkbenchWindowControlContribution {

    private final IRssService service;
    private final SharedImages images;
    private final EventBus eventBus;

    private UpdatingNewsAction updatingNewsAction;
    private MenuManager menuMgr;
    NoNewsMenuListener noNewsMenuListener;
    NewsMenuListener newsMenuListener;

    @Inject
    public NewsToolbarContribution(IRssService service, SharedImages images, EventBus eventBus) {
        this.service = service;
        this.images = images;
        this.eventBus = eventBus;
        eventBus.register(this);
        noNewsMenuListener = new NoNewsMenuListener();
        newsMenuListener = new NewsMenuListener(eventBus);
    }

    @Override
    protected Control createControl(Composite parent) {
        menuMgr = new MenuManager();
        updatingNewsAction = new UpdatingNewsAction();
        ToolBarManager manager = new ToolBarManager(SWT.FLAT | SWT.HORIZONTAL);
        manager.add(updatingNewsAction);
        manager.setContextMenuManager(menuMgr);
        return manager.createControl(parent);
    }

    @Subscribe
    public void handle(NewFeedItemsEvent event) {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

            @Override
            public void run() {
                updatingNewsAction.setAvailableNews();
                menuMgr.setRemoveAllWhenShown(true);
                menuMgr.removeMenuListener(noNewsMenuListener);
                Map<FeedDescriptor, List<IFeedMessage>> messages = service.getMessages(3);
                newsMenuListener.setMessages(messages);
                menuMgr.addMenuListener(newsMenuListener);
            }
        });
    }

    private class UpdatingNewsAction extends Action {
        private UpdatingNewsAction() {
            setNoAvailableNews();
        }

        @Override
        public void run() {
            setNoAvailableNews();

            Map<FeedDescriptor, List<IFeedMessage>> messages = service.getMessages(3);
            if (messages.isEmpty()) {
                new NoNewsNotificationPopup().open();
            } else {
                menuMgr.setRemoveAllWhenShown(true);
                menuMgr.removeMenuListener(noNewsMenuListener);
                newsMenuListener.setMessages(messages);
                menuMgr.addMenuListener(newsMenuListener);
                new NewsNotificationPopup(messages, eventBus).open();
            }
        }

        private void setNoAvailableNews() {
            setImageDescriptor(CommonImages.RSS_INACTIVE);
            setToolTipText(Messages.TOOLTIP_NO_NEW_MESSAGES);
            menuMgr.setRemoveAllWhenShown(true);
            menuMgr.removeMenuListener(newsMenuListener);
            menuMgr.addMenuListener(noNewsMenuListener);
        }

        private void setAvailableNews() {
            setImageDescriptor(CommonImages.RSS_ACTIVE);
            setToolTipText(Messages.TOOLTIP_NEW_MESSAGES);
            menuMgr.setRemoveAllWhenShown(true);
            menuMgr.removeMenuListener(noNewsMenuListener);
            Map<FeedDescriptor, List<IFeedMessage>> messages = service.getMessages(3);
            newsMenuListener.setMessages(messages);
            menuMgr.addMenuListener(newsMenuListener);
        }
    }

}
