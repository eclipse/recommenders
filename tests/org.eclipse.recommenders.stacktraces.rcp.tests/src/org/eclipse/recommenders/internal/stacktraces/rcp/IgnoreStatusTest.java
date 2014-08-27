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

import static java.util.regex.Pattern.quote;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.recommenders.internal.stacktraces.rcp.IgnoreStatusChecker.IgnoreValue;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class IgnoreStatusTest {

    private static final int ERROR_CODE_0 = 0;
    private static final String ERROR_MESSAGE_0 = "some message";
    private static final int ERROR_CODE_1 = 1;
    private static final String ERROR_MESSAGE_1 = "some other message";
    private static final String PLUGIN_ONE = "plugin.id.one";
    private static final String PLUGIN_TWO = "plugin.id.two";

    private static IStatus mockStatus(final String pluginId, final String message, final int errorCode) {
        IStatus mock = mock(IStatus.class);
        when(mock.getPlugin()).thenAnswer(new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return pluginId;
            }
        });
        when(mock.getMessage()).thenAnswer(new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return message;
            }
        });
        when(mock.getCode()).thenAnswer(new Answer<Integer>() {

            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                return errorCode;
            }
        });
        return mock;
    }

    @Test
    public void testMatchOnlyPluginId() {
        IgnoreValue value = new IgnoreValue(quote(PLUGIN_ONE), null);
        IStatus status = mockStatus(PLUGIN_ONE, ERROR_MESSAGE_0, ERROR_CODE_0);
        assertTrue(IgnoreStatusChecker.ignoredValueMatches(value, status));
    }

    @Test
    public void testNoMatchPluginIdAndDifferentMessage() {
        IgnoreValue value = new IgnoreValue(quote(PLUGIN_ONE), quote("other message"));
        IStatus status = mockStatus(PLUGIN_ONE, ERROR_MESSAGE_0, ERROR_CODE_0);
        assertFalse(IgnoreStatusChecker.ignoredValueMatches(value, status));
    }

    @Test
    public void testNoMatchPluginIdAndDifferentMinRange() {
        IgnoreValue value = new IgnoreValue(quote(PLUGIN_ONE), null, Integer.MAX_VALUE, null);
        IStatus status = mockStatus(PLUGIN_ONE, ERROR_MESSAGE_0, ERROR_CODE_0);
        assertFalse(IgnoreStatusChecker.ignoredValueMatches(value, status));
    }

    @Test
    public void testNoMatchPluginIdAndDifferentMaxRange() {
        IgnoreValue value = new IgnoreValue(quote(PLUGIN_ONE), null, null, Integer.MIN_VALUE);
        IStatus status = mockStatus(PLUGIN_ONE, ERROR_MESSAGE_0, ERROR_CODE_0);
        assertFalse(IgnoreStatusChecker.ignoredValueMatches(value, status));
    }

    @Test
    public void testMatchOnlyMessage() {
        IgnoreValue value = new IgnoreValue(null, quote(ERROR_MESSAGE_0));
        IStatus status = mockStatus(PLUGIN_ONE, ERROR_MESSAGE_0, ERROR_CODE_0);
        assertTrue(IgnoreStatusChecker.ignoredValueMatches(value, status));
    }

    @Test
    public void testNoMatchMessageAndDifferentPluginId() {
        IgnoreValue value = new IgnoreValue("other.plugin.id", quote(ERROR_MESSAGE_0));
        IStatus status = mockStatus(PLUGIN_ONE, ERROR_MESSAGE_0, ERROR_CODE_0);
        assertFalse(IgnoreStatusChecker.ignoredValueMatches(value, status));
    }

    @Test
    public void testNoMatchMessageAndDifferentMinRange() {
        IgnoreValue value = new IgnoreValue(null, quote(ERROR_MESSAGE_0), Integer.MAX_VALUE, null);
        IStatus status = mockStatus(PLUGIN_ONE, ERROR_MESSAGE_0, ERROR_CODE_0);
        assertFalse(IgnoreStatusChecker.ignoredValueMatches(value, status));
    }

    @Test
    public void testNoMatchMessageAndDifferentMaxRange() {
        IgnoreValue value = new IgnoreValue(null, quote(ERROR_MESSAGE_0), null, Integer.MIN_VALUE);
        IStatus status = mockStatus(PLUGIN_ONE, ERROR_MESSAGE_0, ERROR_CODE_0);
        assertFalse(IgnoreStatusChecker.ignoredValueMatches(value, status));
    }

    @Test
    public void testMatchOnlyMinErrorCode() {
        IgnoreValue value = new IgnoreValue(0, null);
        IStatus status = mockStatus(PLUGIN_ONE, ERROR_MESSAGE_0, ERROR_CODE_0);
        assertTrue(IgnoreStatusChecker.ignoredValueMatches(value, status));
    }

    @Test
    public void testNoMatchMinErrorCodeAndDifferentPluginId() {
        IgnoreValue value = new IgnoreValue("other.plugin.id", null, 0, null);
        IStatus status = mockStatus(PLUGIN_ONE, ERROR_MESSAGE_0, ERROR_CODE_0);
        assertFalse(IgnoreStatusChecker.ignoredValueMatches(value, status));
    }

    @Test
    public void testNoMatchMinErrorCodeAndDifferentErrorMessage() {
        IgnoreValue value = new IgnoreValue(null, quote("other.message"), 0, null);
        IStatus status = mockStatus(PLUGIN_ONE, ERROR_MESSAGE_0, ERROR_CODE_0);
        assertFalse(IgnoreStatusChecker.ignoredValueMatches(value, status));
    }

    @Test
    public void testMatchOnlyMaxErrorCode() {
        IgnoreValue value = new IgnoreValue(null, Integer.MAX_VALUE);
        IStatus status = mockStatus(PLUGIN_ONE, ERROR_MESSAGE_0, ERROR_CODE_0);
        assertTrue(IgnoreStatusChecker.ignoredValueMatches(value, status));
    }

    @Test
    public void testNoMatchMaxErrorCodeAndDifferentPluginId() {
        IgnoreValue value = new IgnoreValue("other.plugin.id", null, null, Integer.MAX_VALUE);
        IStatus status = mockStatus(PLUGIN_ONE, ERROR_MESSAGE_0, ERROR_CODE_0);
        assertFalse(IgnoreStatusChecker.ignoredValueMatches(value, status));
    }

    @Test
    public void testNoMatchMaxErrorCodeAndDifferentErrorMessage() {
        IgnoreValue value = new IgnoreValue(null, quote("other.message"), null, Integer.MAX_VALUE);
        IStatus status = mockStatus(PLUGIN_ONE, ERROR_MESSAGE_0, ERROR_CODE_0);
        assertFalse(IgnoreStatusChecker.ignoredValueMatches(value, status));
    }

    @Test
    public void testNoMatchWithDifferentPluginId() {
        IgnoreValue value = new IgnoreValue(quote(PLUGIN_TWO), null);
        IStatus status = mockStatus(PLUGIN_ONE, ERROR_MESSAGE_0, ERROR_CODE_0);
        assertFalse(IgnoreStatusChecker.ignoredValueMatches(value, status));
    }

    @Test
    public void testNoMatchWithDifferentMessage() {
        IgnoreValue value = new IgnoreValue(null, quote(ERROR_MESSAGE_1));
        IStatus status = mockStatus(PLUGIN_ONE, ERROR_MESSAGE_0, ERROR_CODE_0);
        assertFalse(IgnoreStatusChecker.ignoredValueMatches(value, status));
    }

    @Test
    public void testNoMatchWithDifferentMinErrorCode() {
        IgnoreValue value = new IgnoreValue(ERROR_CODE_1 + 1, null);
        IStatus status = mockStatus(PLUGIN_ONE, ERROR_MESSAGE_0, ERROR_CODE_1);
        assertFalse(IgnoreStatusChecker.ignoredValueMatches(value, status));
    }

    @Test
    public void testNoMatchWithDifferentMaxErrorCode() {
        IgnoreValue value = new IgnoreValue(null, ERROR_CODE_1 - 1);
        IStatus status = mockStatus(PLUGIN_ONE, ERROR_MESSAGE_0, ERROR_CODE_1);
        assertFalse(IgnoreStatusChecker.ignoredValueMatches(value, status));
    }
}
