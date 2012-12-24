package org.eclipse.recommenders.models.repo;

import examples.UsingModelCache;

/**
 * These events are fired whenever the state of the model cache or its contents have changed to allow others
 * participants to respond to these state changes. Participants can register by subscribing themselves as listeners to
 * the Recommenders' workbench-wide event bus and adding corresponding listener methods like :
 * 
 * <pre>
 * @Subscribe
 * onEvent(ModelUpdatedEvent e){...}
 * </pre>
 * 
 * @see {@link UsingModelCache} for more example usages.
 */
public class ModelCacheEvents {

    public abstract static class ModelCacheEvent {

        public ModelCache cache;

        public ModelCacheEvent(ModelCache cache) {
            this.cache = cache;
        }
    }

    public static class RemoteRepositoryChangedEvent {

        public ModelCache repository;

        public RemoteRepositoryChangedEvent(ModelCache cache) {
            this.repository = repository;
        }
    }

    /**
     * Fired when the given model repository instance was created.
     */
    // REVIEW: not sure that event makes much sense. but it may be used to
    public static class ModelCacheStartedEvent {

        public ModelCache cache;

        public ModelCacheStartedEvent(ModelCache cache) {
            this.cache = cache;
        }
    }

    /**
     * Fired when the given cache is shutdown.
     */
    // REVIEW: not sure we need that event. But if the cache closes, some client may perform some cleanup operation
    public static class ModelCacheClosedEvent {

        public ModelCache cache;

        public ModelCacheClosedEvent(ModelCache cache) {
            this.cache = cache;
        }
    }

    /**
     * Fired whenever an older model archive was replaced by a newer model archive.
     */
    public static class ModelArchiveUpdatedEvent extends ModelCacheEvent {

        public Coordinate model;

        public ModelArchiveUpdatedEvent(ModelCache cache, Coordinate model) {
            super(cache);
            this.model = model;
        }
    }

    /**
     * Fired whenever a new model archive was downloaded and installed into the given cache.
     */
    public static class ModelArchiveDownloadedEvent extends ModelCacheEvent {

        public Coordinate model;

        public ModelArchiveDownloadedEvent(ModelCache cache, Coordinate model) {
            super(cache);
            this.model = model;
        }
    }

    /**
     * Fired when a new model search index was successfully downloaded and installed into the give repository.
     */
    public static class IndexUpdatedEvent extends ModelCacheEvent {

        public Coordinate index;

        public IndexUpdatedEvent(ModelCache cache, Coordinate index) {
            super(cache);
            this.index = index;
        }
    }

}