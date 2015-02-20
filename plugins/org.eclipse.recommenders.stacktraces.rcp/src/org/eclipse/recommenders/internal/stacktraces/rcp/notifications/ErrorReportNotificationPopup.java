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

import static org.apache.commons.lang3.StringUtils.*;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.mylyn.commons.ui.compatibility.CommonColors;
import org.eclipse.mylyn.commons.workbench.AbstractWorkbenchNotificationPopup;
import org.eclipse.mylyn.commons.workbench.forms.CommonFormUtil;
import org.eclipse.mylyn.commons.workbench.forms.ScalingHyperlink;
import org.eclipse.recommenders.internal.stacktraces.rcp.notifications.ExecutableErrorReportUiNotification.Action;
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

public class ErrorReportNotificationPopup extends AbstractWorkbenchNotificationPopup {

    private static final int MAX_LABEL_CHAR_LENGTH = 120;
    private static final int MAX_DESCRIPTION_CHAR_LENGTH = 500;
    private static final int MIN_HEIGHT = 100;
    private static final int MAX_WIDTH = 400;
    private static final int PADDING_EDGE = 5;

    public ErrorReportNotificationPopup(Display display) {
        super(display);
    }

    public ErrorReportNotificationPopup(Display display, int style) {
        super(display, style);
    }

    private ErrorReportUiNotification notification;

    public ErrorReportUiNotification getNotification() {
        return notification;
    }

    public void setNotification(ErrorReportUiNotification notification) {
        this.notification = notification;
    }

    @Override
    protected void createContentArea(Composite parent) {
        Composite contentComposite = new Composite(parent, SWT.NO_FOCUS);
        GridLayout gridLayout = new GridLayout(2, false);
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(contentComposite);
        contentComposite.setLayout(gridLayout);
        contentComposite.setBackground(contentComposite.getBackground());
        final Label notificationLabelIcon = new Label(contentComposite, SWT.NO_FOCUS);
        notificationLabelIcon.setBackground(contentComposite.getBackground());
        final StyledText labelText = new StyledText(contentComposite, SWT.BEGINNING | SWT.READ_ONLY | SWT.MULTI | SWT.WRAP
                | SWT.NO_FOCUS);
        labelText.setForeground(CommonColors.TEXT_QUOTED);
        labelText.setText(abbreviate(notification.getLabel(), MAX_LABEL_CHAR_LENGTH));
        labelText.setBackground(contentComposite.getBackground());
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(labelText);
        String description = notification.getDescription();
        if (isNotBlank(description)) {
            StyledText descriptionText = new StyledText(contentComposite, SWT.BEGINNING | SWT.READ_ONLY | SWT.MULTI | SWT.WRAP
                    | SWT.NO_FOCUS);
            descriptionText.setText(abbreviate(description, MAX_DESCRIPTION_CHAR_LENGTH));
            descriptionText.setBackground(contentComposite.getBackground());
            GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).grab(true, false).align(SWT.FILL, SWT.TOP)
                    .applyTo(descriptionText);
        }
        if (notification instanceof ExecutableErrorReportUiNotification) {
            ExecutableErrorReportUiNotification executableNotification = (ExecutableErrorReportUiNotification) notification;
            Composite linksComposite = new Composite(contentComposite, SWT.NO_FOCUS | SWT.RIGHT);
            GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).grab(true, false).align(SWT.END, SWT.TOP)
                    .applyTo(linksComposite);
            GridLayoutFactory.fillDefaults().numColumns(executableNotification.getActions().size())
                    .applyTo(linksComposite);
            for (final Action action : executableNotification.getActions()) {
                final ScalingHyperlink actionLink = new ScalingHyperlink(linksComposite, SWT.RIGHT | SWT.NO_FOCUS);
                GridDataFactory.fillDefaults().grab(true, false).applyTo(actionLink);
                actionLink.setForeground(CommonColors.HYPERLINK_WIDGET);
                actionLink.registerMouseTrackListener();
                actionLink.setText(action.getName());
                actionLink.setBackground(contentComposite.getBackground());
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

    }

    @Override
    protected String getPopupShellTitle() {
        return notification.getTitle();
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