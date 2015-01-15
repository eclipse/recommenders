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
package org.eclipse.recommenders.internal.stacktraces.rcp;

import org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorReports;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.ModelFactory;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.Status;

class TestUtils {
    private static final ModelFactory FACTORY = ModelFactory.eINSTANCE;

    public static StackTraceElement[] buildTraceForClasses(String... classnames) {
        StackTraceElement[] elements = new StackTraceElement[classnames.length];
        for (int i = 0; i < classnames.length; i++) {
            elements[i] = new StackTraceElement(classnames[i], "anyMethod", classnames[i] + ".java", -1);
        }
        return elements;
    }

    public static Status createStatus(int severity, String pluginId, String message) {
        return createStatus(severity, pluginId, message, null);
    }

    public static Status createStatus(int severity, String pluginId, String message, java.lang.Throwable exception) {
        Status status = FACTORY.createStatus();
        status.setSeverity(severity);
        status.setPluginId(pluginId);
        status.setMessage(message);
        if (exception != null) {
            status.setException(ErrorReports.newThrowable(exception));
        }
        return status;
    }
}
