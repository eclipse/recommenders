/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp;

import static org.eclipse.recommenders.internal.news.rcp.Constants.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.recommenders.internal.news.rcp.l10n.LogMessages;
import org.eclipse.recommenders.internal.news.rcp.l10n.Messages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

public class FeedDescriptor implements Comparable<FeedDescriptor> {

    private final boolean defaultRepository;
    private final String id;
    private final URI uri;
    private final String name;
    private final String pollingInterval;
    private final String description;
    private final String iconPath;
    private final Map<String, String> parameters;
    private final String contributedBy;
    private boolean enabled;

    public FeedDescriptor(FeedDescriptor that) {
        this(that.getId(), that.getUri().toString(), that.getName(), that.isEnabled(), that.isDefaultRepository(),
                that.getPollingInterval(), that.getDescription(), that.getIconPath(), that.getParameters(),
                that.getContributedBy());
    }

    public FeedDescriptor(IConfigurationElement config, boolean enabled, String contributedBy) {
        this(config.getAttribute(ATTRIBUTE_ID), config.getAttribute(ATTRIBUTE_URL), config.getAttribute(ATTRIBUTE_NAME),
                enabled, true, config.getAttribute(ATTRIBUTE_POLLING_INTERVAL),
                config.getAttribute(ATTRIBUTE_DESCRIPTION), config.getAttribute(ATTRIBUTE_ICON),
                getParametersFromConfig(config), contributedBy);
    }

    public FeedDescriptor(String uri, String name, String pollingInterval) {
        this(uri, uri, name, true, false, pollingInterval, null, null, null, null);
    }

    private FeedDescriptor(String id, String uri, String name, boolean enabled, boolean defaultRepository,
            String pollingInterval, String description, String iconPath, Map<String, String> parameters,
            String contributedBy) {
        Preconditions.checkNotNull(id);
        Preconditions.checkArgument(isUrlValid(uri), Messages.FEED_DESCRIPTOR_MALFORMED_URL);

        this.id = id;
        this.uri = stringToUrl(uri);
        this.name = name;
        this.enabled = enabled;
        this.defaultRepository = defaultRepository;
        this.pollingInterval = pollingInterval;
        this.description = description;
        this.iconPath = iconPath;
        this.parameters = parameters;
        this.contributedBy = contributedBy;
    }

    public String getContributedBy() {
        return contributedBy;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public URI getUri() {
        return uri;
    }

    public String getDescription() {
        return description;
    }

    public String getPollingInterval() {
        return pollingInterval;
    }

    public boolean isDefaultRepository() {
        return defaultRepository;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Image getIcon() {
        if (iconPath != null) {
            return AbstractUIPlugin.imageDescriptorFromPlugin(Constants.PLUGIN_ID, iconPath).createImage();
        }
        return null;
    }

    public Map<String, String> getParameters() {
        return this.parameters;
    }

    private String getIconPath() {
        return iconPath;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (getClass() != other.getClass()) {
            return false;
        }
        FeedDescriptor that = (FeedDescriptor) other;
        return Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    public static boolean isUrlValid(String url) {
        URL u;
        try {
            u = new URL(url);
            u.toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            return false;
        }
        return true;
    }

    private static URI stringToUrl(String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            Logs.log(LogMessages.ERROR_FEED_MALFORMED_URL, url);
            return null;
        }
    }

    private static Map<String, String> getParametersFromConfig(IConfigurationElement config) {
        if (config == null) {
            return Collections.emptyMap();
        }
        IConfigurationElement[] urlParameters = config.getChildren(ATTRIBUTE_PARAMETERS);
        if (urlParameters == null || urlParameters.length < 1) {
            return Collections.emptyMap();
        }
        Map<String, String> result = Maps.newHashMap();
        IConfigurationElement[] parameters = urlParameters[0].getChildren(ATTRIBUTE_PARAMETER);
        if (parameters.length < 1) {
            return Collections.emptyMap();
        }
        for (IConfigurationElement element : parameters) {
            result.put(element.getAttribute(ATTRIBUTE_PARAMETER_KEY), element.getAttribute(ATTRIBUTE_PARAMETER_VALUE));
        }

        return result;
    }

    @Override
    public int compareTo(FeedDescriptor that) {
        return this.getName().compareTo(that.getName());
    }
}
