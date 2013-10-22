package org.eclipse.recommenders.sandbox.rcp;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jdt.core.search.TypeReferenceMatch;
import org.eclipse.recommenders.completion.rcp.IRecommendersCompletionContext;
import org.eclipse.recommenders.completion.rcp.processable.SessionProcessor;

public class StaticSessionProcessor extends SessionProcessor {

    @Override
    public boolean startSession(IRecommendersCompletionContext context) {
        final IType expected = context.getExpectedType().orNull();
        if (expected == null)
            return false;

        SearchRequestor requestor = new SearchRequestor() {
            @Override
            public void acceptSearchMatch(SearchMatch match) {
                if (match instanceof TypeReferenceMatch) {
                    TypeReferenceMatch refMatch = (TypeReferenceMatch) match;
                    IMethod localElement = (IMethod) refMatch.getElement();
                    try {
                        if (Flags.isStatic(localElement.getFlags()) && Flags.isPublic(localElement.getFlags())) {
                            System.out.println("match!" + localElement);
                        }
                    } catch (JavaModelException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        SearchEngine searchEngine = new SearchEngine();
        SearchPattern pattern = SearchPattern.createPattern(expected, IJavaSearchConstants.METHOD
                | IJavaSearchConstants.RETURN_TYPE_REFERENCE);
        ICompilationUnit cu = context.getCompilationUnit();
        IJavaElement[] cp = new IJavaElement[] { cu.getJavaProject() };
        IJavaSearchScope scope = SearchEngine.createJavaSearchScope(cp);
        try {
            searchEngine.search(pattern, new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() }, scope,
                    requestor, null /* progress monitor is not used here */);
        } catch (CoreException e) {
            e.printStackTrace();
        }
        return false;
    }
}
