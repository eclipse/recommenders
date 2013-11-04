package org.eclipse.recommenders.internal.models.rcp;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.recommenders.internal.rcp.RcpPlugin;
import org.eclipse.recommenders.models.IProjectCoordinateAdvisor;

public class AdvisorDescriptor {

    private final IConfigurationElement config;
    private boolean enabled;

    public AdvisorDescriptor(AdvisorDescriptor that) {
        this(that.config, that.enabled);
    }

    public AdvisorDescriptor(IConfigurationElement config, boolean enabled) {
        this.config = config;
        this.enabled = enabled;
    }

    public String getId() {
        return config.getAttribute("id");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public IProjectCoordinateAdvisor createAdvisor() throws CoreException {
        try {
            return (IProjectCoordinateAdvisor) config.createExecutableExtension("class");
        } catch (CoreException e) {
            String pluginId = config.getContributor().getName();
            RcpPlugin.logError(e, "failed to instantiate advisor %s:%s", //$NON-NLS-1$
                    pluginId, config.getAttribute("class")); //$NON-NLS-1$
            throw e;
        }
    }
}
