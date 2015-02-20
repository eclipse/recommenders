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

import org.eclipse.recommenders.internal.stacktraces.rcp.Constants;

public class MoreInformationNotification extends StacktracesUiNotification {

    private String message;

    public MoreInformationNotification(String message) {
        super(Constants.NOTIFY_MORE_INFO);
        this.message = message;
    }

    @Override
    public void open() {
    }

    @Override
    public String getDescription() {
        return message;
    }

    @Override
    public String getLabel() {
        return "More information is available";
    }

}
