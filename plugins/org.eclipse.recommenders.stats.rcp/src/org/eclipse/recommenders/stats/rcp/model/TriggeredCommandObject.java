/**
 * Copyright (c) 2013 Timur Achmetow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Timur Achmetow - Initial API and implementation
 */
package org.eclipse.recommenders.stats.rcp.model;

import java.text.MessageFormat;

/**
 * Domain object for the UI tab "commands". This objects will be persisted to the eclipse workspace.
 * 
 * Example 1, which will be persisted: {"commandID":"org.eclipse.ui.window.showKeyAssist", "date":1374179225838}
 * 
 * Example 2: {"commandID":"org.eclipse.ui.edit.copy", "date":1374179228788}
 * 
 */
public class TriggeredCommandObject {
    public final String commandID;
    public final long sessionStarted;
    public int count;

    public TriggeredCommandObject(String commandID, long date) {
        this.commandID = commandID;
        sessionStarted = date;
    }

    /**
     * Collects the triggered command counts, for example: o.e.c.edit.cut : 5 times
     */
    public void incrementCounter() {
        count += 1;
    }

    @Override
    public String toString() {
        return MessageFormat.format("Command: ID: {0}", commandID);
    }
}
