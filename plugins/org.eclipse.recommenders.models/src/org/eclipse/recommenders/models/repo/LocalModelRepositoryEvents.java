package org.eclipse.recommenders.models.repo;


import examples.UsingLocalRepository;

/**
 * These events are fired whenever the state of the model repository or its contents have changed to allow others
 * participants to respond to these state changes. Participants can register by subscribing themselves are listeners to
 * the Recommenders' workbench-wide event bus and adding corresponding listener methods like :
 * 
 * <pre>
 * @Subscribe
 * onModelUpdated(ModelUpdatedEvent e){...}
 * </pre>
 * 
 * @see {@link UsingLocalRepository} for more example usages.
 */
public class LocalModelRepositoryEvents {

    private static class RepositoryEvent {

        public LocalModelRepository repository;

        public RepositoryEvent(LocalModelRepository repository) {
            this.repository = repository;
        }
    }

    public static class RemoteRepositoryChangedEvent {

        public LocalModelRepository repository;

        public RemoteRepositoryChangedEvent(LocalModelRepository repository) {
            this.repository = repository;
        }
    }

    /**
     * Fired when the given model repository instance was created.
     */
    // REVIEW: not sure that event makes much sense. but it may be used to
    public static class RepositoryCreatedEvent {

        public LocalModelRepository repository;

        public RepositoryCreatedEvent(LocalModelRepository repository) {
            this.repository = repository;
        }
    }

    /**
     * Fired when the given repository is shutdown.
     */
    // REVIEW: not sure we need that event. But if the repo closes, some client may perform some cleanup operation
    public static class RepositoryShutdownEvent {

        public LocalModelRepository repository;

        public RepositoryShutdownEvent(LocalModelRepository repository) {
            this.repository = repository;
        }
    }

    /**
     * Fired whenever a new model archive was downloaded and installed into the given repository.
     */
    public static class ModelUpdatedEvent extends RepositoryEvent {

        public Coordinate model;

        public ModelUpdatedEvent(LocalModelRepository repository, Coordinate model) {
            super(repository);
            this.model = model;
        }
    }

    /**
     * Fired when a new model search index was successfully downloaded and installed into the give repository.
     */
    public static class IndexUpdatedEvent extends RepositoryEvent {

        public Coordinate index;

        public IndexUpdatedEvent(LocalModelRepository repository, Coordinate index) {
            super(repository);
            this.index = index;
        }
    }

}