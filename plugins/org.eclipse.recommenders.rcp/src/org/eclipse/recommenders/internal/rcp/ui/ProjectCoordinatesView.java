package org.eclipse.recommenders.internal.rcp.ui;

import java.io.File;

import javax.inject.Inject;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.recommenders.rcp.ClasspathEntryInfo;
import org.eclipse.recommenders.rcp.IClasspathEntryInfoProvider;
import org.eclipse.recommenders.rcp.repo.IModelRepositoryIndex;
import org.eclipse.recommenders.utils.Constants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import org.sonatype.aether.artifact.Artifact;

import com.google.common.base.Optional;

public class ProjectCoordinatesView extends ViewPart {

    public static final String ID = "org.eclipse.recommenders.internal.rcp.ui.ProjectCoordinatesView"; //$NON-NLS-1$
    private Table table;

    @Inject
    IClasspathEntryInfoProvider cpeProvider;

    @Inject
    IModelRepositoryIndex index;

    @Inject
    ModelResolutionFunction resovler;

    private TableViewer tableViewer;

    /**
     * Create contents of the view part.
     * 
     * @param parent
     */
    @Override
    public void createPartControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayout(new FillLayout(SWT.HORIZONTAL));
        {
            Composite composite = new Composite(container, SWT.NONE);
            TableColumnLayout tableLayout = new TableColumnLayout();
            composite.setLayout(tableLayout);
            {
                tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
                table = tableViewer.getTable();
                table.setHeaderVisible(true);
                table.setLinesVisible(true);
                {
                    TableViewerColumn tvcContainer = new TableViewerColumn(tableViewer, SWT.NONE);
                    TableColumn tcContainer = tvcContainer.getColumn();
                    tableLayout.setColumnData(tcContainer,
                            new ColumnWeightData(5, ColumnWeightData.MINIMUM_WIDTH, true));
                    tcContainer.setText("Class Container");
                    tvcContainer.setLabelProvider(new ColumnLabelProvider() {
                        @Override
                        public String getText(Object element) {
                            File f = (File) element;
                            return f.getName();
                        }
                    });
                }
                {
                    TableViewerColumn tvcCoordinate = new TableViewerColumn(tableViewer, SWT.NONE);
                    TableColumn tcCoordinate = tvcCoordinate.getColumn();
                    tableLayout.setColumnData(tcCoordinate, new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH,
                            true));
                    tcCoordinate.setText("Project Coordinate");
                    tvcCoordinate.setLabelProvider(new ColumnLabelProvider() {
                        @Override
                        public String getText(Object element) {
                            File f = (File) element;
                            ClasspathEntryInfo info = cpeProvider.getInfo(f).orNull();
                            if (info != null) {
                                return info.getSymbolicName() + ":" + info.getVersion();
                            }
                            return null;
                        }

                    });
                }
                {
                    TableViewerColumn tvcContainer = new TableViewerColumn(tableViewer, SWT.NONE);
                    TableColumn tcContainer = tvcContainer.getColumn();
                    tableLayout.setColumnData(tcContainer,
                            new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));
                    tcContainer.setText("Path");
                    tvcContainer.setLabelProvider(new ColumnLabelProvider() {
                        @Override
                        public String getText(Object element) {
                            File f = (File) element;
                            return f.getAbsolutePath();
                        }
                    });
                }
                newColumn(tableLayout, Constants.CLASS_CALL_MODELS);
                // newColumn(tableLayout, Constants.CLASS_OVRD_MODEL);
                // newColumn(tableLayout, Constants.CLASS_OVRP_MODEL);
                // newColumn(tableLayout, Constants.CLASS_OVRM_MODEL);
                // newColumn(tableLayout, Constants.CLASS_SELFC_MODEL);
                // newColumn(tableLayout, Constants.CLASS_SELFM_MODEL);

                tableViewer.setContentProvider(ArrayContentProvider.getInstance());
                tableViewer.setSorter(new ViewerSorter() {
                    @Override
                    public int compare(Viewer viewer, Object e1, Object e2) {
                        File f1 = (File) e1;
                        File f2 = (File) e2;

                        return super.compare(viewer, f1.getName(), f2.getName());
                    }
                });
                tableViewer.setInput(cpeProvider.getFiles());
            }
        }
        createActions();
        initializeToolBar();
        initializeMenu();
    }

    private void newColumn(TableColumnLayout tableLayout, final String classifier) {
        TableViewerColumn tvcSelfc = new TableViewerColumn(tableViewer, SWT.NONE);
        TableColumn tcSelfc = tvcSelfc.getColumn();
        tcSelfc.setMoveable(true);
        // tcSelfc.setResizable(false);
        tableLayout.setColumnData(tcSelfc, new ColumnWeightData(1, ColumnWeightData.MINIMUM_WIDTH, true));

        tcSelfc.setText(classifier);
        tvcSelfc.setLabelProvider(new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                File f = (File) element;
                Optional<Artifact> model = findModelArchive(classifier, f);
                return model.isPresent() ? model.get().getArtifactId() + " - " + model.get().getVersion() : null;
            }

            @Override
            public String getToolTipText(Object element) {
                File f = (File) element;
                Optional<Artifact> model = findModelArchive(classifier, f);
                return model.isPresent() ? model.get().toString() : null;
            }

            private Optional<Artifact> findModelArchive(String classifier, File f) {
                return resovler.resolve(f, classifier);
            }

        });
    }

    /**
     * Create the actions.
     */
    private void createActions() {
        // Create the actions
    }

    /**
     * Initialize the toolbar.
     */
    private void initializeToolBar() {
        IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
    }

    /**
     * Initialize the menu.
     */
    private void initializeMenu() {
        IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
    }

    @Override
    public void setFocus() {
        // Set the focus
    }

}
