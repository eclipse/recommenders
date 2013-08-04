package org.eclipse.recommenders.stats.rcp;

import java.text.MessageFormat;

/**
 * Domain object for the UI tab "commands". This objects will be persisted to the eclipse workspace.
 * 
 * Example 1, which will be persisted:
 * {"commandID":"org.eclipse.ui.window.showKeyAssist","name":"Show Key Assist","description"
 * :"Show the key assist dialog",
 * "date":1374179225838,"categoryName":"Window","categoryID":"org.eclipse.ui.category.window"}
 * 
 * Example 2: {"commandID":"org.eclipse.ui.edit.copy","name":"Copy","description":"Copy the selection to the clipboard",
 * "date":1374179228788,"categoryName":"Edit","categoryID":"org.eclipse.ui.category.edit"}
 * 
 */
public class TriggeredCommand {
    public final String commandID;
    public final String name;
    public final String description;
    public final long sessionStarted;
    public int count;

    public TriggeredCommand(String commandID, String name, String description, long date) {
        this.commandID = commandID;
        this.name = name;
        this.description = description;
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
        return MessageFormat.format("Command: {0}, ID: {1}", name, commandID);
    }
}
