package org.eclipse.recommenders.internal.news.rcp.v2.toolbar;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;

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
import org.eclipse.recommenders.internal.news.rcp.l10n.Messages;
import org.eclipse.recommenders.news.api.FeedItem;
import org.eclipse.recommenders.news.api.poll.INewsPollingService;
import org.eclipse.recommenders.news.api.poll.PollingPolicy;
import org.eclipse.recommenders.news.api.poll.PollingRequest;
import org.eclipse.recommenders.news.api.poll.PollingResult;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import com.google.common.collect.ImmutableMap;

@SuppressWarnings("restriction")
public class NewsToolControl {

    private static final class OpenContextMenuAction extends Action {

        private final MenuManager contextMenu;

        private OpenContextMenuAction(MenuManager contextMenu) {
            this.contextMenu = contextMenu;
        }

        @Override
        public void run() {
            contextMenu.getMenu().setVisible(true);
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
    private final INewsPollingService newsService;
    private final ECommandService commandService;
    private final EHandlerService handlerService;

    private ToolBarManager toolBarManager;

    @Inject
    public NewsToolControl(MToolControl modelElement, INewsPollingService newsService, ECommandService commandService,
            EHandlerService handlerService) {
        this.modelElement = modelElement;
        this.newsService = newsService;
        this.commandService = commandService;
        this.handlerService = handlerService;
    }

    @Inject
    public void setEnabled(@Preference(Constants.PREF_NEWS_ENABLED) boolean enabled) {
        modelElement.setVisible(enabled);
    }

    @PostConstruct
    public void createGui(Composite parent) {
        toolBarManager = new ToolBarManager(SWT.FLAT | SWT.HORIZONTAL);

        MenuManager contextMenu = new MenuManager();
        contextMenu.setRemoveAllWhenShown(true);
        contextMenu.addMenuListener(new IMenuListener() {

            @Override
            public void menuAboutToShow(IMenuManager menu) {
                PollingRequest request = new PollingRequest(URI.create("http://planeteclipse.org/planet/rss20.xml"),
                        PollingPolicy.never());
                Collection<PollingResult> results = newsService.poll(Collections.singletonList(request), null);

                for (PollingResult result : results) {
                    MenuManager feedMenu = createFeedMenu(result);
                    menu.add(feedMenu);
                }

                menu.add(new Separator());

                ExecuteCommandAction pollNowAction = new ExecuteCommandAction(Messages.LABEL_POLL_FEEDS,
                        CommonImages.REFRESH, "org.eclipse.recommenders.news.rcp.commands.pollNow",
                        Collections.<String, Object>emptyMap());
                menu.add(pollNowAction);

                menu.add(new Separator());

                ExecuteCommandAction preferencesAction = new ExecuteCommandAction(Messages.LABEL_PREFERENCES, null,
                        "org.eclipse.ui.window.preferences",
                        ImmutableMap.<String, Object>of("preferencePageId", Constants.PREF_PAGE_ID));
                menu.add(preferencesAction);
            }

            private MenuManager createFeedMenu(PollingResult result) {
                MenuManager feedMenu = new MenuManager(result.getFeedUri().toString(), CommonImages.RSS_ACTIVE, null);
                for (FeedItem item : result.getAllFeedItems()) {
                    ExecuteCommandAction openBrowserAction = new ExecuteCommandAction(item.getTitle(), null,
                            "org.eclipse.ui.browser.openBrowser",
                            ImmutableMap.<String, Object>of("url", item.getUri().toString()));
                    feedMenu.add(openBrowserAction);
                }
                return feedMenu;
            }
        });

        toolBarManager.setContextMenuManager(contextMenu);
        toolBarManager.add(new OpenContextMenuAction(contextMenu));

        toolBarManager.createControl(parent);
    }

    @PreDestroy
    public void dispose() {
        if (toolBarManager != null) {
            toolBarManager.dispose();
        }
    }
}
