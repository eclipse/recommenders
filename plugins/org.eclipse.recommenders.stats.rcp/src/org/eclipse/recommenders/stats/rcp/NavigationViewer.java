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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class NavigationViewer {

    private static final String ECLIPSE_USAGE = "Eclipse Usage";
    private static final String COMMANDS = "Commands";
    private static final String CONTENT_ASSIST = "Content Assist";
    private TreeViewer viewer;

    public void createViewer(Composite masterCmp) {
        final TreeColumnLayout layout = createTreeColumnLayout(masterCmp);
        viewer = new TreeViewer(masterCmp, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
        viewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        viewer.getTree().setLinesVisible(false);
        viewer.getTree().setHeaderVisible(false);
        createTreeColumn("", viewer, 130, layout, 50);

        viewer.setContentProvider(new TreeContentProvider());
        viewer.setLabelProvider(new TreeViewerLabelProvider());

        TreeViewerModel model = new TreeViewerModel();
        Category category = new Category();
        category.name = ECLIPSE_USAGE;
        category.items.add(CONTENT_ASSIST);
        category.items.add(COMMANDS);
        model.catList.add(category);

        viewer.setInput(model);
        viewer.expandAll();
        viewer.setSelection(new StructuredSelection(viewer.getExpandedElements()[0]));
    }

    public void addSelectionChangedListener(final StackLayout stack, final Composite detailCmp,
            final Composite statsCmp, final Composite commandsCmp, final Composite eclipseUsageCmp) {
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                ISelection selection = event.getSelection();
                if (selection.isEmpty()) {
                    return;
                }
                if (selection instanceof IStructuredSelection && selection instanceof TreeSelection) {
                    TreeSelection treeSelection = (TreeSelection) selection;
                    if (treeSelection.getFirstElement().toString().equals(CONTENT_ASSIST)) {
                        stack.topControl = statsCmp;
                    } else if (treeSelection.getFirstElement().toString().equals(COMMANDS)) {
                        stack.topControl = commandsCmp;
                    } else if (treeSelection.getFirstElement().toString().equals(ECLIPSE_USAGE)) {
                        stack.topControl = eclipseUsageCmp;
                    }
                    detailCmp.layout();
                }
            }
        });
    }

    private TreeViewerColumn createTreeColumn(String header, TreeViewer viewer, int width, TreeColumnLayout layout,
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

    private TreeColumnLayout createTreeColumnLayout(final Composite comp) {
        final TreeColumnLayout layout = new TreeColumnLayout();
        comp.setLayoutData(new GridData(GridData.FILL_BOTH));
        comp.setLayout(layout);
        return layout;
    }

    public class TreeViewerModel {
        public List<Category> catList = new ArrayList<Category>();
    }

    public class Category {
        public String name;
        public List<String> items = new ArrayList<String>();

        @Override
        public String toString() {
            return name;
        }
    }

    public class TreeContentProvider implements ITreeContentProvider {
        private TreeViewerModel model;

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            model = (TreeViewerModel) newInput;
        }

        @Override
        public Object[] getElements(Object inputElement) {
            return model.catList.toArray();
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof Category) {
                Category cat = (Category) parentElement;
                return cat.items.toArray();
            }
            return null;
        }

        @Override
        public Object getParent(Object element) {
            return null;
        }

        @Override
        public boolean hasChildren(Object element) {
            if (element instanceof Category) {
                return true;
            }
            return false;
        }
    }

    public class TreeViewerLabelProvider extends StyledCellLabelProvider {
        private final Image eclipseImage = createImage("eclipse.gif");
        private final Image statsImage = createImage("stats.gif");
        private final Image commandsImage = createImage("commands.gif");

        @Override
        public void update(ViewerCell cell) {
            if (cell.getElement() instanceof Category) {
                cell.setImage(eclipseImage);
            } else if (cell.getElement().equals(CONTENT_ASSIST)) {
                cell.setImage(statsImage);
            } else if (cell.getElement().equals(COMMANDS)) {
                cell.setImage(commandsImage);
            }
            cell.setText(cell.getElement().toString());
        }

        private Image createImage(String fileName) {
            Bundle bundle = FrameworkUtil.getBundle(NavigationViewer.class);
            URL url = FileLocator.find(bundle, new Path("icons/" + fileName), null);
            ImageDescriptor image = ImageDescriptor.createFromURL(url);
            return image.createImage();
        };

        @Override
        public void dispose() {
            super.dispose();
            eclipseImage.dispose();
            statsImage.dispose();
            commandsImage.dispose();
        }
    }
}
