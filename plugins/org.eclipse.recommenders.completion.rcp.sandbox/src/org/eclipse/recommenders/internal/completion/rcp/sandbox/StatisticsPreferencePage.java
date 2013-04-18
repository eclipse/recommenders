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
package org.eclipse.recommenders.internal.completion.rcp.sandbox;

import static com.google.common.base.Predicates.not;
import static java.lang.Math.round;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.apache.commons.math.stat.StatUtils.mean;
import static org.apache.commons.math.stat.StatUtils.sum;
import static org.eclipse.jface.viewers.StyledString.COUNTER_STYLER;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.primitives.ArrayDoubleList;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.recommenders.internal.completion.rcp.sandbox.CompletionEvent.ProposalKind;
import org.eclipse.recommenders.utils.Bag;
import org.eclipse.recommenders.utils.Names;
import org.eclipse.recommenders.utils.TreeBag;
import org.eclipse.recommenders.utils.names.ITypeName;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.gson.Gson;

public class StatisticsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

    private final class BuggyEventsPredicate implements Predicate<CompletionEvent> {
        @Override
        public boolean apply(final CompletionEvent input) {
            return (input.numberOfProposals < 1) || (input.sessionEnded < input.sessionStarted);
        }
    }

    private final class HasAppliedProposalPredicate implements Predicate<CompletionEvent> {
        @Override
        public boolean apply(final CompletionEvent e) {
            return e.applied != null;
        }
    }

    private static final long MAX_TIME_IN_COMPLETION = TimeUnit.MINUTES.toMillis(2);
    private Composite container;
    private StyledText styledText;
    private StyledString styledString;
    private Collection<CompletionEvent> okayEvents;
    private Collection<CompletionEvent> buggyEvents;
    private Collection<CompletionEvent> appliedEvents;
    private Collection<CompletionEvent> abortedEvents;

    public StatisticsPreferencePage() {
        super("Completion Statistics");

    }

    private void setDescription() {
        String date = "the beginning of recording";
        if (okayEvents.size() > 0) {
            final Date start = new Date(Iterables.getFirst(okayEvents, null).sessionStarted);
            date = format("%tF", start);
        }
        setDescription("Here is a summary of your code completion activity since " + date + ":");
    }

    @Override
    public void init(final IWorkbench workbench) {
        loadEvents();
        setDescription();
    }

    @Override
    protected Control createContents(final Composite parent) {
        createWidgets(parent);
        appendNumberOfCompletionEvents();
        appendNumberOfKeystrokesSaved();
        appendTimeSpent();
        appendNumberOfCompletionsByCompletionKind();
        appendNumberOfCompletionsByReceiverType();

        insertStyledText();
        return container;
    }

    private void createWidgets(final Composite parent) {
        container = new Composite(parent, SWT.NONE);
        container.setLayout(new GridLayout());
        styledText = new StyledText(container, SWT.READ_ONLY | SWT.WRAP);
        styledText.setBackground(container.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        styledString = new StyledString();
        final GridData data = new GridData(GridData.FILL_BOTH);
        data.widthHint = 400;
        styledText.setLayoutData(data);
    }

    private void loadEvents() {
        final File log = StatisticsSessionProcessor.getCompletionLogLocation();
        final Gson gson = StatisticsSessionProcessor.getCompletionLogSerializer();
        final LinkedList<CompletionEvent> events = Lists.newLinkedList();
        try {
            for (final String json : Files.readLines(log, Charsets.UTF_8)) {
                final CompletionEvent event = gson.fromJson(json, CompletionEvent.class);
                events.add(event);
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        buggyEvents = Collections2.filter(events, new BuggyEventsPredicate());
        okayEvents = Collections2.filter(events, not(new BuggyEventsPredicate()));
        appliedEvents = Collections2.filter(okayEvents, new HasAppliedProposalPredicate());
        abortedEvents = Collections2.filter(okayEvents, not(new HasAppliedProposalPredicate()));
    }

    private void appendNumberOfCompletionEvents() {
        int total = 0;
        for (final CompletionEvent e : okayEvents) {
            total += e.numberOfProposals;
        }
        final int completedInPercent = calculatePercentData(appliedEvents);
        styledString.append("Number of times code completion triggered: ")
                .append(format("%,d", okayEvents.size()), COUNTER_STYLER).append("\n");
        final int abortedInPercent = calculatePercentData(abortedEvents);

        styledString.append("Number of concluded completions: ")
                .append(appliedEvents.size() + " (" + completedInPercent + "%)", COUNTER_STYLER).append("\n");
        styledString.append("Number of aborted completions: ")
                .append(abortedEvents.size() + " (" + abortedInPercent + "%)", COUNTER_STYLER).append("\n");
        styledString.append("Number of proposals offered by code completion: ").append(total + "", COUNTER_STYLER)
                .append("\n");
    }

    private int calculatePercentData(final Collection<CompletionEvent> list) {
        if (okayEvents.size() == 0) {
            return okayEvents.size();
        }
        final double division = (list.size() / (double) okayEvents.size()) * 100;
        return (int) Math.round(division);
    }

    private void appendNumberOfKeystrokesSaved() {
        final ArrayDoubleList strokes = new ArrayDoubleList();
        for (final CompletionEvent e : appliedEvents) {
            final int prefix = e.prefix == null ? 0 : e.prefix.length();
            final int completionLength = e.completion == null ? 0 : e.completion.length();
            final int saved = Math.max(0, completionLength - prefix);
            strokes.add(saved);
        }

        final double total = sum(strokes.toArray());
        styledString.append("\nTotal number of keystrokes saved by using code completion: ")
                .append(format("%.0f", total), COUNTER_STYLER).append("\n");

        final double mean = mean(strokes.toArray());
        styledString.append("Average number of keystrokes saved per completion: ").append(format("%.2f", mean),
                COUNTER_STYLER);
        styledString.append("\n");
    }

    private void appendTimeSpent() {
        final ArrayDoubleList spentApplied = computeTimeSpentInCompletion(appliedEvents);
        final long totalApplied = round(sum(spentApplied.toArray()));
        final long meanApplied = round(mean(spentApplied.toArray()));

        final ArrayDoubleList spentAborted = computeTimeSpentInCompletion(abortedEvents);
        final long totalAborted = round(sum(spentAborted.toArray()));
        final long meanAborted = round(mean(spentAborted.toArray()));

        styledString.append("\nTotal Time spent in completion window on ")
        //
                .append("\n   - concluded sessions:    ").append(toTimeString(totalApplied), COUNTER_STYLER)
                //
                .append("\n   - aborted sessions:      ").append(toTimeString(totalAborted), COUNTER_STYLER);

        styledString.append("\n\nAverage time spent in completion window per")
        //
                .append("\n   - concluded session:    ").append(format("%,d ms", meanApplied), COUNTER_STYLER)
                //
                .append("\n   - aborted session:     ").append(format("%,d ms", meanAborted), COUNTER_STYLER);
    }

    private String toTimeString(final long time) {
        return format("%d min, %d sec", MILLISECONDS.toMinutes(time),
                MILLISECONDS.toSeconds(time) - MINUTES.toSeconds(MILLISECONDS.toMinutes(time)));
    }

    private ArrayDoubleList computeTimeSpentInCompletion(final Collection<CompletionEvent> events) {
        final ArrayDoubleList spent = new ArrayDoubleList();
        for (final CompletionEvent e : events) {
            long ms = e.sessionEnded - e.sessionStarted;
            if (ms > MAX_TIME_IN_COMPLETION) {
                ms = MAX_TIME_IN_COMPLETION;
            }
            spent.add(ms);
        }
        return spent;
    }

    private void appendNumberOfCompletionsByCompletionKind() {

        final Bag<ProposalKind> b = TreeBag.newTreeBag();
        for (final ProposalKind kind : ProposalKind.values()) {
            final Collection<CompletionEvent> byKind = Collections2.filter(okayEvents,
                    new Predicate<CompletionEvent>() {

                        @Override
                        public boolean apply(final CompletionEvent input) {
                            return kind == input.applied;
                        }
                    });
            if (byKind.size() > 0) {
                b.add(kind, byKind.size());
            }
        }

        styledString.append("\n\nMost frequently selected completion types were:\n");

        for (final ProposalKind kind : b.topElements(20)) {
            styledString.append("   - completion on ").append(kind.toString().toLowerCase().replace('_', ' '))
                    .append(": ").append(b.count(kind) + "", COUNTER_STYLER).append("\n");
        }
    }

    private void appendNumberOfCompletionsByReceiverType() {
        styledString.append("\nCode completion was triggered most frequently on variables of these types:")
                .append("\n");
        final Bag<ITypeName> b = TreeBag.newTreeBag();
        for (final CompletionEvent e : okayEvents) {
            if (e.receiverType == null) {
                continue;
            }
            b.add(e.receiverType);
        }

        for (final ITypeName type : b.topElements(20)) {
            styledString.append("   - ").append(Names.vm2srcQualifiedType(type)).append(": ")
                    .append(b.count(type) + "", COUNTER_STYLER).append("\n");
        }
    }

    private void insertStyledText() {
        styledText.setText(styledString.toString());
        styledText.setStyleRanges(styledString.getStyleRanges());
    }
}
