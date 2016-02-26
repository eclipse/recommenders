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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.EventTopic;
import org.eclipse.mylyn.commons.notifications.ui.NotificationsUi;
import org.eclipse.recommenders.internal.news.rcp.FeedDescriptor;
import org.eclipse.recommenders.internal.news.rcp.NewsRcpPreferences;
import org.eclipse.recommenders.internal.news.rcp.notifications.NewMessageNotification;
import org.eclipse.recommenders.news.api.Topics;
import org.eclipse.recommenders.news.api.poll.PollingResult;

@Creatable
@Singleton
public class NotificationBridge {

    private final NewsRcpPreferences preferences;

    @Inject
    public NotificationBridge(NewsRcpPreferences preferences) {
        this.preferences = preferences;
    }

    @Inject
    @Optional
    public void handlePollingResult(@EventTopic(Topics.POLLING_RESULT) Collection<PollingResult> pollingResults) {
        Map<FeedDescriptor, PollingResult> map = new HashMap<>();

        List<FeedDescriptor> feedDescriptors = preferences.getFeedDescriptors();
        RESULTS: for (PollingResult pollingResult : pollingResults) {
            if (pollingResult.getAllNewsItems().isEmpty()) {
                continue;
            }

            for (FeedDescriptor feedDescriptor : feedDescriptors) {
                if (feedDescriptor.isEnabled() && pollingResult.getFeedUri().equals(feedDescriptor.getUri())) {
                    map.put(feedDescriptor, pollingResult);
                    continue RESULTS;
                }
            }
        }

        if (!map.isEmpty()) {
            NotificationsUi.getService().notify(Arrays.asList(new NewMessageNotification(map)));
        }
    }
}
