package org.eclipse.recommenders.tests.rcp.utils;

import static org.eclipse.recommenders.utils.rcp.ast.ASTNodeUtils.sameSimpleName;
import static org.eclipse.recommenders.utils.rcp.ast.ASTNodeUtils.stripQualifier;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.recommenders.utils.names.ITypeName;
import org.eclipse.recommenders.utils.names.VmTypeName;
import org.junit.Test;

public class AstNodeUtilsTest {
    AST ast = AST.newAST(AST.JLS4);
    String NAME = "Object";
    SimpleName SIMPLE = ast.newSimpleName("Object");
    QualifiedName QUALIFIED = ast.newQualifiedName(
            ast.newQualifiedName(ast.newSimpleName("java"), ast.newSimpleName("lang")), ast.newSimpleName("Object"));

    /**
     * https://bugs.eclipse.org/bugs/show_bug.cgi?id=405235
     */
    @Test
    public void testBug405235_sameSimpleNameOnSimpleTypes() {
        ITypeName crParam = VmTypeName.OBJECT;

        assertEquals(stripQualifier(SIMPLE).getIdentifier(), SIMPLE.getIdentifier());
        assertEquals(stripQualifier(QUALIFIED).getIdentifier(), SIMPLE.getIdentifier());

        assertTrue(sameSimpleName(newSimpleType(SIMPLE), crParam));
        assertTrue(sameSimpleName(newSimpleType(QUALIFIED), crParam));
    }

    private SimpleType newSimpleType(Name name) {
        return ast.newSimpleType(name);
    }

}
