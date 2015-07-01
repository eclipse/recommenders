/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.recommenders.internal.news.rcp.l10n.Messages;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.google.common.base.Preconditions;

public class FeedDescriptor {

    private IConfigurationElement config;
    private boolean enabled;
    private boolean defaultRepository;
    private String id;
    private URL url;
    private String name;
    private String pollingInterval;

    public FeedDescriptor(FeedDescriptor that) {
        this(that.config, that.enabled);
    }

    public FeedDescriptor(IConfigurationElement config, boolean enabled) {
        this(config.getAttribute("url"), config.getAttribute("name"), config.getAttribute("pollingInterval")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        this.config = config;
        this.enabled = enabled;
        this.id = config.getAttribute("id"); //$NON-NLS-1$
        defaultRepository = true;
        Preconditions.checkNotNull(getId());
        Preconditions.checkArgument(isUrlValid(config.getAttribute("url")), Messages.FEED_DESCRIPTOR_MALFORMED_URL); //$NON-NLS-1$
    }

    public FeedDescriptor(String url, String name, String pollingInterval) {
        Preconditions.checkArgument(isUrlValid(url), Messages.FEED_DESCRIPTOR_MALFORMED_URL);

        id = url;
        enabled = true;
        this.url = stringToUrl(url);
        this.name = name;
        this.pollingInterval = pollingInterval;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public URL getUrl() {
        return url;
    }

    public String getDescription() {
        if (config == null) {
            return name;
        }
        return config.getAttribute("description"); //$NON-NLS-1$
    }

    public String getPollingInterval() {
        if (config == null) {
            return pollingInterval;
        }
        return config.getAttribute("pollingInterval"); //$NON-NLS-1$
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
        if (config == null) {
            return null;
        }
        String iconPath = config.getAttribute("icon"); //$NON-NLS-1$
        if (iconPath != null) {
            return AbstractUIPlugin.imageDescriptorFromPlugin(Constants.PLUGIN_ID, iconPath).createImage();
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FeedDescriptor rhs = (FeedDescriptor) obj;
        if (!getId().equals(rhs.getId())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 43;
        int result = 1;
        result = prime * result + (getId() == null ? 0 : getId().hashCode());
        return result;
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

    private URL stringToUrl(String s) {
        try {
            return new URL(s);
        } catch (MalformedURLException e) {
            // should never happen
            return null;
        }
    }
}
