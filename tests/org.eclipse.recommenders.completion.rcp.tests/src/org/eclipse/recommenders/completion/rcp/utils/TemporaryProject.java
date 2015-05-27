package org.eclipse.recommenders.completion.rcp.utils;

import static com.google.common.base.Optional.*;
import static com.google.common.collect.Sets.newHashSet;
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
import org.apache.commons.lang3.RandomStringUtils;
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
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.recommenders.utils.Pair;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;

public class TemporaryProject {

    public static final IProgressMonitor NULL_PROGRESS_MONITOR = null;
    public static final String JAVA_IDENTIFIER_REGEX = "([a-zA-Z_$\\p{Lu}\\p{Ll}]{1}"
            + "[a-zA-Z_$0-9\\p{Lu}\\p{Ll}\\p{Nl}]*)";

    private static final String BIN_FOLDER_NAME = "bin";
    private static final String BUILD_PATH_SUFFIX = "/" + BIN_FOLDER_NAME;
    private static final String SRC_FOLDER_NAME = "src";
    private static final String SRC_PATH_SUFFIX = "/" + SRC_FOLDER_NAME;

    private final JavaProjectFixture jpf;
    private final IWorkspace ws;
    private final String name;
    private final IProject project;
    private final String projectPath;

    private TemporaryFile tempFile;

    protected TemporaryProject(IWorkspace ws, IFolder classPathAddition) {
        this.ws = ws;
        this.name = RandomStringUtils.randomAlphanumeric(16);
        this.project = ws.getRoot().getProject(name);
        this.jpf = new JavaProjectFixture(ws, name, classPathAddition);
        this.projectPath = project.getLocation().toString();
    }

    public TemporaryFile createFile(CharSequence code) throws CoreException {
        Pair<ICompilationUnit, Set<Integer>> struct = jpf.createFileAndParseWithMarkers(code);

        tempFile = new TemporaryFile(struct.getFirst(), struct.getSecond().iterator().next());
        return tempFile;
    }

    public Optional<IFolder> getProjectClassFiles() {
        if (!compileProject()) {
            return absent();
        }

        return of(project.getFolder(BIN_FOLDER_NAME));
    }

    public boolean compileProject() {
        if (tempFile == null) {
            return false;
        }

        File buildDirectory = new File(projectPath + BUILD_PATH_SUFFIX);
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

    private class JavaProjectFixture {

        private IJavaProject javaProject;

        private JavaProjectFixture(final IWorkspace workspace, final String projectName,
                final IFolder classPathAddition) {
            final IProject project = workspace.getRoot().getProject(projectName);
            final IWorkspaceRunnable populate = new IWorkspaceRunnable() {

                @Override
                public void run(final IProgressMonitor monitor) throws CoreException {
                    createAndOpenProject(project);

                    if (!hasJavaNature(project)) {
                        addJavaNature(project);
                        configureProjectClasspath();
                    }
                }

                private void createAndOpenProject(final IProject project) throws CoreException {
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

                private void configureProjectClasspath() throws JavaModelException {
                    final Set<IClasspathEntry> entries = newHashSet();

                    entries.addAll(asList(javaProject.getRawClasspath()));
                    entries.add(JavaRuntime.getDefaultJREContainerEntry());

                    if (classPathAddition != null) {
                        entries.add(JavaCore.newLibraryEntry(classPathAddition.getFullPath(), null, null));
                    }

                    javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]),
                            NULL_PROGRESS_MONITOR);
                }
            };
            try {
                workspace.run(populate, NULL_PROGRESS_MONITOR);
            } catch (final Exception e) {
                throwUnhandledException(e);
            }
            javaProject = JavaCore.create(project);
        }

        public Pair<ICompilationUnit, Set<Integer>> createFileAndParseWithMarkers(final CharSequence contentWithMarkers)
                throws CoreException {
            final Pair<String, Set<Integer>> content = findMarkers(contentWithMarkers, MARKER);

            final ICompilationUnit cu = createFile(content.getFirst());
            refreshAndBuildProject();

            return Pair.newPair(cu, content.getSecond());
        }

        public Pair<String, Set<Integer>> findMarkers(final CharSequence content, String marker) {
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

        public ICompilationUnit createFile(final String content) throws CoreException {
            final IProject project = javaProject.getProject();

            // get filename
            final String fileName = findClassName(content) + ".java";
            StringBuilder relativeFilePath = new StringBuilder();

            // add the file name and get the file
            relativeFilePath.append(fileName);
            final IPath path = new Path(relativeFilePath.toString());
            final IFile file = project.getFile(path);

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
                    // Do nothing
                }
                attempts++;
                if (attempts > 10) {
                    throw new IllegalStateException("Failed to create file");
                }
            }
            ICompilationUnit cu = (ICompilationUnit) javaProject.findElement(path);
            while (cu == null) {
                cu = (ICompilationUnit) javaProject.findElement(path);
            }
            return cu;
        }

        public void refreshAndBuildProject() throws CoreException {
            final IProject project = javaProject.getProject();
            project.refreshLocal(IResource.DEPTH_INFINITE, NULL_PROGRESS_MONITOR);
            project.build(IncrementalProjectBuilder.FULL_BUILD, NULL_PROGRESS_MONITOR);
        }
    }
}
