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

public class ReportErrorNotification extends StacktracesUiNotification {

    public ReportErrorNotification() {
        super(Constants.NOTIFY_REPORT);
    }

    @Override
    public void open() {
    }

    @Override
    public String getDescription() {
        return "\"We noticed a new error event was logged. Such error events may reveal issues in the Eclipse codebase, and thus we kindly ask you to report them to eclipse.org.\"";
    }

    @Override
    public String getLabel() {
        return "An error was logged";
    }

}
