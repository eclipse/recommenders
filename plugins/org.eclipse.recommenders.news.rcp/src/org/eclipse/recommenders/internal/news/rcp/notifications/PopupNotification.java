/**
 * Copyright (c) 2015 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Haftstein - initial API and implementation based on org.eclipse.mylyn.internal.commons.notifications.ui.popup.NotificationPopup.
 */
package org.eclipse.recommenders.internal.news.rcp.notifications;

import static org.apache.commons.lang3.StringUtils.*;

import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.mylyn.commons.notifications.ui.AbstractUiNotification;
import org.eclipse.mylyn.commons.ui.compatibility.CommonColors;
import org.eclipse.mylyn.commons.workbench.AbstractWorkbenchNotificationPopup;
import org.eclipse.mylyn.commons.workbench.forms.CommonFormUtil;
import org.eclipse.mylyn.commons.workbench.forms.ScalingHyperlink;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

import com.google.common.collect.Lists;

@SuppressWarnings("restriction")
public class PopupNotification extends AbstractWorkbenchNotificationPopup {

    private final class BlockPopupOnModalShellActivationListener implements Listener {

        private boolean popupBlocked = false;
        private boolean popupReactivated = false;
        private String title;
        private Label titleLabel;

        public BlockPopupOnModalShellActivationListener() {
            title = getPopupShellTitle();
            titleLabel = getTitleLabel(getContents());
            title = titleLabel.getText();
            // check existing shells
            IWorkbench workbench = PlatformUI.getWorkbench();
            for (Shell shell : workbench.getDisplay().getShells()) {
                if (isVisibleAndModal(shell) && !popupBlocked) {
                    deactivate();
                }
            }
        }

        private Label getTitleLabel(Control c) {
            if (c instanceof Label) {
                Label l = (Label) c;
                if (getPopupShellTitle().equals(l.getText())) {
                    return l;
                }
            }
            if (c instanceof Composite) {
                for (Control cc : ((Composite) c).getChildren()) {
                    Label l = getTitleLabel(cc);
                    if (l != null) {
                        return l;
                    }
                }
            }
            return null;
        }

        @Override
        public void handleEvent(Event event) {
            if (!isPopupOpen()) {
                return;
            }
            Widget w = event.widget;
            if (w instanceof Shell) {
                Shell shell = (Shell) w;
                if (isVisibleAndModal(shell)) {
                    if (!popupBlocked) {
                        deactivate();
                    }
                } else {
                    if (popupBlocked) {
                        popupReactivated = true;
                        activate();
                    }
                }
            }
        }

        private boolean isPopupOpen() {
            return getShell() != null && !getShell().isDisposed();
        }

        private boolean isVisibleAndModal(Shell shell) {
            int modal = SWT.APPLICATION_MODAL | SWT.SYSTEM_MODAL | SWT.PRIMARY_MODAL;
            return shell.isVisible() && (shell.getStyle() & modal) != 0;
        }

        private void deactivate() {
            popupBlocked = true;
            titleLabel.setText("(Waiting for focus) " + title);
            for (ScalingHyperlink link : links) {
                link.setEnabled(false);
            }
        }

        private void activate() {
            popupBlocked = false;
            titleLabel.setText(title);
            for (ScalingHyperlink link : links) {
                link.setEnabled(true);
            }
        }
    }

    @Override
    public void closeFade() {
        // the close job can not be extended to handle the blocked popup.
        // therefore the closing fade is checked and the job is rescheduled.
        if (blockPopupListener.popupBlocked) {
            scheduleAutoClose();
            return;
        }
        if (blockPopupListener.popupReactivated) {
            // the popup has been reactivated but a close job will still be active and may be triggered short
            // afterwards. For this reason
            // schedule another time interval
            blockPopupListener.popupReactivated = false;
            scheduleAutoClose();
            return;
        }
        super.closeFade();
    }

    private static final int MAX_LABEL_CHAR_LENGTH = 120;
    private static final int MAX_DESCRIPTION_CHAR_LENGTH = 500;
    private static final int MIN_HEIGHT = 100;
    private static final int MAX_WIDTH = 400;
    private static final int PADDING_EDGE = 5;

    private Notification notification;
    private List<ScalingHyperlink> links = Lists.newArrayList();
    private BlockPopupOnModalShellActivationListener blockPopupListener;

    public PopupNotification(Display display) {
        super(display);
    }

