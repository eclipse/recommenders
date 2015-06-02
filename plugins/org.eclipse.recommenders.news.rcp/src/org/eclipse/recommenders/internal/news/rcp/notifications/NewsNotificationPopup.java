/**
 * Copyright (c) 2015 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.news.rcp.notifications;

import static org.eclipse.recommenders.internal.news.rcp.FeedEvents.createFeedMessageReadEvent;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.recommenders.internal.news.rcp.Constants;
import org.eclipse.recommenders.internal.news.rcp.FeedDescriptor;
import org.eclipse.recommenders.internal.news.rcp.Messages;
import org.eclipse.recommenders.news.rcp.IFeedMessage;
import org.eclipse.recommenders.rcp.utils.BrowserUtils;
import org.eclipse.recommenders.rcp.utils.Shells;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.eventbus.EventBus;

public class NewsNotificationPopup extends AbstractNotificationPopup {

    private static final int DELAY_CLOSE_MS = 4000;

    private final Map<FeedDescriptor, List<IFeedMessage>> messages;
    private final EventBus eventBus;

    public NewsNotificationPopup(Map<FeedDescriptor, List<IFeedMessage>> messages, EventBus eventBus) {
        super(Shells.getDisplay());
        this.messages = messages;
        this.eventBus = eventBus;
        setFadingEnabled(true);
        setDelayClose(DELAY_CLOSE_MS);
    }

    @Override
    protected void createContentArea(Composite composite) {
        super.createContentArea(composite);
        composite.setLayout(new GridLayout(1, true));
        int counter = 0;
        Map<FeedDescriptor, List<IFeedMessage>> sortedMap = sortByFirstDate(messages);

        for (Entry<FeedDescriptor, List<IFeedMessage>> entry : sortedMap.entrySet()) {
            if (counter != Constants.DEFAULT_NOTIFICATION_MESSAGES) {
                Label feedTitle = new Label(composite, SWT.NONE);
                GridDataFactory.fillDefaults().hint(AbstractNotificationPopup.MAX_WIDTH, SWT.DEFAULT)
                        .applyTo(feedTitle);
                feedTitle.setFont(CommonFonts.BOLD);
                feedTitle.setText(entry.getKey().getName());

                for (final IFeedMessage message : entry.getValue()) {
                    if (!message.isRead()) {
                        Link link = new Link(composite, SWT.WRAP);
                        link.setText(
                                MessageFormat.format("<a href=\"{1}\">{0}</a>", message.getTitle(), message.getUrl())); //$NON-NLS-1$
                        GridDataFactory.fillDefaults().hint(AbstractNotificationPopup.MAX_WIDTH, SWT.DEFAULT)
                                .applyTo(link);
                        link.addSelectionListener(new SelectionAdapter() {
                            @Override
                            public void widgetSelected(SelectionEvent e) {
                                BrowserUtils.openInExternalBrowser(e.text);
                                eventBus.post(createFeedMessageReadEvent(message.getId()));
                            }
                        });
                        counter++;
                    }
                }
            }
        }
    }

    private static Map<FeedDescriptor, List<IFeedMessage>> sortByFirstDate(
            Map<FeedDescriptor, List<IFeedMessage>> unsortedMap) {
        List<Map.Entry<FeedDescriptor, List<IFeedMessage>>> list = Lists.newArrayList(unsortedMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<FeedDescriptor, List<IFeedMessage>>>() {

            @Override
            public int compare(Entry<FeedDescriptor, List<IFeedMessage>> lhs,
                    Entry<FeedDescriptor, List<IFeedMessage>> rhs) {
                return lhs.getValue().get(0).getDate().compareTo(rhs.getValue().get(0).getDate());
            }

        });
        Map<FeedDescriptor, List<IFeedMessage>> result = Maps.newLinkedHashMap();
        for (Iterator<Map.Entry<FeedDescriptor, List<IFeedMessage>>> iter = list.iterator(); iter.hasNext();) {
            Map.Entry<FeedDescriptor, List<IFeedMessage>> entry = iter.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    @Override
    protected String getPopupShellTitle() {
        return Messages.NOTIFICATION_TITLE;
    }
}
