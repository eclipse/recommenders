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

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public abstract class TreeColumnViewerSorter extends ViewerComparator {
    public static final int ASC = 1;

    public static final int NONE = 0;

    public static final int DESC = -1;

    private int direction = 0;

    private final TreeViewerColumn column;

    private final ColumnViewer viewer;

    public TreeColumnViewerSorter(ColumnViewer viewer, TreeViewerColumn column) {
        this.column = column;
        this.viewer = viewer;
        this.column.getColumn().addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                if (TreeColumnViewerSorter.this.viewer.getComparator() != null) {
                    if (TreeColumnViewerSorter.this.viewer.getComparator() == TreeColumnViewerSorter.this) {
                        int tdirection = direction;
                        if (tdirection == ASC) {
                            setSorter(TreeColumnViewerSorter.this, DESC);
                        } else if (tdirection == DESC) {
                            setSorter(TreeColumnViewerSorter.this, ASC);
                        }
                    } else {
                        setSorter(TreeColumnViewerSorter.this, ASC);
                    }
                } else {
                    setSorter(TreeColumnViewerSorter.this, ASC);
                }
            }
        });
    }

    public void setSorter(TreeColumnViewerSorter sorter, int direction) {
        if (direction == NONE) {
            column.getColumn().getParent().setSortColumn(null);
            column.getColumn().getParent().setSortDirection(SWT.NONE);
            viewer.setComparator(null);
        } else {
            column.getColumn().getParent().setSortColumn(column.getColumn());
            sorter.direction = direction;

            if (direction == ASC) {
                column.getColumn().getParent().setSortDirection(SWT.UP);
            } else {
                column.getColumn().getParent().setSortDirection(SWT.DOWN);
            }

            if (viewer.getComparator() == sorter) {
                viewer.refresh();
            } else {
                viewer.setComparator(sorter);
            }

        }
    }

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        return direction * doCompare(viewer, e1, e2);
    }

    protected abstract int doCompare(Viewer viewer, Object e1, Object e2);
}