    public PopupNotification(Display display, int style) {
        super(display, style);
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    @Override
    protected Image getPopupShellImage(int maximumHeight) {
        if (notification instanceof AbstractUiNotification) {
            return ((AbstractUiNotification) notification).getNotificationImage();
        }
        return super.getPopupShellImage(maximumHeight);
    }

    @Override
    protected void createContentArea(Composite parent) {
        Composite contentComposite = new Composite(parent, SWT.NO_FOCUS);
        GridLayout gridLayout = new GridLayout(2, false);
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(contentComposite);
        contentComposite.setLayout(gridLayout);
        final Label notificationLabelIcon = new Label(contentComposite, SWT.NO_FOCUS);
        notificationLabelIcon.setBackground(parent.getBackground());
        final Text labelText = new Text(contentComposite,
                SWT.BEGINNING | SWT.READ_ONLY | SWT.MULTI | SWT.WRAP | SWT.NO_FOCUS);
        labelText.setForeground(new Color(Display.getDefault(), 232, 33, 33));
        labelText.setText(abbreviate(notification.getLabel(), MAX_LABEL_CHAR_LENGTH));
        GridDataFactory.fillDefaults().grab(true, false).align(SWT.FILL, SWT.TOP).applyTo(labelText);
        String description = notification.getDescription();
        if (isNotBlank(description)) {
            Text descriptionText = new Text(contentComposite,
                    SWT.BEGINNING | SWT.READ_ONLY | SWT.MULTI | SWT.WRAP | SWT.NO_FOCUS);
            descriptionText.setText(abbreviate(description, MAX_DESCRIPTION_CHAR_LENGTH));
            GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).grab(true, false).align(SWT.FILL, SWT.TOP)
                    .applyTo(descriptionText);
        }
        if (notification instanceof Notification) {
            Notification executableNotification = notification;
            Composite linksComposite = new Composite(contentComposite, SWT.NO_FOCUS | SWT.RIGHT);
            GridDataFactory.fillDefaults().span(2, SWT.DEFAULT).grab(true, false).align(SWT.END, SWT.TOP)
                    .applyTo(linksComposite);
            GridLayoutFactory.fillDefaults().numColumns(executableNotification.getActions().size())
                    .applyTo(linksComposite);
            for (final NotificationAction action : executableNotification.getActions()) {
                final ScalingHyperlink actionLink = new ScalingHyperlink(linksComposite, SWT.RIGHT | SWT.NO_FOCUS);
                GridDataFactory.fillDefaults().grab(true, false).applyTo(actionLink);
                actionLink.setForeground(CommonColors.HYPERLINK_WIDGET);
                actionLink.registerMouseTrackListener();
                actionLink.setText(action.getName());
                actionLink.addHyperlinkListener(new HyperlinkAdapter() {
                    @Override
                    public void linkActivated(HyperlinkEvent e) {
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
                        action.execute();
                    }
                });
                links.add(actionLink);
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
        // superclass computes size with SWT.DEFAULT,SWT.DEFAULT. For long text
        // this causes a large width
        // and a small height. Afterwards the height gets maxed to the
        // MIN_HEIGHT value and the width gets trimmed
        // which results in text floating out of the window
        Point initialSize = shell.computeSize(MAX_WIDTH, SWT.DEFAULT);
        int height = Math.max(initialSize.y, MIN_HEIGHT);
        int width = Math.min(initialSize.x, MAX_WIDTH);

        Point size = new Point(width, height);
        shell.setLocation(clArea.width + clArea.x - size.x - PADDING_EDGE,
                clArea.height + clArea.y - size.y - PADDING_EDGE);
        shell.setSize(size);
    }

    private Rectangle getPrimaryClientArea() {
        Monitor primaryMonitor = getShell().getDisplay().getPrimaryMonitor();
        return primaryMonitor != null ? primaryMonitor.getClientArea() : getShell().getDisplay().getClientArea();
    }

    @Override
    public boolean close() {
        if (notification != null) {
            notification.close();
        }
        PlatformUI.getWorkbench().getDisplay().removeFilter(SWT.Activate, blockPopupListener);
        return super.close();
    }

    @Override
    public void create() {
        super.create();
        registerModalShellListener();
    }

    private void registerModalShellListener() {
        blockPopupListener = new BlockPopupOnModalShellActivationListener();
        PlatformUI.getWorkbench().getDisplay().addFilter(SWT.Activate, blockPopupListener);
    }

}
