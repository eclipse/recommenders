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

import java.util.Date;

import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.commons.notifications.ui.AbstractUiNotification;
import org.eclipse.swt.graphics.Image;

public abstract class ErrorReportUiNotification extends AbstractUiNotification {

    public ErrorReportUiNotification(String eventId) {
        super(eventId);
    }

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

    protected String getTitle() {
        return "Error Reporting";
    }
}
