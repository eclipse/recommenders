package org.eclipse.recommenders.snipmatch.rcp;

import org.eclipse.recommenders.snipmatch.ISnippetRepository;

/**
 * Triggered when the snippet repository was closed to inform clients that the snippet repository is currently not
 * available.
 */
public class SnippetRepositoryClosedEvent {
    private final ISnippetRepository repo;

    public SnippetRepositoryClosedEvent(ISnippetRepository repo) {
        this.repo = repo;
    }

    public ISnippetRepository getRepository() {
        return repo;
    }
}