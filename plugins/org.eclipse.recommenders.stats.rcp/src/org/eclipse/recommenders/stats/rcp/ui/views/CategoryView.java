/**
 * Copyright (c) 2013 Timur Achmetow.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Timur Achmetow - Initial API and implementation
 */
package org.eclipse.recommenders.stats.rcp.ui.views;

import org.eclipse.recommenders.stats.rcp.interfaces.IDeveloperActivityPage;
import org.eclipse.recommenders.stats.rcp.ui.util.TableViewerFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class CategoryView implements IDeveloperActivityPage {

    private Composite parent;

    @Override
    public void createContent(Composite detailCmp) {
        parent = TableViewerFactory.createWrapperComposite(detailCmp);

        Label label = new Label(parent, SWT.None);
        label.setText("Eclipse Usage: This category contains informations about the Eclipse usage.");
        label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
    }

    @Override
    public Composite getComposite() {
        return parent;
    }
}
