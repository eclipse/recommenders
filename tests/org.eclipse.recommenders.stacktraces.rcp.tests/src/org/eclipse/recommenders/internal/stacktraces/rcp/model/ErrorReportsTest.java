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
package org.eclipse.recommenders.internal.stacktraces.rcp.model;

import static com.google.common.collect.Lists.newArrayList;
import static org.eclipse.recommenders.internal.stacktraces.rcp.ErrorReportsDTOs.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.recommenders.internal.stacktraces.rcp.ErrorReportsDTOs;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorReports.AnonymizeStacktraceVisitor;
import org.junit.Assert;
import org.junit.Test;

public class ErrorReportsTest {
    private static final List<String> PREFIX_WHITELIST = Arrays.asList("sun.", "java.", "javax.", "org.eclipse.");

    private static final String WHITELISTED_CLASSNAME = "java.lang.RuntimeException";
    private static final String NOT_WHITELISTED_CLASSNAME = "foo.bar.FooBarException";

    private static final String WHITELISTED_CLASSNAME_2 = "java.lang.String";
    private static final String WHITELISTED_METHODNAME_2 = "trim";

    private static final String NOT_WHITELISTED_CLASSNAME_2 = "foo.bar.AnyClass";
    private static final String NOT_WHITELISTED_METHODNAME_2 = "foo";

    private static String ANONYMIZED_TAG = "HIDDEN";

    Settings settings;

    @Test
    public void testClearEventMessage() {
        ErrorReport event = createTestReport();

        ErrorReports.clearMessages(event);

        assertThat(event.getStatus().getMessage(), is(ANONYMIZED_TAG));
    }

    @Test
    public void testClearThrowableMessage() {
        ErrorReport event = createTestReport();
        ErrorReports.clearMessages(event);
        assertThat(event.getStatus().getException().getMessage(), is(ANONYMIZED_TAG));
    }

    @Test
    public void testAnonymizeThrowableDtoClassname() {
        Throwable throwable = createThrowable(NOT_WHITELISTED_CLASSNAME);
        throwable.accept(new AnonymizeStacktraceVisitor(PREFIX_WHITELIST));
        assertThat(throwable.getClassName(), is(ANONYMIZED_TAG));
    }

    @Test
    public void testAnonymizeThrowableDtoWhitelistedClassname() {
        Throwable throwable = createThrowable(WHITELISTED_CLASSNAME);
        throwable.accept(new AnonymizeStacktraceVisitor(PREFIX_WHITELIST));
        assertThat(throwable.getClassName(), is(WHITELISTED_CLASSNAME));
    }

    @Test
    public void testAnonymizeStackTraceElementDtoClassnames() {
        StackTraceElement element = createStackTraceElement(NOT_WHITELISTED_CLASSNAME_2, NOT_WHITELISTED_METHODNAME_2);
        element.accept(new AnonymizeStacktraceVisitor(PREFIX_WHITELIST));
        assertThat(element.getClassName(), is(ANONYMIZED_TAG));
    }

    @Test
    public void testAnonymizeStackTraceElementDtoWhitelistedClassnames() {
        StackTraceElement element = createStackTraceElement(WHITELISTED_CLASSNAME, "");
        element.accept(new AnonymizeStacktraceVisitor(PREFIX_WHITELIST));
        assertThat(element.getClassName(), is(WHITELISTED_CLASSNAME));
    }

    @Test
    public void testAnonymizeStackTraceElementMethodname() {
        StackTraceElement element = createStackTraceElement(NOT_WHITELISTED_CLASSNAME_2, NOT_WHITELISTED_METHODNAME_2);
        element.accept(new AnonymizeStacktraceVisitor(PREFIX_WHITELIST));
        assertThat(element.getMethodName(), is(ANONYMIZED_TAG));
    }

    @Test
    public void testAnonymizeStackTraceElementWhitelistedMethodname() {
        StackTraceElement element = createStackTraceElement(WHITELISTED_CLASSNAME_2, WHITELISTED_METHODNAME_2);
        element.accept(new AnonymizeStacktraceVisitor(PREFIX_WHITELIST));
        assertThat(element.getMethodName(), is(WHITELISTED_METHODNAME_2));
    }

    @Test
    public void testFingerprint() {

        Exception cause = new RuntimeException("cause");
        Exception r1 = new RuntimeException("exception message");

        r1.fillInStackTrace();
        Exception r2 = new RuntimeException("exception message", cause);
        r2.fillInStackTrace();

        IStatus s1 = new Status(IStatus.ERROR, "org.eclipse.recommenders.stacktraces", "some error message", r1);
        IStatus s2 = new Status(IStatus.ERROR, "org.eclipse.recommenders.stacktraces", "some error message", r2);

        settings = ModelFactory.eINSTANCE.createSettings();
        settings.setWhitelistedPackages(newArrayList("org."));

        org.eclipse.recommenders.internal.stacktraces.rcp.model.Status noCause = ErrorReports.newStatus(s1, settings);
        org.eclipse.recommenders.internal.stacktraces.rcp.model.Status withCause = ErrorReports.newStatus(s2, settings);

        Assert.assertNotEquals(noCause.getFingerprint(), withCause.getFingerprint());
    }

