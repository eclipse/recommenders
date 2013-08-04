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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
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
import org.eclipse.recommenders.stats.rcp.interfaces.ITreeCategory;
import org.eclipse.recommenders.stats.rcp.interfaces.ITreeViewerExtension;
import org.eclipse.recommenders.stats.rcp.ui.EvaluateExtensionPointContributions;
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
    private Composite parent;

    public NavigationViewer(Composite detailCmp) {
        parent = detailCmp;
    }

    public void createViewer() {
        final TreeColumnLayout layout = createTreeColumnLayout(parent);
        viewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
        viewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        viewer.getTree().setLinesVisible(false);
        viewer.getTree().setHeaderVisible(false);
        createTreeColumn("", viewer, 130, layout, 50);

        viewer.setContentProvider(new TreeContentProvider());
        viewer.setLabelProvider(new TreeViewerLabelProvider());

        final TreeViewerModel model = new TreeViewerModel();
        Category category = new Category();
        category.setName(ECLIPSE_USAGE);
        category.getItems().add(CONTENT_ASSIST);
        category.getItems().add(COMMANDS);
        model.catList.add(category);

        new EvaluateExtensionPointContributions() {
            @Override
            public void executeCode(ITreeViewerExtension extension) {
                model.catList.add(extension.getCategory());
            }
        }.evaluate();

        viewer.setInput(model);
        viewer.expandAll();
        viewer.setSelection(new StructuredSelection(viewer.getExpandedElements()[0]));
    }

    public Composite getComposite() {
        return parent;
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
                    stack.topControl = findRightComposite(treeSelection, eclipseUsageCmp, statsCmp, commandsCmp);
                    detailCmp.layout();
                }
            }
        });
    }

    private Composite findRightComposite(final TreeSelection treeSelection, Composite eclipseUsageCmp,
            Composite statsCmp, Composite commandsCmp) {
        if (treeSelection.getFirstElement().toString().equals(ECLIPSE_USAGE)) {
            return eclipseUsageCmp;
        } else if (treeSelection.getFirstElement().toString().equals(CONTENT_ASSIST)) {
            return statsCmp;
        } else if (treeSelection.getFirstElement().toString().equals(COMMANDS)) {
            return commandsCmp;
        }

        IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(
                EvaluateExtensionPointContributions.IGREETER_ID);
        try {
            for (IConfigurationElement e : config) {
                final Object o = e.createExecutableExtension("class");
                if (o instanceof ITreeViewerExtension) {
                    ITreeViewerExtension extension = (ITreeViewerExtension) o;
                    if (treeSelection.getFirstElement().toString().equals(extension.getTreeItemText())) {
                        return extension.getPage().getComposite();
                    }
                }
            }
        } catch (CoreException ex) {
            System.out.println(ex.getMessage());
        }
        return null;
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
        public List<ITreeCategory> catList = new ArrayList<ITreeCategory>();
    }

    public class Category implements ITreeCategory {

        private String name;
        private List<String> items = new ArrayList<String>();

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String value) {
            name = value;
        }

        @Override
        public List<String> getItems() {
            return items;
        }

        @Override
        public String toString() {
            return getName();
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
            if (parentElement instanceof ITreeCategory) {
                ITreeCategory cat = (ITreeCategory) parentElement;
                return cat.getItems().toArray();
            }
            return null;
        }

        @Override
        public Object getParent(Object element) {
            return null;
        }

        @Override
        public boolean hasChildren(Object element) {
            if (element instanceof ITreeCategory) {
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
            if (cell.getElement() instanceof ITreeCategory) {
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
