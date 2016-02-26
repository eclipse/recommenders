/**
 * Copyright (c) 2016 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Sewe - initial API and implementation.
 */
package org.eclipse.recommenders.internal.news.rcp.toolbar;

import static org.eclipse.recommenders.internal.news.rcp.MessageUtils.getPeriodStartDate;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.e4.core.commands.ECommandService;
import org.eclipse.e4.core.commands.EHandlerService;
import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.recommenders.internal.news.rcp.CommonImages;
import org.eclipse.recommenders.internal.news.rcp.Constants;
import org.eclipse.recommenders.internal.news.rcp.FeedDescriptor;
import org.eclipse.recommenders.internal.news.rcp.MessageUtils.MessageAge;
import org.eclipse.recommenders.internal.news.rcp.NewsRcpPreferences;
import org.eclipse.recommenders.internal.news.rcp.PreferenceConstants;
import org.eclipse.recommenders.internal.news.rcp.command.Commands;
import org.eclipse.recommenders.internal.news.rcp.l10n.Messages;
import org.eclipse.recommenders.news.api.NewsItem;
import org.eclipse.recommenders.news.api.poll.INewsPollingService;
import org.eclipse.recommenders.news.api.poll.PollingPolicy;
import org.eclipse.recommenders.news.api.poll.PollingRequest;
import org.eclipse.recommenders.news.api.poll.PollingResult;
import org.eclipse.recommenders.news.api.read.IReadItemsStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

@SuppressWarnings("restriction")
public class NewsToolControl {

    private static final class PlaceholderAction extends Action {

        private PlaceholderAction(String label) {
            super(label);
            setEnabled(false);
        }
    }

    private final class OpenContextMenuAction extends Action {

        @Override
        public void run() {
            toolBarManager.getContextMenuManager().getMenu().setVisible(true);
        }

        @Override
        public ImageDescriptor getImageDescriptor() {
            return CommonImages.RSS_ACTIVE;
        }
    }

    private final class ExecuteCommandAction extends Action {

        private final ParameterizedCommand command;

        private ExecuteCommandAction(String text, ImageDescriptor image, String commandId,
                Map<String, Object> commandParameters) {
            super(text, image);
            command = commandService.createCommand(commandId, commandParameters);
        }

        @Override
        public void run() {
            handlerService.executeHandler(command);
        }

        @Override
        public boolean isEnabled() {
            return handlerService.canExecute(command);
        }
    }

    private final MToolControl modelElement;
    private final NewsRcpPreferences preferences;
    private final INewsPollingService pollingService;
    private final IReadItemsStore readItemsStore;
    private final ECommandService commandService;
    private final EHandlerService handlerService;

    private ToolBarManager toolBarManager;

    @Inject
    public NewsToolControl(MToolControl modelElement, NewsRcpPreferences preferences,
            INewsPollingService pollingService, IReadItemsStore readItemsStore, ECommandService commandService,
            EHandlerService handlerService) {
        this.modelElement = modelElement;
        this.preferences = preferences;
        this.pollingService = pollingService;
        this.readItemsStore = readItemsStore;
        this.commandService = commandService;
        this.handlerService = handlerService;
    }

    /**
     * Causes the singleton {@link NotificationBridge} to be instantiated.
     */
    @Inject
    public void initialize(NotificationBridge ignored) {
        // No op
    }

    /**
     * Causes the singleton {@link PollingController} to be instantiated.
     */
    @Inject
    public void initialize(PollingController ignored) {
        // No op
    }

    @Inject
    public void setEnabled(@Preference(PreferenceConstants.NEWS_ENABLED) boolean enabled) {
        modelElement.setToBeRendered(enabled);
    }

