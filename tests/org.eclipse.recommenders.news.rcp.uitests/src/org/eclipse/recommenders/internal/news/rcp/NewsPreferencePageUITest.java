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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.text.MessageFormat;

import org.eclipse.recommenders.internal.news.rcp.l10n.Messages;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.common.base.Joiner;

@RunWith(SWTBotJunit4ClassRunner.class)
public class NewsPreferencePageUITest {
    private static final String VALID_FEED_NAME = "feed"; //$NON-NLS-1$
    private static final String VALID_FEED_NAME_A = "feedA"; //$NON-NLS-1$
    private static final String VALID_FEED_URL = "http://eclipse.org"; //$NON-NLS-1$
    private static final String INVALID_FEED_URL = "http://§$%"; //$NON-NLS-1$
    private static final String CHARACTERS_AND_DIGITS = "abc123"; //$NON-NLS-1$
    private static final String INVALID_FEED_PROTOCOL = "ftp://eclipse.org"; //$NON-NLS-1$

    private SWTWorkbenchBot bot;

    @Before
    public void setUp() {
        bot = new SWTWorkbenchBot();
        openPreferencePage(bot);
    }

    @After
    public void tearDown() {
        if (!bot.activeShell().getText().equals("Preferences")) { //$NON-NLS-1$
            bot.button("Cancel").click(); //$NON-NLS-1$
        }
        bot.button("Restore Defaults").click(); //$NON-NLS-1$
        bot.button("OK").click(); //$NON-NLS-1$
    }

    @Test
    public void testAddCustomFeed() {
        addCustomFeed(bot);

        applyChangesAndReopenPreferencePage();

        assertThat(bot.table().getTableItem(2).getText(), is(equalTo(VALID_FEED_NAME)));
    }

    @Test
    public void testAddCustomFeedWithoutName() {
        bot.button(Messages.PREFPAGE_BUTTON_NEW).click();
        bot.textWithLabel(Messages.FIELD_LABEL_URL).setText(VALID_FEED_URL);

        // the space below is here because TitleAreaDialog also adds a space to messages
        assertThat(bot.text(" " + Messages.FEED_DIALOG_ERROR_EMPTY_NAME), is(notNullValue())); //$NON-NLS-1$
        assertThat(bot.button("OK").isEnabled(), is(false)); //$NON-NLS-1$
    }

    @Test
    public void testAddCustomFeedWithoutURL() {
        bot.button(Messages.PREFPAGE_BUTTON_NEW).click();
        bot.textWithLabel(Messages.FIELD_LABEL_FEED_NAME).setText(VALID_FEED_NAME);

        assertThat(bot.text(" " + Messages.FEED_DIALOG_ERROR_EMPTY_URL), is(notNullValue())); //$NON-NLS-1$
        assertThat(bot.button("OK").isEnabled(), is(false)); //$NON-NLS-1$
    }

    @Test
    public void testAddCustomFeedWithInvalidURL() {
        bot.button(Messages.PREFPAGE_BUTTON_NEW).click();
        bot.textWithLabel(Messages.FIELD_LABEL_FEED_NAME).setText(VALID_FEED_NAME);
        bot.textWithLabel(Messages.FIELD_LABEL_URL).setText(INVALID_FEED_URL);

        assertThat(bot.text(" " + Messages.FEED_DIALOG_ERROR_INVALID_URL), is(notNullValue())); //$NON-NLS-1$
        assertThat(bot.button("OK").isEnabled(), is(false)); //$NON-NLS-1$
    }

    @Test
    public void testAddCustomFeedWithInvalidPollingInterval() {
        bot.button(Messages.PREFPAGE_BUTTON_NEW).click();
        bot.textWithLabel(Messages.FIELD_LABEL_FEED_NAME).setText(VALID_FEED_NAME);
        bot.textWithLabel(Messages.FIELD_LABEL_URL).setText(VALID_FEED_URL);
        bot.textWithLabel(Messages.FIELD_LABEL_POLLING_INTERVAL).setText(CHARACTERS_AND_DIGITS);

        assertThat(bot.text(" " + Messages.FEED_DIALOG_ERROR_POLLING_INTERVAL_DIGITS_ONLY), is(notNullValue())); //$NON-NLS-1$
        assertThat(bot.button("OK").isEnabled(), is(false)); //$NON-NLS-1$
    }

    @Test
    public void testAddCustomFeedWithDuplicateURL() {
        bot.button(Messages.PREFPAGE_BUTTON_NEW).click();
        bot.textWithLabel(Messages.FIELD_LABEL_FEED_NAME).setText(VALID_FEED_NAME);
        bot.textWithLabel(Messages.FIELD_LABEL_URL).setText("http://planeteclipse.org/planet/rss20.xml"); //$NON-NLS-1$

        assertThat(bot.text(" " + MessageFormat.format(Messages.FEED_DIALOG_ERROR_DUPLICATE_FEED, "Planet Eclipse")), //$NON-NLS-1$ //$NON-NLS-2$
                is(notNullValue()));
        assertThat(bot.button("OK").isEnabled(), is(false)); //$NON-NLS-1$
    }

