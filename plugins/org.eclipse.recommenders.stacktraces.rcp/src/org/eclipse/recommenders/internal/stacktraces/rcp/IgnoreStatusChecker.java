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

import com.google.gson.reflect.TypeToken;

class IgnoreStatusChecker {

    static class IgnoreValue {

        String pluginId;
        String messagePattern;

        public IgnoreValue(String pluginId, String messagePattern) {
            this.pluginId = pluginId;
            this.messagePattern = messagePattern;
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
        boolean idMatches = ignoreValue.pluginId == null;
        if (!idMatches) {
            idMatches = ignoreValue.pluginId.equals(status.getPlugin());
        }

        boolean messageMatches = ignoreValue.messagePattern == null;
        if (!messageMatches) {
            messageMatches = Pattern.matches(ignoreValue.messagePattern, status.getMessage());
        }
        return idMatches && messageMatches;
    }

    private Set<IgnoreValue> ignoredValues;

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
            return new File(userHome, ".eclipse/org.eclipse.recommenders.stacktraces/error-ignore.json");
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
