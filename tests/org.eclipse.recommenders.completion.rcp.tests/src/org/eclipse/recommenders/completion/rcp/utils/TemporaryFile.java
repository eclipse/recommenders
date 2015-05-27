package org.eclipse.recommenders.completion.rcp.utils;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.text.java.JavaContentAssistInvocationContext;
import org.eclipse.recommenders.completion.rcp.IRecommendersCompletionContext;
import org.eclipse.recommenders.completion.rcp.RecommendersCompletionContext;
import org.eclipse.recommenders.internal.rcp.CachingAstProvider;
import org.eclipse.recommenders.testing.rcp.jdt.JavaContentAssistContextMock;

@SuppressWarnings("restriction")
public class TemporaryFile {
    private final ICompilationUnit cu;
    private final int cursor;

    public TemporaryFile(ICompilationUnit cu, int cursor) {
        this.cu = cu;
        this.cursor = cursor;
    }

    public IRecommendersCompletionContext triggerContentAssist() throws JavaModelException {
        JavaContentAssistInvocationContext javaContext = new JavaContentAssistContextMock(cu, cursor);
        return new RecommendersCompletionContext(javaContext, new CachingAstProvider());
    }
}
