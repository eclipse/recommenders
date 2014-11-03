package org.eclipse.recommenders.internal.snipmatch.rcp;

import static org.mockito.Mockito.mock;

import org.eclipse.jdt.internal.core.util.SimpleDocument;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.recommenders.rcp.SharedImages;
import org.eclipse.recommenders.snipmatch.rcp.model.SnippetRepositoryConfigurations;
import org.eclipse.swt.graphics.Point;
import org.junit.Test;
import org.mockito.Mockito;

public class SnipmatchContentAssistProcessorTest {

    @Test
    public void test() {
        SnippetRepositoryConfigurations configs = mock(SnippetRepositoryConfigurations.class);
        Repositories repos = mock(Repositories.class);
        SnipmatchContentAssistProcessor sut = new SnipmatchContentAssistProcessor(configs, repos, new SharedImages());
        // Mockito.when(repos.getRepository(Mockito.eq(""))).thenReturn(Optional.of(null));

        ITextViewer viewer = mock(ITextViewer.class);
        Mockito.when(viewer.getSelectedRange()).thenReturn(new Point(2, 0));
        Mockito.when(viewer.getDocument()).thenReturn(new SimpleDocument(""));

        JavaContentAssistInvocationContext context = mock(JavaContentAssistInvocationContext.class);

        sut.setContext(context);
        sut.setTerms("a");

        sut.computeCompletionProposals(viewer, 0);
    }
}
