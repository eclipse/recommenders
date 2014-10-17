package org.eclipse.recommenders.snipmatch.rcp.util

import com.google.common.base.Optional
import java.util.HashSet
import java.util.Set
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.jdt.core.ITypeRoot
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility
import org.eclipse.jdt.ui.SharedASTProvider
import org.eclipse.jface.text.TextSelection
import org.eclipse.recommenders.models.ProjectCoordinate
import org.eclipse.recommenders.models.rcp.IProjectCoordinateProvider
import org.eclipse.recommenders.testing.CodeBuilder
import org.eclipse.recommenders.testing.jdt.JavaProjectFixture
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.mockito.Mockito

import static org.junit.Assert.*
import static org.mockito.Matchers.*
import static org.mockito.Mockito.*
import com.google.common.collect.ImmutableSet
import java.util.Collections

class DependencyExtractorTest {

    private static val FIXTURE = new JavaProjectFixture(ResourcesPlugin.getWorkspace(), "test")

    private static val JRE_1_7_0 = new ProjectCoordinate("jre", "jre", "1.7.0");
    private static val JRE_0_0_0 = new ProjectCoordinate("jre", "jre", "0.0.0");
    private static val FOO_1_0_0 = new ProjectCoordinate("foo", "foo", "1.0.0")
    private static val FOO_0_0_0 = new ProjectCoordinate("foo", "foo", "0.0.0")

    @Rule
    public val TestName testName = new TestName();

    IProjectCoordinateProvider pcProvider

    @Before
    def void setup() {
        pcProvider = Mockito.mock(typeof(IProjectCoordinateProvider));

        when(pcProvider.resolve(argThat(new TypeBindingMatcher("Foo")))).thenReturn(Optional.of(FOO_1_0_0));
        when(pcProvider.resolve(argThat(new TypeBindingMatcher("System")))).thenReturn(Optional.of(JRE_1_7_0));
        when(pcProvider.resolve(argThat(new TypeBindingMatcher("PrintStream")))).thenReturn(Optional.of(JRE_1_7_0));
        when(pcProvider.resolve(argThat(new TypeBindingMatcher("Object")))).thenReturn(Optional.of(JRE_1_7_0));
    }

    /*
     * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=447654
     */
    @Test
    def void testDependenciesForLocalVariables() {
        val code = CodeBuilder::classbody(
            '''
                public void method() {
                  Foo foo = new Foo();
                  $if (foo.isTrue()) {
                      System.out.println("empty list");
                  }$
                
                   Foo foo2 = foo.getFoo();
                
                }
                
                
                
                private class Foo {
                
                	public boolean isTrue(){
                
                		return true;
                
                		}
                
                		public Foo getFoo(){
                
                			retun this;
                }
                  }
            ''')
        val actual = exercise(code)

        val Set<ProjectCoordinate> expected = ImmutableSet.of(JRE_0_0_0, FOO_0_0_0);

        assertEquals(
            expected,
            actual
        )
    }

    /*
     * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=447654
     */
    @Test
    def void testDependenciesOfInstanceVariables() {

        val code = CodeBuilder::classbody(
            '''
                Foo foo;
                public void method() {
                   foo = new Foo();
                    $if (foo.isTrue()) {
                        System.out.println("empty list");
                    }$
                   Foo foo2 = foo.getFoo();
                }
                
                private class Foo {
                    public boolean isTrue(){
                        return true;
                    }
                    public Foo getFoo(){
                        retun this;
                    }
                }
            ''')
        val actual = exercise(code)

        val Set<ProjectCoordinate> expected = ImmutableSet.of(JRE_0_0_0, FOO_0_0_0);

        assertEquals(
            expected,
            actual
        )
    }

    /*
     * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=447654
     */
    @Test
    def void testDependenciesOfSelectionBeforeNewLine() {

        val code = CodeBuilder::classbody(
            '''
                Foo foo;
                public void method() {
                   foo = new Foo();$
                    if (foo.isTrue()) {
                        System.out.println("empty list");
                    }$
                   Foo foo2 = foo.getFoo();
                }
                
                private class Foo {
                    public boolean isTrue(){
                        return true;
                    }
                    public Foo getFoo(){
                        retun this;
                    }
                }
            ''')
        val actual = exercise(code)

        val Set<ProjectCoordinate> expected = ImmutableSet.of(JRE_0_0_0, FOO_0_0_0);

        assertEquals(
            expected,
            actual
        )
    }

    /*
     * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=447654
     */
    @Test
    def void testDependenciesOfSelectionAfterNewLine() {

        val code = CodeBuilder::classbody(
            '''
                Foo foo;
                public void method() {
                   foo = new Foo();
                    $if (foo.isTrue()) {
                        System.out.println("empty list");
                    }
                   $Foo foo2 = foo.getFoo();
                }
                
                private class Foo {
                    public boolean isTrue(){
                        return true;
                    }
                    public Foo getFoo(){
                        retun this;
                    }
                }
            ''')
        val actual = exercise(code)

        val Set<ProjectCoordinate> expected = ImmutableSet.of(JRE_0_0_0, FOO_0_0_0);

        assertEquals(
            expected,
            actual
        )
    }
    
    @Test
    def void testDependencies() {

        val code = CodeBuilder::classbody(
            '''
                Foo foo;
                public void method() {
                   foo = new Foo();
                    if (foo.isTrue()) {
                        $System.out.println("empty list");
                    }
                   $Foo foo2 = foo.getFoo();
                }
                
                private class Foo {
                    public boolean isTrue(){
                        return true;
                    }
                    public Foo getFoo(){
                        retun this;
                    }
                }
            ''')
        val actual = exercise(code)

        val Set<ProjectCoordinate> expected = ImmutableSet.of(JRE_0_0_0);

        assertEquals(
            expected,
            actual
        )
    }
    
    @Test
    def void testDependenciesSimpleNameNotFullySelectedFromStart() {

        val code = CodeBuilder::method(
            '''
                   Object obj$ect$ = new Object();
            ''')
        val actual = exercise(code)

        val Set<ProjectCoordinate> expected = Collections.emptySet();

        assertEquals(
            expected,
            actual
        )
    }
    @Test
    def void testDependenciesSimpleNameNotFullySelectedToEnd() {

        val code = CodeBuilder::method(
            '''
                   Object $obj$ect = new Object();
            ''')
        val actual = exercise(code)

        val Set<ProjectCoordinate> expected = Collections.emptySet();

        assertEquals(
            expected,
            actual
        )
    }

    def exercise(CharSequence code) {
        val struct = FIXTURE.createFileAndParseWithMarkers(code)
        val cu = struct.first;
        val start = struct.second.head;
        val end = struct.second.last;
        val editor = EditorUtility.openInEditor(cu) as CompilationUnitEditor;
        val root = editor.getViewPartInput() as ITypeRoot;
        val ast = SharedASTProvider.getAST(root, SharedASTProvider.WAIT_YES, null);
        val doc = editor.viewer.getDocument();
        val selection = new TextSelection(doc, start, end - start);

        return new DependencyExtractor(ast, selection, pcProvider).extractDependencies;
    }
}
