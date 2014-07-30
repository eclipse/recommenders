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

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class ModelArchiveDownloadCallbackTest {

    @Test
    public void noArchiveDownloadedByDefault() {
        IProgressMonitor monitor = null;
        int totalWorkUnits = 1;
        ModelArchiveDownloadCallback sut = new ModelArchiveDownloadCallback(monitor, totalWorkUnits);
        assertFalse(sut.isArchiveDownloaded());
    }

    @Test
    public void updateMonitorWorkDoesNotSetTheMonitorDone() {
        IProgressMonitor monitor = Mockito.mock(IProgressMonitor.class);
        int totalWorkUnits = 1;
        ModelArchiveDownloadCallback sut = new ModelArchiveDownloadCallback(monitor, totalWorkUnits);
        sut.updateMonitorWorkForSkippedDownloadTasks();
        Mockito.verify(monitor, Mockito.never()).done();
    }

    @Test
    public void updateMonitorWorkFinishesAllWorkUnits() {
        IProgressMonitor monitor = Mockito.mock(IProgressMonitor.class);
        int totalWorkUnits = 12;
        ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);
        ModelArchiveDownloadCallback sut = new ModelArchiveDownloadCallback(monitor, totalWorkUnits);
        // no downloads
        sut.updateMonitorWorkForSkippedDownloadTasks();
        Mockito.verify(monitor, Mockito.atLeastOnce()).worked(captor.capture());
        assertThat(sumI(captor.getAllValues()), CoreMatchers.is(totalWorkUnits));
    }

    @Test
    public void updateMonitorWorkFinishesAllWorkUnitsWithDownloads() {
        final IProgressMonitor monitor = Mockito.mock(IProgressMonitor.class);
        // The callback uses a submonitor for downloads which will not call worked() but internalWorked() on the
        // callback's monitor.
        // The test is only interested in the overall work units and not if they are passed by worked() or
        // internalWorked() to the monitor. For that reason the mock-object forwards all worked()-calls with the same
        // parameter to the internalWorked()-method (as most of the Monitor-Implementations do).
        Mockito.doAnswer(new Answer<Object>() {

            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                monitor.internalWorked((Integer) invocation.getArguments()[0]);
                return invocation;
            }
        }).when(monitor).worked(Mockito.anyInt());
        int totalWorkUnits = 12;
        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        ModelArchiveDownloadCallback sut = new ModelArchiveDownloadCallback(monitor, totalWorkUnits);

        executeDownload(sut, "");
        executeDownload(sut, "as");

        sut.updateMonitorWorkForSkippedDownloadTasks();
        Mockito.verify(monitor, Mockito.atLeastOnce()).internalWorked(captor.capture());
        assertThat(sumD(captor.getAllValues()), CoreMatchers.is((double) totalWorkUnits));

        // alternate version:
        // final IProgressMonitor monitor = Mockito.mock(IProgressMonitor.class);
        //
        // int totalWorkUnits = 12;
        // ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        // ArgumentCaptor<Integer> captor2 = ArgumentCaptor.forClass(Integer.class);
        // ModelArchiveDownloadCallback sut = new ModelArchiveDownloadCallback(monitor, totalWorkUnits);
        //
        // executeDownload(sut, "");
        //
        // sut.updateMonitorWorkForSkippedDownloadTasks();
        // Mockito.verify(monitor, Mockito.atLeastOnce()).internalWorked(captor.capture()); // fails without download
        // Mockito.verify(monitor, Mockito.atLeastOnce()).worked(captor2.capture());// fails without finish work
        // assertThat(sumD(captor.getAllValues()), CoreMatchers.is((double) totalWorkUnits / 2));
        // assertThat(sumI(captor2.getAllValues()), CoreMatchers.is(totalWorkUnits / 2));
    }

    @Test
    public void twoDownloadsFinishWork() {
        IProgressMonitor monitor = Mockito.mock(IProgressMonitor.class);
        int totalWorkUnits = 12;
        ArgumentCaptor<Double> captor = ArgumentCaptor.forClass(Double.class);
        ModelArchiveDownloadCallback sut = new ModelArchiveDownloadCallback(monitor, totalWorkUnits);

        executeDownload(sut, "d1");
        executeDownload(sut, "d2");
        // no skipped downloads to finish!

        Mockito.verify(monitor, Mockito.atLeastOnce()).internalWorked(captor.capture());
        assertThat(sumD(captor.getAllValues()), CoreMatchers.is((double) totalWorkUnits));
    }

    private static void executeDownload(ModelArchiveDownloadCallback callback, String path) {
        callback.downloadInitiated(path);
        callback.downloadStarted(path);
        callback.downloadSucceeded(path);
    }

    private static double sumD(Collection<Double> values) {
        double sum = 0;
        for (Double i : values) {
            sum += i;
        }
        return sum;
    }

    private static int sumI(Collection<Integer> values) {
        int sum = 0;
        for (Integer i : values) {
            sum += i;
        }
        return sum;
    }

}
