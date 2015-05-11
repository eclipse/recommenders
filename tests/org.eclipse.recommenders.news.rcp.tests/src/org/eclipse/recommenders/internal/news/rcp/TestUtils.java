package org.eclipse.recommenders.internal.news.rcp;

import static org.mockito.Mockito.when;

import org.eclipse.core.runtime.IConfigurationElement;
import org.mockito.Mockito;

public class TestUtils {

    private static final String testUrl = "http://planeteclipse.org/planet/rss20.xml";

    public static FeedDescriptor enabled(String id) {
        IConfigurationElement config = Mockito.mock(IConfigurationElement.class);
        when(config.getAttribute("id")).thenReturn(id);
        when(config.getAttribute("url")).thenReturn(testUrl);
        return new FeedDescriptor(config, true);
    }

    public static FeedDescriptor disabled(String id) {
        IConfigurationElement config = Mockito.mock(IConfigurationElement.class);
        when(config.getAttribute("id")).thenReturn(id);
        when(config.getAttribute("url")).thenReturn(testUrl);
        return new FeedDescriptor(config, false);
    }

}
