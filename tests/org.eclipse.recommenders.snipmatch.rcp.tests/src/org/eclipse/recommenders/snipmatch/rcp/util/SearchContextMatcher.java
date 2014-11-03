package org.eclipse.recommenders.snipmatch.rcp.util;

import static java.lang.String.format;

import java.util.Set;

import org.eclipse.recommenders.models.ProjectCoordinate;
import org.eclipse.recommenders.snipmatch.ISearchContext;
import org.eclipse.recommenders.snipmatch.Location;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import com.google.common.base.Preconditions;

public class SearchContextMatcher extends BaseMatcher<ISearchContext> {

    private final String searchTerm;
    private final Location location;
    private final Set<ProjectCoordinate> dependencies;

    private SearchContextMatcher(String searchTerm, Location location, Set<ProjectCoordinate> dependencies) {
        Preconditions.checkNotNull(searchTerm);
        Preconditions.checkNotNull(location);
        Preconditions.checkNotNull(dependencies);
        this.searchTerm = searchTerm;
        this.location = location;
        this.dependencies = dependencies;
    }

    public static SearchContextMatcher context(String searchTerm, Location location, Set<ProjectCoordinate> dependencies) {
        return new SearchContextMatcher(searchTerm, location, dependencies);
    }

    @Override
    public boolean matches(Object item) {
        if (!(item instanceof ISearchContext)) {
            return false;
        }
        ISearchContext context = (ISearchContext) item;

        if (!context.getSearchText().equals(searchTerm)) {
            return false;
        }

        if (context.getLocation() != location) {
            return false;
        }
        return true;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(getDescription(searchTerm, location, dependencies));
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        if (item instanceof ISearchContext) {
            description.appendText("was ");
            ISearchContext context = (ISearchContext) item;
            description.appendText(getDescription(context.getSearchText(), context.getLocation(),
                    context.getDependencies()));
        } else {
            super.describeMismatch(item, description);
        }
    }

    private String getDescription(String searchTerm, Location location, Set<ProjectCoordinate> dependencies) {
        return format("ISearchContext with searchtext '%s', location '%s', dependencies '%s'", searchTerm,
                location.name(), dependencies);
    }
}
