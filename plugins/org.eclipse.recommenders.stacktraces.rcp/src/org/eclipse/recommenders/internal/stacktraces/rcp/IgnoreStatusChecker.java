package org.eclipse.recommenders.internal.stacktraces.rcp;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.recommenders.utils.gson.GsonUtil;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
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

    private Set<IgnoreValue> ignoredValues;

    @VisibleForTesting
    public static void main(String[] args) {
        // TODO remove
        // only for testing purpose
        Set<IgnoreValue> values = Sets.newHashSet(new IgnoreValue("org.eclipse.recommenders.completion.rcp2", null),
                new IgnoreValue("org.eclipse.recommenders.completion.rcp5", "[x]+"));
        String serialize = GsonUtil.serialize(values);
        System.out.println(serialize);

    }

    public IgnoreStatusChecker() {
        File ignoreFile = resolveIgnoreFile();
        if (ignoreFile != null) {
            if (ignoreFile.exists()) {
                Type type = new TypeToken<Set<IgnoreValue>>() {
                }.getType();
                ignoredValues = GsonUtil.deserialize(ignoreFile, type);
            } else {
                // TODO create blank file with folder structure?
            }
        }
    }

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

    private File resolveIgnoreFile() {
        File userHome = resolveUserHome();
        if (userHome != null) {
            return new File(userHome, ".eclipse/org.eclipse.recommenders.stacktraces/error-ignore.json");
        }
        return null;
    }

    private File resolveUserHome() {
        Location userHomeLocation = Platform.getUserLocation();
        try {
            return new File(FileLocator.resolve(userHomeLocation.getURL()).toURI());
        } catch (IOException e) {
            Throwables.propagate(e);
        } catch (URISyntaxException e) {
            Throwables.propagate(e);
        } finally {
            userHomeLocation.release();
        }
        return null;
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
