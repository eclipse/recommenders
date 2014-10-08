/**
Copyright (c) 2014 Olav Lenz.
All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *      Olav Lenz - initial API and implementation
 */
package org.eclipse.recommenders.internal.models.rcp;

import static org.eclipse.recommenders.rcp.SharedImages.Images.OBJ_JAR;

import java.util.Comparator;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.recommenders.injection.InjectionService;
import org.eclipse.recommenders.models.DependencyInfo;
import org.eclipse.recommenders.models.IDependencyListener;
import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.models.rcp.IProjectCoordinateProvider;
import org.eclipse.recommenders.rcp.SharedImages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class ProjectCoordinateSelectionDialog extends FilteredItemsSelectionDialog {

    private LabelProvider labelProvider;

    private final SharedImages images;
    private final IDependencyListener dependencyListener;
    private final IProjectCoordinateProvider pcAdvisor;

    public ProjectCoordinateSelectionDialog(Shell shell) {
        super(shell, true);
        setTitle(Messages.DIALOG_TITLE_SELECT_DEPENDENCY);

        this.images = InjectionService.getInstance().requestInstance(SharedImages.class);
        this.dependencyListener = InjectionService.getInstance().requestInstance(IDependencyListener.class);
        this.pcAdvisor = InjectionService.getInstance().requestInstance(IProjectCoordinateProvider.class);

        this.labelProvider = new LabelProvider() {
            public String getText(Object element) {
                if (element == null) {
                    return ""; //$NON-NLS-1$
                }
                if (element instanceof ProjectCoordinate) {
                    return createLabelForProjectCoordinate((ProjectCoordinate) element);
                }
                return element.toString();
            }

            @Override
            public Image getImage(Object element) {
                if (element instanceof ProjectCoordinate) {
                    return createImageForProjectCoordinate((ProjectCoordinate) element);
                }
                return super.getImage(element);
            }
        };

        setListLabelProvider(labelProvider);
        setDetailsLabelProvider(labelProvider);

    }

    private Set<ProjectCoordinate> resolve(ImmutableSet<DependencyInfo> dependencies) {
        Set<ProjectCoordinate> result = Sets.newHashSet();

        for (DependencyInfo dependencyInfo : dependencies) {
            ProjectCoordinate pc = pcAdvisor.resolve(dependencyInfo).orNull();
            if (pc != null) {
                result.add(pc);
            }
        }

        return result;
    }

    public Image createImageForProjectCoordinate(ProjectCoordinate element) {
        return images.getImage(OBJ_JAR);
    }

    public String createLabelForProjectCoordinate(ProjectCoordinate element) {
        return element.toString();
    }

    @Override
    protected Control createExtendedContentArea(Composite parent) {
        return null;
    }

    private static final String DIALOG_SETTINGS = "ProjectCoordinateSelectionDialog"; //$NON-NLS-1$

    @Override
    protected IDialogSettings getDialogSettings() {
        IDialogSettings settings = JavaPlugin.getDefault().getDialogSettings().getSection(DIALOG_SETTINGS);

        if (settings == null) {
            settings = JavaPlugin.getDefault().getDialogSettings().addNewSection(DIALOG_SETTINGS);
        }

        return settings;
    }

    @Override
    protected IStatus validateItem(Object item) {
        return Status.OK_STATUS;
    }

    @Override
    protected ItemsFilter createFilter() {
        return new ItemsFilter() {
            @Override
            public boolean matchItem(Object item) {
                if (item instanceof ProjectCoordinate) {
                    if (wasAlreadySelected((ProjectCoordinate) item)) {
                        return false;
                    }
                }
                return matches(item.toString());
            }

            @Override
            public String getPattern() {
                String pattern = super.getPattern();
                if (pattern.equals("")) { //$NON-NLS-1$
                    return "?"; //$NON-NLS-1$
                }
                return pattern;
            }

            @Override
            public boolean isConsistentItem(Object item) {
                return true;
            }
        };
    }

    public boolean wasAlreadySelected(ProjectCoordinate pc) {
        return false;
    }

    @Override
    protected Comparator getItemsComparator() {
        return new Comparator() {
            @Override
            public int compare(Object arg0, Object arg1) {
                return arg0.toString().compareTo(arg1.toString());
            }
        };
    }

    @Override
    protected void fillContentProvider(AbstractContentProvider contentProvider, ItemsFilter itemsFilter,
            IProgressMonitor progressMonitor) throws CoreException {
        ImmutableSet<DependencyInfo> dependencies = dependencyListener.getDependencies();
        progressMonitor.beginTask(Messages.DIALOG_RESOLVING_DEPENDENCIES, dependencies.size());

        for (DependencyInfo dependencyInfo : dependencies) {

            ProjectCoordinate pc = pcAdvisor.resolve(dependencyInfo).orNull();
            if (pc != null) {
                contentProvider.add(pc, itemsFilter);
            }

            progressMonitor.worked(1);

        }

    }

    @Override
    public String getElementName(Object item) {
        return labelProvider.getText(item);
    }

    public Set<ProjectCoordinate> getSelectedElements() {
        Set<ProjectCoordinate> selectedElements = Sets.newHashSet();
        Object[] result = getResult();
        if (result != null) {
            for (Object object : result) {
                if (object instanceof ProjectCoordinate) {
                    ProjectCoordinate pc = (ProjectCoordinate) object;
                    selectedElements.add(pc); //$NON-NLS-1$
                }
            }
        }

        return selectedElements;
    }

}
