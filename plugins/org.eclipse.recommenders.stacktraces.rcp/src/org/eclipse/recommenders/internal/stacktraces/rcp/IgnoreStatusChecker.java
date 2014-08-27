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
import org.eclipse.recommenders.utils.gson.GsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.google.gson.reflect.TypeToken;

class IgnoreStatusChecker {

    private static final String IGNORE_FILE_RELATIVE_PATH = ".eclipse/org.eclipse.recommenders.stacktraces/error-ignore.json";

    static class IgnoreValue {

        String pluginIdPattern;
        String messagePattern;
        Range<Integer> errorCodeRange;

        public IgnoreValue(String pluginIdPattern, String messagePattern) {
            this(pluginIdPattern, messagePattern, null);
        }

        public IgnoreValue(Range<Integer> errorCodeRange) {
            this(null, null, errorCodeRange);
        }

        public IgnoreValue(String pluginIdPattern, String messagePattern, Range<Integer> errorCodeRange) {
            this.pluginIdPattern = pluginIdPattern;
            this.messagePattern = messagePattern;
            this.errorCodeRange = errorCodeRange;
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

    private static final Logger LOG = LoggerFactory.getLogger(IgnoreStatusChecker.class);

    static boolean ignoredValueMatches(IgnoreValue ignoreValue, IStatus status) {
        boolean idMatches = ignoreValue.pluginIdPattern == null
                || Pattern.matches(ignoreValue.pluginIdPattern, status.getPlugin());

        boolean messageMatches = ignoreValue.messagePattern == null
                || Pattern.matches(ignoreValue.messagePattern, status.getMessage());

        boolean rangeMatches = ignoreValue.errorCodeRange == null
                || ignoreValue.errorCodeRange.contains(status.getCode());

        return idMatches && messageMatches && rangeMatches;
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
                    LOG.error("unable to create {0}", ignoreFile);
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
                LOG.error("error while creating file {0}", ignoreFile, e);
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
