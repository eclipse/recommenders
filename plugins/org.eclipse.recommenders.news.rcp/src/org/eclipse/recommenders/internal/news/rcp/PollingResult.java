/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp;

import java.util.List;

import org.eclipse.recommenders.internal.news.rcp.StatusFeedMessage.Status;
import org.eclipse.recommenders.news.rcp.IFeedMessage;

import com.google.common.collect.Lists;

public final class PollingResult {
    private final Status status;
    private final List<IFeedMessage> messages;

    public PollingResult(List<IFeedMessage> messages) {
        if (containsInstance(messages, StatusFeedMessage.class)) {
            status = ((StatusFeedMessage) messages.get(0)).getStatus();
            if (messages.isEmpty()) {
                this.messages = messages;
            } else if (messages.size() == 1) {
                this.messages = Lists.newArrayList();
            } else {
                this.messages = messages.subList(1, messages.size() - 1);
            }

        } else {
            status = Status.OK;
            this.messages = Lists.newArrayList(messages);
        }
    }

    public Status getStatus() {
        return status;
    }

    public List<IFeedMessage> getMessages() {
        return messages;
    }

    private static <E> boolean containsInstance(List<E> list, Class<? extends E> clazz) {
        for (E e : list) {
            if (clazz.isInstance(e)) {
                return true;
            }
        }
        return false;
    }
}
