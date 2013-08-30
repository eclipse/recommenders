package org.eclipse.recommenders.calls.rcp.templates;

import static org.eclipse.recommenders.utils.names.VmTypeName.*;
import static org.junit.Assert.assertEquals;

import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.recommenders.internal.calls.rcp.templates.TemplateBuilder;
import org.eclipse.recommenders.utils.names.IMethodName;
import org.eclipse.recommenders.utils.names.VmMethodName;
import org.eclipse.recommenders.utils.names.VmTypeName;
import org.junit.Test;

public class TemplateBuilderTest {

    IMethodName APPEND_DOUBLE_ARRAY_MTHD = VmMethodName.get("Ljava/lang/String.append()[[D");
    IMethodName GET_MTHD = VmMethodName.get("Ljava/lang/String.get()[[D");
    IMethodName GETTER_MTHD = VmMethodName.get("Ljava/lang/String.getString()[[D");
    IMethodName NEW_STRING_CTOR = VmMethodName.get("Ljava/lang/String.<init>()V");
    IMethodName ACCESS_MTHD = VmMethodName.get("Ljava/lang/String.access$1()D");

    TemplateBuilder sut = new TemplateBuilder();

    @Test
    public void testNewVarnameMthd() throws JavaModelException {
        assertEquals("append", sut.suggestId(APPEND_DOUBLE_ARRAY_MTHD));
        assertEquals("append1", sut.suggestId(APPEND_DOUBLE_ARRAY_MTHD));
    }

    @Test
    public void testNewVarnameCtor() throws JavaModelException {
        assertEquals("string", sut.suggestId(NEW_STRING_CTOR));
        assertEquals("string1", sut.suggestId(NEW_STRING_CTOR));
    }

    @Test
    public void testNewVarnameAccessMthd() {
        assertEquals("access1", sut.suggestId(ACCESS_MTHD));
        assertEquals("access11", sut.suggestId(ACCESS_MTHD));
    }

    @Test
    public void testNewVarnameGetterMthd() {
        assertEquals("string", sut.suggestId(GETTER_MTHD));
    }

    @Test
    public void testNewVarnameGetMthd() {
        assertEquals("get", sut.suggestId(GET_MTHD));
    }

    @Test
    public void testToLiteralDouble() {
        assertEquals("double", sut.toLiteral(DOUBLE));
    }

    @Test
    public void testToLiteralDoubleArray() {
        assertEquals("'double[][]'", sut.toLiteral(VmTypeName.get("[[D")));
    }

    @Test
    public void testToLiteralReferenceType() {
        assertEquals("java.lang.ExceptionInInitializerError", sut.toLiteral(JavaLangExceptionInInitializerError));
    }

    @Test
    public void testToLiteralReferenceTypeArray() {
        assertEquals("'java.lang.String[][]'", sut.toLiteral(VmTypeName.get("[[Ljava/lang/String")));
    }

    @Test
    public void testAppendStatement() throws JavaModelException {
        sut.appendStatement("myId", "cmdId", "string1", "string2", "string3");
        assertEquals("${myId:cmdId(string1,string2,string3)}", sut.toString());
    }

}
