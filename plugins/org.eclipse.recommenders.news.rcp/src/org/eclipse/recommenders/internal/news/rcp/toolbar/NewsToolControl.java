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
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import org.eclipse.recommenders.internal.news.rcp.command.Commands;
import org.eclipse.recommenders.internal.news.rcp.NewsRcpPreferences;
import org.eclipse.recommenders.internal.news.rcp.l10n.Messages;
import org.eclipse.recommenders.news.api.NewsItem;
import org.eclipse.recommenders.news.api.poll.INewsPollingService;
import org.eclipse.recommenders.news.api.poll.PollingPolicy;
import org.eclipse.recommenders.news.api.poll.PollingRequest;
import org.eclipse.recommenders.news.api.poll.PollingResult;
import org.eclipse.recommenders.news.api.read.IReadItemsStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

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

        private final String commandId;

        private final Map<String, Object> commandParameters;

        private ExecuteCommandAction(String text, ImageDescriptor image, String commandId,
                Map<String, Object> commandParameters) {
            super(text, image);
            this.commandId = commandId;
            this.commandParameters = commandParameters;
        }

        @Override
        public void run() {
            ParameterizedCommand command = commandService.createCommand(commandId, commandParameters);
            handlerService.executeHandler(command);
        }
    }

    private final MToolControl modelElement;
    private final NewsRcpPreferences preferences;
    private final INewsPollingService newsService;
    private final IReadItemsStore readItemsStore;
    private final ECommandService commandService;
    private final EHandlerService handlerService;

    private ToolBarManager toolBarManager;
    private NotificationBridge notificationBridge;

    @Inject
    public NewsToolControl(MToolControl modelElement, NewsRcpPreferences preferences, INewsPollingService newsService,
            IReadItemsStore readItemsStore, ECommandService commandService, EHandlerService handlerService, NotificationBridge notificationBridge) {
        this.modelElement = modelElement;
        this.preferences = preferences;
        this.newsService = newsService;
        this.readItemsStore = readItemsStore;
        this.commandService = commandService;
        this.handlerService = handlerService;
        this.notificationBridge = notificationBridge;
    }

    @Inject
    public void setEnabled(@Preference(Constants.PREF_NEWS_ENABLED) boolean enabled) {
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
                List<FeedDescriptor> feeds = preferences.getFeedDescriptors();
                boolean feedMenuAdded = false;
                for (FeedDescriptor feed : feeds) {
                    if (!feed.isEnabled()) {
                        continue;
                    }

                    PollingRequest request = new PollingRequest(feed.getUri(), PollingPolicy.never());
                    Collection<PollingResult> results = newsService.poll(Collections.singletonList(request), null);
                    if (results.size() > 0) {
                        PollingResult result = Iterables.getOnlyElement(results);

                        MenuManager feedMenu = createFeedMenu(feed, result);
                        menu.add(feedMenu);
                        feedMenuAdded = true;
                    }
                }

                if (!feedMenuAdded) {
                    PlaceholderAction allFeedDisabledAction = new PlaceholderAction(Messages.LABEL_ALL_FEEDS_DISABLED);
                    menu.add(allFeedDisabledAction);
                }

                menu.add(new Separator());

                ExecuteCommandAction pollNowAction = new ExecuteCommandAction(Messages.LABEL_POLL_FEEDS,
                        CommonImages.REFRESH, Commands.COMMAND_ID_POLL_NEWS_FEEDS,
                        Collections.<String, Object>emptyMap());
                menu.add(pollNowAction);

                menu.add(new Separator());

                ExecuteCommandAction preferencesAction = new ExecuteCommandAction(Messages.LABEL_PREFERENCES, null,
                        Commands.COMMAND_ID_PREFERENCES, ImmutableMap.<String, Object>of(
                                Commands.COMMAND_PARAM_PREFERENCES_PREFERENCE_PAGE_ID, Constants.PREF_PAGE_ID));
                menu.add(preferencesAction);
            }
        });

        toolBarManager.setContextMenuManager(contextMenu);
        toolBarManager.add(new OpenContextMenuAction());

        toolBarManager.createControl(parent);
    }

    private MenuManager createFeedMenu(FeedDescriptor feed, PollingResult result) {
        List<NewsItem> items = result.getAllNewsItems();

        MenuManager feedMenu = new MenuManager();

        int numberOfUnreadMessages = getNumberOfUnreadMessages(items);
        String feedLabel;
        if (numberOfUnreadMessages == 0) {
            feedLabel = MessageFormat.format(Messages.LABEL_READ_FEED, preserveAtSign(feed.getName()));
        } else {
            feedLabel = MessageFormat.format(Messages.LABEL_UNREAD_FEED, preserveAtSign(feed.getName()), numberOfUnreadMessages);
        }
        feedMenu.setMenuText(feedLabel);

        if (feed.getIcon() != null) {
            feedMenu.setImageDescriptor(ImageDescriptor.createFromImage(feed.getIcon()));
        }

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
        feedMenu.add(new ExecuteCommandAction(Messages.LABEL_MARK_AS_READ, null, Commands.COMMAND_ID_READ_NEWS_ITEMS,
                ImmutableMap.<String, Object>of(Commands.COMMAND_PARAM_READ_NEWS_ITEMS_NEWS_ITEMS, items)));

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

    @PreDestroy
    public void dispose() {
        if (toolBarManager != null) {
            toolBarManager.dispose();
        }
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

    public static List<List<NewsItem>> splitMessagesByAge(List<NewsItem> messages) {
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
}
