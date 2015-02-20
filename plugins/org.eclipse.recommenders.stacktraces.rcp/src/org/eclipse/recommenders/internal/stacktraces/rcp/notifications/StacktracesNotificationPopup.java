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
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.mylyn.commons.ui.compatibility.CommonColors;
import org.eclipse.mylyn.commons.workbench.AbstractWorkbenchNotificationPopup;
import org.eclipse.mylyn.commons.workbench.forms.CommonFormUtil;
import org.eclipse.mylyn.commons.workbench.forms.ScalingHyperlink;
import org.eclipse.osgi.util.NLS;
import org.eclipse.recommenders.internal.stacktraces.rcp.notifications.ExecutableStacktracesUiNotification.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
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
    private static final int MIN_HEIGHT = 100;
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
                final StyledText labelText = new StyledText(notificationComposite, SWT.BEGINNING | SWT.READ_ONLY
                        | SWT.MULTI | SWT.WRAP | SWT.NO_FOCUS);
                labelText.setForeground(CommonColors.TEXT_QUOTED);
                labelText.setText(notification.getLabel());
                labelText.setBackground(parent.getBackground());
                GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(labelText);
                String description = notification.getDescription();
                if (StringUtils.isNotBlank(description)) {
                    StyledText descriptionText = new StyledText(notificationComposite, SWT.BEGINNING | SWT.READ_ONLY
                            | SWT.MULTI | SWT.WRAP | SWT.NO_FOCUS);
                    descriptionText.setText(description);
                    descriptionText.setBackground(parent.getBackground());
                    GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).grab(true, false).align(SWT.FILL, SWT.TOP)
                            .applyTo(descriptionText);
                }
                if (notification instanceof ExecutableStacktracesUiNotification) {
                    ExecutableStacktracesUiNotification executableNotification = (ExecutableStacktracesUiNotification) notification;
                    Composite linksComposite = new Composite(notificationComposite, SWT.NO_FOCUS);
                    GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).grab(true, false).align(SWT.FILL, SWT.TOP)
                            .applyTo(linksComposite);
                    GridLayoutFactory.fillDefaults().numColumns(executableNotification.getActions().size())
                    .applyTo(linksComposite);
                    for (final Action action : executableNotification.getActions()) {
                        final ScalingHyperlink actionLink = new ScalingHyperlink(linksComposite, SWT.BEGINNING
                                | SWT.NO_FOCUS);
                        GridDataFactory.fillDefaults().grab(true, false).applyTo(actionLink);
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
    protected String getPopupShellTitle() {
        return "Error Reporting";
    }

    @Override
    protected Color getTitleForeground() {
        return CommonFormUtil.getSharedColors().getColor(IFormColors.TITLE);
    }

    @Override
    protected void initializeBounds() {
        Rectangle clArea = getPrimaryClientArea();
        Shell shell = getShell();
        // superclass computes size with SWT.DEFAULT,SWT.DEFAULT. For long text this causes a large width
        // and a small height. Afterwards the height gets maxed to the MIN_HEIGHT value and the width gets trimmed
        // which results in text floating out of the window
        Point initialSize = shell.computeSize(MAX_WIDTH, SWT.DEFAULT);
        int height = Math.max(initialSize.y, MIN_HEIGHT);
        int width = Math.min(initialSize.x, MAX_WIDTH);

        Point size = new Point(width, height);
        shell.setLocation(clArea.width + clArea.x - size.x - PADDING_EDGE, clArea.height + clArea.y - size.y
                - PADDING_EDGE);
        shell.setSize(size);
    }

    private Rectangle getPrimaryClientArea() {
        Monitor primaryMonitor = getShell().getDisplay().getPrimaryMonitor();
        return primaryMonitor != null ? primaryMonitor.getClientArea() : getShell().getDisplay().getClientArea();
    }
}
