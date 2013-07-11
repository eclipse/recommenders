package org.eclipse.recommenders.models;

import org.eclipse.recommenders.utils.names.IName;

/**
 * An {@link IBasedName} is an identifier qualified with a {@link ProjectCoordinate}. The identifier type can be
 * arbitrary but usually is a sub-type of {@link IName}, hence, the method to obtain the identifier is called
 * {@link #getName()} instead of {@code getIdentifier()}. The {@link ProjectCoordinate} is required to find the right
 * recommendation model for the given identifier. It is in the responsibility of the recommender to qualify the type it
 * wants to make recommendations for. Mapping a {@link ProjectCoordinate} to the actual {@link ModelArchiveCoordinate}
 * is done by the {@link IModelProvider}.
 * 
 * @see IModelProvider#acquireModel(IBasedName)
 */
public interface IBasedName<T> {

    /**
     * Returns the relative part of this name which must not be <code>null</code>.
     */
    T getName();

    /**
     * Returns the base, i.e., the project coordinate, of this name. The coordinate may default to
     * {@link ProjectCoordinate#UNKNOWN} but must not be <code>null</code>.
     */
    ProjectCoordinate getBase();

}
