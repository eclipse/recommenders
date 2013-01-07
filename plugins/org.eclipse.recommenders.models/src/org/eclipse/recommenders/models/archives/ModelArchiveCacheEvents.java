package org.eclipse.recommenders.models.archives;

import examples.UsingModelArchiveCache;

/**
 * These events are fired whenever the state of the {@link ModelArchiveCache} or its contents have changed to allow
 * others participants to respond to these state changes. Participants can register by subscribing themselves as
 * listeners to the Recommenders' workbench-wide event bus and adding corresponding listener methods like :
 * 
 * <pre>
 * @Subscribe
 * onEvent(ModelArchiveUpdatedEvent e){...}
 * </pre>
 * 
 * @see {@link UsingModelArchiveCache} for more example usages.
 */
public class ModelArchiveCacheEvents {

    /**
     * Base class for all events related to {@link ModelArchiveCache}.
     */
    public abstract static class ModelArchiveCacheEvent {

        public final ModelArchiveCache cache;

        public ModelArchiveCacheEvent(ModelArchiveCache cache) {
            this.cache = cache;
        }
    }

    /**
     * Fired when the remote repository URL has changed. This usually triggers a download of the model index and may
     * cause updates of existing model archives.
     */
    public static class RemoteRepositoryChangedEvent extends ModelArchiveCacheEvent {

        public RemoteRepositoryChangedEvent(ModelArchiveCache cache) {
            super(cache);
        }
    }

    /**
     * Fired when the given model repository instance was created. Allows listeners to trigger additional actions like
     * model index updates.
     */
    public static class ModelArchiveCacheOpenedEvent extends ModelArchiveCacheEvent {

        public ModelArchiveCacheOpenedEvent(ModelArchiveCache cache) {
            super(cache);
        }
    }

    /**
     * Fired when the given cache is shutdown. Allows listeners to close other resources based on this repository like
     * search indexes.
     */
    public static class ModelArchiveCacheClosedEvent extends ModelArchiveCacheEvent {

        public ModelArchiveCacheClosedEvent(ModelArchiveCache cache) {
            super(cache);
        }
    }

    /**
     * Fired whenever an older model archive was replaced by a newer model archive.
     * <p>
     * Note that index updates are also published as {@link ModelArchiveInstalledEvent}s.
     */
    public static class ModelArchiveInstalledEvent extends ModelArchiveCacheEvent {

        public ModelArchiveCoordinate coordinate;

        public ModelArchiveInstalledEvent(ModelArchiveCache cache, ModelArchiveCoordinate model) {
            super(cache);
            this.coordinate = model;
        }
    }
}
