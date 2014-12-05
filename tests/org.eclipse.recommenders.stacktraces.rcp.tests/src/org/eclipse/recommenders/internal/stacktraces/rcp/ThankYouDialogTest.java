/**
 * Copyright (c) 2014 Codetrails GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial implementation
 */
package org.eclipse.recommenders.internal.stacktraces.rcp;

import static org.mockito.Mockito.verify;

import org.eclipse.recommenders.internal.stacktraces.rcp.ThankYouDialog.MessageBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Optional;

public class ThankYouDialogTest {

    private static class ReportStateBuilder {

        private static final String BUG_URL = "http://bug/bug42";
        private static final String BUG_ID = "42";
        private static final String BUG_INFORMATION = "Bug information";
        private String bugUrl;
        private String bugId;
        private String information;
        private String[] keywords;
        private String resolved;
        private String status;
        private Boolean created;

        public ReportStateBuilder fillDefaults() {
            bugUrl = BUG_URL;
            bugId = BUG_ID;
            information = null;
            keywords = null;
            resolved = null;
            status = null;
            created = false;
            return this;
        }

        public ReportStateBuilder withInformation() {
            information = BUG_INFORMATION;
            return this;
        }

        public ReportStateBuilder unconfirmed() {
            status = ReportState.UNCONFIRMED;
            resolved = ReportState.UNKNOWN;
            created = true;
            return this;
        }

        public ReportStateBuilder newBug() {
            status = ReportState.NEW;
            resolved = ReportState.UNKNOWN;
            return this;
        }

        public ReportStateBuilder assigned() {
            status = ReportState.ASSIGNED;
            resolved = ReportState.UNKNOWN;
            return this;
        }

        public ReportStateBuilder resolvedFixed() {
            status = ReportState.RESOLVED;
            resolved = ReportState.FIXED;
            return this;
        }

        public ReportStateBuilder resolvedDuplicate() {
            status = ReportState.RESOLVED;
            resolved = ReportState.DUPLICATE;
            return this;
        }

        public ReportStateBuilder resolvedNotEclipse() {
            status = ReportState.RESOLVED;
            resolved = ReportState.NOT_ECLIPSE;
            return this;
        }

        public ReportStateBuilder resolvedWontfix() {
            status = ReportState.RESOLVED;
            resolved = ReportState.WONTFIX;
            return this;
        }

        public ReportStateBuilder resolvedWorksforme() {
            status = ReportState.RESOLVED;
            resolved = ReportState.WORKSFORME;
            return this;
        }

        public ReportStateBuilder resolvedInvalid() {
            status = ReportState.RESOLVED;
            resolved = ReportState.INVALID;
            return this;
        }

        public ReportStateBuilder resolvedMoved() {
            status = ReportState.RESOLVED;
            resolved = ReportState.MOVED;
            return this;
        }

        public ReportStateBuilder resolvedUnknown() {
            status = ReportState.RESOLVED;
            resolved = ReportState.UNKNOWN;
            return this;
        }

        public ReportStateBuilder closedFixed() {
            status = ReportState.CLOSED;
            resolved = ReportState.FIXED;
            return this;
        }

        public ReportStateBuilder closedDuplicate() {
            status = ReportState.CLOSED;
            resolved = ReportState.DUPLICATE;
            return this;
        }

        public ReportStateBuilder closedNotEclipse() {
            status = ReportState.CLOSED;
            resolved = ReportState.NOT_ECLIPSE;
            return this;
        }

        public ReportStateBuilder closedWontfix() {
            status = ReportState.CLOSED;
            resolved = ReportState.WONTFIX;
            return this;
        }

        public ReportStateBuilder closedWorksforme() {
            status = ReportState.CLOSED;
            resolved = ReportState.WORKSFORME;
            return this;
        }

        public ReportStateBuilder closedInvalid() {
            status = ReportState.CLOSED;
            resolved = ReportState.INVALID;
            return this;
        }

        public ReportStateBuilder closedMoved() {
            status = ReportState.CLOSED;
            resolved = ReportState.MOVED;
            return this;
        }

        public ReportStateBuilder closedUnknown() {
            status = ReportState.CLOSED;
            resolved = ReportState.UNKNOWN;
            return this;
        }

        public ReportStateBuilder badServerResponse() {
            status = "Unknown server response";
            return this;
        }

        public ReportState build() {
            return newReportState(bugUrl, bugId, information, keywords, resolved, status, created);
        }

