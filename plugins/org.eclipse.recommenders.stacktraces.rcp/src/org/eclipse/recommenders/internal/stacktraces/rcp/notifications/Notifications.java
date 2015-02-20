/**
 * Copyright (c) 2014 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Haftstein - initial API and implementation.
 */
package org.eclipse.recommenders.internal.stacktraces.rcp.notifications;

import org.eclipse.mylyn.commons.notifications.core.AbstractNotification;
import org.eclipse.mylyn.commons.notifications.ui.NotificationsUi;

import com.google.common.collect.Lists;

public class Notifications {

    /**
     * Shortcut to {@link NotificationsUi#getService()}.notify()
     */
    public static void notify(AbstractNotification... notifications) {
        NotificationsUi.getService().notify(Lists.newArrayList(notifications));
    }
}
