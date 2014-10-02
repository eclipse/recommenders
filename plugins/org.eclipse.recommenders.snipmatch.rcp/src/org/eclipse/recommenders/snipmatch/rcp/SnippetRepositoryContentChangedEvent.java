package org.eclipse.recommenders.snipmatch.rcp;

import org.eclipse.recommenders.snipmatch.ISnippetRepository;

/**
 * Triggered when a snippet was imported.
 */
public class SnippetRepositoryContentChangedEvent {
    private final ISnippetRepository repo;

    public SnippetRepositoryContentChangedEvent(ISnippetRepository repo) {
        this.repo = repo;
    }

    public ISnippetRepository getRepository() {
        return repo;
    }
}