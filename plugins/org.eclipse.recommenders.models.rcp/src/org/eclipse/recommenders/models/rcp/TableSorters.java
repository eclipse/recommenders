/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Olav Lenz - initial API and implementation
 */
package org.eclipse.recommenders.models.rcp;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.TableColumn;

import com.google.common.collect.Maps;

public class TableSorters {

    private final TableViewer tableViewer;
    private final IAction refreshUiAction;
    private final Map<TableColumn, Comparator<Object>> comparators = Maps.newHashMap();

    private TableColumn initialSortingColumn = null;
    private int initialDirection = SWT.UP;

    private TableSorters(TableViewer tableViewer, IAction refreshUiAction) {
        this.tableViewer = tableViewer;
        this.refreshUiAction = refreshUiAction;
    }

    public static TableSorters newConfigurator(TableViewer tableViewer, IAction refreshAction) {
        return new TableSorters(tableViewer, refreshAction);
    }

    public TableSorters add(TableColumn tableColoumn, Comparator<Object> comparator) {
        comparators.put(tableColoumn, comparator);
        return this;
    }

    public TableSorters initialize(TableColumn tableColoumn, int direction) {
        this.initialSortingColumn = tableColoumn;
        this.initialDirection = direction;
        return this;
    }

    public void configure() {
        TableComparator comparator = new TableComparator(tableViewer, refreshUiAction, comparators);
        tableViewer.setComparator(comparator);

        for (Entry<TableColumn, Comparator<Object>> entry : comparators.entrySet()) {
            entry.getKey().addSelectionListener(new SelectionListener(entry.getKey(), comparator));
        }

        if (initialSortingColumn != null) {
            tableViewer.getTable().setSortDirection(initialDirection);
            comparator.initial(initialSortingColumn, initialDirection);
            tableViewer.getTable().setSortColumn(initialSortingColumn);
            refreshUiAction.run();
        }
    }

}

class TableComparator extends ViewerComparator {

    private final Map<TableColumn, Comparator<Object>> comparators;
    private final TableViewer tableViewer;
    private final IAction refreshUiAction;

    private int direction = SWT.UP;
    private TableColumn tableColumn = null;

    public TableComparator(final TableViewer tableViewer, final IAction refreshUiAction,
            final Map<TableColumn, Comparator<Object>> comparators) {
        this.tableViewer = tableViewer;
        this.refreshUiAction = refreshUiAction;
        this.comparators = comparators;
    }

    public void initial(final TableColumn tableColumn, final int direction) {
        this.tableColumn = tableColumn;
        this.direction = direction;
    }

    public void sort(final TableColumn tableColumn) {
        updateDirection(tableColumn);
        this.tableColumn = tableColumn;

        tableViewer.getTable().setSortDirection(direction);
        tableViewer.getTable().setSortColumn(tableColumn);
        refreshUiAction.run();
    }

    private void updateDirection(final TableColumn tableColumn) {
        if (this.tableColumn == null || !this.tableColumn.equals(tableColumn)) {
            direction = SWT.UP;
        } else {
            switch (direction) {
            case SWT.NONE:
                direction = SWT.UP;
                break;
            case SWT.UP:
                direction = SWT.DOWN;
                break;
            default:
                direction = SWT.NONE;
                break;
            }
        }
    }

    @Override
    public int compare(final Viewer viewer, final Object o1, final Object o2) {
        if (direction == SWT.NONE) {
            return 0;
        }

        Comparator<Object> comparator = comparators.get(tableColumn);
        if (comparator == null) {
            return 0;
        }

        int result = comparator.compare(o1, o2);

        if (direction == SWT.DOWN) {
            result *= -1;
        }
        return result;
    }

}

class SelectionListener extends SelectionAdapter {

    private final TableColumn tableColumn;
    private final TableComparator comparator;

    public SelectionListener(final TableColumn tableColumn, final TableComparator comparator) {
        this.tableColumn = tableColumn;
        this.comparator = comparator;
    }

    @Override
    public void widgetSelected(final SelectionEvent e) {
        comparator.sort(tableColumn);
    }
}
