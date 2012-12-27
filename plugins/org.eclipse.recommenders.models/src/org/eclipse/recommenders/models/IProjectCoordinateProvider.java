package org.eclipse.recommenders.models;

import com.google.common.base.Optional;

// REVIEW: I don't like map/mapper/mapping. It's as generic as *Resolver, *Provider and friends and does not give a reader a good understanding what this class is about
// used *Provider here to keep naming in-line with IModelProvider
public interface IProjectCoordinateProvider {

    Optional<ProjectCoordinate> map(Object ideElement);

}
