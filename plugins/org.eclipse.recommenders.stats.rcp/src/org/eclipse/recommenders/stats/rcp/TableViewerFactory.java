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

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

public final class TableViewerFactory {

    public static TableViewer createTableViewer(final Composite parent) {
        final TableViewer viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION
                | SWT.BORDER);
        viewer.getTable().setHeaderVisible(true);
        viewer.getTable().setLinesVisible(true);
        viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
        return viewer;
    }

    public static TableViewerColumn createColumn(final String header, final TableViewer viewer, final int width,
            final TableColumnLayout layout, final int weight) {
        final TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
        column.getColumn().setText(header);
        column.getColumn().setToolTipText(header);
        column.getColumn().setMoveable(true);
        column.getColumn().setAlignment(SWT.CENTER);
        column.getColumn().setResizable(true);
        column.getColumn().setWidth(width);
        layout.setColumnData(column.getColumn(), new ColumnWeightData(weight));
        return column;
    }

    public static TableColumnLayout createTableColumnLayout(final Composite comp) {
        final TableColumnLayout layout = new TableColumnLayout();
        comp.setLayoutData(new GridData(GridData.FILL_BOTH));
        comp.setLayout(layout);
        return layout;
    }

    private TableViewerFactory() {
    }
}
