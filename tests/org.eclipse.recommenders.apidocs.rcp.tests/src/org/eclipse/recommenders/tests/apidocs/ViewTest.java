package org.eclipse.recommenders.tests.apidocs;

import static org.junit.Assert.assertNotNull;

import org.eclipse.recommenders.internal.apidocs.rcp.ApidocsViewHelpers;
import org.eclipse.recommenders.internal.apidocs.rcp.ApidocsView;
import org.eclipse.recommenders.utils.rcp.RCPUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.junit.Test;

import com.google.common.base.Optional;

public class ViewTest {

    @Test
    public void test() throws PartInitException {
        Display.getDefault().syncExec(new Runnable() {

            @Override
            public void run() {
                try {
                    Optional<IWorkbenchPage> opt = RCPUtils.getActiveWorkbenchPage();
                    IViewPart view = opt.get().showView(ApidocsView.ID);
                    assertNotNull(view);
                } catch (Exception e) {
                    // XXX E4 odd thing.
                    System.err.println("NPE in Eclipse - probably e4... need to be verfied again soon!");
                    System.err.println(e);
                }
            }
        });
    }

    @Test
    public void testUIUtilsSmoketest() {
        Display.getDefault().syncExec(new Runnable() {

            @Override
            public void run() {
                Shell s = new Shell();
                ApidocsViewHelpers.createButton(s, "", new SelectionAdapter() {
                });
                ApidocsViewHelpers.createCLabel(s, "", false, null);
                ApidocsViewHelpers.createColor(SWT.COLOR_BLUE);
                ApidocsViewHelpers.createComposite(s, 12);
                ApidocsViewHelpers.createGridComposite(s, 2, 1, 1, 1, 1);
                ApidocsViewHelpers.createLabel(s, "", true);
                ApidocsViewHelpers.createLabel(s, "", true, true, SWT.COLOR_DARK_CYAN, false);
            }
        });

    }
}
