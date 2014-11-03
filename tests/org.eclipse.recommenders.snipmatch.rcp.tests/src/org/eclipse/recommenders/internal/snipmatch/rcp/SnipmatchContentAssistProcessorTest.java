package org.eclipse.recommenders.internal.snipmatch.rcp;

import static org.eclipse.recommenders.models.DependencyInfo.PROJECT_NAME;
import static org.mockito.Mockito.*;

import java.io.File;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.recommenders.models.DependencyInfo;
import org.eclipse.recommenders.models.DependencyType;
import org.eclipse.recommenders.models.IDependencyListener;
import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.models.rcp.IProjectCoordinateProvider;
import org.eclipse.recommenders.rcp.SharedImages;
import org.eclipse.recommenders.snipmatch.ISnippetRepository;
import org.eclipse.recommenders.snipmatch.model.SnippetRepositoryConfiguration;
import org.eclipse.recommenders.snipmatch.rcp.model.SnipmatchRcpModelFactory;
import org.eclipse.recommenders.snipmatch.rcp.model.SnippetRepositoryConfigurations;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

public class SnipmatchContentAssistProcessorTest {

    private static final ProjectCoordinate EXAMPLE_COORDINATE = new ProjectCoordinate("org.example", "example", "1.0.0");
    private static final File PROJECT_DIR = new File("/tmp/example");

    @Test
    public void test() {

        Document document = new Document("");
        Point selectedRange = new Point(2, 0);
        int invocationOffset = 0;

        SnippetRepositoryConfigurations configs = SnipmatchRcpModelFactory.eINSTANCE
                .createSnippetRepositoryConfigurations();
        SnippetRepositoryConfiguration config = mock(SnippetRepositoryConfiguration.class);
        when(config.getDefaultPriority()).thenReturn(0);
        when(config.getId()).thenReturn("id");
        configs.getRepos().add(config);

        Repositories repos = mock(Repositories.class);
        ISnippetRepository snippetRepository = mock(ISnippetRepository.class);
        when(repos.getRepository("id")).thenReturn(Optional.of(snippetRepository));

        DependencyInfo projectInfo = new DependencyInfo(PROJECT_DIR, DependencyType.PROJECT, ImmutableMap.of(
                PROJECT_NAME, "example"));
        IDependencyListener dependencyListener = mock(IDependencyListener.class);
        when(dependencyListener.getDependenciesForProject(projectInfo)).thenReturn(ImmutableSet.<DependencyInfo>of());

        IProjectCoordinateProvider pcProvider = mock(IProjectCoordinateProvider.class);
        when(pcProvider.resolve(projectInfo)).thenReturn(Optional.of(EXAMPLE_COORDINATE));

        SnipmatchContentAssistProcessor sut = new SnipmatchContentAssistProcessor(configs, repos, pcProvider,
                dependencyListener, new SharedImages());

        IJavaProject javaProject = mock(IJavaProject.class, RETURNS_DEEP_STUBS);
        when(javaProject.getProject().getLocation().toFile()).thenReturn(PROJECT_DIR);
        when(javaProject.getElementName()).thenReturn("example");

        ICompilationUnit compilationUnit = mock(ICompilationUnit.class);
        when(compilationUnit.getJavaProject()).thenReturn(javaProject);

        ITextViewer viewer = mock(ITextViewer.class);
        when(viewer.getSelectedRange()).thenReturn(selectedRange);
        when(viewer.getDocument()).thenReturn(document);

        JavaContentAssistInvocationContext context = Mockito.spy(new JavaContentAssistInvocationContext(viewer,
                invocationOffset, mock(IEditorPart.class)));
        doReturn(compilationUnit).when(context).getCompilationUnit();

        sut.setContext(context);
        sut.setTerms("a");
        ICompletionProposal[] result = sut.computeCompletionProposals(viewer, invocationOffset);
    }
}
