/**
 * Copyright (c) 2015 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.news.rcp.notifications;

public abstract class NotificationAction {
    private final String name;

    public NotificationAction(String name) {
        this.name = name;
    }

    public abstract void execute();

    public String getName() {
        return name;
    }
}