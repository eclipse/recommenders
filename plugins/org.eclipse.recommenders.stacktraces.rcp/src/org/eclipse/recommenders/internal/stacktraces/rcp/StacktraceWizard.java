/**
 * Copyright (c) 2010, 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.stacktraces.rcp;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.recommenders.rcp.utils.Selections;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.google.common.collect.Lists;

public class StacktraceWizard extends Wizard implements IWizard {

    class StacktracePage extends WizardPage {

        private TableViewer v;

        protected StacktracePage() {
            super(StacktracePage.class.getName());
        }

        @Override
        public void createControl(Composite parent) {
            Composite container = new Composite(parent, SWT.NONE);
            container.setLayout(new GridLayout(2, false));
            v = new TableViewer(container);
            v.setContentProvider(ArrayContentProvider.getInstance());
            v.setInput(Lists.newArrayList("ask", "ignore", "silent"));
            v.setSelection(new StructuredSelection(prefs.mode));
            setControl(container);
        }

        public void performFinish() {
            String mode = Selections.<String>getFirstSelected(v.getSelection()).orNull();
            prefs.setMode(mode);
        }
    }

    StacktracesRcpPreferences prefs;
    StacktracePage page = new StacktracePage();

    public StacktraceWizard(StacktracesRcpPreferences prefs) {
        this.prefs = prefs;
    }

    @Override
    public void addPages() {
        setWindowTitle("An error was logged...");
        addPage(page);
    }

    @Override
    public boolean performFinish() {
        page.performFinish();
        return true;
    }

}
