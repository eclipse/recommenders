/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.stacktraces.rcp;

import static org.apache.commons.lang3.StringUtils.startsWith;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;

import java.util.Properties;
import java.util.TreeMap;

import org.apache.commons.lang3.SystemUtils;
import org.apache.http.client.fluent.Request;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.contexts.ContextInjectionFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.recommenders.stacktraces.StackTraceEvent;
import org.eclipse.recommenders.stacktraces.ThrowableDto;
import org.eclipse.recommenders.utils.gson.GsonUtil;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.Bundle;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

public class LogListener implements ILogListener, IStartup {

    private static final String PLUGIN_ID = "org.eclipse.recommenders.stacktraces.rcp";

    IEclipseContext ctx = (IEclipseContext) PlatformUI.getWorkbench().getService(IEclipseContext.class);

    StacktracesRcpPreferences pref = ContextInjectionFactory.make(StacktracesRcpPreferences.class, ctx);

    @Override
    public void logging(IStatus status, String nouse) {
        if (pref.modeIgnore()) {
            return;
        }
        if (!status.matches(IStatus.ERROR)) {
            return;
        }
        String plugin = status.getPlugin();
        if (startsWithRecommendersOrCodetrails(plugin)) {
            send(status);
            return;
        }
        Throwable ex = status.getException();
        if (ex != null) {
            for (StackTraceElement ste : ex.getStackTrace()) {
                if (startsWithRecommendersOrCodetrails(ste.getClassName())) {
                    send(status);
                    return;
                }
            }
        }
    }

    private boolean startsWithRecommendersOrCodetrails(String s) {
        return startsWith(s, "org.eclipse.") || startsWith(s, "com.codetrails");
    }

    private void send(final IStatus status) {
        if (pref.modeIgnore()) {
            // double safety. This is checked before elsewhere. But just to make sure...
            return;
        }

        new Job("Sending error log entrie") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                TreeMap<String, String> m = new TreeMap<String, String>();

                Properties properties = System.getProperties();
                for (Object key : properties.keySet()) {
                    m.put((String) key, properties.getProperty((String) key));
                }
                if (pref.modeAsk()) {
                    StackTraceEvent tmp = createDto(status);
                    tmp.name = "[filled on submit]";
                    tmp.email = "[filled on submit]";
                    int open = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                            new StacktraceWizard(pref, GsonUtil.serialize(tmp))).open();
                    if (open != Dialog.OK) {
                        return Status.CANCEL_STATUS;
                    }
                }
                try {
                    StackTraceEvent event = createDto(status);
                    String body = GsonUtil.serialize(event);
                    Request.Post(pref.server).bodyString(body, APPLICATION_JSON).execute();
                } catch (Exception e) {
                    return new Status(IStatus.WARNING, PLUGIN_ID, "Failed to send error log entry", e);
                }
                return new Status(IStatus.INFO, PLUGIN_ID,
                        "Reported error log entry to recommenders.eclipse.org. Thank you for your help.");
            }
        }.schedule();
    }

    private StackTraceEvent createDto(IStatus status) {
        StackTraceEvent event = new StackTraceEvent();
        event.name = pref.name;
        event.email = pref.email;
        event.severity = status.getSeverity();
        event.code = status.getCode();
        event.message = status.getMessage();
        event.pluginId = status.getPlugin();

        event.properties = Maps.newTreeMap();
        event.properties.put("java.runtime.version", SystemUtils.JAVA_RUNTIME_VERSION);
        event.properties.put("os.arch", SystemUtils.OS_ARCH);
        event.properties.put("os.arch", SystemUtils.OS_ARCH);
        event.properties.put("os.name", SystemUtils.OS_NAME);
        event.properties.put("os.version", SystemUtils.OS_VERSION);
        event.properties.put("eclipse.buildId", getProperty("eclipse.buildId", "-"));
        event.properties.put("eclipse.commands", getProperty("eclipse.commands", "-"));
        event.properties.put("eclipse.commands", getProperty("eclipse.commands", "-"));
        event.properties.put("osgi.arch", getProperty("osgi.arch", "-"));
        event.properties.put("osgi.os", getProperty("osgi.os", "-"));
        event.properties.put("osgi.ws", getProperty("osgi.ws", "-"));

        Bundle bundle = Platform.getBundle(status.getPlugin());
        if (bundle != null) {
            event.pluginVersion = bundle.getVersion().toString();
        }

        event.exception = ThrowableDto.from(status.getException());
        return event;
    }

    private String getProperty(String key, String defaultValue) {
        return Objects.firstNonNull(System.getProperty(key), defaultValue);
    }

    @Override
    public void earlyStartup() {
        Platform.addLogListener(this);
    }
}
