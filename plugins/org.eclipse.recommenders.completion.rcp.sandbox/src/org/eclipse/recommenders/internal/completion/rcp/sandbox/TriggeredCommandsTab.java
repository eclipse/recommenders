/**
 * Copyright (c) 2013 Timur Achmetow
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Timur Achmetow - Initial API and implementation
 */
package org.eclipse.recommenders.internal.completion.rcp.sandbox;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
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
        final Map<String, Category> model = loadModelFromWorkspace();
        new Label(parent, SWT.NONE).setText("Number of times commands triggered: " + getTriggeredCommandsSize(model));

        final Composite comp = new Composite(parent, SWT.NONE);
        final TreeColumnLayout layout = createTableColumnLayout(comp);
        final TreeViewer viewer = new TreeViewer(comp, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
        viewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        viewer.getTree().setLinesVisible(true);
        viewer.getTree().setHeaderVisible(true);

        TreeViewerColumn catIdColumn = createColumn("Category (ID)", viewer, 120, layout, 40);
        new TreeColumnViewerSorter(viewer, catIdColumn) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                if (e1 instanceof Category && e2 instanceof Category) {
                    Category cat1 = (Category) e1;
                    Category cat2 = (Category) e2;
                    return new Integer(cat1.getSize()).compareTo(cat2.getSize());
                }
                return 0;
            }
        };

        TreeViewerColumn commandNameColumn = createColumn("Command Name", viewer, 120, layout, 20);
        new TreeColumnViewerSorter(viewer, commandNameColumn) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                if (e1 instanceof TriggeredCommand && e2 instanceof TriggeredCommand) {
                    TriggeredCommand command1 = (TriggeredCommand) e1;
                    TriggeredCommand command2 = (TriggeredCommand) e2;
                    command1.name.equalsIgnoreCase(command2.name);
                }
                return 0;
            }
        };

        TreeViewerColumn countColumn = createColumn("Count", viewer, 80, layout, 10);
        new TreeColumnViewerSorter(viewer, countColumn) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                if (e1 instanceof TriggeredCommand && e2 instanceof TriggeredCommand) {
                    TriggeredCommand command1 = (TriggeredCommand) e1;
                    TriggeredCommand command2 = (TriggeredCommand) e2;
                    return new Integer(command1.count).compareTo(command2.count);
                }
                return 0;
            }
        };

        TreeViewerColumn commandIdColumn = createColumn("Command ID", viewer, 130, layout, 25);
        new TreeColumnViewerSorter(viewer, commandIdColumn) {
            @Override
            protected int doCompare(Viewer viewer, Object e1, Object e2) {
                if (e1 instanceof TriggeredCommand && e2 instanceof TriggeredCommand) {
                    TriggeredCommand command1 = (TriggeredCommand) e1;
                    TriggeredCommand command2 = (TriggeredCommand) e2;
                    command1.commandID.equalsIgnoreCase(command2.commandID);
                }
                return 0;
            }
        };

        createColumn("Description", viewer, 130, layout, 35);

        viewer.setContentProvider(new CommandTreeContentProvider());
        viewer.setLabelProvider(new CommandsLabelProvider());
        viewer.setInput(model);
        viewer.expandAll();
        return parent;
    }

    private int getTriggeredCommandsSize(Map<String, Category> model) {
        int counter = 0;
        for (Category cat : model.values()) {
            counter += cat.getSize();
        }
        return counter;
    }

    private TreeColumnLayout createTableColumnLayout(final Composite comp) {
        final TreeColumnLayout layout = new TreeColumnLayout();
        comp.setLayoutData(new GridData(GridData.FILL_BOTH));
        comp.setLayout(layout);
        return layout;
    }

    private TreeViewerColumn createColumn(String header, TreeViewer viewer, int width, TreeColumnLayout layout,
            int weight) {
        final TreeViewerColumn column = new TreeViewerColumn(viewer, SWT.FILL);
        column.getColumn().setMoveable(true);
        column.getColumn().setText(header);
        column.getColumn().setToolTipText(header);
        column.getColumn().setAlignment(SWT.CENTER);
        column.getColumn().setWidth(width);
        column.getColumn().setResizable(true);
        layout.setColumnData(column.getColumn(), new ColumnWeightData(weight));
        return column;
    }

    private Map<String, Category> loadModelFromWorkspace() {
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

    private Map<String, Category> buildViewerModel(LinkedList<TriggeredCommand> commandList) {
        final Map<String, Category> catMap = new HashMap<String, Category>();
        for (TriggeredCommand command : commandList) {
            if (catMap.containsKey(command.categoryID)) {
                Category category = catMap.get(command.categoryID);
                category.commandList.add(command);
                continue;
            }
            final Category category = new Category();
            category.name = command.categoryName;
            category.key = command.categoryID;
            category.commandList.add(command);
            catMap.put(command.categoryID, category);
        }

        for (Category cat : catMap.values()) {
            System.out.println(cat.name + "Key: " + cat.key);
            final Map<String, TriggeredCommand> commandMap = new HashMap<String, TriggeredCommand>();
            for (TriggeredCommand command : cat.commandList) {
                if (commandMap.containsKey(command.commandID)) {
                    commandMap.get(command.commandID).incrementCounter();
                    continue;
                }
                commandMap.put(command.commandID, command);
            }
            cat.commandList.clear();
            cat.commandList.addAll(commandMap.values());
        }
        return catMap;
    }

    public class CommandsLabelProvider extends StyledCellLabelProvider {

        @Override
        public void update(ViewerCell cell) {
            final StyledString cellText = new StyledString();
            switch (cell.getColumnIndex()) {
            case 0:
                if (cell.getElement() instanceof Category) {
                    final Category category = (Category) cell.getElement();
                    cellText.append(MessageFormat.format("{0} (Count: {1} {2})", category.name, category.getSize(),
                            category.key));
                }
                break;
            case 1:
                if (cell.getElement() instanceof TriggeredCommand) {
                    cellText.append(((TriggeredCommand) cell.getElement()).name);
                }
                break;
            case 2:
                if (cell.getElement() instanceof TriggeredCommand) {
                    cellText.append(Integer.toString(((TriggeredCommand) cell.getElement()).count),
                            StyledString.COUNTER_STYLER);
                }
                break;
            case 3:
                if (cell.getElement() instanceof TriggeredCommand) {
                    cellText.append(((TriggeredCommand) cell.getElement()).commandID);
                }
                break;
            case 4:
                if (cell.getElement() instanceof TriggeredCommand) {
                    if (((TriggeredCommand) cell.getElement()).description != null) {
                        cellText.append(((TriggeredCommand) cell.getElement()).description);
                    }
                }
                break;
            default:
            }

            if (cellText != null) {
                cell.setText(cellText.toString());
                cell.setStyleRanges(cellText.getStyleRanges());
            }
        }
    }

    public class CommandTreeContentProvider implements ITreeContentProvider {

        private Map<String, Category> modelList;

        @Override
        public void dispose() {
        }

        @SuppressWarnings("unchecked")
        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            modelList = (Map<String, Category>) newInput;
        }

        @Override
        public Object[] getElements(Object inputElement) {
            return modelList.values().toArray();
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof Category) {
                Category category = (Category) parentElement;
                return category.commandList.toArray();
            }
            return null;
        }

        @Override
        public Object getParent(Object element) {
            return null;
        }

        @Override
        public boolean hasChildren(Object element) {
            return element instanceof Category;
        }
    }

    public class Category {
        public String name;
        public String key;
        public List<TriggeredCommand> commandList = new ArrayList<TriggeredCommand>();

        public int getSize() {
            int counter = 0;
            for (TriggeredCommand command : commandList) {
                counter += command.count;
            }
            return counter;
        }
    }
}
