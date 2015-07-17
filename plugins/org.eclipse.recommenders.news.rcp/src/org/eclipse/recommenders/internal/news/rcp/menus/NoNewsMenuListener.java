/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp.menus;

import static org.eclipse.recommenders.internal.news.rcp.menus.MarkAsReadAction.newMarkAllAsReadAction;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.recommenders.internal.news.rcp.FeedDescriptor;
import org.eclipse.recommenders.internal.news.rcp.l10n.Messages;
import org.eclipse.recommenders.news.rcp.IFeedMessage;

public class NoNewsMenuListener implements IMenuListener {
    private Map<FeedDescriptor, List<IFeedMessage>> messages;

    public void setMessages(Map<FeedDescriptor, List<IFeedMessage>> messages) {
        this.messages = messages;
    }

    @Override
    public void menuAboutToShow(IMenuManager manager) {
        for (Entry<FeedDescriptor, List<IFeedMessage>> entry : messages.entrySet()) {
            MenuManager menu = new MenuManager(
                    MessageFormat.format(Messages.READ_MESSAGE_OR_FEED, entry.getKey().getName()),
                    entry.getKey().getId());
            if (entry.getKey().getIcon() != null) {
                // in Kepler: The method setImageDescriptor(ImageDescriptor) is undefined for the type MenuManager
                // menu.setImageDescriptor(ImageDescriptor.createFromImage(entry.getKey().getIcon()));
            }
            Action action = new Action() {
            };
            action.setText(entry.getValue().get(0).getTitle());
            action.setEnabled(false);
            menu.add(action);
            manager.add(menu);
        }
        manager.add(new Separator());
        Action markAllAsReadAction = newMarkAllAsReadAction(null);
        markAllAsReadAction.setEnabled(false);
        manager.add(markAllAsReadAction);
        manager.add(new Separator());
        manager.add(new PreferencesAction());
    }

}