    @Test
    public void testAddCustomFeedWithInvalidProtocol() {
        bot.button(Messages.PREFPAGE_BUTTON_NEW).click();
        bot.textWithLabel(Messages.FIELD_LABEL_FEED_NAME).setText(VALID_FEED_NAME);
        bot.textWithLabel(Messages.FIELD_LABEL_URL).setText(INVALID_FEED_PROTOCOL);

        assertThat(
                bot.text(" " + MessageFormat.format(Messages.FEED_DIALOG_ERROR_PROTOCOL_UNSUPPORTED,
                        INVALID_FEED_PROTOCOL, Joiner.on(", ").join(FeedDialog.ACCEPTED_PROTOCOLS))),
                is(notNullValue()));
        assertThat(bot.button("OK").isEnabled(), is(false)); //$NON-NLS-1$
    }

    @Test
    public void testRemoveCustomFeed() {
        addCustomFeed(bot);
        bot.table().getTableItem(2).select();
        bot.button(Messages.PREFPAGE_BUTTON_REMOVE).click();
        applyChangesAndReopenPreferencePage();

        assertThat(bot.table().rowCount(), is(equalTo(2)));
    }

    @Test
    public void testEditCustomFeed() {
        addCustomFeed(bot);
        bot.table().getTableItem(2).select();
        bot.button(Messages.PREFPAGE_BUTTON_EDIT).click();
        bot.textWithLabel(Messages.FIELD_LABEL_FEED_NAME).setText(VALID_FEED_NAME_A);
        bot.button("OK").click();
        applyChangesAndReopenPreferencePage();

        assertThat(bot.table().rowCount(), is(equalTo(3)));
        assertThat(bot.table().getTableItem(2).getText(), is(equalTo(VALID_FEED_NAME_A)));
    }

    @Test
    public void testEditCustomFeedByDoubleClick() {
        addCustomFeed(bot);
        bot.table().doubleClick(2, 0);
        bot.textWithLabel(Messages.FIELD_LABEL_FEED_NAME).setText(VALID_FEED_NAME_A);
        bot.button("OK").click();
        applyChangesAndReopenPreferencePage();

        assertThat(bot.table().rowCount(), is(equalTo(3)));
        assertThat(bot.table().getTableItem(2).getText(), is(equalTo(VALID_FEED_NAME_A)));
    }

    @Test
    public void testCannotEditDefaultFeed() {
        bot.table().doubleClick(1, 0);
        bot.activeShell().activate();

        // the shell will be still Preferences, because it's not possible to edit default repository feed, so the dialog
        // won't be opened
        assertThat(bot.activeShell().getText(), is(equalTo("Preferences"))); //$NON-NLS-1$
        assertThat(bot.button(Messages.PREFPAGE_BUTTON_EDIT).isEnabled(), is(false));
    }

    @Test
    public void testDisableNewsFeed() {
        bot.checkBox(Messages.FIELD_LABEL_NEWS_ENABLED).click();
        applyChangesAndReopenPreferencePage();

        assertThat(bot.checkBox(Messages.FIELD_LABEL_NEWS_ENABLED).isChecked(), is(false));
    }

    @Test
    public void testDisableCustomFeed() {
        addCustomFeed(bot);
        bot.table().getTableItem(2).uncheck();
        applyChangesAndReopenPreferencePage();

        assertThat(bot.table().getTableItem(2).isChecked(), is(false));
    }

    @Test
    public void testDisableDefaultRepositoryFeed() {
        bot.table().getTableItem(0).uncheck();
        applyChangesAndReopenPreferencePage();

        assertThat(bot.table().getTableItem(0).isChecked(), is(false));
    }

    @Test
    public void testRestoreDefaults() {
        addCustomFeed(bot);
        applyChangesAndReopenPreferencePage();

        bot.button("Restore Defaults").click(); //$NON-NLS-1$

        applyChangesAndReopenPreferencePage();

        assertThat(bot.table().rowCount(), is(equalTo(2)));
    }

    @Test
    public void testNotificationLinkLeadsToProperPreferencePage() {
        bot.link().click();

        assertThat(bot.checkBox("Enable notifications"), is(notNullValue())); //$NON-NLS-1$
    }

    @Test
    public void testWebBrowserSettingsinkLeadsToProperPreferencePage() {
        bot.link(1).click("Web Browser");

        assertThat(bot.table().getTableItem(0).getText(), is(equalTo("Default system web browser")));
    }

    private static void openPreferencePage(SWTWorkbenchBot bot) {
        PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

            @Override
            public void run() {
                PreferencesUtil
                        .createPreferenceDialogOn(null, "org.eclipse.recommenders.news.rcp.preferencePage", null, null) //$NON-NLS-1$
                        .open();
            }

        });
        bot.waitUntil(new NodeAvailableAndSelect(bot.tree(), "General", Messages.PREFPAGE_TITLE)); //$NON-NLS-1$
    }

    private void applyChangesAndReopenPreferencePage() {
        bot.button("OK").click();
        openPreferencePage(bot);
    }

    private void addCustomFeed(SWTWorkbenchBot bot) {
        bot.button(Messages.PREFPAGE_BUTTON_NEW).click();
        bot.textWithLabel(Messages.FIELD_LABEL_FEED_NAME).setText(VALID_FEED_NAME);
        bot.textWithLabel(Messages.FIELD_LABEL_URL).setText(VALID_FEED_URL);
        bot.button("OK").click();
    }

    public static class NodeAvailableAndSelect extends DefaultCondition {

        private final SWTBotTree tree;
        private final String parent;
        private final String node;

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
