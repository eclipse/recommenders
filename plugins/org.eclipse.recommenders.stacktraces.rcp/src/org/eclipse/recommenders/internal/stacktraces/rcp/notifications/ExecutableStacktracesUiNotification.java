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

import java.util.List;

public abstract class ExecutableStacktracesUiNotification extends StacktracesUiNotification {
    public ExecutableStacktracesUiNotification(String eventId) {
        super(eventId);
    }

    public static abstract class Action {
        private final String name;

        public Action(String name) {
            this.name = name;
        }

        public abstract void execute();

        public String getName() {
            return name;
        }
    }

    @Override
    public final void open() {
        if (!getActions().isEmpty()) {
            getActions().get(0).execute();
        }
    }

    public abstract List<Action> getActions();

}
