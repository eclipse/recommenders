package org.eclipse.recommenders.calls.rcp

import java.util.HashSet
import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.jdt.core.dom.AST
import org.eclipse.recommenders.calls.ICallModel
import org.eclipse.recommenders.completion.rcp.CompletionSmokeTest
import org.eclipse.recommenders.completion.rcp.MockedIntelligentCompletionProposalComputer
import org.eclipse.recommenders.tests.CodeBuilder
import org.eclipse.recommenders.tests.jdt.JavaProjectFixture
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test

import org.eclipse.recommenders.internal.calls.rcp.CallCompletionSessionProcessor
import static org.eclipse.recommenders.calls.ICallModel.DefinitionKind.*
import static org.eclipse.recommenders.calls.ICallModel.DefinitionKind.*
import static org.eclipse.recommenders.calls.ICallModel.DefinitionKind.*
import static org.eclipse.recommenders.calls.ICallModel.DefinitionKind.*
import static org.eclipse.recommenders.calls.ICallModel.DefinitionKind.*
import static org.eclipse.recommenders.calls.ICallModel.DefinitionKind.*
import static org.eclipse.recommenders.calls.ICallModel.DefinitionKind.*
import static org.eclipse.recommenders.calls.ICallModel.DefinitionKind.*
import org.eclipse.recommenders.calls.ICallModel.DefinitionKind

@Ignore
class QueryTest {

    static val fixture = new JavaProjectFixture(ResourcesPlugin.getWorkspace(), "test")
    CharSequence code

    MockedIntelligentCompletionProposalComputer<CallCompletionSessionProcessor> computer

    CallCompletionSessionProcessor processor

    ICallModel model

    @Test
    def void testDefMethodReturn01() {
        code = CodeBuilder::method(
            '''
            List l = Collections.emptyList();
            l.get(0).$''')
        exercise()
        verifyDefinition(METHOD_RETURN)
    }

    @Test
    def void testDefMethodReturn012() {
        code = CodeBuilder::method(
            '''
            List l;
            Object o = l.get(0);
            o.$''')
        exercise()

        verifyDefinition(METHOD_RETURN)
    }

    @Test
    def void testDefField() {
        code = CodeBuilder::classbody(
            '''
            List l;
            void __test(){
            	l.$;
            }''')
        exercise()

        verifyDefinition(FIELD)
    }

    @Test
    def void testFindCalls01() {
        code = CodeBuilder::method(
            '''
                Object o = null;
                o.equals(new Object() {
                    public boolean equals(Object obj) {
                        o.hashCode();
                        return false;
                    }
                });
                o.$
                }
            ''')
        exercise()

        verifyCalls(newHashSet("equals"))
    }

    @Test
    def void testFindCalls02() {
        code = CodeBuilder::method(
            '''
                Object o = null;
                o.equals();
                Object o2 = null;
                o2.hashCode();
                o.$
                }
            ''')
        exercise()

        verifyCalls(newHashSet("equals"))
    }

    def verifyCalls(HashSet<String> strings) {
        throw new UnsupportedOperationException("TODO: auto-generated method stub")
    }

    @Test
    def void testDefThis01() {
        code = CodeBuilder::method('''$''')
        exercise()

        verifyDefinition(THIS)
    }

    @Test
    def void testDefThis02() {
        code = CodeBuilder::method('''$''')
        exercise()

        verifyDefinition(THIS)
    }

    @Test
    def void testDefThis03() {
        code = CodeBuilder::method('''this.$''')
        exercise()

        verifyDefinition(THIS)
    }

    @Test
    def void testDefThis04() {
        code = CodeBuilder::method('''super.$''')
        exercise()

        verifyDefinition(THIS)
    }

    @Test
    def void testDefThis05() {
        code = CodeBuilder::classbody(
            '''
            public boolean equals(Object o){
            	boolean res = super.equals(o);
            	this.hash$
            }''')
        exercise()

        verifyDefinition(THIS)
    }

    def verifyDefinition(DefinitionKind type) {
        //        Assert.assertEquals(type, model.observedDefinitionType)
    }

    def void exercise() {
        val struct = fixture.createFileAndParseWithMarkers(code)
        val cu = struct.first;
        cu.becomeWorkingCopy(null)

        // just be sure that this file still compiles...
        val ast = cu.reconcile(AST::JLS4, true, true, null, null);
        Assert.assertNotNull(ast)

        computer = CompletionSmokeTest.newCallComputer
        processor = computer.getProcessor
        CompletionSmokeTest.complete(computer, cu, struct.second.head)
        model = processor.model
    }

}
