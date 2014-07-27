package org.eclipse.recommenders.snipmatch;

public class SnipmatchContext implements ISnipmatchContext {

    private final String userQuery;
    private final LocationConstraint locationConstraint;

    public SnipmatchContext(String userQuery, LocationConstraint locationConstraint) {
        this.userQuery = userQuery;
        this.locationConstraint = locationConstraint;
    }

    @Override
    public String getUserQuery() {
        return userQuery;
    }

    @Override
    public LocationConstraint getLocationConstraint() {
        return locationConstraint;
    }

}
