/**
 * Copyright (c) 2010 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Johannes Lerch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.completion.rcp.calls.preferences;

import static org.eclipse.recommenders.internal.completion.rcp.calls.wiring.CallsCompletionPlugin.PLUGIN_ID;

import javax.inject.Inject;

import org.eclipse.jdt.core.IType;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.recommenders.internal.completion.rcp.calls.net.IObjectMethodCallsNet;
import org.eclipse.recommenders.internal.rcp.models.IModelArchiveStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.google.inject.assistedinject.Assisted;

public class CommandSection {

    private final IModelArchiveStore<IType, IObjectMethodCallsNet> modelStore;

    @Inject
    public CommandSection(@Assisted final Composite parent, IModelArchiveStore<IType, IObjectMethodCallsNet> store) {
        this.modelStore = store;

        createButton(createGroup(parent));
    }

    private Composite createGroup(Composite parent) {
        final Composite section = new Composite(parent, SWT.NONE);
        section.setLayout(new GridLayout(2, false));
        section.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.END).create());
        return section;
    }

    private void createButton(Composite parent) {
        Button b = new Button(parent, SWT.PUSH);
        b.setImage(loadImage("/icons/obj16/trash.gif"));
        b.setText("Clear Mappings");
        b.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(final SelectionEvent e) {
                modelStore.getMetadata().clear();
            }
        });
    }

    protected Image loadImage(final String name) {
        final ImageDescriptor desc = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN_ID, name);
        return desc.createImage();
    }
}
