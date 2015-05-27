package org.eclipse.recommenders.completion.rcp.utils;

import static com.google.common.base.Optional.*;
import static com.google.common.collect.Sets.newHashSet;
import static java.io.File.separator;
import static java.util.Arrays.asList;
import static org.eclipse.recommenders.testing.jdt.AstUtils.MARKER;
import static org.eclipse.recommenders.utils.Checks.ensureIsTrue;
import static org.eclipse.recommenders.utils.Pair.newPair;
import static org.eclipse.recommenders.utils.Throws.throwUnhandledException;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.recommenders.utils.Constants;
import org.eclipse.recommenders.utils.Nonnull;
import org.eclipse.recommenders.utils.Pair;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;

public class TemporaryProject {

    public static final IProgressMonitor NULL_PROGRESS_MONITOR = null;
    public static final String JAVA_IDENTIFIER_REGEX = "([a-zA-Z_$\\p{Lu}\\p{Ll}]{1}"
            + "[a-zA-Z_$0-9\\p{Lu}\\p{Ll}\\p{Nl}]*)";

    private static final String BIN_FOLDER_NAME = "bin";
    private static final String SRC_FOLDER_NAME = "src";

    private final Set<TemporaryFile> temporaryFiles = Sets.newHashSet();
    private final IWorkspace workspace;
    private final String name;
    private final IProject project;

    private IJavaProject javaProject;

    protected TemporaryProject(IWorkspace ws, String name) {
        this.workspace = ws;
        this.name = name;
        this.project = ws.getRoot().getProject(name);

        createProject();
    }

    public TemporaryProject withDependencyOnClassesOf(TemporaryProject dependency) {
        IFolder classFileFolder = dependency.getProjectClassFiles().orNull();

        try {
            addToClasspath(JavaCore.newLibraryEntry(classFileFolder.getFullPath(), null, null));
        } catch (JavaModelException e) {
            e.printStackTrace();
        }

        return this;
    }

    public TemporaryFile createFile(CharSequence code) throws CoreException {
        Pair<ICompilationUnit, Set<Integer>> struct = createFileAndParseWithMarkers(code);

        TemporaryFile tempFile = new TemporaryFile(struct.getFirst(), struct.getSecond().iterator().next());
        temporaryFiles.add(tempFile);
        return tempFile;
    }

    protected String getName() {
        return name;
    }

    private Optional<IFolder> getProjectClassFiles() {
        if (!compileProject()) {
            return absent();
        }

        return of(project.getFolder(BIN_FOLDER_NAME));
    }

