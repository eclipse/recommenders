package org.eclipse.recommenders.internal.stacktraces.rcp;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;

import org.eclipse.recommenders.internal.stacktraces.rcp.model.ErrorReports;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.ModelFactory;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.Settings;
import org.eclipse.recommenders.internal.stacktraces.rcp.model.Status;
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

    @Parameters
    public static Collection<Object[]> parameters() {
        List<Object[]> testParams = Lists.newArrayList();
        for (String s : ErrorReports.ignoredInfixes) {
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
        String generatedClassName1 = classNameWithInfixAndSuffix(parametrizedInfix, SUFFIX_1);
        String generatedClassName2 = classNameWithInfixAndSuffix(parametrizedInfix, SUFFIX_2);

        Status status1 = statusForSimpleExceptionWithClassName(generatedClassName1);
        Status status2 = statusForSimpleExceptionWithClassName(generatedClassName2);

        assertEquals(status1.getFingerprint(), status2.getFingerprint());
    }

    public String classNameWithInfixAndSuffix(String infix, String suffix) {
        return "foo.bar.package.Classname" + infix + suffix;
    }

    private Status statusForSimpleExceptionWithClassName(String className) {
        Exception exception = new RuntimeException("exception message");
        exception.setStackTrace(stackTraceForGeneratedClassName(className));
        org.eclipse.core.runtime.IStatus status = new org.eclipse.core.runtime.Status(
                org.eclipse.core.runtime.IStatus.ERROR, "org.eclipse.recommenders.stacktraces", "some error message",
                exception);
        return ErrorReports.newStatus(status, settings);

    }

    private StackTraceElement[] stackTraceForGeneratedClassName(String className) {
        return new java.lang.StackTraceElement[] { new java.lang.StackTraceElement(className, "methodname",
                "generatedFilename", -1) };
    }
}
