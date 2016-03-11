package org.eclipse.recommenders.news.impl.poll;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.eclipse.recommenders.news.api.NewsItem;

public class TestUtils {

    public static Date now(long delta, TimeUnit timeUnit) {
        long now = System.currentTimeMillis();
        return new Date(now + timeUnit.toMillis(delta));
    }

    public static InputStream asInputStream(NewsItem... items) {
        DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);

        StringBuilder rss = new StringBuilder();
        rss.append("<?xml version='1.0' encoding='UTF-8'?>");
        rss.append("<rss version='2.0'>");
        rss.append("<channel>");
        rss.append("<title>Feed</title>");

        for (NewsItem item : items) {
            rss.append("<item>");
            rss.append("<title>").append(item.getTitle()).append("</title>");
            rss.append("<pubDate>").append(formatter.format(item.getDate())).append("</pubDate>");
            rss.append("<link>").append(item.getUri().toString()).append("</link>");
            rss.append("</item>");
        }
        rss.append("</channel>");
        rss.append("</rss>");

        return new ByteArrayInputStream(rss.toString().getBytes(StandardCharsets.UTF_8));
    }
}
