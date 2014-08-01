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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.google.common.collect.Lists;

public class StacktraceWizard extends Wizard implements IWizard {

    class StacktracePage extends WizardPage {

        private TableViewer v;
        private Text emailTxt;
        private Text nameTxt;

        protected StacktracePage() {
            super(StacktracePage.class.getName());
        }

        @Override
        public void createControl(Composite parent) {
            setTitle("An error has been logged. Help us fixing it.");
            setDescription("Please provide any additional information\nthat may help us to reproduce the problem (optional).");
            Composite container = new Composite(parent, SWT.NONE);
            container.setLayout(new GridLayout());

            GridLayoutFactory glFactory = GridLayoutFactory.fillDefaults().numColumns(2);
            GridDataFactory gdFactory = GridDataFactory.fillDefaults().grab(true, false);
            Group personal = new Group(container, SWT.SHADOW_ETCHED_IN | SWT.SHADOW_ETCHED_OUT | SWT.SHADOW_IN
                    | SWT.SHADOW_OUT);
            personal.setText("Personal Information");
            glFactory.applyTo(personal);
            gdFactory.applyTo(personal);
            {
                new Label(personal, SWT.NONE).setText("Name:");
                nameTxt = new Text(personal, SWT.BORDER);
                nameTxt.setText(prefs.name);
                nameTxt.setToolTipText("Optional. May be helpful for the team to see who reported the issue.");
                gdFactory.applyTo(nameTxt);
            }
            {
                new Label(personal, SWT.NONE).setText("Email:");
                emailTxt = new Text(personal, SWT.BORDER);
                emailTxt.setText(prefs.email);
                emailTxt.setToolTipText("Optional. In case your bug report cannot be reproduced by us, an email address allows us to get in touch with you.");
                gdFactory.applyTo(emailTxt);
            }

            Group mode = new Group(container, SWT.NONE);
            gdFactory.applyTo(mode);
            glFactory.applyTo(mode);
            mode.setText("Next Time we see an Error");
            {
                new Label(mode, SWT.NONE).setText("should we:");
                v = new TableViewer(mode);
                v.setContentProvider(ArrayContentProvider.getInstance());
                v.setInput(Lists.newArrayList("ask", "ignore", "silent"));
                v.setSelection(new StructuredSelection(prefs.mode));
                gdFactory.applyTo(v.getControl());
            }
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
        setWindowTitle("We noticed an error...");
        ImageDescriptor img = ImageDescriptor.createFromFile(getClass(), "/icons/wizban/stackframes_wiz.gif");
        setDefaultPageImageDescriptor(img);
        addPage(page);
    }

    @Override
    public boolean performFinish() {
        page.performFinish();
        return true;
    }

}
