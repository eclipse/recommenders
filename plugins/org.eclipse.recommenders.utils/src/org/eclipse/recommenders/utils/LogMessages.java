/**
 * Copyright (c) 2014 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.utils;

import static org.eclipse.core.runtime.IStatus.*;

import org.eclipse.recommenders.utils.Logs.DefaultLogMessage;
import org.osgi.framework.Bundle;

public final class LogMessages extends DefaultLogMessage {

    private static int code = 1;

    private static final Bundle BUNDLE = Logs.getBundle(LogMessages.class);

    public static final LogMessages LOG_WARNING_FAILED_TO_ACCESS_FIELD_REFLECTIVELY = new LogMessages(WARNING,
            "Could not access field {0} of {1} using reflection.");
    public static final LogMessages LOG_WARNING_FAILED_TO_ACCESS_CONSTRUCTOR_REFLECTIVELY = new LogMessages(WARNING,
            "Could not access constructor of {0} using reflection.");
    public static final LogMessages LOG_WARNING_FAILED_TO_ACCESS_METHOD_REFLECTIVELY = new LogMessages(WARNING,
            "Could not access method {0} of {1} using reflection.");

    public static final LogMessages LOG_WARNING_FAILED_TO_ACCESS_CLASS_REFLECTIVELY_LIMITED_FUNCTIONALITY = new LogMessages(
            WARNING, "Could not access class {0} using reflection. Functionality may be limited.");
    public static final LogMessages LOG_WARNING_FAILED_TO_ACCESS_FIELD_REFLECTIVELY_LIMITED_FUNCTIONALITY = new LogMessages(
            WARNING, "Could not access field {0} of {1} using reflection. Functionality may be limited.");
    public static final LogMessages LOG_WARNING_FAILED_TO_ACCESS_CONSTRUCTOR_REFLECTIVELY_LIMITED_FUNCTIONALITY = new LogMessages(
            WARNING, "Could not access constructor of {0} using reflection. Functionality may be limited.");
    public static final LogMessages LOG_WARNING_FAILED_TO_ACCESS_METHOD_REFLECTIVELY_LIMITED_FUNCTIONALITY = new LogMessages(
            WARNING, "Could not access method {0} of {1} using reflection. Functionality may be limited.");

    public static final LogMessages LOG_ERROR_CANNOT_CLOSE_RESOURCE = new LogMessages(ERROR,
            "Failed to close resource \u2018{0}\u2019");

    private LogMessages(int severity, String message) {
        super(severity, code++, message);
    }

    @Override
    public Bundle bundle() {
        return BUNDLE;
    }
}
