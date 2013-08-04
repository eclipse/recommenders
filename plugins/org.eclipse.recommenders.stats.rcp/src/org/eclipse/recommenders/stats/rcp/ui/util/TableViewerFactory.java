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
package org.eclipse.recommenders.stats.rcp.ui.util;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.util.ConfigureColumns;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.SameShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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

    public static Composite createWrapperComposite(Composite parent) {
        Composite newComp = new Composite(parent, SWT.NONE);
        newComp.setLayout(new GridLayout());
        newComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        return newComp;
    }

    /**
     * Cool stuff from Tom Schindl, see here: JFace Snippet Snippet019TableViewerAddRemoveColumnsWithEditingNewAPI
     * Creates a configurable dialog for all table columns.
     */
    public static void addMenu(final TableViewer v) {
        final MenuManager mgr = new MenuManager();
        final Action configureColumns = new Action("Configure Columns...") {
            @Override
            public void run() {
                ConfigureColumns.forTable(v.getTable(), new SameShellProvider(v.getControl()));
            }
        };

        mgr.setRemoveAllWhenShown(true);
        mgr.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
                manager.add(configureColumns);
            }
        });
        v.getControl().setMenu(mgr.createContextMenu(v.getControl()));
    }

    private TableViewerFactory() {
    }
}
