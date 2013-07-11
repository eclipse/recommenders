package org.eclipse.recommenders.models;

import org.eclipse.recommenders.utils.names.ITypeName;

/**
 * Represents an {@link ITypeName} qualified by a {@link ProjectCoordinate} like <i>jre:jre:1.6<i>. Project coordinates
 * are required to find the right recommendation model for the given type. It's in the responsibility of the recommender
 * to qualify the type it wants to make recommendations for.
 */
public class BasedTypeName implements IBasedName<ITypeName> {

    private final ITypeName name;
    private final ProjectCoordinate base;

    public BasedTypeName(ProjectCoordinate base, ITypeName name) {
        this.name = name;
        this.base = base;
    }

    @Override
    public ITypeName getName() {
        return name;
    }

    @Override
    public ProjectCoordinate getBase() {
        return base;
    }

}
