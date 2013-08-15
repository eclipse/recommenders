/**
 * Copyright (c) 2010 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.rcp;

import static org.eclipse.recommenders.internal.rcp.Constants.ACTIVATIONS_BEFORE_SURVEY;
import static org.eclipse.recommenders.internal.rcp.Constants.BUNDLE_NAME;
import static org.eclipse.recommenders.internal.rcp.Constants.FIRST_ACTIVATION_DATE;
import static org.eclipse.recommenders.internal.rcp.Constants.MILLIS_BEFORE_SURVEY;
import static org.eclipse.recommenders.internal.rcp.Constants.NUMBER_OF_ACTIVATIONS;
import static org.eclipse.recommenders.internal.rcp.Constants.SURVEY_DESCRIPTION;
import static org.eclipse.recommenders.internal.rcp.Constants.SURVEY_DISPLAY_DELAY;
import static org.eclipse.recommenders.internal.rcp.Constants.SURVEY_OPT_OUT;
import static org.eclipse.recommenders.internal.rcp.Constants.SURVEY_PREFERENCES_HINT;
import static org.eclipse.recommenders.internal.rcp.Constants.SURVEY_PREFERENCE_PAGE_ID;
import static org.eclipse.recommenders.internal.rcp.Constants.SURVEY_TAKEN;
import static org.eclipse.recommenders.internal.rcp.Constants.SURVEY_URL;

import java.text.ParseException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.recommenders.rcp.utils.LoggingUtils;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;

import com.google.common.annotations.VisibleForTesting;

public class RcpPlugin extends AbstractUIPlugin {

    private static final String DIALOG_TITLE = "Help us improve Code Recommenders";

    private static RcpPlugin plugin;
    public static String P_UUID = "recommenders.uuid"; //$NON-NLS-1$

    public static RcpPlugin getDefault() {
        return plugin;
    }

    public static void log(final CoreException e) {
        LoggingUtils.log(e, getDefault());
    }

    public static void logError(final Exception e, final String format, final Object... args) {
        LoggingUtils.logError(e, getDefault(), format, args);
    }

    public static void logWarning(final Exception e, final String format, final Object... args) {
        LoggingUtils.logError(e, getDefault(), format, args);
    }

    public static void logWarning(final String format, final Object... args) {
        LoggingUtils.logWarning(null, getDefault(), format, args);
    }

    public static void log(final IStatus res) {
        LoggingUtils.log(res, getDefault());
    }

    @Override
    public void start(final BundleContext context) throws Exception {
        plugin = this;
        super.start(context);
        handleSurveyDialog();
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        super.stop(context);
        plugin = null;
    }

    private void handleSurveyDialog() throws ParseException {
        IPreferenceStore preferences = new ScopedPreferenceStore(InstanceScope.INSTANCE, BUNDLE_NAME);
        if (shouldDisplaySurveyDialog(preferences)) {
            new WaitJob("Wait for Survey", SURVEY_DISPLAY_DELAY, preferences);
        }
    }

    @VisibleForTesting
    protected static boolean shouldDisplaySurveyDialog(IPreferenceStore preferences) {
        if (preferences.getBoolean(SURVEY_TAKEN) || preferences.getBoolean(SURVEY_OPT_OUT))
            return false;

        if (enoughActivationsForSurvey(preferences)) {
            if (enoughTimeForActiviation(preferences)) {
                return true;
            }
        }

        return false;
    }

    private static boolean enoughActivationsForSurvey(IPreferenceStore preferences) {
        int numberOfActivations = preferences.getInt(NUMBER_OF_ACTIVATIONS);
        preferences.setValue(NUMBER_OF_ACTIVATIONS, ++numberOfActivations);
        return numberOfActivations >= ACTIVATIONS_BEFORE_SURVEY;
    }

    private static boolean enoughTimeForActiviation(IPreferenceStore preferences) {
        long firstActivationDate = preferences.getLong(FIRST_ACTIVATION_DATE);
        long currentTime = System.currentTimeMillis();
        if (firstActivationDate != 0L) {
            long timeSinceFirstActivation = currentTime - firstActivationDate;
            return timeSinceFirstActivation > MILLIS_BEFORE_SURVEY;
        } else {
            preferences.setValue(FIRST_ACTIVATION_DATE, currentTime);
            return false;
        }
    }

    protected static void showSurveyDialog(final IPreferenceStore preferences) {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
            public void run() {
                Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

                Dialog dialog = new PreferenceLinkDialog(activeShell, DIALOG_TITLE, null, SURVEY_DESCRIPTION,
                        MessageDialog.QUESTION, new String[] { "Take the survey", "No, Thank you" }, 0,
                        SURVEY_PREFERENCES_HINT, SURVEY_PREFERENCE_PAGE_ID);

                if (dialog.open() == Dialog.OK) {
                    try {
                        IWebBrowser browser = PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser();
                        browser.openURL(SURVEY_URL);
                        preferences.setValue(SURVEY_TAKEN, true);
                    } catch (PartInitException e) {
                        e.printStackTrace();
                    }
                } else {
                    preferences.setValue(SURVEY_OPT_OUT, true);
                }
            }
        });
    }

    private static class WaitJob extends Job {

        private IPreferenceStore preferences;

        public WaitJob(String name, long delay, IPreferenceStore preferences) {
            super(name);
            this.preferences = preferences;
            setSystem(true);
            schedule(delay);
        }

        @Override
        public boolean shouldRun() {
            return (!preferences.getBoolean(SURVEY_TAKEN) && !preferences.getBoolean(SURVEY_OPT_OUT));
        }

        @Override
        protected IStatus run(IProgressMonitor monitor) {
            showSurveyDialog(preferences);
            return Status.OK_STATUS;
        }

    }

}
