package org.eclipse.recommenders.internal.news.rcp;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

public class NewsToolbarContribution extends WorkbenchWindowControlContribution {

    @Override
    public boolean isVisible() {
        // TODO Auto-generated method stub
        return super.isVisible();
    }

    @Override
    protected Control createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout(1, false);
        composite.setLayout(layout);
        Label label = new Label(composite, SWT.NONE);
        label.setText("Hello World");
        return composite;
    }

}
