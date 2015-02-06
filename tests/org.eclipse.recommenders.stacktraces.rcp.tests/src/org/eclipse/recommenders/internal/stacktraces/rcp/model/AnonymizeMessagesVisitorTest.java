/**
 * Copyright (c) 2014 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Daniel Haftstein - initial tests.
 */
package org.eclipse.recommenders.internal.stacktraces.rcp.model;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class AnonymizeMessagesVisitorTest {
    private static final ModelFactory FACTORY = ModelFactory.eINSTANCE;

    private AnonymizeMessagesVisitor sut;

    @Before
    public void setUp() {
        sut = Mockito.spy(new AnonymizeMessagesVisitor());
    }

    @Test
    public void testAnonymizeMessagesHandlesStatusAndException() {
        when(sut.anonymize(Mockito.any(String.class))).thenReturn("modified");
        ErrorReport report = FACTORY.createErrorReport();
        Status status = FACTORY.createStatus();
        report.setStatus(status);
        Throwable throwable = FACTORY.createThrowable();
        report.getStatus().setException(throwable);
        report.accept(sut);

        assertThat(report.getStatus().getMessage(), is("modified"));
        assertThat(report.getStatus().getException().getMessage(), is("modified"));
    }

    @Test
    public void testAnonymizeURLS1() {
        ErrorReport report = reportWithStatusMessage("Error: ssh://username@pretty.secret.domain:4242/path/to/something/interesting: Auth fail");
        report.accept(sut);
        assertThat(report.getStatus().getMessage(), is("Error: HIDDEN_PATH Auth fail"));
    }

    @Test
    public void testAnonymizeURLS2() {
        ErrorReport report = reportWithStatusMessage("https://user@gitproviderurl.something/else/repo.git: Read timed out after 300.000 ms");
        report.accept(sut);
        assertThat(report.getStatus().getMessage(), is("HIDDEN_PATH Read timed out after 300.000 ms"));
    }

    // TODO more should go here, don't forget about testing which things to keep in the messages

    private static ErrorReport reportWithStatusMessage(String message) {
        ErrorReport report = FACTORY.createErrorReport();
        Status status = FACTORY.createStatus();
        status.setMessage(message);
        return report;
    }
}
