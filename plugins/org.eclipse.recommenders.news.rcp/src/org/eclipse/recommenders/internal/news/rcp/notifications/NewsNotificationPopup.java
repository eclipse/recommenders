/**
 * Copyright (c) 2015 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 *    Pawel Nowak - displaying only latest feeds, refactor
 */
package org.eclipse.recommenders.internal.news.rcp.notifications;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.ui.compatibility.CommonFonts;
import org.eclipse.mylyn.commons.ui.dialogs.AbstractNotificationPopup;
import org.eclipse.recommenders.internal.news.rcp.FeedDescriptor;
import org.eclipse.recommenders.internal.news.rcp.l10n.Messages;
import org.eclipse.recommenders.news.api.NewsItem;
import org.eclipse.recommenders.news.api.poll.PollingResult;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

public class NewsNotificationPopup extends AbstractNotificationPopup {

    private static final int DELAY_CLOSE_MS = 4000;
    private static final int DEFAULT_NOTIFICATION_MESSAGES = 6;
    private static final int MAX_WIDTH = 400; // Taken from AbstractNotificationPopup.MAX_WIDTH

    private final Map<FeedDescriptor, PollingResult> messages;

    public NewsNotificationPopup(Display display, Map<FeedDescriptor, PollingResult> messages) {
        super(display);
        this.messages = messages;
        setFadingEnabled(true);
        setDelayClose(DELAY_CLOSE_MS);
    }

    @Override
    protected void createContentArea(Composite composite) {
        super.createContentArea(composite);
        composite.setLayout(new GridLayout(1, true));

        processNotificationData(composite, messages);

        Label hint = new Label(composite, SWT.NONE);
        GridDataFactory.fillDefaults().hint(MAX_WIDTH, SWT.DEFAULT).applyTo(hint);
        hint.setText(Messages.HINT_MORE_MESSAGES);
    }

    private void processNotificationData(Composite composite, Map<FeedDescriptor, PollingResult> sortedMap) {
        int feedCounter = 0;
        int messagesPerFeed = DEFAULT_NOTIFICATION_MESSAGES < sortedMap.size() ? 1
                : DEFAULT_NOTIFICATION_MESSAGES / sortedMap.size();
        for (Entry<FeedDescriptor, PollingResult> entry : sortedMap.entrySet()) {
            if (feedCounter < DEFAULT_NOTIFICATION_MESSAGES) {
                Label feedTitle = new Label(composite, SWT.NONE);
                GridDataFactory.fillDefaults().hint(MAX_WIDTH, SWT.DEFAULT).applyTo(feedTitle);
                feedTitle.setFont(CommonFonts.BOLD);
                feedTitle.setText(entry.getKey().getName());

                feedCounter = feedCounter
                        + processMessages(composite, entry.getValue().getAllNewsItems(), messagesPerFeed, entry.getKey());
            }
        }
    }

    private int processMessages(Composite composite, List<NewsItem> list, int calculatedMessagesPerFeed,
            final FeedDescriptor feed) {
        int messagesPerFeed = 0;
        for (final NewsItem message : list) {
            if (messagesPerFeed < calculatedMessagesPerFeed) {
                Link link = new Link(composite, SWT.WRAP);
                link.setText(MessageFormat.format("<a href=\"{1}\">{0}</a>", message.getTitle(), message.getUri())); //$NON-NLS-1$
                GridDataFactory.fillDefaults().hint(MAX_WIDTH, SWT.DEFAULT).applyTo(link);
                link.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        // TODO
                    }
                });
                messagesPerFeed++;
            }
        }
        return messagesPerFeed;
    }

    @Override
    protected String getPopupShellTitle() {
        return Messages.NOTIFICATION_TITLE;
    }
}
