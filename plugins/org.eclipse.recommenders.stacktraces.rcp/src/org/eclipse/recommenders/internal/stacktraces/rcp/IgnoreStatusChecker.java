/**
 * Copyright (c) 2014 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Haftstein - initial implementation.
 */
package org.eclipse.recommenders.internal.stacktraces.rcp;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.recommenders.utils.Logs;
import org.eclipse.recommenders.utils.gson.GsonUtil;

import com.google.common.collect.Sets;
import com.google.gson.reflect.TypeToken;

class IgnoreStatusChecker {

    private static final String IGNORE_FILE_RELATIVE_PATH = ".eclipse/org.eclipse.recommenders.stacktraces/error-ignore.json";

    static class IgnoreValue {

        String pluginIdPattern;
        String messagePattern;
        Integer errorCodeMin;
        Integer errorCodeMax;

        public IgnoreValue(String pluginIdPattern, String messagePattern) {
            this(pluginIdPattern, messagePattern, null, null);
        }

        public IgnoreValue(Integer errorCodeMin, Integer errorCodeMax) {
            this(null, null, errorCodeMin, errorCodeMax);
        }

        public IgnoreValue(String pluginIdPattern, String messagePattern, Integer errorCodeMin, Integer errorCodeMax) {
            this.pluginIdPattern = pluginIdPattern;
            this.messagePattern = messagePattern;
            this.errorCodeMin = errorCodeMin;
            this.errorCodeMax = errorCodeMax;
        }

        @Override
        public int hashCode() {
            return HashCodeBuilder.reflectionHashCode(this);
        }

        @Override
        public boolean equals(Object obj) {
            return EqualsBuilder.reflectionEquals(this, obj);
        }

    }

    static boolean ignoredValueMatches(IgnoreValue ignoreValue, IStatus status) {
        boolean idMatches = ignoreValue.pluginIdPattern == null
                || Pattern.matches(ignoreValue.pluginIdPattern, status.getPlugin());

        boolean messageMatches = ignoreValue.messagePattern == null
                || Pattern.matches(ignoreValue.messagePattern, status.getMessage());

        boolean minMatches = ignoreValue.errorCodeMin == null || status.getCode() >= ignoreValue.errorCodeMin;

        boolean maxMatches = ignoreValue.errorCodeMax == null || status.getCode() <= ignoreValue.errorCodeMax;

        return idMatches && messageMatches && minMatches && maxMatches;
    }

    private Set<IgnoreValue> ignoredValues = Sets.newHashSet();

    public IgnoreStatusChecker() {
        File ignoreFile = resolveIgnoreFile();
        if (ignoreFile != null) {
            if (ignoreFile.exists()) {
                parseIgnoredValues(ignoreFile);
            } else {
                boolean created = createDefaultFile(ignoreFile);
                if (!created) {
                    Logs.log(new LogMessages(IStatus.ERROR, "unable to create {0}"), ignoreFile);
                }
            }
        }
    }

    private File resolveIgnoreFile() {
        File userHome = new File(System.getProperty("user.home"));
        if (userHome.exists()) {
            return new File(userHome, IGNORE_FILE_RELATIVE_PATH);
        } else {
            return null;
        }
    }

    private void parseIgnoredValues(File ignoreFile) {
        Type ignoreValueSetType = new TypeToken<Set<IgnoreValue>>() {
        }.getType();
        ignoredValues = GsonUtil.deserialize(ignoreFile, ignoreValueSetType);
    }

    private boolean createDefaultFile(File ignoreFile) {
        File parentFile = ignoreFile.getParentFile();
        if (parentFile.exists() || parentFile.mkdirs()) {
            try {
                return ignoreFile.createNewFile();
            } catch (IOException e) {
                Logs.log(new LogMessages(IStatus.ERROR, "error while creating file {0}"), ignoreFile, e);
            }
        }
        return false;
    }

    public boolean isOnIgnoreList(IStatus status) {
        for (IgnoreValue ignoreValue : ignoredValues) {
            if (ignoredValueMatches(ignoreValue, status)) {
                return true;
            }
        }
        return false;
    }

}
