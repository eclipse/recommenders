package org.eclipse.recommenders.models.cache;

public interface IModelArchiveFactory<K, M> {

    IModelArchive<K, M> create(ModelArchiveCoordinate coord, ModelArchiveCache repository);
}
