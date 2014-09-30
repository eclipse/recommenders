package org.eclipse.recommenders.internal.stacktraces.rcp.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.google.common.io.Files;

public class FilterServiceTest {

    private ErrorReport report;
    private FilterService sut;

    @Test
    public void testFirstTimeSeen() throws Exception {
        assertThat(sut.shouldSend(report), is(true));
    }

    @Test
    public void testSentOnceShouldNotRequire() throws Exception {
        sut.sent(report);
        assertThat(sut.shouldSend(report), is(false));
    }

    @Test
    public void testRemoveFirstElementsAfterMaxLimitReached() throws Exception {

        for (int i = 0; i < 150; i++) {
            String key = "fingerprint" + i;
            ErrorReport report = newReport(key);
            sut.sent(report);
        }
        for (int i = 0; i < 50; i++) {
            String key = "fingerprint" + i;
            assertThat(key + " is still in the list", sut.fingerprints.containsKey(key), is(false));
            assertTrue(sut.ignores.getSent().size() > 70);
        }
    }

    @Test
    public void testIO() throws Exception {
        File tmp = File.createTempFile("tmp", "xmi");

        sut = new FilterService(tmp);
        sut.open();
        sut.sent(report);
        sut.close();

        sut.open();

    }

    @Test
    public void testEmptyFile() throws Exception {
        File tmp = File.createTempFile("tmp", "xmi");
        Files.touch(tmp);
        sut = new FilterService(tmp);
        sut.open();
        sut.sent(report);
        sut.close();
        sut.open();

    }

    @Before
    public void before() {
        sut = new FilterService(new File(""));
        sut.open();

        report = newReport("fingerprint");
    }

    private ErrorReport newReport(String fingerprint) {
        ErrorReport report = ModelFactory.eINSTANCE.createErrorReport();
        Status status = ModelFactory.eINSTANCE.createStatus();
        status.setFingerprint(fingerprint);
        report.setStatus(status);
        return report;
    }
}
