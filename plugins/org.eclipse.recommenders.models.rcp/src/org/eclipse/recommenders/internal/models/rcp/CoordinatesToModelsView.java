package org.eclipse.recommenders.internal.models.rcp;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.recommenders.models.DependencyInfo;
import org.eclipse.recommenders.models.IMappingProvider;
import org.eclipse.recommenders.models.IModelRepository;
import org.eclipse.recommenders.models.ModelArchiveCoordinate;
import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.utils.Constants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;

import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;

public class CoordinatesToModelsView extends ViewPart {

    private Composite parent;
    private TreeViewer treeViewer;
    private EclipseDependencyListener eclipseDependencyListener;

    private String IMG_JRE = "icons/cview16/classpath.gif";
    private String IMG_JAR = "icons/cview16/jar_obj.gif";
    private String IMG_PROJECT = "icons/cview16/projects.gif";
    private ImageProvider imageProvider;

    private IMappingProvider mappingProvider;
    private IModelRepository modelRepository;

    @Inject
    public CoordinatesToModelsView(final EventBus workspaceBus,
            final EclipseDependencyListener eclipseDependencyListener, final IMappingProvider mappingProvider,
            final IModelRepository modelRepository) {
        this.eclipseDependencyListener = eclipseDependencyListener;
        this.mappingProvider = mappingProvider;
        this.modelRepository = modelRepository;
        imageProvider = new ImageProvider();
        workspaceBus.register(this);
    }

    @Override
    public void createPartControl(Composite parent) {
        this.parent = parent;

        Tree dependencyTree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        dependencyTree.setHeaderVisible(true);
        dependencyTree.setLinesVisible(true);

        TreeColumn column1 = new TreeColumn(dependencyTree, SWT.LEFT);
        column1.setAlignment(SWT.LEFT);
        column1.setText("Dependency");
        column1.setWidth(200);
        TreeColumn column2 = new TreeColumn(dependencyTree, SWT.RIGHT);
        column2.setAlignment(SWT.LEFT);
        column2.setText("Project Coordinate");
        column2.setWidth(200);
        TreeColumn column3 = new TreeColumn(dependencyTree, SWT.RIGHT);
        column3.setAlignment(SWT.LEFT);
        column3.setText("Model Coordinate");
        column3.setWidth(200);
        TreeColumn column4 = new TreeColumn(dependencyTree, SWT.RIGHT);
        column4.setAlignment(SWT.LEFT);
        column4.setText("CALL");
        column4.setWidth(50);
        TreeColumn column5 = new TreeColumn(dependencyTree, SWT.RIGHT);
        column5.setAlignment(SWT.LEFT);
        column5.setText("OVRM");
        column5.setWidth(50);
        TreeColumn column6 = new TreeColumn(dependencyTree, SWT.RIGHT);
        column6.setAlignment(SWT.LEFT);
        column6.setText("OVRP");
        column6.setWidth(50);
        TreeColumn column7 = new TreeColumn(dependencyTree, SWT.RIGHT);
        column7.setAlignment(SWT.LEFT);
        column7.setText("OVRD");
        column7.setWidth(50);
        TreeColumn column8 = new TreeColumn(dependencyTree, SWT.RIGHT);
        column8.setAlignment(SWT.LEFT);
        column8.setText("SELFC");
        column8.setWidth(50);
        TreeColumn column9 = new TreeColumn(dependencyTree, SWT.RIGHT);
        column9.setAlignment(SWT.LEFT);
        column9.setText("SELFM");
        column9.setWidth(50);

        treeViewer = new TreeViewer(dependencyTree);

        updateContent();
    }

    private void updateContent() {
        if (parent != null) {
            parent.getDisplay().syncExec(new Runnable() {

                @Override
                public void run() {
                    List<Project> projects = createModel();

                    treeViewer.setContentProvider(new ContentProvider());
                    treeViewer.setLabelProvider(new LabelProvider());
                    treeViewer.setInput(projects);
                    treeViewer.refresh();
                }

            });
        }

    }

    public void refreshTable() {
        treeViewer.setLabelProvider(new LabelProvider());
        treeViewer.refresh();
    }

    private List<Project> createModel() {
        List<Project> result = new ArrayList<Project>();

        Set<DependencyInfo> projectsDependencyInfos = eclipseDependencyListener.getProjects();
        for (DependencyInfo projectDI : projectsDependencyInfos) {
            Project project = new Project(projectDI);
            List<Dependency> dependencies = new ArrayList<Dependency>();
            Set<DependencyInfo> dependenciesForProject = eclipseDependencyListener.getDependenciesForProject(projectDI);
            for (DependencyInfo dependencyInfo : dependenciesForProject) {
                if (!dependencyInfo.equals(projectDI)) {
                    dependencies.add(new Dependency(dependencyInfo, project));
                }
            }
            project.setDependencies(dependencies);
            result.add(project);
        }
        return result;
    }

