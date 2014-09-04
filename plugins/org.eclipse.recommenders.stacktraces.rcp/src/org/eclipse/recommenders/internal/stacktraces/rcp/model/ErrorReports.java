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
package org.eclipse.recommenders.internal.stacktraces.rcp.model;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.SystemUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.recommenders.utils.AnonymousId;
import org.eclipse.recommenders.utils.gson.EmfFieldExclusionStrategy;
import org.eclipse.recommenders.utils.gson.UuidTypeAdapter;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ErrorReports {

    private static final String HIDDEN = "HIDDEN";
    private static final List<String> PREFIX_WHITELIST = Arrays.asList("sun.", "java.", "javax.", "org.eclipse.");

    private static ModelFactory factory = ModelFactory.eINSTANCE;

    public static ErrorReport copy(ErrorReport org) {
        return EcoreUtil.copy(org);
    }

    public static String toJson(ErrorReport report, Settings settings, boolean pretty) {
        // work on a copy:
        report = copy(report);

        report.setName(settings.getName());
        report.setEmail(settings.getEmail());
        if (settings.isAnonymizeStrackTraceElements()) {
            anonymizeStackTrace(report);
        }
        if (settings.isAnonymizeMessages()) {
            clearMessages(report);
        }

        Gson gson = createGson(pretty);
        String json = gson.toJson(report);
        return json;
    }

    private static Gson createGson(boolean pretty) {
        GsonBuilder builder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        builder.registerTypeAdapter(UUID.class, new UuidTypeAdapter());
        builder.addSerializationExclusionStrategy(new EmfFieldExclusionStrategy());
        if (pretty) {
            builder.setPrettyPrinting();
        }
        Gson gson = builder.create();
        return gson;
    }

    public static ErrorReport newErrorReport(IStatus event, Settings settings) {
        ErrorReport mReport = factory.createErrorReport();
        mReport.setAnonymousId(AnonymousId.getId());
        mReport.setEventId(UUID.randomUUID());
        mReport.setName(settings.getName());
        mReport.setEmail(settings.getEmail());

        mReport.setJavaRuntimeVersion(SystemUtils.JAVA_RUNTIME_VERSION);
        mReport.setEclipseBuildId(System.getProperty("eclipse.buildId", "-"));
        mReport.setOsgiArch(System.getProperty("osgi.arch", "-"));
        mReport.setOsgiWs(System.getProperty("osgi.ws", "-"));
        mReport.setOsgiOs(System.getProperty(Constants.FRAMEWORK_OS_NAME, "-"));
        mReport.setOsgiOsVersion(System.getProperty(Constants.FRAMEWORK_OS_VERSION, "-"));
        mReport.setStatus(newStatus(event));
        return mReport;
    }

    public static Status newStatus(IStatus status) {
        Status mStatus = factory.createStatus();
        mStatus.setMessage(status.getMessage());
        mStatus.setSeverity(status.getSeverity());
        mStatus.setCode(status.getCode());
        mStatus.setPluginId(status.getPlugin());

        Bundle bundle = Platform.getBundle(status.getPlugin());
        if (bundle != null) {
            mStatus.setPluginVersion(bundle.getVersion().toString());
        }

        if (status.getException() != null) {
            mStatus.setException(newThrowable(status.getException()));
        }

        List<Status> mChildren = mStatus.getChildren();
        for (IStatus child : status.getChildren()) {
            if (child == status) {
                System.out.println("err");
            }
            mChildren.add(newStatus(child));
        }
        return mStatus;
    }

    public static Throwable newThrowable(java.lang.Throwable throwable) {
        Throwable mThrowable = factory.createThrowable();
        mThrowable.setMessage(throwable.getMessage());
        mThrowable.setClassName(throwable.getClass().getName());
        List<StackTraceElement> mStackTrace = mThrowable.getStackTrace();
        for (java.lang.StackTraceElement stackTraceElement : throwable.getStackTrace()) {
            StackTraceElement mStackTraceElement = factory.createStackTraceElement();
            mStackTraceElement.setFileName(stackTraceElement.getFileName());
            mStackTraceElement.setClassName(stackTraceElement.getClassName());
            mStackTraceElement.setMethodName(stackTraceElement.getMethodName());
            mStackTraceElement.setLineNumber(stackTraceElement.getLineNumber());
            mStackTrace.add(mStackTraceElement);
        }
        java.lang.Throwable cause = throwable.getCause();
        if (cause != null) {
            if (cause == throwable) {
                System.out.println("err");
            }
            mThrowable.setCause(newThrowable(cause));
        }
        return mThrowable;
    }

    public static boolean isWhitelisted(String className, List<String> whitelist) {
        for (String whiteListedPrefix : whitelist) {
            if (className.startsWith(whiteListedPrefix)) {
                return true;
            }
        }
        return false;
    }

    public static void clearMessages(ErrorReport event) {
        Status status = event.getStatus();
        clearMessages(status);
    }

    public static void clearMessages(Status status) {
        status.setMessage(HIDDEN);
        if (status.getException() != null) {
            clearMessages(status.getException());
        }
        for (Status child : status.getChildren()) {
            clearMessages(child);
        }
    }

    public static void clearMessages(Throwable throwable) {
        throwable.setMessage(HIDDEN);
        if (throwable.getCause() != null) {
            clearMessages(throwable.getCause());
        }
    }

    private static void anonymizeStackTrace(ErrorReport report) {
        if (report.getStatus() != null) {
            anonymizeStackTrace(report.getStatus());
        }
    }

    private static void anonymizeStackTrace(Status status) {
        if (status.getException() != null) {
            anonymizeStackTrace(status.getException());
        }
        for (Status child : status.getChildren()) {
            anonymizeStackTrace(child);
        }
    }

    public static void anonymizeStackTrace(Throwable throwable) {
        if (!isWhitelisted(throwable.getClassName(), PREFIX_WHITELIST)) {
            throwable.setClassName(HIDDEN);
        }
        for (StackTraceElement e : throwable.getStackTrace()) {
            anonymizeStackTraceElement(e);
        }
    }

    public static void anonymizeStackTraceElement(StackTraceElement element) {
        if (!isWhitelisted(element.getClassName(), PREFIX_WHITELIST)) {
            element.setClassName(HIDDEN);
            element.setMethodName(HIDDEN);
            element.setFileName(HIDDEN);
            element.setLineNumber(-1);
        }
    }
}
