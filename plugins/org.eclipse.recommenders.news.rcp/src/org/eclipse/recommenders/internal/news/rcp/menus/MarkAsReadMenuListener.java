/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp.menus;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.recommenders.internal.news.rcp.FeedDescriptor;
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
        MarkAsReadAction action = new MarkAsReadAction(eventBus, messages);
        manager.add(new Separator());
        manager.add(action);
    }

    public void setMessages(Map<FeedDescriptor, List<IFeedMessage>> messages) {
        this.messages = messages;
    }
}
