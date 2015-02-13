package org.eclipse.recommenders.internal.rcp;

import static org.eclipse.recommenders.internal.rcp.Constants.COMMAND_HREF_ID;
import static org.eclipse.recommenders.internal.rcp.LogMessages.LOG_ERROR_FAILED_TO_EXECUTE_COMMAND;

import java.text.MessageFormat;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.recommenders.utils.Logs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

public class RootPreferenceLink implements Comparable<RootPreferenceLink> {

    private final int priority;
    private final String text;
    private final String commandId;
    private final String uri;
    private final Image icon;

    public RootPreferenceLink(int priority, String commandId, String text, String uri, Image icon) {
        this.priority = priority;
        this.text = text;
        this.commandId = commandId;
        this.uri = uri;
        this.icon = icon;
    }

    public Link appendLink(Composite content) {
        Label label = new Label(content, SWT.BEGINNING);
        label.setImage(icon);

        Link link = new Link(content, SWT.BEGINNING);
        link.setText(MessageFormat.format(text, uri));

        link.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                executeCommand(commandId, uri);
            }
        });

        return link;
    }

    private void executeCommand(String commandId, String value) {
        ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
        IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);

        if (value.isEmpty()) {
            try {
                handlerService.executeCommand(commandId, null);
            } catch (Exception e) {
                Logs.log(LOG_ERROR_FAILED_TO_EXECUTE_COMMAND, commandId, e);
            }
        }

        try {
            Command command = commandService.getCommand(commandId);
            IParameter commandParmeter = command.getParameter(COMMAND_HREF_ID);
            Parameterization parameterization = new Parameterization(commandParmeter, value);
            ParameterizedCommand parameterizedCommand = new ParameterizedCommand(command,
                    new Parameterization[] { parameterization });
            handlerService.executeCommand(parameterizedCommand, null);
        } catch (Exception e) {
            Logs.log(LOG_ERROR_FAILED_TO_EXECUTE_COMMAND, commandId, e);
        }
    }

    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(RootPreferenceLink other) {
        if (priority == other.priority) {
            return 0;
        } else if (priority > other.priority) {
            return 1;
        }
        return -1;
    }
}
