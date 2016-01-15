package org.eclipse.recommenders.jdt.templates;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.ui.SharedASTProvider;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.recommenders.testing.jdt.JavaProjectFixture;
import org.eclipse.recommenders.utils.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.base.Joiner;
import com.google.common.collect.Ordering;

@SuppressWarnings("restriction")
@RunWith(Parameterized.class)
public class SnippetCodeBuilderTest {

    private static final Joiner JOINER = Joiner.on('\n');
    private static final String NO_SNIPPET = "";

    private JavaProjectFixture fixture = new JavaProjectFixture(ResourcesPlugin.getWorkspace(), getClass().getName());

    private final CharSequence code;
    private final String expectedResult;

    private CompilationUnitEditor editor;
    private IRegion selection;

    private static int testCount;

    public SnippetCodeBuilderTest(String description, CharSequence code, String expectedResult) {
        this.code = code;
        this.expectedResult = expectedResult;
    }

    @Parameters(name = "{index}: {0}")
    public static Collection<Object[]> scenarios() {
        Collection<Object[]> scenarios = new LinkedList<>();

        // @formatter:off

        scenarios.add(scenario("No selection",
                multiLine("class Example { }"),
                NO_SNIPPET));

        scenarios.add(scenario("Local variable variable declaration",
                multiLine("package org.example;",
                          "class Example {",
                          "   void m() {",
                          "      $String s;$",
                          "    }",
                          "}"),
                multiLine("String ${s:newName(java.lang.String)};",
                          "${cursor}")));

        scenarios.add(scenario("Local variable declaration and initialization",
                multiLine("package org.example;",
                          "class Example {",
                          "   void m() {",
                          "      $int i = 1;$",
                          "    }",
                          "}"),
                multiLine("int ${i:newName(int)} = 1;",
                          "${cursor}")));

        scenarios.add(scenario("Local variable declaration and assignment with line break",
                multiLine("package org.example;",
                          "class Example {",
                          "   void m() {",
                          "      $int i = 0;",
                          "      i = 1;$",
                          "    }",
                          "}"),
                multiLine("int ${i:newName(int)} = 0;",
                          "${i} = 1;",
                          "${cursor}")));

        scenarios.add(scenario("Array declaration and initialization",
                multiLine("package org.example;",
                          "class Example {",
                          "   void m() {",
                          "      $String[][] s = new String[][] { { \"hello\" } };$",
                          "    }",
                          "}"),
                multiLine("String[][] ${s:newName('java.lang.String[][]')} = new String[][] { { \"hello\" } };",
                          "${cursor}")));

        scenarios.add(scenario("Field access of array field",
                multiLine("package org.example;",
                          "class Example {",
                          "   String f[];",
                          "   void m() {",
                          "      $f = null;$",
                          "    }",
                          "}"),
                multiLine("${f:field('java.lang.String[]')} = null;",
                          "${cursor}")));

        scenarios.add(scenario("Static field reference",
                multiLine("package org.example;",
                          "class Example {",
                          "   static String F;",
                          "   void m() {",
                          "      $F = null;$",
                          "    }",
                          "}"),
                multiLine("F = null;",
                          "${importStatic:importStatic(org.example.Example.F)}${cursor}")));

        scenarios.add(scenario("Method call on local variable of imported type",
                multiLine("package org.example;",
                          "import java.util.ArrayList;",
                          "import java.util.List;",
                          "class Example {",
                          "   void m() {",
                          "      $List l = new ArrayList;",
                          "      l.hashCode();$",
                          "    }",
                          "}"),
                multiLine("List ${l:newName(java.util.List)} = new ArrayList;",
                          "${l}.hashCode();",
                          "${import:import(java.util.ArrayList, java.util.List)}${cursor}")));

        scenarios.add(scenario("Variable declaration of imported nested type",
                multiLine("package org.example;",
                          "import java.util.Map;",
                          "class Example {",
                          "   void m() {",
                          "      $Map.Entry e = null;$",
                          "    }",
                          "}"),
                multiLine("Map.Entry ${e:newName(java.util.Map.Entry)} = null;",
                          "${import:import(java.util.Map)}${cursor}")));

        // @formatter:on

        return scenarios;
    }

    private static Object[] scenario(String description, CharSequence code, String expectedResult) {
        return new Object[] { description, code, expectedResult };
    }

    private static String multiLine(String... lines) {
        String result = JOINER.join(lines);
        result += '\n';
        return result;
    }

    @Before
    public void setUp() throws Exception {
        fixture = new JavaProjectFixture(ResourcesPlugin.getWorkspace(),
                SnippetCodeBuilderTest.class.getName() + testCount++);

        Pair<ICompilationUnit, List<Integer>> struct = fixture.createFileAndPackageAndParseWithMarkers(code);
        ICompilationUnit cu = struct.getFirst();
        final int start, end;
        if (!struct.getSecond().isEmpty()) {
            start = Ordering.natural().min(struct.getSecond());
            end = Ordering.natural().max(struct.getSecond());
        } else {
            start = -1;
            end = -1;
        }
        editor = (CompilationUnitEditor) EditorUtility.openInEditor(cu);
        selection = new Region(start, end - start);
    }

    @Test
    public void test() throws Exception {
        ITypeRoot root = (ITypeRoot) editor.getViewPartInput();
        CompilationUnit ast = SharedASTProvider.getAST(root, SharedASTProvider.WAIT_YES, null);
        IDocument document = editor.getViewer().getDocument();
        SnippetCodeBuilder sut = new SnippetCodeBuilder(ast, document, selection);

        String result = sut.build();

        assertThat(result, is(equalTo(expectedResult)));
    }
}
