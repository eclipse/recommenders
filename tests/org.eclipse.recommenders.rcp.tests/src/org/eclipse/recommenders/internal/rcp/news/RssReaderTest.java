package org.eclipse.recommenders.internal.rcp.news;

import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.time.DateUtils.truncatedCompareTo;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.eclipse.recommenders.utils.Pair;
import org.junit.Test;

public class RssReaderTest {

    private static final String TITLE_1 = "Title 1";
    private static final String TITLE_2 = "Title 2";

    @Test
    public void testNullString() {
        List<Pair<Date, String>> entries = RssReader.getEntries(null, new Date());
        assertThat(entries.isEmpty(), is(true));
    }

    @Test
    public void testEmptyString() {
        List<Pair<Date, String>> entries = RssReader.getEntries("", new Date());
        assertThat(entries.isEmpty(), is(true));
    }

    @Test
    public void testNullDate() {
        XmlBuilder xml = new XmlBuilder();
        xml.addEntry(new Date(), TITLE_1);
        List<Pair<Date, String>> entries = RssReader.getEntries(xml.build(), null);
        assertThat(entries.isEmpty(), is(true));
    }

    @Test
    public void testIllegalXml() {
        List<Pair<Date, String>> entries = RssReader.getEntries("<rss><item></rss>", new Date());
        assertThat(entries.isEmpty(), is(true));
    }

    @Test
    public void testSingleEntry() {
        Date publishDate = DateUtils.addDays(new Date(), -1);
        Date lastCheckedDate = DateUtils.addDays(new Date(), -2);
        XmlBuilder xml = new XmlBuilder();
        xml.addEntry(publishDate, TITLE_1);

        List<Pair<Date, String>> entries = RssReader.getEntries(xml.build(), lastCheckedDate);

        assertThat(truncatedCompareTo(entries.get(0).getFirst(), publishDate, Calendar.MINUTE), is(0));
        assertThat(entries.get(0).getSecond(), is(equalTo(TITLE_1)));
        assertThat(entries.size(), is(1));
    }

    @Test
    public void testTwoEntries() {
        Date publishDate1 = DateUtils.addDays(new Date(), -1);
        Date publishDate2 = DateUtils.addDays(new Date(), -2);
        Date lastCheckedDate = DateUtils.addDays(new Date(), -3);
        XmlBuilder xml = new XmlBuilder();
        xml.addEntry(publishDate1, TITLE_1);
        xml.addEntry(publishDate2, TITLE_2);

        List<Pair<Date, String>> entries = RssReader.getEntries(xml.build(), lastCheckedDate);

        assertThat(truncatedCompareTo(entries.get(0).getFirst(), publishDate1, Calendar.MINUTE), is(0));
        assertThat(entries.get(0).getSecond(), is(equalTo(TITLE_1)));
        assertThat(truncatedCompareTo(entries.get(1).getFirst(), publishDate2, Calendar.MINUTE), is(0));
        assertThat(entries.get(1).getSecond(), is(equalTo(TITLE_2)));
        assertThat(entries.size(), is(2));
    }

    @Test
    public void testTwoEntriesOneAlreadyChecked() {
        Date publishDate1 = DateUtils.addDays(new Date(), -1);
        Date publishDate2 = DateUtils.addDays(new Date(), -3);
        Date lastCheckedDate = DateUtils.addDays(new Date(), -2);
        XmlBuilder xml = new XmlBuilder();
        xml.addEntry(publishDate1, TITLE_1);
        xml.addEntry(publishDate2, TITLE_2);

        List<Pair<Date, String>> entries = RssReader.getEntries(xml.build(), lastCheckedDate);

        assertThat(truncatedCompareTo(entries.get(0).getFirst(), publishDate1, Calendar.MINUTE), is(0));
        assertThat(entries.get(0).getSecond(), is(equalTo(TITLE_1)));
        assertThat(entries.size(), is(1));
    }

    private class XmlBuilder {

        private final StringBuilder sb;

        public XmlBuilder() {
            sb = new StringBuilder();
            sb.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?><rss version=\"2.0\" xml:base=\"http://www.example.org\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:fb=\"http://www.facebook.com/2008/fbml\" xmlns:content=\"http://purl.org/rss/1.0/modules/content/\" xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" xmlns:og=\"http://ogp.me/ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" xmlns:sioc=\"http://rdfs.org/sioc/ns#\" xmlns:sioct=\"http://rdfs.org/sioc/types#\" xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema#\" xmlns:schema=\"http://schema.org/\">");
            sb.append("<channel>");
            sb.append("<title>Blog</title>");
            sb.append("<link>http://www.example.org</link>");
            sb.append("<description></description>");
            sb.append("<language>en</language>");
        }

        public XmlBuilder addEntry(Date date, String title) {
            sb.append("<item>");
            sb.append(format("<title>{0}</title>", title));
            sb.append(format("<link>{0}</link>", "http://www.example.org"));
            sb.append(format("<description>{0}</description>", "Description"));
            sb.append(format("<pubDate>{0}</pubDate>", RssReader.DATE_FORMAT.format(date)));
            sb.append("</item>");
            return this;
        }

        public String build() {
            sb.append("</channel>");
            sb.append("</rss>");
            return sb.toString();
        }
    }
}
