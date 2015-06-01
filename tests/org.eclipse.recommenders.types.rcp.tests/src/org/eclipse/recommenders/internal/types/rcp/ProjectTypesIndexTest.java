package org.eclipse.recommenders.internal.types.rcp;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.File;
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

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

public class ProjectTypesIndexTest {

    private static final String SUPER_CLASS = "SuperClass";
    private static final String SUB_CLASS = "SubClass";
    private static final String SUB_SUB_CLASS = "SubClassType";

    private static final String INTERFACE_A = "IInterfaceA";
    private static final String INTERFACE_B = "IInterfaceB";
    private static final String IMPLEMENTATION = "Implementation";

    private static final String NO_SUPER_CLASS = "";

    private static final ITypeName SUPER_CLASS_TYPE = VmTypeName.get(SUPER_CLASS);
    private static final ITypeName INTERFACE_A_TYPE = VmTypeName.get(INTERFACE_A);
    private static final ITypeName INTERFACE_B_TYPE = VmTypeName.get(INTERFACE_B);

    @ClassRule
    public static final TemporaryWorkspace WORKSPACE = new TemporaryWorkspace();

    @Rule
    public TemporaryFolder indexDir = new TemporaryFolder();

    @Test
    public void testUnseenProjectNeedsRebuild() throws Exception {
        TemporaryProject dependency = WORKSPACE.createProject();
        dependency.createFile(classDeclaration(SUPER_CLASS, NO_SUPER_CLASS));
        TemporaryProject project = WORKSPACE.createProject();
        project.withDependencyOnJarOf(dependency);

        ProjectTypesIndex sut = createSut(project, dependency);

        boolean needsRebuild = sut.needsRebuild();

        assertThat(needsRebuild, is(true));
    }

    @Test
    public void testIndexedProjectNeedsNoRebuild() throws Exception {
        TemporaryProject dependency = WORKSPACE.createProject();
        dependency.createFile(classDeclaration(SUPER_CLASS, NO_SUPER_CLASS));
        TemporaryProject project = WORKSPACE.createProject();

        project.withDependencyOnJarOf(dependency);

        ProjectTypesIndex sut = createSut(project, dependency);

        sut.rebuild(SubMonitor.convert(null));
        boolean needsRebuild = sut.needsRebuild();

        assertThat(needsRebuild, is(false));
    }

    @Test
    public void testSimpleSuperclass() throws Exception {
        TemporaryProject dependency = WORKSPACE.createProject();
        dependency.createFile(classDeclaration(SUPER_CLASS, NO_SUPER_CLASS));
        dependency.createFile(classDeclaration(SUB_CLASS, SUPER_CLASS));

        TemporaryProject project = WORKSPACE.createProject();
        project.withDependencyOnJarOf(dependency);

        ProjectTypesIndex sut = createSut(project, dependency);

        sut.rebuild(SubMonitor.convert(null));
        ImmutableSet<String> subtypes = sut.doSubtypes(SUPER_CLASS_TYPE, "S");

        assertThat(subtypes, containsInAnyOrder(SUPER_CLASS, SUB_CLASS));
        assertThat(subtypes, hasSize(2));
    }

    @Test
    public void testSuperclassChain() throws Exception {
        TemporaryProject dependency = WORKSPACE.createProject();
        dependency.createFile(classDeclaration(SUPER_CLASS, NO_SUPER_CLASS));
        dependency.createFile(classDeclaration(SUB_CLASS, SUPER_CLASS));
        dependency.createFile(classDeclaration(SUB_SUB_CLASS, SUB_CLASS));

        TemporaryProject project = WORKSPACE.createProject();
        project.withDependencyOnJarOf(dependency);

        ProjectTypesIndex sut = createSut(project, dependency);

        sut.rebuild(SubMonitor.convert(null));
        ImmutableSet<String> subtypes = sut.doSubtypes(SUPER_CLASS_TYPE, "S");

        assertThat(subtypes, containsInAnyOrder(SUPER_CLASS, SUB_CLASS, SUB_SUB_CLASS));
        assertThat(subtypes, hasSize(3));
    }

