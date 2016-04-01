/**
 * Copyright (c) 2014 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.testing;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.junit.rules.ExternalResource;

public class RetainSystemProperties extends ExternalResource {

    private final ConcurrentMap<String, String> originalProperties = new ConcurrentHashMap<>();

    public String setProperty(String key, String value) {
        String previousValue = System.setProperty(key, value);
        originalProperties.putIfAbsent(key, value);
        return previousValue;
    }

    @Override
    protected void after() {
        for (Entry<String, String> entry : originalProperties.entrySet()) {
            System.setProperty(entry.getKey(), entry.getValue());
        }
    }
}
