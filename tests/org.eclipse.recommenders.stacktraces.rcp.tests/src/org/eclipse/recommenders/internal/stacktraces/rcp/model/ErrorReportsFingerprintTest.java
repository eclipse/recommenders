package org.eclipse.recommenders.internal.stacktraces.rcp.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorReports.ThrowableFingerprintComputer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
public class ErrorReportsFingerprintTest {

    private static final String SUFFIX_1 = "42wasd42";
    private static final String SUFFIX_2 = "qwerty123";
    private String parametrizedInfix;
    private Settings settings;

    ModelFactory modelFactory = ModelFactory.eINSTANCE;

    @Parameters
    public static Collection<Object[]> parameters() {
        List<Object[]> testParams = Lists.newArrayList();
        for (String s : ErrorReports.IGNORED_INFIXES) {
            testParams.add(new Object[] { s });
        }
        return testParams;
    }

    public ErrorReportsFingerprintTest(String generatedInfix) {
        this.parametrizedInfix = generatedInfix;
    }

    @Before
    public void init() {
        settings = ModelFactory.eINSTANCE.createSettings();
        settings.getWhitelistedPackages().add("org.");
    }

    @Test
    public void testFingerprintGeneratedClassesEqual() {
        StackTraceElement stackTraceElement1 = modelFactory.createStackTraceElement();
        stackTraceElement1.setClassName(classNameWithInfixAndSuffix(parametrizedInfix, SUFFIX_1));
        StackTraceElement stackTraceElement2 = modelFactory.createStackTraceElement();
        stackTraceElement2.setClassName(classNameWithInfixAndSuffix(parametrizedInfix, SUFFIX_2));

        ThrowableFingerprintComputer sut = new ErrorReports.ThrowableFingerprintComputer(new ArrayList<String>(), 2);
        sut.visit(stackTraceElement1);
        String fingerprint1 = sut.hash();
        sut.visit(stackTraceElement2);
        String fingerprint2 = sut.hash();

        assertEquals(fingerprint1, fingerprint2);
    }

    public String classNameWithInfixAndSuffix(String infix, String suffix) {
        return "foo.bar.package.Classname" + infix + suffix;
    }
}