    public class Project {
        private List<Dependency> dependencies;
        private DependencyInfo projectDependencyInfo;

        public Project(DependencyInfo projectDependencyInfo) {
            super();
            this.projectDependencyInfo = projectDependencyInfo;
        }

        public List<Dependency> getDependencies() {
            return dependencies;
        }

        public void setDependencies(List<Dependency> dependencies) {
            this.dependencies = dependencies;
        }

        public DependencyInfo getProjectDependencyInfo() {
            return projectDependencyInfo;
        }

        public void setProjectDependencyInfo(DependencyInfo projectDependencyInfo) {
            this.projectDependencyInfo = projectDependencyInfo;
        }
    }

    public class Dependency {
        private DependencyInfo dependencyInfo;
        private Project parent;

        public Dependency(DependencyInfo dependencyInfo, Project parent) {
            super();
            this.dependencyInfo = dependencyInfo;
            this.parent = parent;
        }

        public DependencyInfo getDependencyInfo() {
            return dependencyInfo;
        }

        public void setDependencyInfo(DependencyInfo dependencyInfo) {
            this.dependencyInfo = dependencyInfo;
        }

        public Project getParent() {
            return parent;
        }

        public void setParent(Project parent) {
            this.parent = parent;
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    public class LabelProvider implements ITableLabelProvider {

        @Override
        public void addListener(ILabelProviderListener listener) {
            // TODO Auto-generated method stub
        }

        @Override
        public void dispose() {
            // TODO Auto-generated method stub
        }

        @Override
        public boolean isLabelProperty(Object element, String property) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void removeListener(ILabelProviderListener listener) {
            // TODO Auto-generated method stub

        }

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            switch (columnIndex) {
            case 0:
                if (element instanceof Project) {
                    return imageProvider.provideImage(IMG_PROJECT);
                }
                if (element instanceof Dependency) {
                    Dependency dependency = (Dependency) element;
                    return getImageForDependencyTyp(dependency.getDependencyInfo());
                }
            }
            return null;
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            switch (columnIndex) {
            case 0:
                if (element instanceof Project) {
                    return ((Project) element).getProjectDependencyInfo().getFile().getName();
                }
                if (element instanceof Dependency) {
                    return ((Dependency) element).getDependencyInfo().getFile().getName();
                }
            case 1:
                if (element instanceof Dependency) {
                    Dependency dependency = (Dependency) element;
                    Optional<ProjectCoordinate> projectCoordinate = mappingProvider
                            .searchForProjectCoordinate(dependency.getDependencyInfo());
                    if (projectCoordinate.isPresent()) {
                        return projectCoordinate.get().toString();
                    } else {
                        return null;
                    }
                }
            case 2:
                if (element instanceof Dependency) {
                    Dependency dependency = (Dependency) element;
                    Optional<ProjectCoordinate> projectCoordinate = mappingProvider
                            .searchForProjectCoordinate(dependency.getDependencyInfo());
                    if (projectCoordinate.isPresent()) {
                        Optional<ModelArchiveCoordinate> findBestModelArchive = modelRepository.findBestModelArchive(
                                projectCoordinate.get(), Constants.CLASS_CALL_MODELS);
                        if (findBestModelArchive.isPresent()) {
                            ModelArchiveCoordinate modelArchiveCoordinate = findBestModelArchive.get();
                            String s = modelArchiveCoordinate.getGroupId() + ":" + modelArchiveCoordinate.getArtifactId() + ":" + modelArchiveCoordinate.getExtension();
                            
                            if (s.equals("3.0.0")){
                                System.out.println("Blöd");
                            }
                            
                            
                            return s;
                        }
                    } else {
                        return null;
                    }
                }
            case 3:
                if (element instanceof Dependency) {
                    Dependency dependency = (Dependency) element;
                    Optional<ProjectCoordinate> projectCoordinate = mappingProvider
                            .searchForProjectCoordinate(dependency.getDependencyInfo());
                    if (projectCoordinate.isPresent()) {
                        Optional<ModelArchiveCoordinate> findBestModelArchive = modelRepository.findBestModelArchive(
                                projectCoordinate.get(), Constants.CLASS_CALL_MODELS);
                        if (findBestModelArchive.isPresent()) {
                            return findBestModelArchive.get().getVersion();
                        }
                    } else {
                        return null;
                    }
                }
            case 4:
                if (element instanceof Dependency) {
                    Dependency dependency = (Dependency) element;
                    Optional<ProjectCoordinate> projectCoordinate = mappingProvider
                            .searchForProjectCoordinate(dependency.getDependencyInfo());
                    if (projectCoordinate.isPresent()) {
                        Optional<ModelArchiveCoordinate> findBestModelArchive = modelRepository.findBestModelArchive(
                                projectCoordinate.get(), Constants.CLASS_OVRM_MODEL);
                        if (findBestModelArchive.isPresent()) {
                            return findBestModelArchive.get().getVersion();
                        }
                    } else {
                        return null;
                    }
                }
            case 5:
                if (element instanceof Dependency) {
                    Dependency dependency = (Dependency) element;
                    Optional<ProjectCoordinate> projectCoordinate = mappingProvider
                            .searchForProjectCoordinate(dependency.getDependencyInfo());
                    if (projectCoordinate.isPresent()) {
                        Optional<ModelArchiveCoordinate> findBestModelArchive = modelRepository.findBestModelArchive(
                                projectCoordinate.get(), Constants.CLASS_OVRP_MODEL);
                        if (findBestModelArchive.isPresent()) {
                            return findBestModelArchive.get().getVersion();
                        }
                    } else {
                        return null;
                    }
                }
            case 6:
                if (element instanceof Dependency) {
                    Dependency dependency = (Dependency) element;
                    Optional<ProjectCoordinate> projectCoordinate = mappingProvider
                            .searchForProjectCoordinate(dependency.getDependencyInfo());
                    if (projectCoordinate.isPresent()) {
                        Optional<ModelArchiveCoordinate> findBestModelArchive = modelRepository.findBestModelArchive(
                                projectCoordinate.get(), Constants.CLASS_OVRD_MODEL);
                        if (findBestModelArchive.isPresent()) {
                            return findBestModelArchive.get().getVersion();
                        }
                    } else {
                        return null;
                    }
                }
            case 7:
                if (element instanceof Dependency) {
                    Dependency dependency = (Dependency) element;
                    Optional<ProjectCoordinate> projectCoordinate = mappingProvider
                            .searchForProjectCoordinate(dependency.getDependencyInfo());
                    if (projectCoordinate.isPresent()) {
                        Optional<ModelArchiveCoordinate> findBestModelArchive = modelRepository.findBestModelArchive(
                                projectCoordinate.get(), Constants.CLASS_SELFC_MODEL);
                        if (findBestModelArchive.isPresent()) {
                            return findBestModelArchive.get().getVersion();
                        }
                    } else {
                        return null;
                    }
                }
            case 8:
                if (element instanceof Dependency) {
                    Dependency dependency = (Dependency) element;
                    Optional<ProjectCoordinate> projectCoordinate = mappingProvider
                            .searchForProjectCoordinate(dependency.getDependencyInfo());
                    if (projectCoordinate.isPresent()) {
                        Optional<ModelArchiveCoordinate> findBestModelArchive = modelRepository.findBestModelArchive(
                                projectCoordinate.get(), Constants.CLASS_SELFM_MODEL);
                        if (findBestModelArchive.isPresent()) {
                            return findBestModelArchive.get().getVersion();
                        }
                    } else {
                        return null;
                    }
                }
            }
            return null;
        }

        private Image getImageForDependencyTyp(final DependencyInfo dependencyInfo) {
            switch (dependencyInfo.getType()) {
            case JRE:
                return imageProvider.provideImage(IMG_JRE);
            case JAR:
                return imageProvider.provideImage(IMG_JAR);
            case PROJECT:
                return imageProvider.provideImage(IMG_PROJECT);
            default:
                return null;
            }
        }

    }

    public class ContentProvider implements ITreeContentProvider {

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        @Override
        public Object[] getElements(Object inputElement) {
            return getChildren(inputElement);
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof List) {
                return ((List<?>) parentElement).toArray();
            }
            if (parentElement instanceof Project) {
                return ((Project) parentElement).getDependencies().toArray();
            }
            return new Object[0];
        }

        @Override
        public Object getParent(Object element) {
            if (element instanceof Dependency)
                return ((Dependency) element).getParent();
            return null;
        }

        @Override
        public boolean hasChildren(Object element) {
            if (element instanceof List) {
                return !((List<?>) element).isEmpty();
            }
            if (element instanceof Project) {
                return !((Project) element).getDependencies().isEmpty();
            }
            return false;
        }

    }

    @Override
    public void setFocus() {
        treeViewer.getControl().setFocus();
    }

}
