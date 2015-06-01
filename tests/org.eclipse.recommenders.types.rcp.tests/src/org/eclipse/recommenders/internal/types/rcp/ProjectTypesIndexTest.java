package org.eclipse.recommenders.internal.types.rcp;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.recommenders.testing.CodeBuilder;
import org.eclipse.recommenders.testing.rcp.completion.rules.TemporaryProject;
import org.eclipse.recommenders.testing.rcp.completion.rules.TemporaryWorkspace;
import org.eclipse.recommenders.utils.names.ITypeName;
import org.eclipse.recommenders.utils.names.VmTypeName;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ProjectTypesIndexTest {

    private static final ITypeName SUPERCLASS = VmTypeName.get("SuperClass");
    private static final ITypeName SubClass = VmTypeName.get("SubClass");

    @ClassRule
    public static final TemporaryWorkspace WORKSPACE = new TemporaryWorkspace();

    @Rule
    public TemporaryFolder indexDir = new TemporaryFolder();

    @Test
    public void testUnseenProjectNeedsRebuild() throws Exception {
        TemporaryProject projectA = WORKSPACE.createProject();
        TemporaryProject projectB = WORKSPACE.createProject();
        // TODO change to jar dependency
        projectA.withDependencyOnClassesOf(projectB);

        ProjectTypesIndex sut = createSut(projectA);

        boolean needsRebuild = sut.needsRebuild();

        assertThat(needsRebuild, is(true));
    }

    @Test
    public void testIndexedProjectNeedsNoRebuild() throws Exception {
        TemporaryProject projectA = WORKSPACE.createProject();
        TemporaryProject projectB = WORKSPACE.createProject();
        // TODO change to jar dependency
        projectA.withDependencyOnClassesOf(projectB);

        ProjectTypesIndex sut = createSut(projectA);

        sut.rebuild(SubMonitor.convert(null));
        boolean needsRebuild = sut.needsRebuild();

        assertThat(needsRebuild, is(false));
    }

    @Test
    public void test() throws Exception {
        TemporaryProject projectA = WORKSPACE.createProject();
        projectA.createFile(CodeBuilder.classDeclaration("SuperClass", "", ""));
        projectA.createFile(CodeBuilder.classDeclaration("SubClass", "", "SubClass"));

        TemporaryProject projectB = WORKSPACE.createProject();

        // TODO change to withDependencyOnJarsOf()
        projectB.withDependencyOnClassesOf(projectA);

        ProjectTypesIndex sut = createSut(projectB);

        boolean needsRebuild = sut.needsRebuild();

        assertThat(needsRebuild, is(true));
    }

    private ProjectTypesIndex createSut(TemporaryProject project) throws IOException {
        ProjectTypesIndex sut = new ProjectTypesIndex(project.getJavaProject(), indexDir.newFolder(), true);
        sut.initialize();
        return sut;
    }
}
