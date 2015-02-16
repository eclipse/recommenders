package org.eclipse.recommenders.internal.rcp.news;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.eclipse.recommenders.utils.Nullable;
import org.eclipse.recommenders.utils.Pair;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public class RssReader {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z", Locale.UK);

    public static List<Pair<Date, String>> getEntries(@Nullable String xml, @Nullable Date lastChecked) {
        if (Strings.isNullOrEmpty(xml)) {
            return Collections.emptyList();
        }
        if (lastChecked == null) {
            return Collections.emptyList();
        }
        List<Pair<Date, String>> entries = Lists.newArrayList();
        XPath xPath = XPathFactory.newInstance().newXPath();
        try {
            NodeList items = (NodeList) xPath.compile("rss/channel/item").evaluate(
                    new InputSource(new StringReader(xml)), XPathConstants.NODESET);
            for (int i = 0; i < items.getLength(); i++) {
                Node item = items.item(i);
                String evaluate = xPath.evaluate("pubDate", item);
                Date date = DATE_FORMAT.parse(evaluate);
                if (date.before(lastChecked)) {
                    continue;
                }
                String title = xPath.evaluate("title", item);
                entries.add(Pair.newPair(date, title));
            }
            return entries;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