        private static ReportState newReportState(String bugUrl, String bugId, String information, String[] keywords,
                String resolved, String status, Boolean created) {
            ReportState mock = Mockito.mock(ReportState.class);
            Mockito.when(mock.getBugUrl()).thenReturn(Optional.fromNullable(bugUrl));
            Mockito.when(mock.getBugId()).thenReturn(Optional.fromNullable(bugId));
            Mockito.when(mock.getInformation()).thenReturn(Optional.fromNullable(information));
            Mockito.when(mock.getKeywords()).thenReturn(Optional.fromNullable(keywords));
            Mockito.when(mock.getResolved()).thenReturn(Optional.fromNullable(resolved));
            Mockito.when(mock.getStatus()).thenReturn(Optional.fromNullable(status));
            Mockito.when(mock.isCreated()).thenReturn(created);
            return mock;
        }
    }

    private ReportStateBuilder builder;
    private MessageBuilder sut;

    @Before
    public void setUp() {
        sut = Mockito.spy(new ThankYouDialog.MessageBuilder());
        builder = new ReportStateBuilder();
    }

    @Test
    public void testUnconfirmedUnknown() {
        sut.state = builder.fillDefaults().unconfirmed().build();
        sut.buildText();
        Mockito.verify(sut).messageNewBugCreated();
    }

    @Test
    public void testNewUnknown() {
        sut.state = builder.fillDefaults().newBug().build();
        sut.buildText();
        Mockito.verify(sut).messageMatchedAgainstExistingBug();
    }

    @Test
    public void testAssignedUnknown() {
        sut.state = builder.fillDefaults().assigned().build();
        sut.buildText();
        Mockito.verify(sut).messageMatchedAgainstExistingBug();
    }

    @Test
    public void testResolvedFixed() {
        sut.state = builder.fillDefaults().resolvedFixed().build();
        sut.buildText();
        Mockito.verify(sut).messageFixed();
    }

    @Test
    public void testResolvedDuplicate() {
        sut.state = builder.fillDefaults().resolvedDuplicate().build();
        sut.buildText();
        Mockito.verify(sut).messageDuplicate();
    }

    @Test
    public void testResolvedWontfix() {
        sut.state = builder.fillDefaults().resolvedWontfix().build();
        sut.buildText();
        Mockito.verify(sut).messageNormal();
    }

    @Test
    public void testResolvedNotEclipse() {
        sut.state = builder.fillDefaults().resolvedNotEclipse().build();
        sut.buildText();
        Mockito.verify(sut).messageNormal();
    }

    @Test
    public void testResolvedWorksforme() {
        sut.state = builder.fillDefaults().resolvedWorksforme().build();
        sut.buildText();
        verify(sut).messageWorksforme();
    }

    @Test
    public void testResolvedInvalid() {
        sut.state = builder.fillDefaults().resolvedInvalid().build();
        sut.buildText();
        verify(sut).messageNormal();
    }

    @Test
    public void testResolvedMoved() {
        sut.state = builder.fillDefaults().resolvedMoved().build();
        sut.buildText();
        verify(sut).messageMoved();
    }

    @Test
    public void testResolvedUnknown() {
        sut.state = builder.fillDefaults().resolvedUnknown().build();
        sut.buildText();
        verify(sut).messageUnknown(Mockito.anyString());
    }

    @Test
    public void testClosedFixed() {
        sut.state = builder.fillDefaults().closedFixed().build();
        sut.buildText();
        verify(sut).messageFixed();
    }

    @Test
    public void testClosedDuplicate() {
        sut.state = builder.fillDefaults().closedDuplicate().build();
        sut.buildText();
        Mockito.verify(sut).messageDuplicate();
    }

    @Test
    public void testClosedWontfix() {
        sut.state = builder.fillDefaults().closedWontfix().build();
        sut.buildText();
        verify(sut).messageNormal();
    }

    @Test
    public void testClosedNotEclipse() {
        sut.state = builder.fillDefaults().closedNotEclipse().build();
        sut.buildText();
        verify(sut).messageNormal();
    }

    @Test
    public void testClosedWorksforme() {
        sut.state = builder.fillDefaults().closedWorksforme().build();
        sut.buildText();
        verify(sut).messageWorksforme();
    }

    @Test
    public void testClosedInvalid() {
        sut.state = builder.fillDefaults().closedInvalid().build();
        sut.buildText();
        verify(sut).messageNormal();
    }

    @Test
    public void testClosedMoved() {
        sut.state = builder.fillDefaults().closedMoved().build();
        sut.buildText();
        verify(sut).messageMoved();
    }

    @Test
    public void testClosedUnknown() {
        sut.state = builder.fillDefaults().closedUnknown().build();
        sut.buildText();
        verify(sut).messageUnknown(Mockito.anyString());
    }

    @Test
    public void testUnknownStatus() {
        sut.state = builder.fillDefaults().badServerResponse().build();
        sut.buildText();
        verify(sut).messageUnknownServerResponse();
    }

    @Test
    public void testInfo() {
        sut.state = builder.fillDefaults().withInformation().build();
        sut.buildText();
        verify(sut).messageCommitterInfo();
    }

}