    @Test
    public void testSimpleInterface() throws Exception {
        TemporaryProject dependency = WORKSPACE.createProject();
        dependency.createFile(interfaceDeclaration(INTERFACE_A, NO_SUPER_CLASS));
        dependency.createFile(classDeclaration(IMPLEMENTATION, NO_SUPER_CLASS, INTERFACE_A));

        TemporaryProject project = WORKSPACE.createProject();
        project.withDependencyOnJarOf(dependency);

        ProjectTypesIndex sut = createSut(project, dependency);

        sut.rebuild(SubMonitor.convert(null));
        ImmutableSet<String> subtypes = sut.doSubtypes(INTERFACE_A_TYPE, "I");

        assertThat(subtypes, containsInAnyOrder(INTERFACE_A, IMPLEMENTATION));
        assertThat(subtypes, hasSize(2));
    }

    @Test
    public void testInterfaceChain() throws Exception {
        TemporaryProject dependency = WORKSPACE.createProject();
        dependency.createFile(interfaceDeclaration(INTERFACE_A, NO_SUPER_CLASS));
        dependency.createFile(interfaceDeclaration(INTERFACE_B, INTERFACE_A));
        dependency.createFile(classDeclaration(IMPLEMENTATION, NO_SUPER_CLASS, INTERFACE_B));

        TemporaryProject project = WORKSPACE.createProject();
        project.withDependencyOnJarOf(dependency);

        ProjectTypesIndex sut = createSut(project, dependency);

        sut.rebuild(SubMonitor.convert(null));
        ImmutableSet<String> subtypes = sut.doSubtypes(INTERFACE_A_TYPE, "I");

        assertThat(subtypes, containsInAnyOrder(INTERFACE_A, INTERFACE_B, IMPLEMENTATION));
        assertThat(subtypes, hasSize(3));
    }

    @Test
    public void testMultipleInterfaces() throws Exception {
        TemporaryProject dependency = WORKSPACE.createProject();
        dependency.createFile(interfaceDeclaration(INTERFACE_A, NO_SUPER_CLASS));
        dependency.createFile(interfaceDeclaration(INTERFACE_B, NO_SUPER_CLASS));
        dependency.createFile(classDeclaration(IMPLEMENTATION, NO_SUPER_CLASS, INTERFACE_A, INTERFACE_B));

        TemporaryProject project = WORKSPACE.createProject();
        project.withDependencyOnJarOf(dependency);

        ProjectTypesIndex sut = createSut(project, dependency);

        sut.rebuild(SubMonitor.convert(null));
        ImmutableSet<String> subtypesOfInterfaceA = sut.doSubtypes(INTERFACE_A_TYPE, "I");

        assertThat(subtypesOfInterfaceA, containsInAnyOrder(INTERFACE_A, IMPLEMENTATION));
        assertThat(subtypesOfInterfaceA, hasSize(2));

        ImmutableSet<String> subtypesOfInterfaceB = sut.doSubtypes(INTERFACE_B_TYPE, "I");

        assertThat(subtypesOfInterfaceB, containsInAnyOrder(INTERFACE_B, IMPLEMENTATION));
        assertThat(subtypesOfInterfaceB, hasSize(2));
    }

    private ProjectTypesIndex createSut(TemporaryProject project, TemporaryProject dependency) throws IOException {
        ProjectTypesIndex sut = new ProjectTypesIndex(project.getJavaProject(), indexDir.newFolder(),
                new File(dependency.getJarPath()));
        sut.initialize();
        return sut;
    }

    private static CharSequence classDeclaration(String name, String superClass, String... interfaces) {
        return declaration("class", name, superClass, interfaces);
    }

    private static CharSequence interfaceDeclaration(String name, String superClass, String... interfaces) {
        return declaration("interface", name, superClass, interfaces);
    }

    private static CharSequence declaration(String kind, String name, String superClass, String... interfaces) {
        StringBuilder sb = new StringBuilder();
        sb.append(kind);
        sb.append(' ');
        sb.append(name);
        if (!superClass.isEmpty()) {
            sb.append(" extends ");
            sb.append(superClass);
        }
        if (interfaces.length > 0) {
            sb.append(" implements ");
        }
        sb.append(Joiner.on(',').join(interfaces));
        return CodeBuilder.classDeclaration(sb.toString(), "");
    }
}
