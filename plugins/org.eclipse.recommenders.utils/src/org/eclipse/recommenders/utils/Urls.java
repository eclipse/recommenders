package org.eclipse.recommenders.utils;

import static org.apache.commons.lang3.StringUtils.removeEnd;

import java.net.MalformedURLException;
import java.net.URL;

import com.google.common.base.Throwables;

public class Urls {

    public static String mangle(URL url) {
        return mangle(url.toExternalForm());
    }

    public static String mangle(String url) {
        return removeEnd(url.replaceAll("\\W", "_"), "_");
    }

    public static URL toUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw Throwables.propagate(e);
        }
    }

    private Urls() {
    }
}
