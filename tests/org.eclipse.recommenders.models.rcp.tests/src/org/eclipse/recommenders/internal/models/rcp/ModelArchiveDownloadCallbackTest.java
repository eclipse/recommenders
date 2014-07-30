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

import static org.junit.Assert.assertThat;
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
public class ModelArchiveDownloadCallbackTest {

    private static final int TOTAL_WORK_UNITS = 100;
    private static final int MAXIMUM_DOWNLOADS = 2;

    private static final String DOWNLOAD_1 = "org/eclipse/recommenders/index/0.0.0-SNAPSHOT/maven-metadata.xml";
    private static final String DOWNLOAD_2 = "org/eclipse/recommenders/index/0.0.0-SNAPSHOT/index-0.0.0-20140605.014212-1.zip";
    private static final String DOWNLOAD_3 = "org/eclipse/acceleo/org.eclipse.acceleo.common/3.0.0-SNAPSHOT/org.eclipse.acceleo.common-3.0.0-20140605.013426-4-ovrd.zip";

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
    public void noWorkDoneAtBegin() {
        new ModelArchiveDownloadCallback(monitor, TOTAL_WORK_UNITS, MAXIMUM_DOWNLOADS);

        verify(monitor, never()).done();
    }

    @Test
    public void downloadsFinishedDoesNotSetTheMonitorDone() {
        ModelArchiveDownloadCallback sut = new ModelArchiveDownloadCallback(monitor, TOTAL_WORK_UNITS,
                MAXIMUM_DOWNLOADS);

        sut.allDownloadsFinished();

        verify(monitor, never()).done();
    }

    @Test
    public void allDownloadsFinishedHandlesAllWorkUnits() {
        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        ModelArchiveDownloadCallback sut = new ModelArchiveDownloadCallback(monitor, TOTAL_WORK_UNITS,
                MAXIMUM_DOWNLOADS);

        sut.allDownloadsFinished();

        verify(monitor, atLeastOnce()).internalWorked(captor.capture());
        // 50 for the first skipped download, 50 for the second skipped download
        assertValues(captor, 50D, 50D);
    }

    @Test
    public void allDownloadsFinishedWorksWithPreviousDownloads1() {
        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        ModelArchiveDownloadCallback sut = new ModelArchiveDownloadCallback(monitor, TOTAL_WORK_UNITS,
                MAXIMUM_DOWNLOADS);

        sut.downloadInitiated(DOWNLOAD_1);
        sut.downloadStarted(DOWNLOAD_1);
        sut.downloadSucceeded(DOWNLOAD_1);
        sut.allDownloadsFinished();

        verify(monitor, atLeastOnce()).internalWorked(captor.capture());
        // 50 for the first download, 50 for the skipped download
        assertValues(captor, 50D, 50D);
    }

    @Test
    public void allDownloadsFinishedWorksWithPreviousDownloads2() {
        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        ModelArchiveDownloadCallback sut = new ModelArchiveDownloadCallback(monitor, TOTAL_WORK_UNITS,
                MAXIMUM_DOWNLOADS);

        sut.downloadInitiated(DOWNLOAD_1);
        sut.downloadStarted(DOWNLOAD_1);
        sut.downloadSucceeded(DOWNLOAD_1);
        sut.downloadInitiated(DOWNLOAD_2);
        sut.downloadStarted(DOWNLOAD_2);
        sut.downloadSucceeded(DOWNLOAD_2);
        sut.allDownloadsFinished();

        verify(monitor, atLeastOnce()).internalWorked(captor.capture());
        // 50 for the first, 50 for the second
        assertValues(captor, 50D, 50D);
    }

    @Test
    public void allDownloadsFinishedHandlesWorkUnitsRemainder() {
        int workUnits = 13;
        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        ModelArchiveDownloadCallback sut = new ModelArchiveDownloadCallback(monitor, workUnits, MAXIMUM_DOWNLOADS);
    
        sut.allDownloadsFinished();
    
        verify(monitor, atLeastOnce()).internalWorked(captor.capture());
        assertValues(captor, 6D, 6D, 1D);
    }

