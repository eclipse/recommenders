package org.eclipse.recommenders.internal.stacktraces.rcp;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.recommenders.utils.gson.GsonUtil;
import org.osgi.framework.Bundle;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Throwables;
import com.google.common.collect.Sets;

class IgnoreStatusChecker {

    static class IgnoreValues {
        Set<String> pluginIds;
        Set<Pattern> messagePatterns;
    }

    @VisibleForTesting
    public static void main(String[] args) {
        // TODO remove
        // only for testing purpose
        IgnoreValues values = new IgnoreValues();
        values.pluginIds = Sets.newHashSet("org.eclipse.recommenders.completion.rcp2",
                "org.eclipse.recommenders.completion.rcp");
        values.messagePatterns = Sets.newHashSet(Arrays.asList(Pattern.compile(Pattern.quote("test message1")),
                Pattern.compile("[x]*")));
        System.out.println(GsonUtil.serialize(values));
    }

    private IgnoreValues ignoredValues;

    public IgnoreStatusChecker() {
        File ignoreFile = resolveIgnoreFile();
        ignoredValues = GsonUtil.deserialize(ignoreFile, IgnoreValues.class);
    }

    private File resolveIgnoreFile() {
        Bundle bundle = Platform.getBundle("org.eclipse.recommenders.stacktraces.rcp");
        URL fileURL = bundle.getEntry("files/error-ignore.json");
        if (fileURL == null) {
            throw new RuntimeException("ignore-file not found");
        }
        File file = null;
        try {
            file = new File(FileLocator.resolve(fileURL).toURI());
        } catch (URISyntaxException e) {
            Throwables.propagate(e);
        } catch (IOException e) {
            Throwables.propagate(e);
        }
        return file;
    }

    public boolean isOnIgnoreList(IStatus status) {
        for (String pluginId : ignoredValues.pluginIds) {
            if (pluginId.equals(status.getPlugin())) {
                return true;
            }
        }
        for (Pattern pattern : ignoredValues.messagePatterns) {
            if (pattern.matcher(status.getMessage()).matches()) {
                return true;
            }
        }
        return false;
    }

}
