package org.eclipse.recommenders.news.impl.poll;

import static org.eclipse.recommenders.news.impl.poll.TestUtils.asInputStream;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.util.Date;
import java.util.List;

import org.eclipse.recommenders.news.api.NewsItem;
import org.junit.Test;

public class DefaultFeedItemStoreTest {

    private static final URI FEED_URI = URI.create("http://www.example.org/feed.xml");

    private static final NewsItem FIRST_ITEM = new NewsItem("Item 1", new Date(0L),
            URI.create("http://www.example.org/items/1"), null);
    private static final NewsItem SECOND_ITEM = new NewsItem("Item 2", new Date(1000L),
            URI.create("http://www.example.org/items/2"), null);

    @Test
    public void testUpdateWithAddedItems() throws Exception {
        IFeedItemStore sut = new DefaultFeedItemStore();
        sut.udpate(FEED_URI, asInputStream(FIRST_ITEM), null);

        List<NewsItem> newItems = sut.udpate(FEED_URI, asInputStream(FIRST_ITEM, SECOND_ITEM), null);

        assertThat(newItems, contains(SECOND_ITEM));
        assertThat(newItems, hasSize(1));
    }

    @Test
    public void testUpdateWithRemovedItems() throws Exception {
        IFeedItemStore sut = new DefaultFeedItemStore();
        sut.udpate(FEED_URI, asInputStream(FIRST_ITEM, SECOND_ITEM), null);

        List<NewsItem> newItems = sut.udpate(FEED_URI, asInputStream(SECOND_ITEM), null);

        assertThat(newItems, hasSize(0));
    }

    @Test
    public void testUpdateWithoutChanges() throws Exception {
        IFeedItemStore sut = new DefaultFeedItemStore();
        sut.udpate(FEED_URI, asInputStream(FIRST_ITEM, SECOND_ITEM), null);

        List<NewsItem> newItems = sut.udpate(FEED_URI, asInputStream(FIRST_ITEM, SECOND_ITEM), null);

        assertThat(newItems, hasSize(0));
    }

    @Test
    public void testGetFeedItemsWithoutUpdate() {
        IFeedItemStore sut = new DefaultFeedItemStore();

        List<NewsItem> items = sut.getNewsItems(FEED_URI);

        assertThat(items, hasSize(0));
    }

    @Test
    public void testGetFeedItemsAfterEmptyUpdate() throws Exception {
        IFeedItemStore sut = new DefaultFeedItemStore();
        sut.udpate(FEED_URI, asInputStream(), null);

        List<NewsItem> items = sut.getNewsItems(FEED_URI);

        assertThat(items, hasSize(0));
    }

    @Test
    public void testGetFeedItemsAfterSingleUpdate() throws Exception {
        IFeedItemStore sut = new DefaultFeedItemStore();
        sut.udpate(FEED_URI, asInputStream(FIRST_ITEM), null);

        List<NewsItem> items = sut.getNewsItems(FEED_URI);

        assertThat(items, contains(FIRST_ITEM));
        assertThat(items, hasSize(1));
    }

    @Test
    public void testGetFeedItemsAfterTwoComplementaryUpdates() throws Exception {
        IFeedItemStore sut = new DefaultFeedItemStore();
        sut.udpate(FEED_URI, asInputStream(FIRST_ITEM), null);
        sut.udpate(FEED_URI, asInputStream(SECOND_ITEM), null);

        List<NewsItem> items = sut.getNewsItems(FEED_URI);

        assertThat(items, contains(SECOND_ITEM));
        assertThat(items, hasSize(1));
    }

    @Test
    public void testGetFeedItemsAfterTwoIncrementalUpdates() throws Exception {
        IFeedItemStore sut = new DefaultFeedItemStore();
        sut.udpate(FEED_URI, asInputStream(FIRST_ITEM), null);
        sut.udpate(FEED_URI, asInputStream(FIRST_ITEM, SECOND_ITEM), null);

        List<NewsItem> items = sut.getNewsItems(FEED_URI);

        assertThat(items, contains(FIRST_ITEM, SECOND_ITEM));
        assertThat(items, hasSize(2));
    }
}
