package org.eclipse.recommenders.snipmatch.rcp;

import org.eclipse.recommenders.snipmatch.ISnippetRepository;

/**
 * Triggered when the snippet repository was opened to inform clients that the snippet repository is available.
 * <p>
 * Clients of this event may consider refreshing themselves whenever they receive this event. Clients get notified
 * in a background process.
 */
public class SnippetRepositoryOpenedEvent {

    private final ISnippetRepository repo;

    public SnippetRepositoryOpenedEvent(ISnippetRepository repo) {
        this.repo = repo;
    }

    public ISnippetRepository getRepository() {
        return repo;
    }
}