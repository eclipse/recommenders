/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp.menus;

import static org.eclipse.recommenders.internal.news.rcp.FeedEvents.createFeedMessageReadEvent;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.recommenders.internal.news.rcp.FeedDescriptor;
import org.eclipse.recommenders.internal.news.rcp.Messages;
import org.eclipse.recommenders.news.rcp.IFeedMessage;

import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;

public class MarkAsReadMenuListener implements IMenuListener {

    private final EventBus eventBus;
    private Map<FeedDescriptor, List<IFeedMessage>> messages = Maps.newHashMap();

    public MarkAsReadMenuListener(EventBus eventBus) {
        super();
        this.eventBus = eventBus;
    }

    @Override
    public void menuAboutToShow(IMenuManager manager) {
        Action action = new Action() {

            @Override
            public void run() {
                for (Entry<FeedDescriptor, List<IFeedMessage>> entry : messages.entrySet()) {
                    for (final IFeedMessage message : entry.getValue()) {
                        eventBus.post(createFeedMessageReadEvent(message.getId()));
                    }
                }
            }
        };
        action.setText(Messages.LABEL_MARK_AS_READ);
        manager.add(action);
    }

    public void setMessages(Map<FeedDescriptor, List<IFeedMessage>> messages) {
        this.messages = messages;
    }
}
