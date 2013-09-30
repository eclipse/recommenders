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

import static org.eclipse.core.databinding.UpdateSetStrategy.POLICY_ON_REQUEST;
import static org.eclipse.core.databinding.beans.PojoObservables.observeMap;
import static org.eclipse.core.databinding.beans.PojoProperties.set;
import static org.eclipse.jface.databinding.viewers.ViewerProperties.checkedElements;
import static org.eclipse.recommenders.internal.completion.rcp.Constants.*;
import static org.eclipse.recommenders.utils.Checks.cast;

import javax.inject.Inject;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateSetStrategy;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.databinding.viewers.IViewerObservableSet;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ObservableSetContentProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.recommenders.completion.rcp.CompletionRcpPreferences;
import org.eclipse.recommenders.completion.rcp.processable.SessionProcessorDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class PreferencePage extends org.eclipse.jface.preference.PreferencePage implements IWorkbenchPreferencePage {

    @Inject
    private CompletionRcpPreferences prefs;

    private DataBindingContext ctx;
    private CheckboxTableViewer v;

    private IObservableSet processors;
    private IObservableSet enabled;

    @Override
    public void init(IWorkbench workbench) {
        setMessage("Recommenders Completion Settings");
        setDescription("Configure which of Code Recommendes intelligent completion should be enabled:");
        setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, Constants.BUNDLE_ID));
    }

    @Override
    protected Control createContents(Composite parent) {
        GridDataFactory f = GridDataFactory.fillDefaults();
        Composite container = new Composite(parent, SWT.NONE);
        container.setLayoutData(f.grab(true, true).create());
        container.setLayout(new GridLayout());
        v = CheckboxTableViewer.newCheckList(container, SWT.BORDER);
        v.getTable().setLayoutData(f.hint(300, 150).grab(true, false).create());

        initDataBindings();
        return container;
    }

    protected void initDataBindings() {
        ctx = new DataBindingContext();
        ObservableSetContentProvider cp = new ObservableSetContentProvider();
        IObservableMap elements = observeMap(cp.getKnownElements(), SessionProcessorDescriptor.class, P_NAME);
        v.setLabelProvider(new ObservableMapLabelProvider(elements) {
            @Override
            public Image getColumnImage(Object element, int columnIndex) {
                SessionProcessorDescriptor p = cast(element);
                return p.getIcon();
            }
        });
        v.setContentProvider(cp);
        processors = set(P_PROCESSORS).observe(prefs);
        v.setInput(processors);
        //
        IViewerObservableSet checked = checkedElements(SessionProcessorDescriptor.class).observe(v);
        enabled = set(P_ENABLED).observe(prefs);
        ctx.bindSet(checked, enabled, new UpdateSetStrategy(POLICY_ON_REQUEST), null);
    }

    @Override
    public boolean performOk() {
        ctx.updateModels();
        return super.performOk();
    }

    @Override
    public void dispose() {
        ctx.dispose();
    }
}
