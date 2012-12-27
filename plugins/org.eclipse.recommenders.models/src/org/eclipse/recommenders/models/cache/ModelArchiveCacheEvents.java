package org.eclipse.recommenders.models.cache;

import examples.UsingModelArchiveCache;

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
 * @see {@link UsingModelArchiveCache} for more example usages.
 */
public class ModelArchiveCacheEvents {

    public abstract static class ModelCacheEvent {

        public ModelArchiveCache cache;

        public ModelCacheEvent(ModelArchiveCache cache) {
            this.cache = cache;
        }
    }

    public static class RemoteRepositoryChangedEvent {

        public ModelArchiveCache cache;

        public RemoteRepositoryChangedEvent(ModelArchiveCache cache) {
            this.cache = cache;
        }
    }

    /**
     * Fired when the given model repository instance was created.
     */
    // REVIEW: not sure that event makes much sense. but it may be used to
    public static class ModelCacheStartedEvent {

        public ModelArchiveCache cache;

        public ModelCacheStartedEvent(ModelArchiveCache cache) {
            this.cache = cache;
        }
    }

    /**
     * Fired when the given cache is shutdown.
     */
    // REVIEW: not sure we need that event. But if the cache closes, some client may perform some cleanup operation
    public static class ModelCacheClosedEvent {

        public ModelArchiveCache cache;

        public ModelCacheClosedEvent(ModelArchiveCache cache) {
            this.cache = cache;
        }
    }

    /**
     * Fired whenever an older model archive was replaced by a newer model archive.
     */
    public static class ModelArchiveUpdatedEvent extends ModelCacheEvent {

        public ModelArchiveCoordinate model;

        public ModelArchiveUpdatedEvent(ModelArchiveCache cache, ModelArchiveCoordinate model) {
            super(cache);
            this.model = model;
        }
    }

    /**
     * Fired whenever a new model archive was downloaded and installed into the given cache.
     */
    public static class ModelArchiveDownloadedEvent extends ModelCacheEvent {

        public ModelArchiveCoordinate model;

        public ModelArchiveDownloadedEvent(ModelArchiveCache cache, ModelArchiveCoordinate model) {
            super(cache);
            this.model = model;
        }
    }

    /**
     * Fired when a new model search index was successfully downloaded and installed into the give repository.
     */
    public static class IndexUpdatedEvent extends ModelCacheEvent {

        public ModelArchiveCoordinate index;

        public IndexUpdatedEvent(ModelArchiveCache cache, ModelArchiveCoordinate index) {
            super(cache);
            this.index = index;
        }
    }

}