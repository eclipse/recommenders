package org.eclipse.recommenders.completion.rcp.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.junit.rules.ExternalResource;

public class TemporaryWorkspace extends ExternalResource {

    private List<TemporaryProject> tempProjects;
    private IWorkspace workspace;

    public TemporaryWorkspace() {
    }

    public TemporaryProject createProject() {
        TemporaryProject project = new TemporaryProject(workspace, getNewRandomName());
        tempProjects.add(project);
        return project;
    }

    private String getNewRandomName() {
        String name = RandomStringUtils.randomAlphanumeric(16);

        while (isNameAlreadyUsed(name)) {
            name = RandomStringUtils.randomAlphanumeric(16);
        }

        return name;
    }

    private boolean isNameAlreadyUsed(String name) {
        for (TemporaryProject tempProject : tempProjects) {
            if (tempProject.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void before() throws Throwable {
        this.workspace = ResourcesPlugin.getWorkspace();
        this.tempProjects = new ArrayList<TemporaryProject>();
    }

    @Override
    protected void after() {
        IProject[] projects = workspace.getRoot().getProjects(IResource.NONE);

        for (IProject project : projects) {
            try {
                project.delete(true, null);
            } catch (CoreException e) {
                e.printStackTrace();
            }
        }

        for (TemporaryProject project : tempProjects) {
            project.cleanUp();
        }

        this.workspace = null;
    }
}
