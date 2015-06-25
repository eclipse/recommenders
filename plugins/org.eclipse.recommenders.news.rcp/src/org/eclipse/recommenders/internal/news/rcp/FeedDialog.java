/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.recommenders.internal.news.rcp.l10n.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FeedDialog extends TitleAreaDialog {
    private Text nameValue;
    private Text urlValue;
    private Text pollingIntervalValue;
    private FeedDescriptor feed;

    public FeedDialog(Shell parentShell) {
        super(parentShell);
    }

    public FeedDialog(Shell parentShell, FeedDescriptor feed) {
        super(parentShell);
        this.feed = feed;
    }

    @Override
    public void create() {
        super.create();
        setTitle(Messages.FEED_DIALOG_TITLE);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        Composite container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout(2, false);
        container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        container.setLayout(layout);
        createFeed(container);
        return area;
    }

    private void createFeed(Composite container) {
        Label name = new Label(container, SWT.NONE);
        name.setText(Messages.FIELD_LABEL_FEED_NAME);
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        nameValue = new Text(container, SWT.BORDER);
        if (feed != null) {
            nameValue.setText(feed.getName());
        }
        nameValue.setLayoutData(gridData);
        Label url = new Label(container, SWT.NONE);
        url.setText(Messages.FIELD_LABEL_URL);
        urlValue = new Text(container, SWT.BORDER);
        if (feed != null) {
            urlValue.setText(feed.getUrl().toString());
        }
        urlValue.setLayoutData(gridData);
        Label pollingInterval = new Label(container, SWT.NONE);
        pollingInterval.setText(Messages.FIELD_LABEL_POLLING_INTERVAL);
        pollingIntervalValue = new Text(container, SWT.BORDER);
        pollingIntervalValue.setTextLimit(4);
        pollingIntervalValue.setText(Constants.DEFAULT_POLLING_INTERVAL.toString());
        if (feed != null) {
            pollingIntervalValue.setText(feed.getPollingInterval());
        }
        pollingIntervalValue.setLayoutData(gridData);
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    @Override
    protected void okPressed() {
        if (!pollingIntervalValue.getText().matches("[0-9]+")) {
            MessageDialog.openError(getShell(), Messages.FEED_DIALOG_ERROR,
                    Messages.FEED_DESCRIPTOR_POLLING_INTERVAL_DIGITS_ONLY);
            return;
        }
        try {
            feed = new FeedDescriptor(urlValue.getText(), nameValue.getText(), pollingIntervalValue.getText());
        } catch (IllegalArgumentException e) {
            MessageDialog.openError(getShell(), Messages.FEED_DIALOG_ERROR, Messages.FEED_DESCRIPTOR_MALFORMED_URL);
            return;
        }
        super.okPressed();
    }

    public FeedDescriptor getFeed() {
        return feed;
    }
}
