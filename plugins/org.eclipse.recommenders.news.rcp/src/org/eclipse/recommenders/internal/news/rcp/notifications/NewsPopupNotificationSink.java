/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp.notifications;

import org.eclipse.mylyn.commons.notifications.core.NotificationSink;
import org.eclipse.mylyn.commons.notifications.core.NotificationSinkEvent;

public class NewsPopupNotificationSink extends NotificationSink {

    @Override
    public void notify(NotificationSinkEvent event) {
        System.out.println("something should happen here");

    }

}