    private boolean compileProject() {
        if (temporaryFiles.isEmpty()) {
            return false;
        }

        File buildDirectory = new File(project.getLocation().toString() + separator + BIN_FOLDER_NAME);
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

    private void addToClasspath(@Nonnull final IClasspathEntry classpathEntry) throws JavaModelException {
        final Set<IClasspathEntry> entries = newHashSet();

        entries.addAll(asList(javaProject.getRawClasspath()));
        entries.add(classpathEntry);

        IClasspathEntry[] classpaths = entries.toArray(new IClasspathEntry[entries.size()]);
        javaProject.setRawClasspath(classpaths, NULL_PROGRESS_MONITOR);
    }

    private void createProject() {
        final IWorkspaceRunnable populate = new IWorkspaceRunnable() {

            @Override
            public void run(final IProgressMonitor monitor) throws CoreException {
                createAndOpenProject(project);

                if (!hasJavaNature(project)) {
                    addJavaNature(project);
                    addToClasspath(JavaRuntime.getDefaultJREContainerEntry());
                    addSourcePackageFragmentRoot(project);
                }
            }

            private void createAndOpenProject(IProject project) throws CoreException {
                if (!project.exists()) {
                    project.create(NULL_PROGRESS_MONITOR);
                }
                project.open(NULL_PROGRESS_MONITOR);
            }

            private boolean hasJavaNature(final IProject project) throws CoreException {
                final IProjectDescription description = project.getDescription();
                final String[] natures = description.getNatureIds();
                return ArrayUtils.contains(natures, JavaCore.NATURE_ID);
            }

            private void addJavaNature(final IProject project) throws CoreException {
                final IProjectDescription description = project.getDescription();
                final String[] natures = description.getNatureIds();
                final String[] newNatures = ArrayUtils.add(natures, JavaCore.NATURE_ID);

                description.setNatureIds(newNatures);
                project.setDescription(description, NULL_PROGRESS_MONITOR);
                javaProject = JavaCore.create(project);
            }

            private void addSourcePackageFragmentRoot(IProject project) throws CoreException {
                // create the source folder
                IFolder sourceFolder = project.getFolder(SRC_FOLDER_NAME);
                sourceFolder.create(false, true, null);

                // replace the classpath's project root entry with the src folder
                IPackageFragmentRoot src = javaProject.getPackageFragmentRoot(sourceFolder);
                IClasspathEntry[] entries = javaProject.getRawClasspath();

                for (int i = 0; i < entries.length; i++) {
                    if (entries[i].getPath().toString().equals(separator + name)) {
                        entries[i] = JavaCore.newSourceEntry(src.getPath());
                        break;
                    }
                }

                javaProject.setRawClasspath(entries, null);
            }
        };

        try {
            workspace.run(populate, NULL_PROGRESS_MONITOR);
        } catch (final Exception e) {
            e.printStackTrace();
            throwUnhandledException(e);
        }
        javaProject = JavaCore.create(project);
    }

    private Pair<ICompilationUnit, Set<Integer>> createFileAndParseWithMarkers(final CharSequence contentWithMarkers)
            throws CoreException {
        final Pair<String, Set<Integer>> content = findMarkers(contentWithMarkers, MARKER);

        final ICompilationUnit cu = createFile(content.getFirst());
        refreshAndBuildProject();

        return Pair.newPair(cu, content.getSecond());
    }

    private Pair<String, Set<Integer>> findMarkers(final CharSequence content, String marker) {
        final Set<Integer> markers = Sets.newTreeSet();
        int pos = 0;
        final StringBuilder sb = new StringBuilder(content);
        while ((pos = sb.indexOf(marker, pos)) != -1) {
            sb.deleteCharAt(pos);
            markers.add(pos);
            ensureIsTrue(pos <= sb.length());
            pos--;
        }
        return newPair(sb.toString(), markers);
    }

    private ICompilationUnit createFile(final String content) throws CoreException {
        // get filename
        final String fileName = findClassName(content) + Constants.DOT_JAVA;

        // add the file name and get the file
        StringBuilder projectRelativeFilePath = new StringBuilder();
        projectRelativeFilePath.append(SRC_FOLDER_NAME);
        projectRelativeFilePath.append(separator);

        projectRelativeFilePath.append(fileName);
        final IPath projectRelativePath = new Path(projectRelativeFilePath.toString());

        final IFile file = project.getFile(projectRelativePath);

        // delete file
        if (file.exists()) {
            file.delete(true, NULL_PROGRESS_MONITOR);
        }

        // create file
        final ByteArrayInputStream is = new ByteArrayInputStream(content.getBytes());
        file.create(is, true, NULL_PROGRESS_MONITOR);
        int attempts = 0;
        while (!file.exists()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            attempts++;
            if (attempts > 10) {
                throw new IllegalStateException("Failed to create file");
            }
        }

        // get the compilation unit
        Path srcRelativePath = new Path(fileName);
        ICompilationUnit cu = (ICompilationUnit) javaProject.findElement(srcRelativePath);
        while (cu == null) {
            cu = (ICompilationUnit) javaProject.findElement(srcRelativePath);
        }
        return cu;
    }

    private static String findClassName(final CharSequence source) {
        Pattern p = Pattern.compile(".*?class\\s+" + JAVA_IDENTIFIER_REGEX + ".*", Pattern.DOTALL);
        Matcher matcher = p.matcher(source);
        if (!matcher.matches()) {
            p = Pattern.compile(".*interface\\s+" + JAVA_IDENTIFIER_REGEX + ".*", Pattern.DOTALL);
            matcher = p.matcher(source);
        }
        assertTrue(matcher.matches());
        return matcher.group(1);
    }

    private void refreshAndBuildProject() throws CoreException {
        final IProject project = javaProject.getProject();
        project.refreshLocal(IResource.DEPTH_INFINITE, NULL_PROGRESS_MONITOR);
        project.build(IncrementalProjectBuilder.FULL_BUILD, NULL_PROGRESS_MONITOR);
    }
}
