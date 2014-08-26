package org.eclipse.recommenders.stacktraces.rcp.actions;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.recommenders.internal.stacktraces.rcp.LogListener;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class LogListenerFillStacktraceTest {

    @SuppressWarnings("restriction")
    @Test
    public void test() {
        // setup:
        Status empty = new Status(IStatus.ERROR, "debug", "has no stacktrace");
        Assert.assertThat(empty.getException(), CoreMatchers.nullValue());
        // exercise:
        LogListener.insertDebugStacktraceIfEmpty(empty);
        // verify:
        assertThat(empty.getException(), notNullValue());
        assertTrue(empty.getException().getStackTrace().length > 0);
    }

}
