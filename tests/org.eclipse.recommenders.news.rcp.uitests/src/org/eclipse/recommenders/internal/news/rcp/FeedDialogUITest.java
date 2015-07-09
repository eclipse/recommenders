/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Based on https://github.com/eclipse/cdt/blob/master/build/org.eclipse.cdt.autotools.ui.tests/src/org/eclipse/cdt/autotools/ui/tests/AbstractTest.java
 */
package org.eclipse.recommenders.internal.news.rcp;

import static org.junit.Assert.fail;

import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.eclipse.recommenders.internal.news.rcp.l10n.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SWTBotJunit4ClassRunner.class)
public class FeedDialogUITest {
    private static final String VALID_FEED_NAME = "feed"; //$NON-NLS-1$
    private static final String VALID_FEED_URL = "http://eclipse.org"; //$NON-NLS-1$
    private static SWTWorkbenchBot bot;
    private static SWTBotShell mainShell;

    private NewsRcpPreferences preferences = new NewsRcpPreferences();;

    @BeforeClass
    public static void beforeClass() throws Exception {
        bot = new SWTWorkbenchBot();
    }

    @AfterClass
    public static void sleep() {
        bot.sleep(2000);
    }

    @Test
    public void testAddCustomFeed() {
        List<FeedDescriptor> feeds = preferences.getFeedDescriptors();
        SWTBotView startingView = bot.activeView();
        if (startingView.getTitle().equals("Welcome")) { //$NON-NLS-1$
            startingView.close();
        }
        mainShell = getMainShell();
        if (Platform.getOS().equals(Platform.OS_MACOSX)) {
            // On Mac, the Preferences menu is under the system menu
            final IWorkbench workbench = PlatformUI.getWorkbench();
            workbench.getDisplay().asyncExec(new Runnable() {
                @Override
                public void run() {
                    IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
                    if (window != null) {
                        Menu appMenu = workbench.getDisplay().getSystemMenu();
                        for (MenuItem item : appMenu.getItems()) {
                            if (item.getText().startsWith("Preferences")) { //$NON-NLS-1$
                                Event event = new Event();
                                event.time = (int) System.currentTimeMillis();
                                event.widget = item;
                                event.display = workbench.getDisplay();
                                item.setSelection(true);
                                item.notifyListeners(SWT.Selection, event);
                                break;
                            }
                        }
                    }
                }
            });
        } else {
            clickMainMenu("Window", "Preferences"); //$NON-NLS-1$
        }
        SWTBotShell shell = bot.shell("Preferences"); //$NON-NLS-1$
        shell.activate();
        bot.text().setText(Messages.PREFPAGE_TITLE);
        bot.waitUntil(new NodeAvailableAndSelect(bot.tree(), "General", Messages.PREFPAGE_TITLE)); //$NON-NLS-1$
        bot.button(Messages.PREFPAGE_BUTTON_NEW).click();
        bot.textWithLabel(Messages.FIELD_LABEL_FEED_NAME).setText(VALID_FEED_NAME);
        bot.textWithLabel(Messages.FIELD_LABEL_URL).setText(VALID_FEED_URL);
    }

    private static void clickMainMenu(String... items) {
        if (items.length == 0) {
            return;
        }
        mainShell.setFocus();
        SWTBotMenu menu = bot.menu(items[0]);
        for (int i = 1; i < items.length; i++) {
            menu = menu.menu(items[i]);
        }
        menu.click();
    }

    private static SWTBotShell getMainShell() {
        for (SWTBotShell shellBot : bot.shells()) {
            if (shellBot.getText().toLowerCase().contains("eclipse")) {
                return shellBot;
            }
        }
        fail("Could not find main shell");
        return null;
    }

    public static class NodeAvailableAndSelect extends DefaultCondition {
        private SWTBotTree tree;
        private String parent;
        private String node;

        /**
         * Wait for a tree node (with a known parent) to become visible, and select it when it does. Note that this wait
         * condition should only be used after having made an attempt to reveal the node.
         *
         * @param tree
         *            The SWTBotTree that contains the node to select.
         * @param parent
         *            The text of the parent node that contains the node to select.
         * @param node
         *            The text of the node to select.
         */
        public NodeAvailableAndSelect(SWTBotTree tree, String parent, String node) {
            this.tree = tree;
            this.node = node;
            this.parent = parent;
        }

        @Override
        public boolean test() {
            try {
                SWTBotTreeItem parentNode = tree.getTreeItem(parent);
                parentNode.getNode(node).select();
                return true;
            } catch (WidgetNotFoundException e) {
                return false;
            }
        }

        @Override
        public String getFailureMessage() {
            return "Timed out waiting for " + node; //$NON-NLS-1$
        }
    }

}
