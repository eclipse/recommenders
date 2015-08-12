/**
 * Copyright (c) 2015 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Haftstein - initial API and implementation.
 */
package org.eclipse.recommenders.internal.news.rcp.notifications;

import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.commons.notifications.ui.AbstractUiNotification;
import org.eclipse.swt.graphics.Image;

import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;

@SuppressWarnings("restriction")
public abstract class Notification extends AbstractUiNotification {

    private EventBus bus;
    private boolean unhandled = true;
    private PopupNotification popup;

    public Notification(String eventId, EventBus bus) {
        super(eventId);
        this.bus = bus;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
        return Platform.getAdapterManager().getAdapter(this, adapter);
    }

    @Override
    public Image getNotificationImage() {
        return null;
    }

    @Override
    public Image getNotificationKindImage() {
        return null;
    }

    @Override
    public Date getDate() {
        return new Date();
    }

    public String getTitle() {
        return "Error Reporting";
    }

    public void setPopup(PopupNotification popup) {
        this.popup = popup;
    }

    public Optional<PopupNotification> getPopup() {
        return Optional.fromNullable(popup);
    }

    /**
     * Returns the list of actions for this notification. The first action (if present) will be selected as default
     * action for the notification and used in {@link #open()}
     */
    public abstract List<NotificationAction> getActions();

    @Override
    public void open() {
        List<NotificationAction> actions = getActions();
        if (!actions.isEmpty()) {
            actions.get(0).execute();
        }
    }

    public void fireEvent(Object event) {
        unhandled = false;
        PopupNotification popup = getPopup().orNull();
        if (popup != null) {
            popup.close();
        }
        bus.post(event);
    }

    /**
     * Called by the sink when the notification popup was closed.
     */
    public void close() {
        if (unhandled) {
            unhandled();
        }
    }

    public void unhandled() {
    }

}