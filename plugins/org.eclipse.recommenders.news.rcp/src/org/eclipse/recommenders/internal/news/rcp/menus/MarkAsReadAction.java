/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp.menus;

import static org.eclipse.recommenders.internal.news.rcp.FeedEvents.createFeedMessagesReadEvent;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.recommenders.internal.news.rcp.FeedDescriptor;
import org.eclipse.recommenders.internal.news.rcp.l10n.Messages;
import org.eclipse.recommenders.news.rcp.IFeedMessage;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;

public class MarkAsReadAction extends Action {
    private final EventBus eventBus;
    private Map<FeedDescriptor, List<IFeedMessage>> messages = Maps.newHashMap();

    public MarkAsReadAction(EventBus eventBus, Map<FeedDescriptor, List<IFeedMessage>> messages) {
        super();
        this.eventBus = eventBus;
        this.messages = messages;
    }

    @Override
    public void run() {
        Set<String> ids = Sets.newConcurrentHashSet();
        for (Entry<FeedDescriptor, List<IFeedMessage>> entry : messages.entrySet()) {
            for (final IFeedMessage message : entry.getValue()) {
                ids.add(message.getId());
            }
        }
        eventBus.post(createFeedMessagesReadEvent(ids));
    }

    @Override
    public String getText() {
        return Messages.LABEL_MARK_AS_READ;
    }
}
