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
import java.util.HashMap;
import java.util.Map;

import org.eclipse.recommenders.models.mapping.ElementType;
import org.eclipse.recommenders.models.mapping.IElementInfo;

import com.google.common.base.Optional;

public class ElementInfo implements IElementInfo {

    private File file;
    private ElementType type;
    private Map<String, String> attributes = new HashMap<String, String>();

    public ElementInfo(File file, ElementType type) {
        this.file = file;
        this.type = type;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public ElementType getType() {
        return type;
    }

    @Override
    public Optional<String> getAttribute(String key) {
        return Optional.of(attributes.get(key));
    }

    public void setAttribute(String key, String value) {
        attributes.put(key, value);
    }

}
