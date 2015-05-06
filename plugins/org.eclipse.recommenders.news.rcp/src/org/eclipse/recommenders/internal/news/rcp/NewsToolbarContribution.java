/**
 * Copyright (c) 2015 Codetrails GmbH. All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: Johannes Dorn - initial API and implementation.
 */
package org.eclipse.recommenders.internal.news.rcp;

import javax.inject.Inject;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.recommenders.rcp.SharedImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class NewsToolbarContribution extends WorkbenchWindowControlContribution {

    private final SharedImages images;
    private final EventBus bus;

    private Label clickableLable;
    private boolean updates = false;

    @Inject
    public NewsToolbarContribution(SharedImages images, EventBus bus) {
        this.images = images;
        this.bus = bus;
        bus.register(this);
    }

    @Override
    protected Control createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().applyTo(composite);

        clickableLable = new Label(composite, SWT.NONE);
        setNoAvailableNews();

        clickableLable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                if (!updates) {
                    return;
                }

                Rectangle labelBounds = clickableLable.getBounds();
                if (labelBounds == null) {
                    return;
                }

                if (labelBounds.contains(new Point(e.x, e.y))) {
                    setNoAvailableNews();
                    new Job("read") {

                        @Override
                        protected IStatus run(IProgressMonitor monitor) {
                            bus.post(new NewFeedItemsEvent());
                            return Status.OK_STATUS;
                        }

                    }.schedule(2000);
                }
            }
        });
        return composite;
    }

    private void setNoAvailableNews() {
        updates = false;
        clickableLable.setImage(images.getImage(SharedImages.Images.OBJ_CONTAINER));
    }

    private void setAvailableNews() {
        updates = true;
        clickableLable.setImage(images.getImage(SharedImages.Images.OBJ_NEWSLETTER));
    }

    @Subscribe
    public void handle(NewFeedItemsEvent event) {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

            @Override
            public void run() {
                setAvailableNews();
            }

        });
    }

    public class NewFeedItemsEvent {
    }

}
