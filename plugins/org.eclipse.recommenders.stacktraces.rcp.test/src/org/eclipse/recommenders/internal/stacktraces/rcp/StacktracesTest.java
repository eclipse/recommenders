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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.recommenders.internal.stacktraces.rcp.dto.StackTraceElementDto;
import org.eclipse.recommenders.internal.stacktraces.rcp.dto.StackTraceEvent;
import org.eclipse.recommenders.internal.stacktraces.rcp.dto.ThrowableDto;
import org.junit.Test;

public class StacktracesTest {

    private static String ANONYMIZED_TAG = "HIDDEN";

    private static <T> void assertNotEmpty(T[] values) {
        assertTrue(values.length > 0);
    }

    public static StackTraceEvent createTestEvent() {
        RuntimeException cause = new RuntimeException("cause");
        cause.fillInStackTrace();
        Exception exception = new RuntimeException("exception message", cause);
        exception.fillInStackTrace();
        IStatus status = new Status(IStatus.ERROR, "org.eclipse.recommenders.stacktraces", "some error message",
                exception);
        StacktracesRcpPreferences pref = new StacktracesRcpPreferences();

        return Stacktraces.createDto(status, pref);
    }

    @Test
    public void testClearEventMessage() {
        StackTraceEvent event = createTestEvent();

        Stacktraces.clearMessages(event);

        assertThat(event.message, is(ANONYMIZED_TAG));
    }

    @Test
    public void testClearThrowableMessage() {
        StackTraceEvent event = createTestEvent();

        Stacktraces.clearMessages(event);

        assertNotEmpty(event.chain);
        for (ThrowableDto dto : event.chain) {
            assertThat(dto.message, is(ANONYMIZED_TAG));
        }
    }

    @Test
    public void testAnonymizeThrowableDtoClassname() {
        ThrowableDto throwable = new ThrowableDto();
        throwable.classname = "foo.bar.FooBarException";

        Stacktraces.anonymizeThrowable(throwable);

        assertThat(throwable.classname, is(ANONYMIZED_TAG));
    }

    @Test
    public void testAnonymizeThrowableDtoWhitelistedClassname() {
        ThrowableDto throwable = new ThrowableDto();
        throwable.classname = "java.lang.RuntimeException";

        Stacktraces.anonymizeThrowable(throwable);

        assertThat(throwable.classname, is("java.lang.RuntimeException"));
    }

    @Test
    public void testAnonymizeStackTraceElementDtoClassnames() {
        StackTraceElementDto element = new StackTraceElementDto();
        element.classname = "foo.bar.SubClass";

        Stacktraces.anonymizeStackTraceElement(element);

        assertThat(element.classname, is(ANONYMIZED_TAG));
    }

    @Test
    public void testAnonymizeStackTraceElementDtoWhitelistedClassnames() {
        StackTraceElementDto element = new StackTraceElementDto();
        element.classname = "java.lang.String";

        Stacktraces.anonymizeStackTraceElement(element);

        assertThat(element.classname, is("java.lang.String"));
    }

    @Test
    public void testAnonymizeStackTraceElementMethodname() {
        StackTraceElementDto element = new StackTraceElementDto();
        element.classname = "foo.bar.SubClass";
        element.methodname = "foo";

        Stacktraces.anonymizeStackTraceElement(element);

        assertThat(element.methodname, is(ANONYMIZED_TAG));
    }

    @Test
    public void testAnonymizeStackTraceElementWhitelistedMethodname() {
        StackTraceElementDto element = new StackTraceElementDto();
        element.classname = "java.lang.String";
        element.methodname = "trim";

        Stacktraces.anonymizeStackTraceElement(element);

        assertThat(element.methodname, is("trim"));
    }
}
