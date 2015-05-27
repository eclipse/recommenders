package org.eclipse.recommenders.completion.rcp.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.junit.rules.ExternalResource;

public class TemporaryWorkspace extends ExternalResource {

    private List<TemporaryProject> projects = new ArrayList<TemporaryProject>();
    private final IWorkspace workspace;

    public TemporaryWorkspace() {
        this.workspace = ResourcesPlugin.getWorkspace();
    }

    public TemporaryProject createProject() {
        TemporaryProject project = new TemporaryProject(workspace);
        projects.add(project);
        return project;
    }
}