    @PostConstruct
    public void createGui(Composite parent) {
        toolBarManager = new ToolBarManager(SWT.FLAT | SWT.HORIZONTAL);

        MenuManager contextMenu = new MenuManager();
        contextMenu.setRemoveAllWhenShown(true);
        contextMenu.addMenuListener(new IMenuListener() {

            @Override
            public void menuAboutToShow(IMenuManager menu) {
                Map<FeedDescriptor, PollingResult> feedContents = retrieveFeedContents(
                        preferences.getFeedDescriptors());

                if (feedContents.isEmpty()) {
                    PlaceholderAction allFeedDisabledAction = new PlaceholderAction(Messages.LABEL_ALL_FEEDS_DISABLED);
                    menu.add(allFeedDisabledAction);
                } else {
                    for (Entry<FeedDescriptor, PollingResult> entry : sortByName(feedContents.entrySet())) {
                        MenuManager feedMenu = createFeedMenu(entry.getKey(), entry.getValue());
                        menu.add(feedMenu);
                    }
                }

                menu.add(new Separator());

                ExecuteCommandAction pollNowAction = new ExecuteCommandAction(Messages.LABEL_POLL_FEEDS,
                        CommonImages.REFRESH, Commands.COMMAND_ID_POLL_NEWS_FEEDS,
                        Collections.<String, Object>emptyMap());
                menu.add(pollNowAction);

                ExecuteCommandAction markAsReadAction = new ExecuteCommandAction(Messages.LABEL_MARK_AS_READ, null,
                        Commands.COMMAND_ID_READ_NEWS_ITEMS, ImmutableMap.<String, Object>of(
                                Commands.COMMAND_PARAM_READ_NEWS_ITEMS_NEWS_ITEMS, getAllItems(feedContents.values())));
                menu.add(markAsReadAction);

                menu.add(new Separator());

                ExecuteCommandAction preferencesAction = new ExecuteCommandAction(Messages.LABEL_PREFERENCES, null,
                        Commands.COMMAND_ID_PREFERENCES, ImmutableMap.<String, Object>of(
                                Commands.COMMAND_PARAM_PREFERENCES_PREFERENCE_PAGE_ID, Constants.PREF_PAGE_ID));
                menu.add(preferencesAction);
            }

            private Iterable<Entry<FeedDescriptor, PollingResult>> sortByName(Set<Entry<FeedDescriptor, PollingResult>> entrySet) {
                return Ordering.natural().onResultOf(new Function<Entry<FeedDescriptor, PollingResult>, String>() {

                    @Override
                    public String apply(Entry<FeedDescriptor, PollingResult> entry) {
                        return entry.getKey().getName();
                    }}).sortedCopy(entrySet);
            }
        });

        toolBarManager.setContextMenuManager(contextMenu);
        toolBarManager.add(new OpenContextMenuAction());

        toolBarManager.createControl(parent);
    }

    private MenuManager createFeedMenu(FeedDescriptor feed, PollingResult result) {
        List<NewsItem> items = result.getAllNewsItems();

        int numberOfUnreadMessages = getNumberOfUnreadMessages(items);
        String feedLabel;
        if (numberOfUnreadMessages == 0) {
            feedLabel = MessageFormat.format(Messages.LABEL_READ_FEED, preserveAtSign(feed.getName()));
        } else {
            feedLabel = MessageFormat.format(Messages.LABEL_UNREAD_FEED, preserveAtSign(feed.getName()),
                    numberOfUnreadMessages);
        }

        MenuManager feedMenu = new MenuManager(feedLabel);

        List<List<NewsItem>> ageGroups = splitMessagesByAge(items);
        List<String> labels = ImmutableList.of(Messages.LABEL_TODAY, Messages.LABEL_YESTERDAY, Messages.LABEL_THIS_WEEK,
                Messages.LABEL_LAST_WEEK, Messages.LABEL_THIS_MONTH, Messages.LABEL_LAST_MONTH,
                Messages.LABEL_THIS_YEAR, Messages.LABEL_OLDER_ENTRIES, Messages.LABEL_UNDETERMINED_ENTRIES);
        for (int i = 0; i < MessageAge.values().length; i++) {
            List<NewsItem> ageGroup = ageGroups.get(i);
            if (ageGroup.isEmpty()) {
                continue;
            }

            feedMenu.add(new Separator());
            feedMenu.add(new PlaceholderAction(labels.get(i)));

            for (NewsItem item : ageGroup) {
                String itemLabel;
                if (readItemsStore.isRead(item)) {
                    itemLabel = MessageFormat.format(Messages.LABEL_READ_ITEM, preserveAtSign(item.getTitle()));
                } else {
                    itemLabel = MessageFormat.format(Messages.LABEL_UNREAD_ITEM, preserveAtSign(item.getTitle()));
                }

                ExecuteCommandAction readNewsItemAction = new ExecuteCommandAction(itemLabel, null,
                        Commands.COMMAND_ID_READ_NEWS_ITEMS,
                        ImmutableMap.<String, Object>of(Commands.COMMAND_PARAM_READ_NEWS_ITEMS_NEWS_ITEMS,
                                Collections.singleton(item), Commands.COMMAND_PARAM_READ_NEWS_ITEMS_OPEN_BROWSER,
                                true));
                feedMenu.add(readNewsItemAction);
            }
        }

        feedMenu.add(new Separator());

        ExecuteCommandAction markAsReadAction = new ExecuteCommandAction(Messages.LABEL_MARK_AS_READ, null,
                Commands.COMMAND_ID_READ_NEWS_ITEMS,
                ImmutableMap.<String, Object>of(Commands.COMMAND_PARAM_READ_NEWS_ITEMS_NEWS_ITEMS, items));
        feedMenu.add(markAsReadAction);

        return feedMenu;
    }

