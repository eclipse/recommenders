package org.eclipse.recommenders.internal.snipmatch.rcp;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.recommenders.snipmatch.ISnippet;
import org.eclipse.recommenders.snipmatch.model.SnippetRepositoryConfiguration;
import org.eclipse.recommenders.utils.Recommendation;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProposalSorterTest {

    private final ProposalSorter sut = new ProposalSorter();

    @Test
    public void testCompareSnippetsByRepositoryPriority() throws Exception {
        SnippetProposal s1 = mockSnippetProposal(0, 1.0, "snippet");
        SnippetProposal s2 = mockSnippetProposal(1, 1.0, "snippet");

        assertThat(sut.compare(s1, s2), is(lessThan(0)));
        assertThat(sut.compare(s2, s1), is(greaterThan(0)));
    }

    @Test
    public void testCompareSnippetsByRelevance() throws Exception {
        SnippetProposal s1 = mockSnippetProposal(0, 1.0, "snippet");
        SnippetProposal s2 = mockSnippetProposal(0, 0.5, "snippet");

        assertThat(sut.compare(s1, s2), is(lessThan(0)));
        assertThat(sut.compare(s2, s1), is(greaterThan(0)));
    }

    @Test
    public void testCompareSnippetsByName() throws Exception {
        SnippetProposal s1 = mockSnippetProposal(0, 1.0, "snippet1");
        SnippetProposal s2 = mockSnippetProposal(0, 1.0, "snippet2");

        assertThat(sut.compare(s1, s2), is(lessThan(0)));
        assertThat(sut.compare(s2, s1), is(greaterThan(0)));
    }

    @Test
    public void testCompareRepos() throws Exception {
        RepositoryProposal r1 = mockRepositoryProposal(0, "repo");
        RepositoryProposal r2 = mockRepositoryProposal(1, "repo");

        assertThat(sut.compare(r1, r2), is(lessThan(0)));
        assertThat(sut.compare(r2, r1), is(greaterThan(0)));
    }

    @Test
    public void testCompareRepoWithOwnSnippet() throws Exception {
        RepositoryProposal r = mockRepositoryProposal(0, "repo");
        SnippetProposal s = mockSnippetProposal(0, 1.0, "snippet");

        assertThat(sut.compare(r, s), is(lessThan(0)));
        assertThat(sut.compare(s, r), is(greaterThan(0)));
    }

    @Test
    public void testCompareRepoWithSnippetFromHighPriorityRepo() throws Exception {
        RepositoryProposal r = mockRepositoryProposal(0, "repo");
        SnippetProposal s = mockSnippetProposal(1, 1.0, "snippet");

        assertThat(sut.compare(r, s), is(lessThan(0)));
        assertThat(sut.compare(s, r), is(greaterThan(0)));
    }

    @Test
    public void testCompareRepoWithSnippetFromLowPriorityRepo() throws Exception {
        RepositoryProposal r = mockRepositoryProposal(1, "repo");
        SnippetProposal s = mockSnippetProposal(0, 1.0, "snippet");

        assertThat(sut.compare(r, s), is(greaterThan(0)));
        assertThat(sut.compare(s, r), is(lessThan(0)));
    }

    private SnippetProposal mockSnippetProposal(int repositoryPriority, double relevance, String name) throws Exception {
        ISnippet snippet = mock(ISnippet.class);
        when(snippet.getName()).thenReturn(name);
        Recommendation<ISnippet> recommendation = mock(Recommendation.class);
        when(recommendation.getProposal()).thenReturn(snippet);
        when(recommendation.getRelevance()).thenReturn(relevance);

        Template template = mock(Template.class);
        TemplateContext context = mock(TemplateContext.class);
        Region region = mock(Region.class);
        Device device = mock(Device.class);
        Image image = new Image(device, 1, 1);
        SnippetProposal proposal = SnippetProposal.newSnippetProposal(recommendation, repositoryPriority, template,
                context, region, image);
        return proposal;
    }

    private RepositoryProposal mockRepositoryProposal(int repositoryPriority, String name) {
        SnippetRepositoryConfiguration config = mock(SnippetRepositoryConfiguration.class);
        when(config.getName()).thenReturn(name);

        return new RepositoryProposal(config, repositoryPriority, 1);
    }
}
