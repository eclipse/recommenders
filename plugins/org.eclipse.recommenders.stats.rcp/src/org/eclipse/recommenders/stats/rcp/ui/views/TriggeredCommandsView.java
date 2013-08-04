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
package org.eclipse.recommenders.stats.rcp.ui.views;

import static org.eclipse.recommenders.stats.rcp.ui.util.TableSorters.setCommandCountSorter;
import static org.eclipse.recommenders.stats.rcp.ui.util.TableSorters.setCommandID;
import static org.eclipse.recommenders.stats.rcp.ui.util.TableSorters.setCommandNameSorter;
import static org.eclipse.recommenders.stats.rcp.ui.util.TableViewerFactory.createColumn;
import static org.eclipse.recommenders.stats.rcp.ui.util.TableViewerFactory.createTableColumnLayout;
import static org.eclipse.recommenders.stats.rcp.ui.util.TableViewerFactory.createTableViewer;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.recommenders.stats.rcp.events.TriggeredCommandCollector;
import org.eclipse.recommenders.stats.rcp.interfaces.IDeveloperActivityPage;
import org.eclipse.recommenders.stats.rcp.model.TriggeredCommandObject;
import org.eclipse.recommenders.stats.rcp.ui.util.TableViewerFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.gson.Gson;

public class TriggeredCommandsView implements IDeveloperActivityPage {

    private Composite parent;

    @Override
    public void createContent(Composite detailCmp) {
        parent = TableViewerFactory.createWrapperComposite(detailCmp);

        final Collection<TriggeredCommandObject> model = loadModelFromWorkspace();
        new Label(parent, SWT.NONE).setText("Number of times commands triggered: " + getTriggeredCommandsSize(model));

        final Composite comp = new Composite(parent, SWT.NONE);
        comp.setLayout(new GridLayout());
        comp.setLayoutData(new GridData(GridData.FILL_BOTH));

        final TableColumnLayout layout = createTableColumnLayout(comp);
        final TableViewer viewer = createTableViewer(comp);

        TableViewerColumn commandNameColumn = createColumn("Command Name", viewer, 120, layout, 20);
        setCommandNameSorter(viewer, commandNameColumn);

        TableViewerColumn countColumn = createColumn("Count", viewer, 80, layout, 8);
        setCommandCountSorter(viewer, countColumn);

        TableViewerColumn commandIdColumn = createColumn("Command ID", viewer, 130, layout, 40);
        setCommandID(viewer, commandIdColumn);
        commandIdColumn.getColumn().setWidth(0);

        createColumn("Description", viewer, 130, layout, 40);
        countColumn.getColumn().getParent().setSortColumn(countColumn.getColumn());
        countColumn.getColumn().getParent().setSortDirection(SWT.DOWN);

        TableViewerFactory.addMenu(viewer);
        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(new CommandsLabelProvider());
        viewer.setInput(model);
    }

    @Override
    public Composite getComposite() {
        return parent;
    }

    private int getTriggeredCommandsSize(Collection<TriggeredCommandObject> model) {
        int counter = 0;
        for (TriggeredCommandObject command : model) {
            counter += command.count;
        }
        return counter;
    }

    private Collection<TriggeredCommandObject> loadModelFromWorkspace() {
        final File file = TriggeredCommandCollector.getCommandLogLocation();
        System.out.println("Load file: " + file.getAbsolutePath());
        final Gson gson = new Gson();

        final LinkedList<TriggeredCommandObject> events = Lists.newLinkedList();
        try {
            for (String json : Files.readLines(file, Charsets.UTF_8)) {
                TriggeredCommandObject commandData = gson.fromJson(json, TriggeredCommandObject.class);
                events.add(commandData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buildViewerModel(events);
    }

    private Collection<TriggeredCommandObject> buildViewerModel(LinkedList<TriggeredCommandObject> commandList) {
        final Map<String, TriggeredCommandObject> commandMap = new HashMap<String, TriggeredCommandObject>();
        for (TriggeredCommandObject command : commandList) {
            if (commandMap.containsKey(command.commandID)) {
                commandMap.get(command.commandID).incrementCounter();
            } else {
                command.incrementCounter();
                commandMap.put(command.commandID, command);
            }
        }
        return commandMap.values();
    }

    public class CommandsLabelProvider extends StyledCellLabelProvider {
        private ICommandService commandService;

        public CommandsLabelProvider() {
            commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
        }

        @Override
        public void update(ViewerCell cell) {
            if (isNotValidCommandObject(cell)) {
                return;
            }

            final StyledString cellText = new StyledString();
            TriggeredCommandObject command = (TriggeredCommandObject) cell.getElement();

            switch (cell.getColumnIndex()) {
            case 0:
                cellText.append(getCommandName(command.commandID));
                break;
            case 1:
                cellText.append(Integer.toString(command.count), StyledString.COUNTER_STYLER);
                break;
            case 2:
                cellText.append(command.commandID);
                break;
            case 3:
                cellText.append(getCommandDescription(command.commandID));
                break;
            default:
            }

            cell.setText(cellText.toString());
            cell.setStyleRanges(cellText.getStyleRanges());
        }

        public String getCommandName(String commandID) {
            Command command = commandService.getCommand(commandID);
            try {
                return command.getName();
            } catch (NotDefinedException e) {
                return "";
            }
        }

        public String getCommandDescription(String commandID) {
            Command command = commandService.getCommand(commandID);
            try {
                return command.getDescription() != null ? command.getDescription() : "";
            } catch (NotDefinedException e) {
                return "";
            }
        }

        private boolean isNotValidCommandObject(ViewerCell cell) {
            return !(cell.getElement() instanceof TriggeredCommandObject);
        }
    }
}
