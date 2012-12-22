package org.eclipse.recommenders.models;

/**
 * Minimal API for model providers. Model providers may use local zip-files obtained by, e.g., a 
 * {@link org.eclipse.recommenders.models.repo.LocalModelRepository} or may directly communicate with a remote
 * web-service. 
 * <p>
 * Clients of this API are typically code completion engines and auxiliary documentation
 * providers in IDEs, or evaluation frameworks. The whole framework relies on the usage of {@link Gav}s. A {@link Gav},
 * put simple, is a maven-like identifier for a java class container (i.e., a project inside the IDE, or a jar-file).
 */

