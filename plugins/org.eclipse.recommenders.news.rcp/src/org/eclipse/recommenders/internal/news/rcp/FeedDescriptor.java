package org.eclipse.recommenders.internal.news.rcp;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class FeedDescriptor {

    private final IConfigurationElement config;
    private boolean enabled;

    public FeedDescriptor(FeedDescriptor that) {
        this(that.config, that.enabled);
    }

    public FeedDescriptor(IConfigurationElement config, boolean enabled) {
        this.config = config;
        this.enabled = enabled;
    }

    public String getId() {
        return config.getAttribute("id"); //$NON-NLS-1$
    }

    public String getName() {
        return config.getAttribute("name"); //$NON-NLS-1$
    }

    public String getUrl() {
        return config.getAttribute("url"); //$NON-NLS-1$
    }

    public String getDescription() {
        return config.getAttribute("description"); //$NON-NLS-1$
    }

    public String getPollingInterval() {
        return config.getAttribute("pollingInterval"); //$NON-NLS-1$
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Image getIcon() {
        String iconPath = config.getAttribute("icon");
        return AbstractUIPlugin.imageDescriptorFromPlugin(Constants.PLUGIN_ID, iconPath).createImage();
    }

}
