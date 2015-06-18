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

import static org.eclipse.recommenders.internal.news.rcp.FeedEvents.createFeedMessageReadEvent;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.recommenders.internal.news.rcp.FeedDescriptor;
import org.eclipse.recommenders.internal.news.rcp.l10n.Messages;
import org.eclipse.recommenders.news.rcp.IFeedMessage;
import org.eclipse.recommenders.news.rcp.INewsService;
import org.eclipse.recommenders.rcp.utils.BrowserUtils;
import org.eclipse.recommenders.rcp.utils.Shells;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;

public class NewsNotificationPopup extends AbstractNotificationPopup {

    private static final int DELAY_CLOSE_MS = 4000;
    private static final int DEFAULT_NOTIFICATION_MESSAGES = 6;
    private static final int DEFAULT_NOTIFICATION_MESSAGE_PER_FEED = 2;

    private final Map<FeedDescriptor, List<IFeedMessage>> messages;
    private final EventBus eventBus;
    private final INewsService service;
    private final Map<FeedDescriptor, Date> feedLatestMessageDates = Maps.newHashMap();

    public NewsNotificationPopup(Map<FeedDescriptor, List<IFeedMessage>> messages, EventBus eventBus,
            INewsService service, Display display) {
        super(Shells.getDisplay());
        this.messages = messages;
        this.eventBus = eventBus;
        this.service = service;
        setFadingEnabled(true);
        setDelayClose(DELAY_CLOSE_MS);
    }

    @Override
    protected void createContentArea(Composite composite) {
        super.createContentArea(composite);
        composite.setLayout(new GridLayout(1, true));
        Map<FeedDescriptor, List<IFeedMessage>> sortedMap = sortByDate(messages);

        processNotificationData(composite, sortedMap);

        Label hint = new Label(composite, SWT.NONE);
        GridDataFactory.fillDefaults().hint(AbstractNotificationPopup.MAX_WIDTH, SWT.DEFAULT).applyTo(hint);
        hint.setText(Messages.bind(Messages.HINT_MORE_MESSAGES, countUnreadMessages(sortedMap)));
        service.updateFeedDates(feedLatestMessageDates);
    }

    @VisibleForTesting
    protected static Map<FeedDescriptor, List<IFeedMessage>> sortByDate(Map<FeedDescriptor, List<IFeedMessage>> map) {
        for (Map.Entry<FeedDescriptor, List<IFeedMessage>> entry : map.entrySet()) {
            List<IFeedMessage> list = entry.getValue();
            Collections.sort(list, new Comparator<IFeedMessage>() {
                @Override
                public int compare(IFeedMessage lhs, IFeedMessage rhs) {
                    return rhs.getDate().compareTo(lhs.getDate());
                }
            });
            entry.setValue(list);
        }
        return map;
    }

    private void processNotificationData(Composite composite, Map<FeedDescriptor, List<IFeedMessage>> sortedMap) {
        int counter = 0;
        for (Entry<FeedDescriptor, List<IFeedMessage>> entry : sortedMap.entrySet()) {
            if (counter < DEFAULT_NOTIFICATION_MESSAGES) {
                Label feedTitle = new Label(composite, SWT.NONE);
                GridDataFactory.fillDefaults().hint(AbstractNotificationPopup.MAX_WIDTH, SWT.DEFAULT)
                        .applyTo(feedTitle);
                feedTitle.setFont(CommonFonts.BOLD);
                feedTitle.setText(entry.getKey().getName());

                processMessages(composite, counter, entry.getValue());
            }
            feedLatestMessageDates.put(entry.getKey(), entry.getValue().get(0).getDate());
        }
    }

    private void processMessages(Composite composite, int counter, List<IFeedMessage> messages) {
        int innerCounter = 0;
        for (final IFeedMessage message : messages) {
            if (counter < DEFAULT_NOTIFICATION_MESSAGES && !message.isRead()
                    && innerCounter < DEFAULT_NOTIFICATION_MESSAGE_PER_FEED) {
                Link link = new Link(composite, SWT.WRAP);
                link.setText(MessageFormat.format("<a href=\"{1}\">{0}</a>", message.getTitle(), message.getUrl())); //$NON-NLS-1$
                GridDataFactory.fillDefaults().hint(AbstractNotificationPopup.MAX_WIDTH, SWT.DEFAULT).applyTo(link);
                link.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        BrowserUtils.openInExternalBrowser(e.text);
                        eventBus.post(createFeedMessageReadEvent(message.getId()));
                    }
                });
                counter++;
                innerCounter++;
            }
        }
    }

    private int countUnreadMessages(Map<FeedDescriptor, List<IFeedMessage>> map) {
        int result = 0;
        for (Map.Entry<FeedDescriptor, List<IFeedMessage>> entry : map.entrySet()) {
            for (IFeedMessage message : entry.getValue()) {
                if (!message.isRead()) {
                    result++;
                }
            }
        }
        return result;
    }

    @Override
    protected String getPopupShellTitle() {
        return Messages.NOTIFICATION_TITLE;
    }
}
