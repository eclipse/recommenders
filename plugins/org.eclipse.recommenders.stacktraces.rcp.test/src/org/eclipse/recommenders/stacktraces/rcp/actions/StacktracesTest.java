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

 import org.eclipse.core.runtime.IStatus;
 import org.eclipse.core.runtime.Status;
 import org.eclipse.recommenders.internal.stacktraces.rcp.Stacktraces;
 import org.eclipse.recommenders.internal.stacktraces.rcp.StacktracesRcpPreferences;
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
         assertTrue(clearedMessages.chain.length > 0);
         for (ThrowableDto dto : clearedMessages.chain) {
             assertThat(dto.message, is(ANONYMIZED_TAG));
         }
     }

 }
