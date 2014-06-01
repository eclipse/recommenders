/**
 * Copyright (c) 2014 Olav Lenz.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Olav Lenz - initial API and implementation.
 */
package org.eclipse.recommenders.snipmatch;

import java.io.File;

import com.google.common.base.Optional;

public interface ISnippetRepositoryProvider {

    boolean isApplicable(String identifier);

    ISnippetRepositoryConfiguration convert(String stringRepresentation);

    String convert(ISnippetRepositoryConfiguration configuration);

    Optional<ISnippetRepository> create(ISnippetRepositoryConfiguration configuration, File basedir);

}
