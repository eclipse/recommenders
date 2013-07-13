/**
 * Copyright (c) 2013 Timur Achmetow
 * All rights reserved. Thiimport org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Display;
pse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Timur Achmetow - Initial API and implementation
 */
package org.eclipse.recommenders.internal.completion.rcp.sandbox;

import java.text.MessageFormat;

public class TriggeredCommand {
    public final String commandID;
    public final String name;
    public final String description;
    public final long date;
    public int count;
    public final String categoryName;
    public final String categoryID;

    public TriggeredCommand(String commandID, String name, String description, long date, int count,
            String categoryName, String categoryID) {
        this.commandID = commandID;
        this.name = name;
        this.description = description;
        this.date = date;
        this.count = count;
        this.categoryName = categoryName;
        this.categoryID = categoryID;
    }

    @Override
    public String toString() {
        return MessageFormat.format("Command: {0}, ID: {1}", name, commandID);
    }
}
