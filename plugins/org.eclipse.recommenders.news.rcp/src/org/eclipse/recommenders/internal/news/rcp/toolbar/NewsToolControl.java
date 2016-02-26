package org.eclipse.recommenders.internal.news.rcp.toolbar;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.di.extensions.Preference;
import org.eclipse.e4.ui.model.application.ui.menu.MToolControl;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.recommenders.internal.news.rcp.CommonImages;
import org.eclipse.recommenders.internal.news.rcp.Constants;
import org.eclipse.recommenders.news.api.INewsService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

import com.google.common.collect.ImmutableMap;

@SuppressWarnings("restriction")
public class NewsToolControl {

    private final MToolControl modelElement;
    private final INewsService newsService;

    @Inject
    public NewsToolControl(MToolControl modelElement, INewsService newsService) {
        this.modelElement = modelElement;
        this.newsService = newsService;
    }

    @Inject
    public void setEnabled(@Preference(Constants.PREF_NEWS_ENABLED) boolean enabled) {
        modelElement.setVisible(enabled);
    }

    @PostConstruct
    public void createGui(Composite parent, IWorkbenchWindow serviceLocator) {
        MenuManager contextMenu = new MenuManager();

        final ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT | SWT.HORIZONTAL);
        toolBarManager.setContextMenuManager(contextMenu);

        CommandContributionItemParameter contributionParameters = new CommandContributionItemParameter(serviceLocator,
                null, "org.eclipse.ui.browser.openBrowser", SWT.NONE);
        contributionParameters.label = "Open Manual...";
        contributionParameters.parameters = ImmutableMap.of("url", "http://www.eclipse.org/recommenders/manual/");
        contextMenu.add(new CommandContributionItem(contributionParameters));

        toolBarManager.add(new Action() {

            @Override
            public void run() {
                toolBarManager.getContextMenuManager().getMenu().setVisible(true);
            }

            @Override
            public ImageDescriptor getImageDescriptor() {
                return CommonImages.RSS_ACTIVE;
            }
        });

        toolBarManager.createControl(parent);
    }
}