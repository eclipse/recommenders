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

import static org.eclipse.recommenders.stats.rcp.TableViewerFactory.*;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.gson.Gson;

public class TriggeredCommandsTab {

    public Composite createContent(TabFolder folder) {
        final Composite parent = new Composite(folder, SWT.NONE);
        parent.setLayout(new GridLayout());
        final Collection<TriggeredCommand> model = loadModelFromWorkspace();
        new Label(parent, SWT.NONE).setText("Number of times commands triggered: " + getTriggeredCommandsSize(model));

        final Composite comp = new Composite(parent, SWT.NONE);
        final TableColumnLayout layout = createTableColumnLayout(comp);
        final TableViewer viewer = createTableViewer(comp);

        TableViewerColumn commandNameColumn = createColumn("Command Name", viewer, 120, layout, 20);
        new ColumnViewerSorter(viewer, commandNameColumn) {
            @Override
            protected int doCompare(Viewer viewer, Object object1, Object object2) {
                TriggeredCommand command1 = (TriggeredCommand) object1;
                TriggeredCommand command2 = (TriggeredCommand) object2;
                return command1.name.compareToIgnoreCase(command2.name);
            }
        };

        TableViewerColumn countColumn = createColumn("Count", viewer, 80, layout, 8);
        new ColumnViewerSorter(viewer, countColumn) {
            @Override
            protected int doCompare(Viewer viewer, Object object1, Object object2) {
                TriggeredCommand command1 = (TriggeredCommand) object1;
                TriggeredCommand command2 = (TriggeredCommand) object2;
                return new Integer(command1.count).compareTo(command2.count);
            }
        };

        TableViewerColumn commandIdColumn = createColumn("Command ID", viewer, 130, layout, 40);
        new ColumnViewerSorter(viewer, commandIdColumn) {
            @Override
            protected int doCompare(Viewer viewer, Object object1, Object object2) {
                TriggeredCommand command1 = (TriggeredCommand) object1;
                TriggeredCommand command2 = (TriggeredCommand) object2;
                return command1.commandID.compareToIgnoreCase(command2.commandID);
            }
        };

        createColumn("Description", viewer, 130, layout, 40);
        countColumn.getColumn().getParent().setSortColumn(countColumn.getColumn());
        countColumn.getColumn().getParent().setSortDirection(SWT.DOWN);

        viewer.setContentProvider(new ArrayContentProvider());
        viewer.setLabelProvider(new CommandsLabelProvider());
        viewer.setInput(model);
        return parent;
    }

    private int getTriggeredCommandsSize(Collection<TriggeredCommand> model) {
        int counter = 0;
        for (TriggeredCommand command : model) {
            counter += command.count;
        }
        return counter;
    }

    private Collection<TriggeredCommand> loadModelFromWorkspace() {
        final File file = TriggeredCommandCollector.getCommandLogLocation();
        final Gson gson = new Gson();

        final LinkedList<TriggeredCommand> events = Lists.newLinkedList();
        try {
            for (String json : Files.readLines(file, Charsets.UTF_8)) {
                TriggeredCommand commandData = gson.fromJson(json, TriggeredCommand.class);
                events.add(commandData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buildViewerModel(events);
    }

    private Collection<TriggeredCommand> buildViewerModel(LinkedList<TriggeredCommand> commandList) {
        final Map<String, TriggeredCommand> commandMap = new HashMap<String, TriggeredCommand>();
        for (TriggeredCommand command : commandList) {
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
        @Override
        public void update(ViewerCell cell) {
            final StyledString cellText = new StyledString();
            switch (cell.getColumnIndex()) {
            case 0:
                if (cell.getElement() instanceof TriggeredCommand) {
                    cellText.append(((TriggeredCommand) cell.getElement()).name);
                }
                break;
            case 1:
                if (cell.getElement() instanceof TriggeredCommand) {
                    cellText.append(Integer.toString(((TriggeredCommand) cell.getElement()).count),
                            StyledString.COUNTER_STYLER);
                }
                break;
            case 2:
                if (cell.getElement() instanceof TriggeredCommand) {
                    cellText.append(((TriggeredCommand) cell.getElement()).commandID);
                }
                break;
            case 3:
                if (cell.getElement() instanceof TriggeredCommand) {
                    if (((TriggeredCommand) cell.getElement()).description != null) {
                        cellText.append(((TriggeredCommand) cell.getElement()).description);
                    }
                }
                break;
            default:
            }

            cell.setText(cellText.toString());
            cell.setStyleRanges(cellText.getStyleRanges());
        }
    }
}
