/**
 * Copyright (c) 2014 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Haftstein - initial API and implementation.
 */
package org.eclipse.recommenders.internal.stacktraces.rcp.notifications;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.ui.compatibility.CommonColors;
import org.eclipse.mylyn.commons.workbench.AbstractWorkbenchNotificationPopup;
import org.eclipse.mylyn.commons.workbench.forms.CommonFormUtil;
import org.eclipse.mylyn.commons.workbench.forms.ScalingHyperlink;
import org.eclipse.osgi.util.NLS;
import org.eclipse.recommenders.internal.stacktraces.rcp.notifications.ExecutableStacktracesUiNotification.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

public class StacktracesNotificationPopup extends AbstractWorkbenchNotificationPopup {

    private List<StacktracesUiNotification> notifications;

    public StacktracesNotificationPopup(Display display, int style) {
        super(display, style);
    }

    public StacktracesNotificationPopup(Display display) {
        super(display);
    }

    public List<StacktracesUiNotification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<StacktracesUiNotification> notifications) {
        this.notifications = notifications;
    }

    private static final int NUM_NOTIFICATIONS_TO_DISPLAY = 4;
    private static final int MIN_HEIGHT = 200;
    private static final int MAX_WIDTH = 400;
    private static final int PADDING_EDGE = 5;

    @Override
    protected void createContentArea(Composite parent) {
        int count = 0;
        for (final StacktracesUiNotification notification : notifications) {
            Composite notificationComposite = new Composite(parent, SWT.NO_FOCUS);
            GridLayout gridLayout = new GridLayout(2, false);
            GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(notificationComposite);
            notificationComposite.setLayout(gridLayout);
            notificationComposite.setBackground(parent.getBackground());

            if (count < NUM_NOTIFICATIONS_TO_DISPLAY) {
                final Label notificationLabelIcon = new Label(notificationComposite, SWT.NO_FOCUS);
                notificationLabelIcon.setBackground(parent.getBackground());
                final Text labelText = new Text(notificationComposite, SWT.BEGINNING | SWT.WRAP | SWT.NO_FOCUS);
                GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(labelText);
                labelText.setForeground(CommonColors.HYPERLINK_WIDGET);
                labelText.setText(notification.getLabel());
                labelText.setBackground(parent.getBackground());
                String description = notification.getDescription();
                if (StringUtils.isNotBlank(description)) {
                    Text descriptionText = new Text(notificationComposite, SWT.WRAP | SWT.NO_FOCUS);
                    descriptionText.setText(description);
                    descriptionText.setBackground(parent.getBackground());
                    GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).grab(true, true).align(SWT.FILL, SWT.TOP)
                            .applyTo(descriptionText);
                }
                if (notification instanceof ExecutableStacktracesUiNotification) {
                    ExecutableStacktracesUiNotification executableNotification = (ExecutableStacktracesUiNotification) notification;
                    Composite linksComposite = new Composite(parent, SWT.NO_FOCUS);
                    GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).grab(true, false).align(SWT.FILL, SWT.TOP)
                            .applyTo(linksComposite);
                    for (final Action action : executableNotification.getActions()) {
                        final ScalingHyperlink actionLink = new ScalingHyperlink(notificationComposite, SWT.BEGINNING
                                | SWT.NO_FOCUS);
                        actionLink.setForeground(CommonColors.HYPERLINK_WIDGET);
                        actionLink.registerMouseTrackListener();
                        actionLink.setText(action.getName());
                        actionLink.setBackground(parent.getBackground());
                        actionLink.addHyperlinkListener(new HyperlinkAdapter() {
                            @Override
                            public void linkActivated(HyperlinkEvent e) {
                                action.execute();
                                IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                                if (window != null) {
                                    Shell windowShell = window.getShell();
                                    if (windowShell != null) {
                                        if (windowShell.getMinimized()) {
                                            windowShell.setMinimized(false);
                                        }

                                        windowShell.open();
                                        windowShell.forceActive();
                                    }
                                }
                            }
                        });
                    }
                }

            } else {
                int numNotificationsRemain = notifications.size() - count;
                ScalingHyperlink remainingLink = new ScalingHyperlink(notificationComposite, SWT.NO_FOCUS);
                remainingLink.setForeground(CommonColors.HYPERLINK_WIDGET);
                remainingLink.registerMouseTrackListener();
                remainingLink.setBackground(parent.getBackground());

                remainingLink.setText(NLS.bind("{0} more", numNotificationsRemain)); //$NON-NLS-1$
                GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).applyTo(remainingLink);
                remainingLink.addHyperlinkListener(new HyperlinkAdapter() {
                    @Override
                    public void linkActivated(HyperlinkEvent e) {
                        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                        if (window != null) {
                            Shell windowShell = window.getShell();
                            if (windowShell != null) {
                                windowShell.setMaximized(true);
                                windowShell.open();
                            }
                        }
                    }
                });
                break;
            }
            count++;
        }
    }

    @Override
    protected void createTitleArea(Composite parent) {
        super.createTitleArea(parent);
    }

    @Override
    protected String getPopupShellTitle() {
        return "Error Reporting";
    }

    @Override
    protected Color getTitleForeground() {
        return CommonFormUtil.getSharedColors().getColor(IFormColors.TITLE);
    }

    // TODO same in superclasse, remove if no control over size is required
    // @Override
    // protected void initializeBounds() {
    // Rectangle clArea = getPrimaryClientArea();
    // Shell shell = getShell();
    // Point initialSize = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
    // int height = Math.max(initialSize.y, MIN_HEIGHT);
    // int width = Math.min(initialSize.x, MAX_WIDTH);
    //
    // Point size = new Point(width, height);
    // shell.setLocation(clArea.width + clArea.x - size.x - PADDING_EDGE, clArea.height + clArea.y - size.y
    // - PADDING_EDGE);
    // shell.setSize(size);
    // }
    //
    // private Rectangle getPrimaryClientArea() {
    // Monitor primaryMonitor = getShell().getDisplay().getPrimaryMonitor();
    // return primaryMonitor != null ? primaryMonitor.getClientArea() : getShell().getDisplay().getClientArea();
    // }
}