    @Test
    public void testFingerprintNested() {
        Exception root = new RuntimeException("root");
        IStatus s1 = new Status(IStatus.ERROR, "org.eclipse.recommenders.stacktraces", "some error message", root);
        IStatus s2 = new MultiStatus("org.eclipse.recommenders.stacktraces", 0, new IStatus[] { s1 },
                "some error message", root);

        settings = ModelFactory.eINSTANCE.createSettings();
        settings.setWhitelistedPackages(newArrayList("org."));

        org.eclipse.recommenders.internal.stacktraces.rcp.model.Status normal = ErrorReports.newStatus(s1, settings);
        org.eclipse.recommenders.internal.stacktraces.rcp.model.Status multi = ErrorReports.newStatus(s2, settings);

        Assert.assertNotEquals(normal.getFingerprint(), multi.getFingerprint());
    }

    @Test
    public void testCoreExceptionHandling() {
        IStatus causingStatus = new Status(IStatus.ERROR, "the.causing.plugin", "first message");
        java.lang.Throwable causingException = new CoreException(causingStatus);
        IStatus causedStatus = new Status(IStatus.WARNING, "some.calling.plugin", "any other message", causingException);
        java.lang.Throwable rootException = new CoreException(causedStatus);
        IStatus rootEvent = new Status(IStatus.ERROR, "org.eclipse.recommenders.stacktraces", "someErrorMessage",
                rootException);
        settings = ModelFactory.eINSTANCE.createSettings();
        settings.setWhitelistedPackages(newArrayList("org."));

        org.eclipse.recommenders.internal.stacktraces.rcp.model.Status rootStatus = ErrorReports.newStatus(rootEvent,
                settings);

        org.eclipse.recommenders.internal.stacktraces.rcp.model.Status child = rootStatus.getChildren().get(0);
        org.eclipse.recommenders.internal.stacktraces.rcp.model.Status leaf = child.getChildren().get(0);
        assertThat(child.getPluginId(), is("some.calling.plugin"));
        assertThat(leaf.getPluginId(), is("the.causing.plugin"));
    }

    @Test
    public void testMultistatusDuplicateChildFiltering() {
        settings = ModelFactory.eINSTANCE.createSettings();
        settings.setWhitelistedPackages(newArrayList("org."));

        Exception e1 = new Exception("Stack Trace");
        e1.setStackTrace(createStacktraceForClasses("java.lang.Object", "org.eclipse.core.internal.jobs.WorkerPool",
                "org.eclipse.core.internal.jobs.WorkerPool", "org.eclipse.core.internal.jobs.Worker"));
        IStatus s1 = new Status(IStatus.ERROR, "org.eclipse.ui.monitoring",
                "Thread 'Worker-3' tid=39 (TIMED_WAITING)\n"
                        + "Waiting for: org.eclipse.core.internal.jobs.WorkerPool@416dc7fc", e1);

        Exception e2 = new Exception("Stack Trace");
        e2.setStackTrace(ErrorReportsDTOs.createStacktraceForClasses("java.lang.Object",
                "org.eclipse.core.internal.jobs.WorkerPool", "org.eclipse.core.internal.jobs.WorkerPool",
                "org.eclipse.core.internal.jobs.Worker"));
        IStatus s2 = new Status(IStatus.ERROR, "org.eclipse.ui.monitoring",
                "Thread 'Worker-2' tid=36 (TIMED_WAITING)\n"
                        + "Waiting for: org.eclipse.core.internal.jobs.WorkerPool@416dc7fc", e2);

        IStatus multi = new MultiStatus("org.eclipse.ui.monitoring", 0, new IStatus[] { s1, s2 },
                "UI freeze of 10s at 08:09:02.936", new RuntimeException("stand-in-stacktrace"));
        org.eclipse.recommenders.internal.stacktraces.rcp.model.Status newStatus = ErrorReports.newStatus(multi,
                settings);
        assertThat(newStatus.getChildren().size(), is(1));
        assertThat(newStatus.getMessage(),
                is("UI freeze of 10s at 08:09:02.936 [1 child-status duplicates removed by Error Reporting]"));
    }

