/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Olav Lenz - initial API and implementation
 */
package org.eclipse.recommenders.models.mapping.impl;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import org.eclipse.recommenders.models.mapping.DependencyType;
import org.eclipse.recommenders.models.mapping.IDependencyInfo;
import org.eclipse.recommenders.utils.Checks;

import com.google.common.base.Optional;

public class DependencyInfo implements IDependencyInfo {

    private final File file;
    private final DependencyType type;
    private final Map<String, String> attributes;

    public DependencyInfo(File file, DependencyType type) {
        this(file, type, Collections.<String, String> emptyMap());
    }

    public DependencyInfo(File file, DependencyType type, Map<String, String> attributes) {
        this.file = file;
        this.type = type;
        this.attributes = Checks.ensureIsNotNull(attributes);
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public DependencyType getType() {
        return type;
    }

    @Override
    public Optional<String> getAttribute(String key) {
        return Optional.fromNullable(attributes.get(key));
    }

}