    @Test
    public void downloadProgressHandled() {
        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        int maximumNumberOfDownloads = 1;
        ModelArchiveDownloadCallback sut = new ModelArchiveDownloadCallback(monitor, TOTAL_WORK_UNITS,
                maximumNumberOfDownloads);
        long totalDownloadSize = 1024;

        sut.downloadInitiated(DOWNLOAD_1);
        sut.downloadProgressed(DOWNLOAD_1, totalDownloadSize / 4, totalDownloadSize);
        sut.downloadProgressed(DOWNLOAD_1, totalDownloadSize / 2, totalDownloadSize);
        sut.downloadProgressed(DOWNLOAD_1, totalDownloadSize, totalDownloadSize);
        sut.downloadSucceeded(DOWNLOAD_1);
        verify(monitor, atLeastOnce()).internalWorked(captor.capture());
        // first 25% downloaded, then 50%, then 100%
        // (or 25% work added, again 25% work added, then 50% work added)
        assertValues(captor, 25D, 25D, 50D);
    }

    @Test
    public void downloadProgressWithMultipleDownloads() {
        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        ModelArchiveDownloadCallback sut = new ModelArchiveDownloadCallback(monitor, TOTAL_WORK_UNITS,
                MAXIMUM_DOWNLOADS);
        long totalDownloadSize = 1024;

        sut.downloadInitiated(DOWNLOAD_1);
        sut.downloadStarted(DOWNLOAD_1);
        sut.downloadProgressed(DOWNLOAD_1, totalDownloadSize / 4, totalDownloadSize);
        sut.downloadProgressed(DOWNLOAD_1, totalDownloadSize / 2, totalDownloadSize);
        sut.downloadProgressed(DOWNLOAD_1, totalDownloadSize, totalDownloadSize);
        sut.downloadSucceeded(DOWNLOAD_1);
        sut.downloadInitiated(DOWNLOAD_2);
        sut.downloadStarted(DOWNLOAD_2);
        sut.downloadSucceeded(DOWNLOAD_2);
        verify(monitor, atLeastOnce()).internalWorked(captor.capture());
        // 12,12,25 for the first download
        // 1 for the remaining units in the first download
        // 50 for the second download
        assertValues(captor, 12D, 12D, 25D, 1D, 50D);
    }

    @Test
    public void downloadFailedFinishesWork() {
        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        int maximumDownloads = 1;
        ModelArchiveDownloadCallback sut = new ModelArchiveDownloadCallback(monitor, TOTAL_WORK_UNITS, maximumDownloads);

        sut.downloadInitiated(DOWNLOAD_3);
        sut.downloadStarted(DOWNLOAD_3);
        sut.downloadFailed(DOWNLOAD_3);

        verify(monitor, atLeastOnce()).internalWorked(captor.capture());
        assertValues(captor, (double) TOTAL_WORK_UNITS);
    }

    @Test
    public void workUnitsRemainderAddedToFirstDownload() {
        int workUnits = 13;
        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        ModelArchiveDownloadCallback sut = new ModelArchiveDownloadCallback(monitor, workUnits, MAXIMUM_DOWNLOADS);

        sut.downloadInitiated(DOWNLOAD_1);
        sut.downloadStarted(DOWNLOAD_1);
        sut.downloadSucceeded(DOWNLOAD_1);
        sut.downloadInitiated(DOWNLOAD_2);
        sut.downloadStarted(DOWNLOAD_2);
        sut.downloadSucceeded(DOWNLOAD_2);
        sut.allDownloadsFinished();

        verify(monitor, atLeastOnce()).internalWorked(captor.capture());
        //6 for the first download + 1 for the remainder, 6 for the second
        assertValues(captor, 7D, 6D);
    }

    @Test
    public void maximumDownloadsFinishWork() {
        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        ModelArchiveDownloadCallback sut = new ModelArchiveDownloadCallback(monitor, TOTAL_WORK_UNITS,
                MAXIMUM_DOWNLOADS);

        sut.downloadInitiated(DOWNLOAD_1);
        sut.downloadStarted(DOWNLOAD_1);
        sut.downloadSucceeded(DOWNLOAD_1);
        sut.downloadInitiated(DOWNLOAD_2);
        sut.downloadStarted(DOWNLOAD_2);
        sut.downloadSucceeded(DOWNLOAD_2);
        // no skipped downloads to finish!

        verify(monitor, atLeastOnce()).internalWorked(captor.capture());
        assertValues(captor, 50D, 50D);
    }

    private static void assertValues(ArgumentCaptor<Double> captor, Double... expectedValues) {
        List<Double> allValues = captor.getAllValues();
        assertThat(allValues, Matchers.equalTo(Arrays.asList(expectedValues)));
    }

}
