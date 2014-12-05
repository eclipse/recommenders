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

import static org.apache.commons.lang3.ArrayUtils.toArray;

import java.text.MessageFormat;
import java.util.LinkedList;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

@RunWith(Parameterized.class)
public class ThankYouDialogTest {

    private static class ReportStateBuilder {

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

        public ReportStateBuilder needInfo() {
            keywords = new String[] { ReportState.KEYWORD_NEEDINFO };
            return this;
        }

        public ReportState build() {
            return newReportState(bugUrl, bugId, information, keywords, resolved, status, created);
        }
    }

    private static final String BUG_URL = "http://bug/bug42";
    private static final String BUG_ID = "42";
    private static final String BUG_INFORMATION = "Bug information";
    private static final ReportStateBuilder builder = new ReportStateBuilder();

    private static String MESSAGE_INFO = MessageFormat.format(Messages.THANKYOUDIALOG_COMMITTER_MESSAGE,
            BUG_INFORMATION);

    private static String MESSAGE_END = Messages.THANKYOUDIALOG_THANK_YOU_FOR_HELP;

    private static ReportState S_UNCONFIRMED_UNKNOWN = builder.fillDefaults().unconfirmed().build();
    private static ReportState S_NEW_UNKNOWN = builder.fillDefaults().newBug().build();
    private static ReportState S_ASSIGNED_UNKNOWN = builder.fillDefaults().assigned().build();

    private static ReportState S_RESOLVED_FIXED = builder.fillDefaults().resolvedFixed().build();
    private static ReportState S_RESOLVED_DUPLICATE = builder.fillDefaults().resolvedDuplicate().build();
    private static ReportState S_RESOLVED_WONTFIX = builder.fillDefaults().resolvedWontfix().build();
    private static ReportState S_RESOLVED_WORKSFORME = builder.fillDefaults().resolvedWorksforme().build();
    private static ReportState S_RESOLVED_INVALID = builder.fillDefaults().resolvedInvalid().build();

    private static ReportState S_CLOSED_FIXED = builder.fillDefaults().closedFixed().build();
    private static ReportState S_CLOSED_DUPLICATE = builder.fillDefaults().closedDuplicate().build();
    private static ReportState S_CLOSED_WONTFIX = builder.fillDefaults().closedWontfix().build();
    private static ReportState S_CLOSED_WORKSFORME = builder.fillDefaults().closedWorksforme().build();
    private static ReportState S_CLOSED_INVALID = builder.fillDefaults().closedInvalid().build();

    private ReportState state;
    private String messageShouldContain;

    public ThankYouDialogTest(ReportState state, String messageShouldContain, String description) {
        this.state = state;
        this.messageShouldContain = messageShouldContain;
    }

    @Parameters(name = "{2}")
    public static Iterable<Object[]> parameters() {
        LinkedList<Object[]> tests = Lists.newLinkedList();
        tests.add(toArray(S_UNCONFIRMED_UNKNOWN, Messages.THANKYOUDIALOG_NEW, "UNCONFIRMED UNKNOWN"));
        tests.add(toArray(S_NEW_UNKNOWN, "###", "NEW UNKNOWN"));
        tests.add(toArray(S_ASSIGNED_UNKNOWN, "###", "ASSIGNED UNKNOWN"));
        tests.add(toArray(S_RESOLVED_FIXED, "###", "RESOLVED FIXED"));
        tests.add(toArray(S_RESOLVED_DUPLICATE, "###", "RESOLVED DUPLICATE"));
        tests.add(toArray(S_RESOLVED_WONTFIX, "###", "RESOLVED WONTFIX"));
        tests.add(toArray(S_RESOLVED_WORKSFORME, "###", "RESOLVED WORKSFORME"));
        tests.add(toArray(S_RESOLVED_INVALID, "###", "RESOLVED INVALID"));
        tests.add(toArray(S_CLOSED_FIXED, "###", "CLOSED FIXED"));
        tests.add(toArray(S_CLOSED_DUPLICATE, "###", "CLOSED DUPLICATE"));
        tests.add(toArray(S_CLOSED_WONTFIX, "###", "CLOSED WONTFIX"));
        tests.add(toArray(S_CLOSED_WORKSFORME, "###", "CLOSED WORKSFORME"));
        tests.add(toArray(S_CLOSED_INVALID, "###", "CLOSED INVALID"));

        return tests;
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

    @Test
    public void testMessageForState() {
        ThankYouDialog sut = new ThankYouDialog(null, state);

        // split at each placeholder like {0}
        String[] split = messageShouldContain.split("\\{(.+?)\\}");

        String buildText = sut.buildText();
        for (String s : split) {
            Assert.assertThat(buildText, Matchers.containsString(s));
        }
    }
}
