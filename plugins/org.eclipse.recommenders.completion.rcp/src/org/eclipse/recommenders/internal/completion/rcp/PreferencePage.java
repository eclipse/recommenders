/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.completion.rcp;

import static org.eclipse.jface.databinding.viewers.ViewerProperties.checkedElements;
import static org.eclipse.recommenders.utils.Checks.cast;

import java.util.Set;

import javax.inject.Inject;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.property.Properties;
import org.eclipse.jface.databinding.viewers.IViewerObservableSet;
import org.eclipse.jface.databinding.viewers.ObservableSetContentProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.recommenders.completion.rcp.processable.SessionProcessorDescriptor;
import org.eclipse.recommenders.completion.rcp.processable.SessionProcessorDescriptor.EnabledSessionProcessorPredicate;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class PreferencePage extends org.eclipse.jface.preference.PreferencePage implements IWorkbenchPreferencePage {

    Set<SessionProcessorDescriptor> processors;
    Set<SessionProcessorDescriptor> enabled;
    private CheckboxTableViewer viewer;
    private DataBindingContext ctx;
    private IViewerObservableSet checked;

    @Inject
    public PreferencePage(SessionProcessorDescriptor[] pr) {
        processors = ImmutableSet.copyOf(pr);
        enabled = Sets.filter(processors, new EnabledSessionProcessorPredicate());
    }

    @Override
    public void init(IWorkbench workbench) {
        setMessage("Recommenders Completion Settings");
        setDescription("Configure which of Code Recommendes intelligent completion should be enabled:");
    }

    @Override
    protected Control createContents(Composite parent) {
        GridDataFactory f = GridDataFactory.fillDefaults();
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(f.grab(true, true).create());
        container.setLayout(new GridLayout());
        viewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER);
        viewer.getTable().setLayoutData(f.hint(300, 150).grab(true, false).create());
        ColumnViewerToolTipSupport.enableFor(viewer);
        initDataBindings();
        return container;
    }

    protected void initDataBindings() {
        ctx = new DataBindingContext();
        ObservableSetContentProvider cp = new ObservableSetContentProvider();
        viewer.setLabelProvider(new ColumnLabelProvider() {

            @Override
            public String getText(Object o) {
                return s(o).getName();
            }

            @Override
            public Image getImage(Object o) {
                return s(o).getIcon();
            }

            @Override
            public String getToolTipText(Object o) {
                // TODO should display some description
                return s(o).getId();
            }

            private SessionProcessorDescriptor s(Object element) {
                return cast(element);
            }
        });
        viewer.setContentProvider(cp);
        IObservableSet processors = Properties.selfSet(this.processors).observe(this.processors);
        viewer.setInput(processors);
        checked = checkedElements(SessionProcessorDescriptor.class).observe(viewer);
        viewer.setCheckedElements(enabled.toArray());
    }

    @Override
    public boolean performOk() {
        for (SessionProcessorDescriptor d : (Set<SessionProcessorDescriptor>) checked) {
            d.setEnabled(true);
        }
        for (SessionProcessorDescriptor d : Sets.difference(processors, checked)) {
            d.setEnabled(false);
        }
        return super.performOk();
    }

    @Override
    public void dispose() {
        ctx.dispose();
    }
}
