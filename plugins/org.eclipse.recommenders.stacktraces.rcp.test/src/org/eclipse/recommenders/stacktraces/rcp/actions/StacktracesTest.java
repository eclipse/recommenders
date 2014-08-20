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
package org.eclipse.recommenders.stacktraces.rcp.actions;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.recommenders.internal.stacktraces.rcp.Stacktraces;
import org.eclipse.recommenders.internal.stacktraces.rcp.StacktracesRcpPreferences;
import org.eclipse.recommenders.internal.stacktraces.rcp.dto.StackTraceElementDto;
import org.eclipse.recommenders.internal.stacktraces.rcp.dto.StackTraceEvent;
import org.eclipse.recommenders.internal.stacktraces.rcp.dto.ThrowableDto;
import org.junit.Test;

public class StacktracesTest {

    private static String ANONYMIZED_TAG = "HIDDEN";

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
    public void testSameElementOnClearMessage() {
        StackTraceEvent event = createTestEvent();
        StackTraceEvent clearedMessages = Stacktraces.clearMessages(event);
        assertThat(clearedMessages, sameInstance(event));
    }

    @Test
    public void testEventMessageCleared() {
        StackTraceEvent event = createTestEvent();
        StackTraceEvent clearedMessages = Stacktraces.clearMessages(event);
        assertThat(clearedMessages.message, is(ANONYMIZED_TAG));
    }

    @Test
    public void testThrowableDtoMessageCleared() {
        StackTraceEvent event = createTestEvent();
        StackTraceEvent clearedMessages = Stacktraces.clearMessages(event);
        assertNotEmpty(clearedMessages.chain);
        for (ThrowableDto dto : clearedMessages.chain) {
            assertThat(dto.message, is(ANONYMIZED_TAG));
        }
    }

    @Test
    public void testSameElementOnAnonymize() {
        StackTraceEvent event = createTestEvent();
        StackTraceEvent clearedMessages = Stacktraces.anonymize(event);
        assertThat(clearedMessages, sameInstance(event));
    }

    @Test
    public void testAnonymizeThrowableDtoClassname() {
        StackTraceEvent event = new StackTraceEvent();
        ThrowableDto dto1 = new ThrowableDto();
        ThrowableDto dto2 = new ThrowableDto();
        dto1.classname = "foo.bar.FooBarException";
        dto2.classname = "java.lang.RuntimeException";
        event.chain = new ThrowableDto[] { dto1, dto2 };

        Stacktraces.anonymize(event);

        assertThat(dto1.classname, is(ANONYMIZED_TAG));
        assertThat(dto2.classname, is("java.lang.RuntimeException"));
    }

    @Test
    public void testAnonymizeStackTraceElementDtoClassnames() {
        StackTraceEvent event = new StackTraceEvent();
        ThrowableDto dto1 = new ThrowableDto();
        dto1.classname = "foo.bar.Class";
        StackTraceElementDto dto1_1 = new StackTraceElementDto();
        dto1_1.classname = "foo.bar.SubClass";
        StackTraceElementDto dto1_2 = new StackTraceElementDto();
        dto1_2.classname = "java.lang.String";
        dto1.elements = Arrays.asList(dto1_1, dto1_2);
        event.chain = new ThrowableDto[] { dto1 };

        Stacktraces.anonymize(event);

        assertThat(dto1_1.classname, is(ANONYMIZED_TAG));
        assertThat(dto1_2.classname, is("java.lang.String"));

    }

    @Test
    public void testAnonymizeStackTraceElementDtoMethods() {
        StackTraceEvent event = new StackTraceEvent();
        ThrowableDto dto1 = new ThrowableDto();
        dto1.classname = "foo.bar.Class";
        StackTraceElementDto dto1_1 = new StackTraceElementDto();
        dto1_1.classname = "foo.bar.SubClass";
        dto1_1.methodname = "foo";
        StackTraceElementDto dto1_2 = new StackTraceElementDto();
        dto1_2.classname = "java.lang.String";
        dto1_2.methodname = "trim";
        dto1.elements = Arrays.asList(dto1_1, dto1_2);
        event.chain = new ThrowableDto[] { dto1 };

        Stacktraces.anonymize(event);

        assertThat(dto1_1.methodname, is(ANONYMIZED_TAG));
        assertThat(dto1_2.methodname, is("trim"));

    }

    private <T> void assertNotEmpty(T[] values) {
        assertTrue(values.length > 0);
    }
}
