/**
 * Copyright (c) 2015 Pawel Nowak.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.recommenders.internal.news.rcp;

import static org.mockito.Mockito.*;

import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.recommenders.news.rcp.IFeedMessage;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

public class TestUtils {

    private static final String TEST_URL = "http://planeteclipse.org/planet/rss20.xml";

    public static FeedDescriptor enabled(String id) {
        IConfigurationElement config = Mockito.mock(IConfigurationElement.class);
        when(config.getAttribute("id")).thenReturn(id);
        when(config.getAttribute("url")).thenReturn(TEST_URL);
        return new FeedDescriptor(config, true);
    }

    public static FeedDescriptor disabled(String id) {
        IConfigurationElement config = Mockito.mock(IConfigurationElement.class);
        when(config.getAttribute("id")).thenReturn(id);
        when(config.getAttribute("url")).thenReturn(TEST_URL);
        return new FeedDescriptor(config, false);
    }

    public static List<IFeedMessage> mockMessages(boolean... readMessages) {
        List<IFeedMessage> feedMessages = Lists.newArrayList();
        for (boolean isRead : readMessages) {
            IFeedMessage message = mock(IFeedMessage.class);
            when(message.isRead()).thenReturn(isRead);
            feedMessages.add(message);
        }
        return feedMessages;
    }

}
