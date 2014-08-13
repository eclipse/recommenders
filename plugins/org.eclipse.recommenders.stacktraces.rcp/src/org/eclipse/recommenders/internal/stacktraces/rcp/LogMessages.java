package org.eclipse.recommenders.internal.stacktraces.rcp;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public enum LogMessages {

    NO_INTERNET(IStatus.WARNING, 12, "Could not connect to server. Your IP is '%s'");

    static Bundle b = FrameworkUtil.getBundle(LogMessages.class);
    static ILog log = Platform.getLog(b);

    private int severity;
    private int code;
    private String message;

    private LogMessages(int severity, int code, String message) {
        this.severity = severity;
        this.code = code;
        this.message = message;
    }

    public IStatus toStatus(Throwable t, Object... args) {
        return new Status(severity, b.getSymbolicName(), code, String.format(message, args), t);
    }

    public static void log(LogMessages msg) {
        LogMessages.log(msg, null, (Object[]) null);
    }

    public static void log(LogMessages msg, Object... args) {
        LogMessages.log(msg, null, args);
    }

    public static void log(LogMessages msg, Throwable t, Object... args) {
        log.log(msg.toStatus(t, args));
    }

}
