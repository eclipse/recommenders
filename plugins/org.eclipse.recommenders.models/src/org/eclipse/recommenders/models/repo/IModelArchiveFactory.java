package org.eclipse.recommenders.models.repo;

public interface IModelArchiveFactory<K, M> {

    IModelArchive<K, M> create(Coordinate coord, ModelCache repository);
}
