package org.eclipse.recommenders.utils;

import static org.eclipse.recommenders.utils.Urls.*;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import org.junit.Test;

public class UrlsTest {

    private static final String VALID_ABSOLUTE = "http://download.eclipse.org/recommenders/models/2.0/v201210_1212/";
    private static final String VALID_ESCAPED = "http___download_eclipse_org_recommenders_models_2_0_v201210_1212_";

    @Test
    public void testMangle() {
        String out = mangle(VALID_ABSOLUTE);
        assertEquals(VALID_ESCAPED, out);
    }

    @Test
    public void testMangleUrl() {
        String out = mangle(toUrl(VALID_ABSOLUTE));
        assertEquals(VALID_ESCAPED, out);
    }

    @Test
    public void testToUrl() throws MalformedURLException {
        URL out = toUrl(VALID_ABSOLUTE);
        assertEquals(new URL(VALID_ABSOLUTE), out);
    }

    @Test(expected = RuntimeException.class)
    public void testToUrFails() {
        Urls.toUrl("http/");
    }

    @Test
    public void testValidAbsoluteUri() throws Exception {
        URI expectedUri = new URI("http://download.eclipse.org/recommenders");

        assertThat(parseURI("http://download.eclipse.org/recommenders").get(), is(expectedUri));
    }

    @Test
    public void testValidRelativeUri() throws Exception {
        URI expectedUri = new URI("download.eclipse.org/recommenders/models/2.0/");
        assertThat(parseURI("download.eclipse.org/recommenders/models/2.0/").get(), is(expectedUri));
    }

    @Test
    public void testEmptyUri() throws Exception {
        URI uri = new URI("");
        assertThat(parseURI("").get(), is(uri));
    }

    @Test
    public void testInvalidUri() throws Exception {
        assertEquals(parseURI("<>").isPresent(), false);
    }

    @Test
    public void testValidAbsoluteUriWithSupportedProtocol() throws Exception {
        URI uri = new URI("http://download.eclipse.org/recommenders");
        assertEquals(isUriProtocolSupported(uri, "http", "file", "https"), true);
    }

    @Test
    public void testValidRelativeUriProtocol() throws Exception {
        URI uri = new URI("download.eclipse.org/recommenders/models/2.0/v201210_1212/");
        assertEquals(isUriProtocolSupported(uri, "http", "file", "https"), false);
    }

    @Test
    public void testValidAbsoluteUriWithUnsupportedProtocol() throws Exception {
        URI uri = new URI("http://download.eclipse.org/recommenders");
        assertEquals(isUriProtocolSupported(uri, "file"), false);
    }

    @Test
    public void testValidAbsoluteUriWithEmptyProtocolList() throws Exception {
        URI uri = new URI("http://download.eclipse.org/recommenders");
        assertEquals(isUriProtocolSupported(uri), false);
    }

    @Test
    public void testLowerUpperCaseValidAbsoluteUriWithSupportedProtocol() throws Exception {
        URI uri = new URI("http://download.eclipse.org/recommenders");
        assertEquals(isUriProtocolSupported(uri, "HTTP", "file", "https"), true);
    }

    @Test
    public void testAbsoluteUriProtocolIsSubstringOfSupportedProtocol() throws Exception {
        URI uri = new URI("http://download.eclipse.org/recommenders");
        assertEquals(isUriProtocolSupported(uri, "file", "https"), false);
    }
}