    private int getNumberOfUnreadMessages(List<NewsItem> items) {
        int numberOfUnreadMessages = 0;
        for (NewsItem item : items) {
            if (!readItemsStore.isRead(item)) {
                numberOfUnreadMessages++;
            }
        }
        return numberOfUnreadMessages;
    }

    /**
     * @see org.eclipse.jface.action.IAction#setText(java.lang.String)
     * @see <a href="https://bugs.eclipse.org/bugs/show_bug.cgi?id=486086">Bug 486086</a>
     */
    private static String preserveAtSign(String actionText) {
        String atSign = "@"; //$NON-NLS-1$
        if (actionText.contains(atSign)) {
            return actionText + atSign;
        } else {
            return actionText;
        }
    }

    @PreDestroy
    public void dispose() {
        if (toolBarManager != null) {
            toolBarManager.dispose();
        }
    }

    private Map<FeedDescriptor, PollingResult> retrieveFeedContents(List<FeedDescriptor> feeds) {
        List<PollingRequest> requests = new ArrayList<>(feeds.size());
        for (FeedDescriptor feed : feeds) {
            if (!feed.isEnabled()) {
                continue;
            }
            requests.add(new PollingRequest(feed.getUri(), PollingPolicy.never()));
        }

        if (requests.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<FeedDescriptor, PollingResult> feedContents = new HashMap<>();
        Collection<PollingResult> results = pollingService.poll(requests, null);
        Iterator<FeedDescriptor> feedsIterator = feeds.iterator();
        Iterator<PollingResult> resultsIterator = results.iterator();
        while (feedsIterator.hasNext() && resultsIterator.hasNext()) {
            FeedDescriptor feed = feedsIterator.next();
            PollingResult result = resultsIterator.next();
            feedContents.put(feed, result);
        }

        return feedContents;
    }

    private static List<List<NewsItem>> splitMessagesByAge(List<NewsItem> messages) {
        Locale locale = Locale.getDefault();
        Calendar calendar = Calendar.getInstance(locale);
        List<List<NewsItem>> result = new ArrayList<>();
        for (int i = 0; i < MessageAge.values().length; i++) {
            List<NewsItem> list = Lists.newArrayList();
            result.add(list);
        }

        if (messages == null) {
            return result;
        }
        Date today = DateUtils.truncate(calendar.getTime(), Calendar.DAY_OF_MONTH);
        for (NewsItem message : messages) {
            for (MessageAge messageAge : MessageAge.values()) {
                if (message.getDate() == null) {
                    result.get(MessageAge.UNDETERMINED.getIndex()).add(message);
                    break;
                }
                if (message.getDate().after(getPeriodStartDate(messageAge, today, locale))
                        || message.getDate().equals(getPeriodStartDate(messageAge, today, locale))) {
                    result.get(messageAge.getIndex()).add(message);
                    break;
                }
            }
            if (message.getDate() != null
                    && message.getDate().before(getPeriodStartDate(MessageAge.OLDER, today, locale))) {
                result.get(MessageAge.OLDER.getIndex()).add(message);
            }
        }
        return result;
    }

    private static Collection<NewsItem> getAllItems(Collection<PollingResult> results) {
        Collection<NewsItem> allItems = new ArrayList<>();
        for (PollingResult result : results) {
            allItems.addAll(result.getAllNewsItems());
        }
        return allItems;
    }
}
