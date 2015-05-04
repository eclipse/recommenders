/**
 * Copyright (c) 2015 Codetrails GmbH. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Johannes Dorn - initial API and implementation.
 */
package org.eclipse.recommenders.internal.news.rcp;

import javax.inject.Inject;

import org.eclipse.core.expressions.PropertyTester;

public class VisibleTester extends PropertyTester {

    private NewsRcpPreferences preferences;

    @Inject
    public VisibleTester(NewsRcpPreferences preferences) {
        this.preferences = preferences;
    }

    @Override
    public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
        return preferences.isEnabled();
    }

}
