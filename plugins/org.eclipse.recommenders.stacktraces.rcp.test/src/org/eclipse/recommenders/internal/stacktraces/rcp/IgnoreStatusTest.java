/**
 * Copyright (c) 2014 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Haftstein - initial tests.
 */
package org.eclipse.recommenders.internal.stacktraces.rcp;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.recommenders.internal.stacktraces.rcp.IgnoreStatusChecker.IgnoreValue;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class IgnoreStatusTest {

    private static IStatus mockStatus(final String pluginId, final String message) {
        IStatus mock = mock(IStatus.class);
        when(mock.getPlugin()).then(new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return pluginId;
            }
        });
        when(mock.getMessage()).then(new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return message;
            }
        });
        return mock;
    }

    @Test
    public void testIgnoreOnlyPluginId() {
        IgnoreValue value = new IgnoreValue("plugin.id", null);
        IStatus status = mockStatus("plugin.id", "some message");
        assertTrue(IgnoreStatusChecker.ignoredValueMatches(value, status));
    }

    @Test
    public void testIgnoreOnlyMessage() {
        IgnoreValue value = new IgnoreValue(null, "some message");
        IStatus status = mockStatus("plugin.id", "some message");
        assertTrue(IgnoreStatusChecker.ignoredValueMatches(value, status));
    }

    @Test
    public void testNoMatchWithDifferendPluginId() {
        IgnoreValue value = new IgnoreValue("plugin.id.other", "some message");
        IStatus status = mockStatus("plugin.id", "some message");
        assertFalse(IgnoreStatusChecker.ignoredValueMatches(value, status));
    }

    @Test
    public void testNoMatchWithDifferendMessage() {
        IgnoreValue value = new IgnoreValue("plugin.id", "some other message");
        IStatus status = mockStatus("plugin.id", "some message");
        assertFalse(IgnoreStatusChecker.ignoredValueMatches(value, status));
    }
}
