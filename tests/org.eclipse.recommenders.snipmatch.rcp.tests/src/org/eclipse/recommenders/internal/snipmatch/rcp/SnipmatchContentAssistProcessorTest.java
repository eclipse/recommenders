package org.eclipse.recommenders.internal.snipmatch.rcp;

import static org.eclipse.recommenders.models.DependencyInfo.PROJECT_NAME;
import static org.eclipse.recommenders.snipmatch.rcp.util.ProposalMatcher.proposal;
import static org.eclipse.recommenders.snipmatch.rcp.util.SearchContextMatcher.context;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.ui.text.IJavaPartitions;
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
import org.eclipse.recommenders.snipmatch.ISnippet;
import org.eclipse.recommenders.snipmatch.ISnippetRepository;
import org.eclipse.recommenders.snipmatch.Location;
import org.eclipse.recommenders.snipmatch.Snippet;
import org.eclipse.recommenders.snipmatch.rcp.model.EclipseGitSnippetRepositoryConfiguration;
import org.eclipse.recommenders.snipmatch.rcp.model.SnipmatchRcpModelFactory;
import org.eclipse.recommenders.snipmatch.rcp.model.SnippetRepositoryConfigurations;
import org.eclipse.recommenders.utils.Recommendation;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.junit.Test;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class SnipmatchContentAssistProcessorTest {

    private static final String SNIPPET_CODE = "code";
    private static final String ANY_SEARCH_TERM = "";
    private static final String SEARCH_TERM = "searchTerm";
    private static final List<String> NO_EXTRA_SEARCH_TERMS = Collections.emptyList();
    private static final List<String> NO_TAGS = Collections.emptyList();
    private static final String NO_SELECTION = null;

    private static final Document DOCUMENT = new Document("Document");

    private static final ProjectCoordinate EXAMPLE_COORDINATE = new ProjectCoordinate("org.example", "example", "1.0.0");
    private static final Set<ProjectCoordinate> NO_PROJECT_COORDINATES = ImmutableSet.of();
    private static final Set<ProjectCoordinate> PROJECT_COORDINATES = ImmutableSet.of(EXAMPLE_COORDINATE);

    private static final File PROJECT_DIR = new File("/tmp/example");
    private static final DependencyInfo PROJECT_INFO = new DependencyInfo(PROJECT_DIR, DependencyType.PROJECT,
            ImmutableMap.of(PROJECT_NAME, "example"));

    private static final ImmutableSet<DependencyInfo> DEPENDENCIES = ImmutableSet.of(PROJECT_INFO);
    private static final ImmutableSet<DependencyInfo> NO_DEPENDENCIES = ImmutableSet.of();

    private IDependencyListener dependencyListener;
    private IProjectCoordinateProvider pcProvider;
    private ICompilationUnit compilationUnit;
    private SnippetRepositoryConfigurations configs;
    private Repositories repos;
    private SnipmatchContentAssistProcessor sut;
    private ITextViewer viewer;

    public void setUp(Document document, Point selectedRange, ImmutableSet<DependencyInfo> dependencies) {
        dependencyListener = mock(IDependencyListener.class);

        pcProvider = mock(IProjectCoordinateProvider.class);

        IJavaProject javaProject = mock(IJavaProject.class, RETURNS_DEEP_STUBS);
        when(javaProject.getProject().getLocation().toFile()).thenReturn(PROJECT_DIR);
        when(javaProject.getElementName()).thenReturn("example");

        when(pcProvider.resolve(PROJECT_INFO)).thenReturn(Optional.of(EXAMPLE_COORDINATE));
        when(dependencyListener.getDependenciesForProject(PROJECT_INFO)).thenReturn(dependencies);

        compilationUnit = mock(ICompilationUnit.class);
        when(compilationUnit.getJavaProject()).thenReturn(javaProject);

        configs = SnipmatchRcpModelFactory.eINSTANCE.createSnippetRepositoryConfigurations();
        repos = mock(Repositories.class);

        viewer = mock(ITextViewer.class);
        when(viewer.getDocument()).thenReturn(document);
        when(viewer.getSelectedRange()).thenReturn(selectedRange);

        JavaContentAssistInvocationContext context = spy(new JavaContentAssistInvocationContext(viewer, 0,
                mock(IEditorPart.class)));
        doReturn(compilationUnit).when(context).getCompilationUnit();

        sut = new SnipmatchContentAssistProcessor(configs, repos, pcProvider, dependencyListener, new SharedImages());

        sut.setContext(context);
    }

    @Test
    public void testEmptySearchText() {
        setUp(DOCUMENT, new Point(2, 0), NO_DEPENDENCIES);

        ISnippetRepository repo = mockRepository(10, ANY_SEARCH_TERM, Location.FILE, NO_PROJECT_COORDINATES, "snippet");

        sut.setTerms("");

        verifyZeroInteractions(repo);
        List<ICompletionProposal> result = Arrays.asList(sut.computeCompletionProposals(viewer, 0));
        assertThat(result.isEmpty(), is(true));
    }

    @Test
    public void testSnippetsAreFound() {
        setUp(DOCUMENT, new Point(2, 0), NO_DEPENDENCIES);

        ISnippetRepository repo = mockRepository(10, SEARCH_TERM, Location.FILE, NO_PROJECT_COORDINATES, "snippet");

        sut.setTerms(SEARCH_TERM);

        List<ICompletionProposal> result = Arrays.asList(sut.computeCompletionProposals(viewer, 0));

        verify(repo).search(argThat(context(SEARCH_TERM, Location.FILE, NO_PROJECT_COORDINATES)));
        assertThat(result, hasItem(proposal("snippet", 0, NO_SELECTION)));
        assertThat(result.size(), is(1));
    }

    @Test
    public void testSnippetsInTwoRepos() {
        setUp(DOCUMENT, new Point(2, 0), NO_DEPENDENCIES);

        ISnippetRepository repo1 = mockRepository(10, SEARCH_TERM, Location.FILE, NO_PROJECT_COORDINATES, "snippet1");
        ISnippetRepository repo2 = mockRepository(20, SEARCH_TERM, Location.FILE, NO_PROJECT_COORDINATES, "snippet2");

        sut.setTerms(SEARCH_TERM);

        List<ICompletionProposal> result = Arrays.asList(sut.computeCompletionProposals(viewer, 0));

        verify(repo1).search(argThat(context(SEARCH_TERM, Location.FILE, NO_PROJECT_COORDINATES)));
        verify(repo2).search(argThat(context(SEARCH_TERM, Location.FILE, NO_PROJECT_COORDINATES)));
        assertThat(result, hasItem(proposal("snippet1", 0, NO_SELECTION)));
        assertThat(result, hasItem(proposal("snippet2", 1, NO_SELECTION)));
        assertThat(result.size(), is(2));
    }

    @Test
    public void testLocationIsPassedToSearch() {
        setUp(DOCUMENT, new Point(2, 0), NO_DEPENDENCIES);

        ISnippetRepository repo1 = mockRepository(10, SEARCH_TERM, Location.JAVADOC, NO_PROJECT_COORDINATES, "snippet1");
        ISnippetRepository repo2 = mockRepository(20, SEARCH_TERM, Location.FILE, NO_PROJECT_COORDINATES, "snippet2");

        sut.setTerms(SEARCH_TERM);

        List<ICompletionProposal> result = Arrays.asList(sut.computeCompletionProposals(viewer, 0));

        verify(repo1).search(argThat(context(SEARCH_TERM, Location.FILE, NO_PROJECT_COORDINATES)));
        verify(repo2).search(argThat(context(SEARCH_TERM, Location.FILE, NO_PROJECT_COORDINATES)));
        assertThat(result, hasItem(proposal("snippet2", 1, NO_SELECTION)));
        assertThat(result.size(), is(1));
    }

    @Test
    public void testSelection() {
        setUp(DOCUMENT, new Point(2, 2), NO_DEPENDENCIES);

        ISnippetRepository repo = mockRepository(10, SEARCH_TERM, Location.FILE, NO_PROJECT_COORDINATES, "snippet");

        sut.setTerms(SEARCH_TERM);

        List<ICompletionProposal> result = Arrays.asList(sut.computeCompletionProposals(viewer, 0));

        verify(repo).search(argThat(context(SEARCH_TERM, Location.FILE, NO_PROJECT_COORDINATES)));
        assertThat(result, hasItem(proposal("snippet", 0, "cu")));
        assertThat(result.size(), is(1));
    }

    @Test
    public void testDependency() throws Exception {
        Document document = spy(DOCUMENT);
        doReturn(IJavaPartitions.JAVA_DOC).when(document).getContentType(eq("___java_partitioning"), eq(0), eq(true));
        setUp(document, new Point(2, 0), DEPENDENCIES);

        ISnippetRepository repo = mockRepository(10, SEARCH_TERM, Location.JAVADOC, PROJECT_COORDINATES, "snippet");

        sut.setTerms(SEARCH_TERM);

        List<ICompletionProposal> result = Arrays.asList(sut.computeCompletionProposals(viewer, 0));

        verify(repo).search(argThat(context(SEARCH_TERM, Location.JAVADOC, PROJECT_COORDINATES)));
        assertThat(result, hasItem(proposal("snippet", 0, NO_SELECTION)));
        assertThat(result.size(), is(1));
    }

    private ISnippetRepository mockRepository(int priority, String searchTerm, Location location,
            Set<ProjectCoordinate> dependencies, String... snippetNames) {
        ArrayList<Recommendation<ISnippet>> recommendations = Lists.newArrayList();
        for (String snippetName : snippetNames) {
            recommendations.add(createRecommendation(snippetName));
        }
        ISnippetRepository repo = mock(ISnippetRepository.class);
        when(repo.search(argThat(context(searchTerm, location, dependencies)))).thenReturn(recommendations);

        String id = UUID.randomUUID().toString();
        when(repos.getRepository(id)).thenReturn(Optional.of(repo));

        EclipseGitSnippetRepositoryConfiguration config = SnipmatchRcpModelFactory.eINSTANCE
                .createEclipseGitSnippetRepositoryConfiguration();
        config.setPriority(priority);
        config.setId(id);
        configs.getRepos().add(config);

        return repo;
    }

    private Recommendation<ISnippet> createRecommendation(String name) {
        ISnippet snippet = new Snippet(UUID.randomUUID(), name, ANY_SEARCH_TERM, NO_EXTRA_SEARCH_TERMS, NO_TAGS,
                SNIPPET_CODE, Location.FILE);
        return Recommendation.newRecommendation(snippet, 1.0);
    }
}