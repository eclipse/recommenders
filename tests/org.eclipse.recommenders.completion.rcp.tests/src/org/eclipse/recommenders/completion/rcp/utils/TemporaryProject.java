package org.eclipse.recommenders.completion.rcp.utils;

import static com.google.common.base.Optional.*;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.recommenders.testing.jdt.JavaProjectFixture;
import org.eclipse.recommenders.utils.Pair;
import org.eclipse.recommenders.utils.Zips;

import com.google.common.base.Optional;

public class TemporaryProject {

    private static final String BUILD_PATH_SUFFIX = "/bin";
    private static final String ZIP_OUTPUT_PATH_SUFFIX = "/output.zip";

    private final JavaProjectFixture jpf;
    private final String name;
    private final IProject project;

    private TemporaryFile tempFile;

    protected TemporaryProject(IWorkspace ws) {
        this.name = RandomStringUtils.randomAlphanumeric(16);
        this.project = ws.getRoot().getProject(name);
        this.jpf = new JavaProjectFixture(ws, name);
    }

    public TemporaryFile createFile(CharSequence code) throws CoreException {
        Pair<ICompilationUnit, Set<Integer>> struct = jpf.createFileAndParseWithMarkers(code);

        tempFile = new TemporaryFile(struct.getFirst(), struct.getSecond().iterator().next());
        return tempFile;
    }

    public Optional<File> getProjectJar() {
        if (!compileProject()) {
            return Optional.absent();
        }

        String projectPath = project.getLocation().toString();

        String buildPath = projectPath + BUILD_PATH_SUFFIX;
        File buildDirectory = new File(buildPath);

        String zipOutputPath = projectPath + ZIP_OUTPUT_PATH_SUFFIX;
        File zipOutput = new File(zipOutputPath);

        try {
            Zips.zip(buildDirectory, zipOutput);
        } catch (IOException e) {
            e.printStackTrace();
            return absent();
        }

        if (zipOutput.exists()) {
            return of(zipOutput);
        } else {
            return absent();
        }
    }

    private boolean compileProject() {
        if (tempFile == null) {
            return false;
        }

        String buildPath = project.getLocation().toString() + BUILD_PATH_SUFFIX;
        File buildDirectory = new File(buildPath);

        if (!buildDirectory.exists()) {
            buildDirectory.mkdirs();
        }

        try {
            project.build(IncrementalProjectBuilder.FULL_BUILD, null);
        } catch (CoreException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void cleanUp() {
    }
}
