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
package org.eclipse.recommenders.stats.rcp;

import java.io.File;

import org.apache.commons.lang3.SystemUtils;
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
                TriggeredCommand triggeredCommand = new TriggeredCommand(commandId, System.currentTimeMillis());
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
