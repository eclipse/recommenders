package org.eclipse.recommenders.internal.snipmatch.rcp

import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility
import org.eclipse.jface.text.TextSelection
import org.eclipse.recommenders.snipmatch.Snippet
import org.eclipse.recommenders.tests.CodeBuilder
import org.eclipse.recommenders.tests.jdt.JavaProjectFixture
import static org.junit.Assert.*
import org.junit.Test

class CreateSnippetHandlerTest {

    static val fixture = new JavaProjectFixture(ResourcesPlugin.getWorkspace(), "test")
    CharSequence code

    Snippet actual

    @Test
    def void test01() {
        code = CodeBuilder::method(
            '''
                $String[] s[] = new String[0][];
                s.hashCode();
                s.equals(s);$
            ''')
        exercise()

        assertEquals(
            '''
                String[] ${s:var(java.lang.String[][])}[] = new String[0][];
                ${s}.hashCode();
                ${s}.equals(${s});
                ${:import(java.lang.String)}${cursor}
            '''.toString,
            actual.code
        )

    }

    def void exercise() {
        val struct = fixture.createFileAndParseWithMarkers(code)
        val cu = struct.first;
        val start = struct.second.head;
        val end = struct.second.last;
//        cu.becomeWorkingCopy(null)
        val editor = EditorUtility.openInEditor(cu)as CompilationUnitEditor;
        editor.selectionProvider.selection = new TextSelection(start, end - start)
        val sut = new CreateSnippetHandler
        actual = sut.createSnippet(editor)
    }
}
