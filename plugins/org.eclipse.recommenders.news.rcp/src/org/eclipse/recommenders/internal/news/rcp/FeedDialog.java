/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.recommenders.internal.news.rcp.l10n.Messages;
import org.eclipse.recommenders.utils.Urls;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;

public class FeedDialog extends TitleAreaDialog {
    private static final List<String> ACCEPTED_PROTOCOL = ImmutableList.of("http"); //$NON-NLS-1$

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
        super.getButton(OK).setEnabled(false);
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
        nameValue.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                updateDialog();
            }
        });
        urlValue.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                updateDialog();
            }
        });
        pollingIntervalValue.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                updateDialog();
            }
        });
    }

    @Override
    protected boolean isResizable() {
        return false;
    }

    @Override
    protected void okPressed() {
        try {
            feed = new FeedDescriptor(urlValue.getText(), nameValue.getText(), pollingIntervalValue.getText());
        } catch (IllegalArgumentException e) {
            // MessageDialog.openError(getShell(), Messages.FEED_DIALOG_ERROR, Messages.FEED_DESCRIPTOR_MALFORMED_URL);
            return;
        }
        super.okPressed();
    }

    public FeedDescriptor getFeed() {
        return feed;
    }

    public void updateDialog() {
        setErrorMessage(null);

        if (Strings.isNullOrEmpty(nameValue.getText())) {
            setErrorMessage(Messages.FEED_DIALOG_ERROR_EMPTY_NAME);
            super.getButton(OK).setEnabled(false);
        } else if (Strings.isNullOrEmpty(urlValue.getText())) {
            setErrorMessage(Messages.FEED_DIALOG_ERROR_EMPTY_URL);
            super.getButton(OK).setEnabled(false);
        } else if (!Urls.isUriProtocolSupported(Urls.parseURI(urlValue.getText()).orNull(), ACCEPTED_PROTOCOL)) {
            setErrorMessage(MessageFormat.format(Messages.FEED_DIALOG_ERROR_PROTOCOL_UNSUPPORTED, urlValue.getText()));
            super.getButton(OK).setEnabled(false);
        } else if (!FeedDescriptor.isUrlValid(urlValue.getText())) {
            setErrorMessage(Messages.FEED_DIALOG_ERROR_INVALID_URL);
            super.getButton(OK).setEnabled(false);
        } else if (!pollingIntervalValue.getText().matches("[0-9]+")) {
            setErrorMessage(Messages.FEED_DESCRIPTOR_POLLING_INTERVAL_DIGITS_ONLY);
            super.getButton(OK).setEnabled(false);
        }

        if (getErrorMessage() == null) {
            super.getButton(OK).setEnabled(true);
        }
    }
}
