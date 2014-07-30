/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package org.eclipse.recommenders.internal.models.rcp;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(MockitoJUnitRunner.class)
public class MultipleDownloadCallbackTest {

    private static final int ONE_HUNDRED_PERCENT = 100;
    private static final int MAXIMUM_DOWNLOADS = 2;

    private static final String MAVEN_METADATA_XML = "org/eclipse/recommenders/index/0.0.0-SNAPSHOT/maven-metadata.xml";
    private static final String INDEX_ZIP = "org/eclipse/recommenders/index/0.0.0-SNAPSHOT/index-0.0.0-20140605.014212-1.zip";
    private static final String JRE_ZIP = "jre/jre/1.0.0-SNAPSHOT/jre-1.0.0-20140605.013426-1-call.zip";

    @Mock
    private IProgressMonitor monitor;

    @Before
    public void setUp() {
        // The callback uses a submonitor for downloads which will not call worked() but internalWorked() on the
        // callback's monitor.
        // Tests are only interested in the overall work units and not if they are passed by worked() or
        // internalWorked() to the monitor. For that reason the mock-object forwards all worked()-calls with the same
        // parameter to the internalWorked()-method (as most of the Monitor-Implementations do).
        doAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                monitor.internalWorked((Integer) invocation.getArguments()[0]);
                return invocation;
            }
        }).when(monitor).worked(anyInt());
    }

    @Test
    public void testMonitorNotDoneByDefault() {
        new MultipleDownloadCallback(monitor, ONE_HUNDRED_PERCENT, MAXIMUM_DOWNLOADS);

        verify(monitor, never()).done();
    }

    @Test
    public void testDownloadsFinishedDoesNotSetTheMonitorDone() {
        MultipleDownloadCallback sut = new MultipleDownloadCallback(monitor, ONE_HUNDRED_PERCENT, MAXIMUM_DOWNLOADS);

        sut.allDownloadsFinished();

        verify(monitor, never()).done();
    }

    @Test
    public void testAllWorkDoneIfFinished() {
        ArgumentCaptor<Double> workedUnits = ArgumentCaptor.forClass(Double.class);
        MultipleDownloadCallback sut = new MultipleDownloadCallback(monitor, ONE_HUNDRED_PERCENT, MAXIMUM_DOWNLOADS);

        sut.allDownloadsFinished();

        verify(monitor, atLeastOnce()).internalWorked(workedUnits.capture());
        // 50 for the first skipped download, 50 for the second skipped download
        assertCapturedSequence(workedUnits, 50.0, 50.0);
    }

    @Test
    public void testAllWorkDoneIfFinishedWithOneDownload() {
        ArgumentCaptor<Double> workedUnits = ArgumentCaptor.forClass(Double.class);
        MultipleDownloadCallback sut = new MultipleDownloadCallback(monitor, ONE_HUNDRED_PERCENT, MAXIMUM_DOWNLOADS);

        sut.downloadInitiated(MAVEN_METADATA_XML);
        sut.downloadStarted(MAVEN_METADATA_XML);
        sut.downloadSucceeded(MAVEN_METADATA_XML);
        sut.allDownloadsFinished();

        verify(monitor, atLeastOnce()).internalWorked(workedUnits.capture());
        // 50 for the first download, 50 for the skipped download
        assertCapturedSequence(workedUnits, 50.0, 50.0);
    }

    @Test
    public void testNoDownloadNoSucceeded() {
        MultipleDownloadCallback sut = new MultipleDownloadCallback(monitor, ONE_HUNDRED_PERCENT, MAXIMUM_DOWNLOADS);
        assertFalse(sut.isDownloadSucceeded());
    }

    @Test
    public void testDownloadSucceeded() {
        int maximumNumberOfDownloads = 1;
        MultipleDownloadCallback sut = new MultipleDownloadCallback(monitor, ONE_HUNDRED_PERCENT,
                maximumNumberOfDownloads);

        sut.downloadInitiated(MAVEN_METADATA_XML);
        sut.downloadStarted(MAVEN_METADATA_XML);
        sut.downloadSucceeded(MAVEN_METADATA_XML);

        assertTrue(sut.isDownloadSucceeded());
    }

    @Test
    public void testSucceededWithOneFailedDownload() {
        MultipleDownloadCallback sut = new MultipleDownloadCallback(monitor, ONE_HUNDRED_PERCENT, MAXIMUM_DOWNLOADS);

        sut.downloadInitiated(MAVEN_METADATA_XML);
        sut.downloadStarted(MAVEN_METADATA_XML);
        sut.downloadSucceeded(MAVEN_METADATA_XML);
        sut.downloadInitiated(INDEX_ZIP);
        sut.downloadStarted(INDEX_ZIP);
        sut.downloadFailed(INDEX_ZIP);
        assertTrue(sut.isDownloadSucceeded());
    }

    @Test
    public void testNoDetailedProgressIfDownloadSizeNotKnown() {
        ArgumentCaptor<Double> workedUnits = ArgumentCaptor.forClass(Double.class);
        int numberOfDownloads = 1;
        MultipleDownloadCallback sut = new MultipleDownloadCallback(monitor, ONE_HUNDRED_PERCENT, numberOfDownloads);

        sut.downloadInitiated(MAVEN_METADATA_XML);
        sut.downloadStarted(MAVEN_METADATA_XML);
        sut.downloadProgressed(MAVEN_METADATA_XML, 512, -1);
        sut.downloadProgressed(MAVEN_METADATA_XML, 1024, -1);
        sut.downloadSucceeded(MAVEN_METADATA_XML);

        verify(monitor, atLeastOnce()).internalWorked(workedUnits.capture());
        // 100 for the download, no progress-steps
        assertCapturedSequence(workedUnits, 100.0);
    }

    @Test
    public void testFinishWithAllDownloads() {
        ArgumentCaptor<Double> workedUnits = ArgumentCaptor.forClass(Double.class);
        MultipleDownloadCallback sut = new MultipleDownloadCallback(monitor, ONE_HUNDRED_PERCENT, MAXIMUM_DOWNLOADS);

        sut.downloadInitiated(MAVEN_METADATA_XML);
        sut.downloadStarted(MAVEN_METADATA_XML);
        sut.downloadSucceeded(MAVEN_METADATA_XML);
        sut.downloadInitiated(INDEX_ZIP);
        sut.downloadStarted(INDEX_ZIP);
        sut.downloadSucceeded(INDEX_ZIP);
        sut.allDownloadsFinished();

        verify(monitor, atLeastOnce()).internalWorked(workedUnits.capture());
        // 50 for the first, 50 for the second, finished has no more effect
        assertCapturedSequence(workedUnits, 50.0, 50.0);
    }

    @Test
    public void testFinishedWithRemainderInWorkUnits() {
        int workUnits = 13;
        ArgumentCaptor<Double> workedUnits = ArgumentCaptor.forClass(Double.class);
        MultipleDownloadCallback sut = new MultipleDownloadCallback(monitor, workUnits, MAXIMUM_DOWNLOADS);

        sut.allDownloadsFinished();

        verify(monitor, atLeastOnce()).internalWorked(workedUnits.capture());
        assertCapturedSequence(workedUnits, 6.0, 6.0, 1.0);
    }

    @Test
    public void testDownloadProgressIfSizeKnown() {
        ArgumentCaptor<Double> workedUnits = ArgumentCaptor.forClass(Double.class);
        int maximumNumberOfDownloads = 1;
        MultipleDownloadCallback sut = new MultipleDownloadCallback(monitor, ONE_HUNDRED_PERCENT,
                maximumNumberOfDownloads);

        sut.downloadInitiated(MAVEN_METADATA_XML);
        sut.downloadProgressed(MAVEN_METADATA_XML, 256, 1024);
        sut.downloadProgressed(MAVEN_METADATA_XML, 512, 1024);
        sut.downloadProgressed(MAVEN_METADATA_XML, 1024, 1024);
        sut.downloadSucceeded(MAVEN_METADATA_XML);

        verify(monitor, atLeastOnce()).internalWorked(workedUnits.capture());
        // first 25% downloaded, then 50%, then 100%
        // (or 25% work added, again 25% work added, then 50% work added)
        assertCapturedSequence(workedUnits, 25.0, 25.0, 50.0);
    }

    @Test
    public void testDownloadProgressWithMultipleDownloads() {
        ArgumentCaptor<Double> workedUnits = ArgumentCaptor.forClass(Double.class);
        MultipleDownloadCallback sut = new MultipleDownloadCallback(monitor, ONE_HUNDRED_PERCENT, MAXIMUM_DOWNLOADS);
        long totalDownloadSize = 1024;

        sut.downloadInitiated(MAVEN_METADATA_XML);
        sut.downloadStarted(MAVEN_METADATA_XML);
        sut.downloadProgressed(MAVEN_METADATA_XML, totalDownloadSize / 4, totalDownloadSize);
        sut.downloadProgressed(MAVEN_METADATA_XML, totalDownloadSize / 2, totalDownloadSize);
        sut.downloadProgressed(MAVEN_METADATA_XML, totalDownloadSize, totalDownloadSize);
        sut.downloadSucceeded(MAVEN_METADATA_XML);
        sut.downloadInitiated(INDEX_ZIP);
        sut.downloadStarted(INDEX_ZIP);
        sut.downloadSucceeded(INDEX_ZIP);

        verify(monitor, atLeastOnce()).internalWorked(workedUnits.capture());
        // 12,12,25 for the first download
        // 1 for the remaining units in the first download
        // 50 for the second download
        assertCapturedSequence(workedUnits, 12.0, 12.0, 25.0, 1.0, 50.0);
    }

    @Test
    public void testFinishedWorkWithFailedDownload() {
        ArgumentCaptor<Double> workedUnits = ArgumentCaptor.forClass(Double.class);
        int maximumNumberOfDownloads = 1;
        MultipleDownloadCallback sut = new MultipleDownloadCallback(monitor, ONE_HUNDRED_PERCENT,
                maximumNumberOfDownloads);

        sut.downloadInitiated(JRE_ZIP);
        sut.downloadStarted(JRE_ZIP);
        sut.downloadFailed(JRE_ZIP);

        verify(monitor, atLeastOnce()).internalWorked(workedUnits.capture());
        assertCapturedSequence(workedUnits, (double) ONE_HUNDRED_PERCENT);
    }

    @Test
    public void testWorkUnitRemainderSplit() {
        int workUnits = 17;
        ArgumentCaptor<Double> workedUnits = ArgumentCaptor.forClass(Double.class);
        int maximumNumberOfDownloads = 3;
        MultipleDownloadCallback sut = new MultipleDownloadCallback(monitor, workUnits, maximumNumberOfDownloads);

        sut.downloadInitiated(MAVEN_METADATA_XML);
        sut.downloadStarted(MAVEN_METADATA_XML);
        sut.downloadSucceeded(MAVEN_METADATA_XML);
        sut.downloadInitiated(INDEX_ZIP);
        sut.downloadStarted(INDEX_ZIP);
        sut.downloadSucceeded(INDEX_ZIP);
        sut.downloadInitiated(JRE_ZIP);
        sut.downloadStarted(JRE_ZIP);
        sut.downloadSucceeded(JRE_ZIP);
        sut.allDownloadsFinished();

        verify(monitor, atLeastOnce()).internalWorked(workedUnits.capture());
        // 5 for the first download + 1 for the remainder
        // 5 for the second download + 1 for the remainder
        // 5 for the last download
        assertCapturedSequence(workedUnits, 6.0, 6.0, 5.0);
    }

    @Test
    public void testMaximumDownloadsFinishWork() {
        ArgumentCaptor<Double> workedUnits = ArgumentCaptor.forClass(Double.class);
        MultipleDownloadCallback sut = new MultipleDownloadCallback(monitor, ONE_HUNDRED_PERCENT, MAXIMUM_DOWNLOADS);

        sut.downloadInitiated(MAVEN_METADATA_XML);
        sut.downloadStarted(MAVEN_METADATA_XML);
        sut.downloadSucceeded(MAVEN_METADATA_XML);
        sut.downloadInitiated(INDEX_ZIP);
        sut.downloadStarted(INDEX_ZIP);
        sut.downloadSucceeded(INDEX_ZIP);
        // no skipped downloads to finish!

        verify(monitor, atLeastOnce()).internalWorked(workedUnits.capture());
        // 50 for the first download, 50 for the second download
        assertCapturedSequence(workedUnits, 50.0, 50.0);
    }

    private static void assertCapturedSequence(ArgumentCaptor<Double> captor, Double... expectedValues) {
        List<Double> allValues = captor.getAllValues();
        assertThat(allValues, Matchers.equalTo(Arrays.asList(expectedValues)));
    }

}
