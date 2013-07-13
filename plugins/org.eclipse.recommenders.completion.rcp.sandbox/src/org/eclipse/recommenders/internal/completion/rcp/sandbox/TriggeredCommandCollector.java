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

import java.io.File;

import org.apache.commons.lang3.SystemUtils;
import org.eclipse.core.commands.Category;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;

public class TriggeredCommandCollector implements IStartup {

    private final File file = getCommandLogLocation();

    private final IExecutionListener listener = new IExecutionListener() {

        @Override
        public void preExecute(String commandId, ExecutionEvent event) {
            try {
                String name = event.getCommand().getName();
                String description = event.getCommand().getDescription();
                Category category = event.getCommand().getCategory();

                TriggeredCommand triggeredCommand = new TriggeredCommand(commandId, name, description,
                        System.currentTimeMillis(), 1, category.getName(), category.getId());
                Gson gson = new Gson();
                Files.append(gson.toJson(triggeredCommand) + SystemUtils.LINE_SEPARATOR, file, Charsets.UTF_8);
                System.out.println(triggeredCommand);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void postExecuteSuccess(String commandId, Object returnValue) {
        }

        @Override
        public void postExecuteFailure(String commandId, ExecutionException exception) {
        }

        @Override
        public void notHandled(String commandId, NotHandledException exception) {
        }
    };

    @Override
    public void earlyStartup() {
        ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getAdapter(ICommandService.class);
        if (commandService != null) {
            commandService.addExecutionListener(listener);
        }
    }

    public static File getCommandLogLocation() {
        Bundle bundle = FrameworkUtil.getBundle(StatisticsSessionProcessor.class);
        IPath location = Platform.getStateLocation(bundle);
        return new File(location.toFile(), "triggered-commands.txt");
    }
}