    @Test
    public void testMultistatusChildFilteringHandlesEmptyStacktrace() {
        settings = ModelFactory.eINSTANCE.createSettings();
        settings.setWhitelistedPackages(newArrayList("org."));

        Exception e1 = new Exception("Stack Trace 1");
        e1.setStackTrace(new java.lang.StackTraceElement[0]);
        IStatus s1 = new Status(IStatus.ERROR, "org.eclipse.ui.monitoring",
                "Thread 'Signal Dispatcher' tid=4 (RUNNABLE)", e1);

        IStatus multi = new MultiStatus("org.eclipse.ui.monitoring", 0, new IStatus[] { s1 },
                "UI freeze of 10s at 08:09:02.936", new RuntimeException("stand-in-stacktrace"));
        org.eclipse.recommenders.internal.stacktraces.rcp.model.Status newStatus = ErrorReports.newStatus(multi,
                settings);
        assertThat(newStatus.getChildren().size(), is(0));
    }

    @Test
    public void testPrettyPrintNullSafe1() {
        ModelFactory mf = ModelFactory.eINSTANCE;
        settings = mf.createSettings();
        ErrorReport report = mf.createErrorReport();
        ErrorReports.prettyPrint(report, settings);

    }

    @Test
    public void testPrettyPrintNullSafe2() {
        ModelFactory mf = ModelFactory.eINSTANCE;
        settings = mf.createSettings();
        ErrorReport report = mf.createErrorReport();
        report.setStatus(mf.createStatus());
        ErrorReports.prettyPrint(report, settings);
    }

    @Test
    public void testPrettyPrintNullSafe3() {
        ModelFactory mf = ModelFactory.eINSTANCE;
        settings = mf.createSettings();
        settings.setWhitelistedPackages(newArrayList("org."));

        ErrorReport report = mf.createErrorReport();
        report.setStatus(mf.createStatus());
        Throwable t = mf.createThrowable();
        t.setClassName("org.test");
        report.getStatus().setException(t);
        ErrorReports.prettyPrint(report, settings);

    }

    @Test
    public void testMultistatusMainStacktracesNotFiltered() {
        settings = ModelFactory.eINSTANCE.createSettings();
        settings.setWhitelistedPackages(newArrayList("org."));

        Exception e1 = new Exception("Stack Trace");
        java.lang.StackTraceElement[] stackTrace = createStacktraceForClasses("java.lang.Thread",
                "org.eclipse.recommenders.stacktraces.rcp.actions.UiFreezeAction",
                "org.eclipse.ui.internal.PluginAction", "org.eclipse.ui.internal.WWinPluginAction",
                "org.eclipse.jface.action.ActionContributionItem", "org.eclipse.jface.action.ActionContributionItem",
                "org.eclipse.jface.action.ActionContributionItem", "org.eclipse.swt.widgets.EventTable",
                "org.eclipse.swt.widgets.Display", "org.eclipse.swt.widgets.Widget", "org.eclipse.swt.widgets.Display",
                "org.eclipse.swt.widgets.Display", "org.eclipse.e4.ui.internal.workbench.swt.PartRenderingEngine",
                "org.eclipse.core.databinding.observable.Realm",
                "org.eclipse.e4.ui.internal.workbench.swt.PartRenderingEngine",
                "org.eclipse.e4.ui.internal.workbench.E4Workbench", "org.eclipse.ui.internal.Workbench",
                "org.eclipse.core.databinding.observable.Realm", "org.eclipse.ui.internal.Workbench",
                "org.eclipse.ui.PlatformUI", "org.eclipse.ui.internal.ide.application.IDEApplication",
                "org.eclipse.equinox.internal.app.EclipseAppHandle",
                "org.eclipse.core.runtime.internal.adaptor.EclipseAppLauncher",
                "org.eclipse.core.runtime.internal.adaptor.EclipseAppLauncher",
                "org.eclipse.core.runtime.adaptor.EclipseStarter", "org.eclipse.core.runtime.adaptor.EclipseStarter",
                "sun.reflect.NativeMethodAccessorImpl", "sun.reflect.NativeMethodAccessorImpl",
                "sun.reflect.DelegatingMethodAccessorImpl", "java.lang.reflect.Method",
                "org.eclipse.equinox.launcher.Main", "org.eclipse.equinox.launcher.Main",
                "org.eclipse.equinox.launcher.Main", "org.eclipse.equinox.launcher.Main",
                "org.eclipse.equinox.launcher.Main");
        e1.setStackTrace(stackTrace);
        IStatus s1 = new Status(IStatus.ERROR, "org.eclipse.ui.monitoring", "Sample at 11:25:04.447 (+1,331s)\n"
                + "Thread 'main' tid=1 (TIMED_WAITING)", e1);

        IStatus multi = new MultiStatus("org.eclipse.ui.monitoring", 0, new IStatus[] { s1 },
                "UI freeze of 6,0s at 11:24:59.108", new RuntimeException("stand-in-stacktrace"));
        org.eclipse.recommenders.internal.stacktraces.rcp.model.Status newStatus = ErrorReports.newStatus(multi,
                settings);
        assertThat(newStatus.getChildren().size(), is(1));
        assertThat(newStatus.getChildren().get(0).getException().getStackTrace().size(), is(stackTrace.length));
    }

}