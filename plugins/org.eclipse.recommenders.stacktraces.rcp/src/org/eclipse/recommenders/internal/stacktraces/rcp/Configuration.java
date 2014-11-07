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
package org.eclipse.recommenders.internal.stacktraces.rcp;

import java.util.List;
import java.util.Set;

import org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorReports.StatusFilterSetting;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class Configuration {

    // Cache
    public static final int PREVIOUS_ERROR_CACHE_MAXIMUM_SIZE = 30;
    public static final int PREVIOUS_ERROR_CACHE_EXPIRE_AFTER_ACCESS_MINUTES = 10;

    // Whitelist for sending
    public static final String WHITELISTED_PLUGINS = "org.eclipse.;org.apache.log4j.;com.codetrails.;";
    public static final String WHITELISTED_PACKAGES = "org.eclipse.;;;org.apache.;java.;javax.;javafx.;sun.;com.sun.;com.codetrails.;org.osgi.;com.google.;ch.qos.;org.slf4j.;";

    // Classes removed from top of stand-in-stacktrace
    public static final Set<String> STAND_IN_STACKTRACE_BLACKLIST = ImmutableSet.of("java.security.AccessController",
            "org.eclipse.core.internal.runtime.Log", "org.eclipse.core.internal.runtime.RuntimeLog",
            "org.eclipse.core.internal.runtime.PlatformLogWriter",
            "org.eclipse.osgi.internal.log.ExtendedLogReaderServiceFactory",
            "org.eclipse.osgi.internal.log.ExtendedLogReaderServiceFactory$3",
            "org.eclipse.osgi.internal.log.ExtendedLogServiceFactory",
            "org.eclipse.osgi.internal.log.ExtendedLogServiceImpl", "org.eclipse.osgi.internal.log.LoggerImpl",
            "org.eclipse.recommenders.internal.stacktraces.rcp.DebugStacktraceProvider",
            "org.eclipse.recommenders.internal.stacktraces.rcp.LogListener");

    // values for anonymization
    public static final String HIDDEN = "HIDDEN";
    public static final String SOURCE_BEGIN_MESSAGE = "----------------------------------- SOURCE BEGIN -------------------------------------";
    public static final String SOURCE_FILE_REMOVED = "source file contents removed";

    // Filter settings for known child-stacktraces of a multistatus
    public static final List<StatusFilterSetting> MULTISTATUS_CHILD_STACKTRACES_FILTER_SETTINGS = ImmutableList
            .of(
            // at java.lang.Object.wait(Object.java:-2)
            // at java.lang.Object.wait(Object.java:502)
            // at org.eclipse.osgi.framework.eventmgr.EventManager$EventThread.getNextEvent(EventManager.java:400)
            // at org.eclipse.osgi.framework.eventmgr.EventManager$EventThread.run(EventManager.java:336)
            new StatusFilterSetting("org.eclipse.ui.monitoring", "java.lang.Object", "java.lang.Object",
                    "org.eclipse.osgi.framework.eventmgr.EventManager",
                    "org.eclipse.osgi.framework.eventmgr.EventManager"),
            // at java.lang.Object.wait(Object.java:-2)
            // at org.eclipse.core.internal.jobs.WorkerPool.sleep(WorkerPool.java:188)
            // at org.eclipse.core.internal.jobs.WorkerPool.startJob(WorkerPool.java:220)
            // at org.eclipse.core.internal.jobs.Worker.run(Worker.java:52)
                    new StatusFilterSetting("org.eclipse.ui.monitoring", "java.lang.Object",
                            "org.eclipse.core.internal.jobs.WorkerPool", "org.eclipse.core.internal.jobs.WorkerPool",
                            "org.eclipse.core.internal.jobs.Worker"),
                    // at java.lang.Object.wait(Object.java:-2)
                    // at java.lang.Object.wait(Object.java:502)
                    // at org.eclipse.jdt.internal.core.search.processing.JobManager.run(JobManager.java:382)
                    // at java.lang.Thread.run(Thread.java:745)
                    new StatusFilterSetting("org.eclipse.ui.monitoring", "java.lang.Object", "java.lang.Object",
                            "org.eclipse.jdt.internal.core.search.processing.JobManager", "java.lang.Thread"),
                    // at org.eclipse.pde.internal.core.PluginModelManager.initializeTable(PluginModelManager.java:496)
                    // at org.eclipse.pde.internal.core.PluginModelManager.targetReloaded(PluginModelManager.java:473)
                    // at
                    // org.eclipse.pde.internal.core.RequiredPluginsInitializer$1.run(RequiredPluginsInitializer.java:34)
                    // at org.eclipse.core.internal.jobs.Worker.run(Worker.java:55)
                    new StatusFilterSetting("org.eclipse.ui.monitoring",
                            "org.eclipse.pde.internal.core.PluginModelManager",
                            "org.eclipse.pde.internal.core.PluginModelManager",
                            "org.eclipse.pde.internal.core.RequiredPluginsInitializer",
                            "org.eclipse.core.internal.jobs.Worker"),
                    // at java.lang.Object.wait(Object.java:-2)
                    // at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:142)
                    // at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:158)
                    // at
                    // org.eclipse.emf.common.util.CommonUtil$1ReferenceClearingQueuePollingThread.run(CommonUtil.java:70)
                    new StatusFilterSetting("org.eclipse.ui.monitoring", "java.lang.Object",
                            "java.lang.ref.ReferenceQueue", "java.lang.ref.ReferenceQueue",
                            "org.eclipse.emf.common.util.CommonUtil"),
                    // at java.lang.Object.wait(Object.java:-2)
                    // at org.eclipse.core.internal.jobs.InternalWorker.run(InternalWorker.java:59)
                    new StatusFilterSetting("org.eclipse.ui.monitoring", "java.lang.Object",
                            "org.eclipse.core.internal.jobs.InternalWorker"),
                    // at java.lang.Object.wait(Object.java:-2)
                    // at org.eclipse.equinox.internal.util.impl.tpt.timer.TimerImpl.run(TimerImpl.java:141)
                    // at java.lang.Thread.run(Thread.java:745)
                    new StatusFilterSetting("org.eclipse.ui.monitoring", "java.lang.Object",
                            "org.eclipse.equinox.internal.util.impl.tpt.timer.TimerImpl", "java.lang.Thread"),
                    // at java.lang.Object.wait(Object.java:-2)
                    // at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:142)
                    // at java.lang.ref.ReferenceQueue.remove(ReferenceQueue.java:158)
                    // at java.lang.ref.Finalizer$FinalizerThread.run(Finalizer.java:209)
                    new StatusFilterSetting("org.eclipse.ui.monitoring", "java.lang.Object",
                            "java.lang.ref.ReferenceQueue", "java.lang.ref.ReferenceQueue", "java.lang.ref.Finalizer"),
                    // at java.lang.Object.wait(Object.java:-2)
                    // at java.lang.Object.wait(Object.java:502)
                    // at java.lang.ref.Reference$ReferenceHandler.run(Reference.java:157)
                    new StatusFilterSetting("org.eclipse.ui.monitoring", "java.lang.Object", "java.lang.Object",
                            "java.lang.ref.Reference$ReferenceHandler"),
                    // at java.lang.Object.wait(Object.java:-2)
                    // at java.lang.Object.wait(Object.java:502)
                    // at org.eclipse.equinox.internal.util.impl.tpt.threadpool.Executor.run(Executor.java:106)
                    new StatusFilterSetting("org.eclipse.ui.monitoring", "java.lang.Object", "java.lang.Object",
                            "org.eclipse.equinox.internal.util.impl.tpt.threadpool.Executor"));

}
