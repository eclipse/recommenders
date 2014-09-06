package org.eclipse.recommenders.internal.stacktraces.rcp;

import static org.eclipse.recommenders.internal.stacktraces.rcp.model.ModelFactory.eINSTANCE;

import org.eclipse.recommenders.internal.stacktraces.rcp.model.impl.VisitorImpl;
import org.junit.Test;

public class VisitorTest {

    VisitorImpl someVisitor = new VisitorImpl() {
    };

    @Test
    public void testNullSafe() {
        eINSTANCE.createStatus().accept(someVisitor);
        eINSTANCE.createErrorReport().accept(someVisitor);
        eINSTANCE.createStackTraceElement().accept(someVisitor);
        eINSTANCE.createThrowable().accept(someVisitor);
        eINSTANCE.createBundle().accept(someVisitor);
    }
}
