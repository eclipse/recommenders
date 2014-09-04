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

import java.util.UUID;

import org.apache.commons.lang3.SystemUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.recommenders.utils.AnonymousId;
import org.eclipse.recommenders.utils.gson.UuidTypeAdapter;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ErrorReports {

    private static ModelFactory factory = ModelFactory.eINSTANCE;

    public static ErrorReport copy(ErrorReport org) {
        return EcoreUtil.copy(org);
    }

    public String toJson(ErrorReport report, boolean pretty) {
        GsonBuilder builder = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        builder.registerTypeAdapter(UUID.class, new UuidTypeAdapter());
        if (pretty) {
            builder.setPrettyPrinting();
        }
        builder.addDeserializationExclusionStrategy(new ExclusionStrategy() {

            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                if (f.getName().length() > 1 && f.getName().charAt(0) == 'e'
                        && Character.isUpperCase(f.getName().charAt(1))) {
                    return true;
                }
                return false;
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        });
        Gson gson = builder.create();
        return gson.toJson(report);
    }

    public static ErrorReport from(IStatus event, Settings settings) {
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
        mReport.setStatus(from(event));
        return mReport;
    }

    public static Status from(IStatus status) {
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
            mStatus.setException(from(status.getException()));
        }

        EList<Status> mChildren = mStatus.getChildren();
        for (IStatus child : status.getChildren()) {
            mChildren.add(from(child));
        }
        return mStatus;
    }

    public static Throwable from(java.lang.Throwable throwable) {
        Throwable mThrowable = factory.createThrowable();
        mThrowable.setMessage(throwable.getMessage());
        mThrowable.setClassName(throwable.getClass().getName());
        EList<StackTraceElement> mStackTrace = mThrowable.getStackTrace();
        for (java.lang.StackTraceElement stackTraceElement : throwable.getStackTrace()) {
            StackTraceElement mStackTraceElement = factory.createStackTraceElement();
            mStackTraceElement.setFileName(stackTraceElement.getFileName());
            mStackTraceElement.setClassName(stackTraceElement.getClassName());
            mStackTraceElement.setMethodName(stackTraceElement.getMethodName());
            mStackTraceElement.setLineNumber(stackTraceElement.getLineNumber());
            mStackTrace.add(mStackTraceElement);
        }
        if (throwable.getCause() != null) {
            mThrowable.setCause(from(throwable.getCause()));
        }
        return mThrowable;
    }
}
