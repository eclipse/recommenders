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

import static com.google.common.base.Objects.equal;
import static org.eclipse.recommenders.internal.stacktraces.rcp.Constants.*;
import static org.eclipse.recommenders.internal.stacktraces.rcp.model.RememberSendAction.*;
import static org.eclipse.recommenders.internal.stacktraces.rcp.model.SendAction.ASK;
import static org.eclipse.recommenders.utils.Logs.log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.RememberSendAction;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.SendAction;
import org.osgi.service.prefs.BackingStoreException;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

@SuppressWarnings("restriction")
public class StacktracesRcpPreferences {

    private static final long MS_PER_DAY = TimeUnit.DAYS.toMillis(1);

    @Inject
    @Preference(PROP_SERVER)
    private String server;

    @Inject
    @Preference(PROP_CONFIGURED)
    private boolean configured;

    @Inject
    @Preference(PROP_NAME)
    private String name;

    @Inject
    @Preference(PROP_EMAIL)
    private String email;

    @Inject
    @Preference(PROP_SKIP_SIMILAR_ERRORS)
    private boolean skipSimilarErrors;

    @Inject
    @Preference(PROP_ANONYMIZE_STACKTRACES)
    private boolean anonymizeStacktraces;

    @Inject
    @Preference(PROP_ANONYMIZE_MESSAGES)
    private boolean anonymizeMessages;

    @Inject
    @Preference(PROP_REMEMBER_SETTING_PERIOD_START)
    private long rememberSendActionPeriodStart;

    private List<String> whitelistedPlugins;

    private List<String> whitelistedPackages;

    private SendAction sendAction;

    private RememberSendAction rememberSendAction;

    @Inject
    @Preference
    @VisibleForTesting
    IEclipsePreferences store;

    @PostConstruct
    void initialize() {
        if (equal(rememberSendAction, RESTART)) {
            setSendAction(ASK);
        }
        internal_update24hSendAction();
    }

    public String getServer() {
        return server;
    }

    public void setServer(String value) {
        this.server = value;
        store.put(PROP_SERVER, value);
        internal_save();

    }

    public boolean isConfigured() {
        return configured;
    }

    public void setConfigured(boolean value) {
        this.configured = value;
        store.putBoolean(PROP_CONFIGURED, value);
        internal_save();

    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
        store.put(PROP_NAME, value);
        internal_save();

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String value) {
        this.email = value;
        store.put(PROP_EMAIL, value);
        internal_save();

    }

    public boolean isSkipSimilarErrors() {
        return skipSimilarErrors;
    }

    public void setSkipSimilarErrors(boolean value) {
        this.skipSimilarErrors = value;
        store.putBoolean(PROP_SKIP_SIMILAR_ERRORS, value);
        internal_save();

    }

    public boolean isAnonymizeStacktraces() {
        return anonymizeStacktraces;
    }

    public void setAnonymizeStacktraces(boolean value) {
        this.anonymizeStacktraces = value;
        store.putBoolean(PROP_ANONYMIZE_STACKTRACES, value);
        internal_save();

    }

    public boolean isAnonymizeMessages() {
        return anonymizeMessages;
    }

    public void setAnonymizeMessages(boolean value) {
        this.anonymizeMessages = value;
        store.putBoolean(PROP_ANONYMIZE_MESSAGES, value);
        internal_save();

    }

    public SendAction getSendAction() {
        internal_update24hSendAction();
        return sendAction;
    }

    public void setSendAction(SendAction value) {
        this.sendAction = value;
        store.put(PROP_SEND_ACTION, value.name());
        internal_save();

    }

    public RememberSendAction getRememberSendAction() {
        return rememberSendAction;
    }

    public void setRememberSendAction(RememberSendAction value) {
        this.rememberSendAction = value;
        store.put(PROP_REMEMBER_SEND_ACTION, value.name());
        internal_save();
    }

    public long getRememberSendActionPeriodStart() {
        return rememberSendActionPeriodStart;
    }

    public void setRememberSendActionPeriodStart(long valueInMs) {
        this.rememberSendActionPeriodStart = valueInMs;
        store.putLong(PROP_REMEMBER_SETTING_PERIOD_START, valueInMs);
        internal_save();
    }

    public List<String> getWhitelistedPlugins() {
        return whitelistedPlugins;
    }

    public List<String> getWhitelistedPackages() {
        return whitelistedPackages;
    }

    @Inject
    void internal_setWhitelistedPackages(@Preference(PROP_WHITELISTED_PACKAGES) String value) {
        this.whitelistedPackages = internal_parseWhitelist(value);
    }

    private static ArrayList<String> internal_parseWhitelist(String value) {
        Iterable<String> ids = Splitter.on(';').omitEmptyStrings().trimResults().split(value);
        return Lists.newArrayList(ids);
    }

    @Inject
    void internal_setWhitelistedPlugins(@Preference(PROP_WHITELISTED_PLUGINS) String value) {
        this.whitelistedPlugins = internal_parseWhitelist(value);
    }

    @Inject
    void internal_setSendAction(@Preference(PROP_SEND_ACTION) String value) {
        this.sendAction = internal_parseAndOverwriteSendAction(value);
    }

    @Inject
    void internal_setRememberSendAction(@Preference(PROP_REMEMBER_SEND_ACTION) String value) {
        this.rememberSendAction = internal_parseRememberSendAction(value);
    }

    RememberSendAction internal_parseRememberSendAction(String value) {
        try {
            return RememberSendAction.valueOf(value);
        } catch (IllegalArgumentException e) {
            log(LogMessages.FAILED_TO_PARSE_SEND_MODE, value, RememberSendAction.NONE);
            return RememberSendAction.NONE;
        }
    }

    SendAction internal_parseAndOverwriteSendAction(String value) {
        try {
            return SendAction.valueOf(value);
        } catch (IllegalArgumentException e) {
            log(LogMessages.FAILED_TO_PARSE_SEND_MODE, value, SendAction.ASK);
            return SendAction.ASK;
        }
    }

    void internal_update24hSendAction() {
        if (equal(rememberSendAction, HOURS_24)) {
            long elapsedTime = System.currentTimeMillis() - rememberSendActionPeriodStart;
            if (elapsedTime >= MS_PER_DAY) {
                setSendAction(ASK);
            }
        }
    }

    void internal_save() {
        try {
            store.flush();
        } catch (BackingStoreException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
